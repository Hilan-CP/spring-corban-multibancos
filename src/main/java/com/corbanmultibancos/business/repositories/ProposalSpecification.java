package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Proposal;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;

//classe construtora de predicados para consultas com Criteria API
public class ProposalSpecification {

	public static Specification<Proposal> likeEmployeeName(String employeeName){
		return (root, query, builder) -> {
			return builder.like(builder.lower(root.get("employee").get("name")), "%" + employeeName.toLowerCase() + "%");
		};
	}

	public static Specification<Proposal> equalBankCode(Integer bankCode){
		return (root, query, builder) -> {
			return builder.equal(root.get("bank").get("code"), bankCode);
		};
	}

	public static Specification<Proposal> dateBetween(String dateField, LocalDate beginDate, LocalDate endDate){
		return (root, query, builder) -> {
			return builder.between(root.get(dateField), beginDate, endDate);
		};
	}

	//usa fetch na consulta que busca dados e ignora fetch na countQuery
	//fetch causa erros na countQuery
	//https://stackoverflow.com/questions/29348742/spring-data-jpa-creating-specification-query-fetch-joins
	public static Specification<Proposal> fetchRelatedEntities(){
		return (root, query, builder) -> {
			if(isNotCountQuery(query)) {
				root.fetch("customer");
				root.fetch("bank");
				Fetch<Proposal, Employee> employee = root.fetch("employee");
				employee.fetch("team");
				employee.fetch("user").fetch("role");
			}
			return null;
		};
	}
	
	private static boolean isNotCountQuery(CriteriaQuery<?> query) {
		return query.getResultType() != Long.class;
	}
}
