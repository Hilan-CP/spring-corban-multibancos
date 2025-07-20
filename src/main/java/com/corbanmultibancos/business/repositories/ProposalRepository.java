package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long>, ProposalCriteriaRepository {

	@EntityGraph(attributePaths = {"employee", "bank", "customer", "employee.user"})
	Optional<Proposal> findByCode(String code);
}
