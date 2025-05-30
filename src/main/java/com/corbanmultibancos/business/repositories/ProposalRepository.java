package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long>, ProposalCriteriaRepository {

	Optional<Proposal> findByCode(String code);
}
