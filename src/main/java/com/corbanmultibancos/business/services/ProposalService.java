package com.corbanmultibancos.business.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.ProposalRepository;

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
	public Page<ProposalDataDTO> getProposals(String code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate, Pageable pageable) {
		return null;
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