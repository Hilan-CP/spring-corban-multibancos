package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.dto.ProposalFilterDTO;
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
	public Page<ProposalDataDTO> findByFilter(ProposalFilterDTO filter, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProposalDataDTO> criteria = builder.createQuery(ProposalDataDTO.class);
		Root<Proposal> root = criteria.from(Proposal.class);
		criteria.multiselect(getSelections(root));
		criteria.where(predicateBuilder(filter, root, builder));

		TypedQuery<ProposalDataDTO> query = entityManager.createQuery(criteria);
		Long count = 0L;
		if(pageable.isPaged()) {
			query.setFirstResult((int) pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
			count = countQuery(filter);
		}
		List<ProposalDataDTO> result = query.getResultList();
		return new PageImpl<>(result, pageable, count);
	}

	private Long countQuery(ProposalFilterDTO filter) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Proposal> proposal = query.from(Proposal.class);
		query.select(builder.count(proposal));
		query.where(predicateBuilder(filter, proposal, builder));
		return entityManager.createQuery(query).getSingleResult();
	}

	private Predicate[] predicateBuilder(ProposalFilterDTO filter, Root<Proposal> root, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<>();
		if (filter.getUserId() != null) {
			predicates.add(equalEmployeeId(filter.getUserId(), root, builder));
		}
		if (!filter.getEmployeeName().isBlank()) {
			predicates.add(likeEmployeeName(filter.getEmployeeName(), root, builder));
		}
		if (filter.getBankCode() != null) {
			predicates.add(equalBankCode(filter.getBankCode(), root, builder));
		}
		predicates.add(dateBetween(filter.getDateField(), filter.getBeginDate(), filter.getEndDate(), root, builder));
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

	private Predicate equalEmployeeId(Long id, Root<Proposal> root, CriteriaBuilder builder) {
		return builder.equal(root.get("employee").get("id"), id);
	}

	private List<Selection<?>> getSelections(Root<Proposal> proposal) {
		List<Selection<?>> selections = List.of(
				proposal.get("id"),
				proposal.get("code"),
				proposal.get("rawValue"),
				proposal.get("generation"),
				proposal.get("payment"),
				proposal.get("status"),
				proposal.get("employee").get("name"),
				proposal.get("bank").get("name"),
				proposal.get("customer").get("cpf"),
				proposal.get("customer").get("name"));
		return selections;
	}
}