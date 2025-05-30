package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long>, ProposalCriteriaRepository {

	Optional<Proposal> findByCode(String code);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
			+ "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
			+ "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
			+ "FROM Proposal proposal "
			+ "JOIN proposal.employee employee "
			+ "WHERE LOWER(employee.name) LIKE LOWER(CONCAT('%', :employeeName, '%')) "
			+ "AND proposal.generation BETWEEN :beginDate AND :endDate",
			countQuery = "SELECT COUNT(proposal) "
					+ "FROM Proposal proposal "
					+ "JOIN proposal.employee employee "
					+ "WHERE LOWER(employee.name) LIKE LOWER(CONCAT('%', :employeeName, '%')) "
					+ "AND proposal.generation BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByEmployeeAndGenerationDate(String employeeName, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
            + "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
            + "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
            + "FROM Proposal proposal "
            + "JOIN proposal.employee employee "
            + "WHERE LOWER(employee.name) LIKE LOWER(CONCAT('%', :employeeName, '%')) "
            + "AND proposal.payment BETWEEN :beginDate AND :endDate",
            countQuery = "SELECT COUNT(proposal) "
                    + "FROM Proposal proposal "
                    + "JOIN proposal.employee employee "
                    + "WHERE LOWER(employee.name) LIKE LOWER(CONCAT('%', :employeeName, '%')) "
                    + "AND proposal.payment BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByEmployeeAndPaymentDate(String employeeName, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
            + "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
            + "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
            + "FROM Proposal proposal "
            + "JOIN proposal.bank bank "
            + "WHERE bank.code = :bankCode "
            + "AND proposal.generation BETWEEN :beginDate AND :endDate",
            countQuery = "SELECT COUNT(proposal) "
                    + "FROM Proposal proposal "
                    + "JOIN proposal.bank bank "
                    + "WHERE bank.code = :bankCode "
                    + "AND proposal.generation BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByBankAndGenerationDate(Integer bankCode, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
            + "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
            + "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
            + "FROM Proposal proposal "
            + "JOIN proposal.bank bank "
            + "WHERE bank.code = :bankCode "
            + "AND proposal.payment BETWEEN :beginDate AND :endDate",
            countQuery = "SELECT COUNT(proposal) "
                    + "FROM Proposal proposal "
                    + "JOIN proposal.bank bank "
                    + "WHERE bank.code = :bankCode "
                    + "AND proposal.payment BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByBankAndPaymentDate(Integer bankCode, LocalDate beginDate, LocalDate endDate, Pageable pageable);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
            + "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
            + "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
            + "FROM Proposal proposal "
            + "WHERE proposal.generation BETWEEN :beginDate AND :endDate",
            countQuery = "SELECT COUNT(proposal) "
                    + "FROM Proposal proposal "
                    + "WHERE proposal.generation BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByGenerationDate(LocalDate beginDate, LocalDate endDate, Pageable pageable);

	@Query(value = "SELECT new com.corbanmultibancos.business.dto.ProposalDataDTO("
            + "proposal.id, proposal.code, proposal.rawValue, proposal.generation, proposal.payment, proposal.status, "
            + "proposal.employee.name, proposal.bank.name, proposal.customer.cpf, proposal.customer.name) "
            + "FROM Proposal proposal "
            + "WHERE proposal.payment BETWEEN :beginDate AND :endDate",
            countQuery = "SELECT COUNT(proposal) "
                    + "FROM Proposal proposal "
                    + "WHERE proposal.payment BETWEEN :beginDate AND :endDate")
	Page<ProposalDataDTO> findByPaymentDate(LocalDate beginDate, LocalDate endDate, Pageable pageable);

}
