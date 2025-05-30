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
	public Page<ProposalDataDTO> getProposals(Integer code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate, Pageable pageable) {
		Map<String, Object> fields = new HashMap<>();
		fields.put("dateField", "generation");
		fields.put("beginDate", LocalDate.of(2025, 1, 1));
		fields.put("endDate", LocalDate.now());
		fields.put("employeeName", "jo");
		return proposalRepository.findByEmployee(fields, pageable);
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
