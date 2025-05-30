package com.corbanmultibancos.business.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.mappers.ProposalMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.ProposalRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProposalService {
	private static final String PROPOSAL_NOT_FOUND = "Proposta não encontrada";
	private static final String RESOURCES_NOT_FOUND = "Proposta, funcionário, cliente ou banco não foram encontrados";
	private static final String DATE_PARAMETERS = "As datas informadas são inválidas";
	private static final String MULTIPLE_PARAMS = "Não é permitida a busca usando código da proposta, nome do funcionário e código banco juntos";

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
		Optional<Proposal> result = proposalRepository.findById(id);
		Proposal proposal = result.orElseThrow(() -> new ResourceNotFoundException(PROPOSAL_NOT_FOUND));
		return ProposalMapper.toProposalDataDto(proposal);
	}

	@Transactional(readOnly = true)
	public Page<ProposalDataDTO> getProposals(String code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate, Pageable pageable) {
		validateParameters(code, employeeName, bankCode, dateFieldName, beginDate, endDate);
		Page<ProposalDataDTO> page;
		if(!code.isBlank()) {
			page = getProposalByCode(code);
		}
		else {
			Map<String, Object> filter = new HashMap<>();
			filter.put("dateField", dateFieldName);
			filter.put("beginDate", beginDate);
			filter.put("endDate", endDate);
			if(!employeeName.isBlank()) {
				filter.put("employeeName", employeeName);
			}
			if(bankCode != null && bankCode != 0) {
				filter.put("bankCode", bankCode);
			}
			page = proposalRepository.findBy(filter, pageable);
		}
		return page;
	}

	private Page<ProposalDataDTO> getProposalByCode(String code) {
		Optional<Proposal> result = proposalRepository.findByCode(code);
		if(result.isPresent()) {
			ProposalDataDTO proposalDto = ProposalMapper.toProposalDataDto(result.get());
			return new PageImpl<>(List.of(proposalDto));
		}
		return Page.empty();
	}

	@Transactional
	public ProposalDataDTO createProposal(ProposalCreateDTO proposalDto) {
		try {
			Proposal proposal = new Proposal();
			ProposalMapper.copyProposalCreateDtoToEntity(proposalDto, proposal);
			setEmployeeAndCustomerAndBank(proposalDto, proposal);
			proposal = proposalRepository.save(proposal);
			return ProposalMapper.toProposalDataDto(proposal);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(RESOURCES_NOT_FOUND);
		}
	}

	@Transactional
	public ProposalDataDTO updateProposal(Long id, ProposalCreateDTO proposalDto) {
		try {
			Proposal proposal = proposalRepository.getReferenceById(id);
			ProposalMapper.copyProposalCreateDtoToEntity(proposalDto, proposal);
			setEmployeeAndCustomerAndBank(proposalDto, proposal);
			proposal = proposalRepository.save(proposal);
			return ProposalMapper.toProposalDataDto(proposal);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(RESOURCES_NOT_FOUND);
		}
	}

	private void validateParameters(String code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate) {
		int optionalParams = 0;
		if(!code.isBlank()) {
			optionalParams++;
		}
		if(!employeeName.isBlank()) {
			optionalParams++;
		}
		if(bankCode != null && bankCode != 0) {
			optionalParams++;
		}
		if(optionalParams > 1) {
			throw new IllegalParameterException(MULTIPLE_PARAMS);
		}
		if(!dateFieldName.equals("generation") && !dateFieldName.equals("payment")) {
			throw new IllegalParameterException(DATE_PARAMETERS);
		}
		if(beginDate == null || endDate == null) {
			throw new IllegalParameterException(DATE_PARAMETERS);
		}
		if(beginDate.isAfter(endDate)) {
			throw new IllegalParameterException(DATE_PARAMETERS);
		}
	}

	private void setEmployeeAndCustomerAndBank(ProposalCreateDTO proposalDto, Proposal proposal) {
		if(proposalDto.getEmployeeId() != null) {
			Employee employee = employeeRepository.getReferenceById(proposalDto.getEmployeeId());
			proposal.setEmployee(employee);
		}
		if(proposalDto.getCustomerId() != null) {
			Customer customer = customerRepository.getReferenceById(proposalDto.getCustomerId());
			proposal.setCustomer(customer);
		}
		if(proposalDto.getBankId() != null) {
			Bank bank = bankRepository.getReferenceById(proposalDto.getBankId());
			proposal.setBank(bank);
		}
	}
}