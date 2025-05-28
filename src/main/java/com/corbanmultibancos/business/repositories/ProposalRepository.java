package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

	Optional<Proposal> findByCode(String code);

	Page<Proposal> findByEmployeeAndGenerationDate(String employeeName, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	Page<Proposal> findByEmployeeAndPaymentDate(String employeeName, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	Page<Proposal> findByBankAndGenerationDate(Long bankCode, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	Page<Proposal> findByBankAndPaymentDate(Long bankCode, LocalDate beginDate, LocalDate endDate, Pageable pageable);
	
	Page<Proposal> findByGenerationDate(LocalDate beginDate, LocalDate endDate, Pageable pageable);
	
	Page<Proposal> findByPaymentDate(LocalDate beginDate, LocalDate endDate, Pageable pageable);
}
