package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Proposal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public class ProposalCriteriaRepositoryImpl implements ProposalCriteriaRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<ProposalDataDTO> findBy(Map<String, Object> filter, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProposalDataDTO> criteria = builder.createQuery(ProposalDataDTO.class);
		Root<Proposal> root = criteria.from(Proposal.class);
		criteria.multiselect(getSelections(root));
		criteria.where(predicateBuilder(filter, root, builder));

		TypedQuery<ProposalDataDTO> query = entityManager.createQuery(criteria);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		List<ProposalDataDTO> result = query.getResultList();
		
		Long count = countQuery(filter);
		return new PageImpl<>(result, pageable, count);
	}

	private Long countQuery(Map<String, Object> filter) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Proposal> proposal = query.from(Proposal.class);
		query.select(builder.count(proposal));
		query.where(predicateBuilder(filter, proposal, builder));
		return entityManager.createQuery(query).getSingleResult();
	}

	private Predicate[] predicateBuilder(Map<String, Object> filter, Root<Proposal> root, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<>();
		if (filter.containsKey("employeeName")) {
			predicates.add(likeEmployeeName((String) filter.get("employeeName"), root, builder));
		}
		else if (filter.containsKey("bankCode")) {
			predicates.add(equalBankCode((Integer) filter.get("bankCode"), root, builder));
		}
		predicates.add(dateBetween((String) filter.get("dateField"),
				(LocalDate) filter.get("beginDate"),
				(LocalDate) filter.get("endDate"),
				root, builder));
		return predicates.toArray(new Predicate[0]);
	}

	private Predicate likeEmployeeName(String employeeName, Root<Proposal> root, CriteriaBuilder builder) {
		return builder.like(builder.lower(root.get("employee").get("name")), "%" + employeeName.toLowerCase() + "%");
	}

	private Predicate equalBankCode(Integer bankCode, Root<Proposal> root, CriteriaBuilder builder) {
		return builder.equal(root.get("bank").get("code"), bankCode);
	}

	private Predicate dateBetween(String fieldName, LocalDate begin, LocalDate end, Root<Proposal> root,
			CriteriaBuilder builder) {
		return builder.between(root.get(fieldName), begin, end);
	}

	private List<Selection<?>> getSelections(Root<Proposal> proposal) {
		List<Selection<?>> selections = List.of(
				proposal.get("id").alias("id"),
				proposal.get("code").alias("code"),
				proposal.get("rawValue").alias("rawValue"),
				proposal.get("generation").alias("generation"),
				proposal.get("payment").alias("payment"),
				proposal.get("status").alias("status"),
				proposal.get("employee").get("name").alias("employeeName"),
				proposal.get("bank").get("name").alias("bankName"),
				proposal.get("customer").get("cpf").alias("customerCpf"),
				proposal.get("customer").get("name").alias("customerName"));
		return selections;
	}
}