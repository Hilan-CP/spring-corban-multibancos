package com.corbanmultibancos.business.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.mappers.ProposalMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.ProposalRepository;
import com.corbanmultibancos.business.repositories.ProposalSpecification;

@Service
public class ProposalService {

	@Autowired
	private ProposalRepository proposalRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private BankRepository bankRepository;

	@Transactional(readOnly = true)
	public ProposalDataDTO getProposalById(Long id) {
		return null;
	}

	@Transactional(readOnly = true)
	public Page<ProposalDataDTO> getProposals(Integer code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate, Pageable pageable) {
		Specification<Proposal> predicates = ProposalSpecification.fetchRelatedEntities()
				.and(ProposalSpecification.likeEmployeeName(""))
				.and(ProposalSpecification.dateBetween("generation", LocalDate.of(2025, 1, 1), LocalDate.now()));
		Page<Proposal> result = proposalRepository.findAll(predicates, pageable);
		return result.map(proposal -> ProposalMapper.toProposalDataDto(proposal));
//		return proposalRepository.findByEmployeeAndGenerationDate("", LocalDate.of(2025, 1, 1), LocalDate.now(), pageable);
	}

	@Transactional
	public ProposalDataDTO createProposal(ProposalCreateDTO proposalDto) {
		return null;
	}

	@Transactional
	public ProposalDataDTO updateProposal(Long id, ProposalCreateDTO proposalDto) {
		return null;
	}
}
