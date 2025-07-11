package com.corbanmultibancos.business.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.entities.ProposalStatus;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.mappers.ProposalMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.repositories.ProposalRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;
import com.corbanmultibancos.business.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProposalService {

	@Autowired
	private ProposalRepository proposalRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private BankRepository bankRepository;
	
	@Autowired
	private ProposalCsvExporterService exporterService;
	
	@Autowired
	private AuthorizationService authorizationService;

	@Transactional(readOnly = true)
	public ProposalDataDTO getProposalById(Long id) {
		Optional<Proposal> result = proposalRepository.findById(id);
		Proposal proposal = result.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PROPOSAL_NOT_FOUND));
		authorizationService.authorizeAdminOrOwner(proposal.getEmployee().getId());
		return ProposalMapper.toProposalDataDto(proposal);
	}

	@Transactional(readOnly = true)
	public Page<ProposalDataDTO> getProposals(String code, String employeeName, Integer bankCode, String dateFieldName,
			LocalDate beginDate, LocalDate endDate, Pageable pageable) {
		validateParameters(code, employeeName, bankCode, dateFieldName, beginDate, endDate);
		User user = authorizationService.getLoggedUser();
		Page<ProposalDataDTO> page;
		Map<String, Object> filter = new HashMap<>();
		filter.put("dateField", dateFieldName);
		filter.put("beginDate", beginDate);
		filter.put("endDate", endDate);
		if(!authorizationService.isAdmin(user)) {
			filter.put("userId", user.getId());
		}
		if(!code.isBlank()) {
			filter.put("code", code);
		}
		if(!employeeName.isBlank()) {
			filter.put("employeeName", employeeName);
		}
		if(bankCode != null && bankCode != 0) {
			filter.put("bankCode", bankCode);
		}
		page = proposalRepository.findBy(filter, pageable);
		return page;
	}

	public byte[] getProposalsAsCsvData(String code, String employeeName, Integer bankCode, String dateField,
			LocalDate beginDate, LocalDate endDate) {
		Page<ProposalDataDTO> result = getProposals(code, employeeName, bankCode, dateField, beginDate, endDate, Pageable.unpaged());
		return exporterService.writeProposalsAsBytes(result.getContent());
	}

	@Transactional
	public ProposalDataDTO createProposal(ProposalCreateDTO proposalDto) {
		Proposal proposal = new Proposal();
		ProposalMapper.copyProposalCreateDtoToEntity(proposalDto, proposal);
		updateProposalStatus(proposal);
		setEmployeeAndCustomerAndBank(proposalDto, proposal);
		proposal = proposalRepository.save(proposal);
		return ProposalMapper.toProposalDataDto(proposal);
	}

	@Transactional
	public ProposalDataDTO updateProposal(Long id, ProposalCreateDTO proposalDto) {
		try {
			Proposal proposal = proposalRepository.getReferenceById(id);
			authorizationService.authorizeAdminOrOwner(proposal.getEmployee().getId());
			ProposalMapper.copyProposalCreateDtoToEntity(proposalDto, proposal);
			updateProposalStatus(proposal);
			setEmployeeAndCustomerAndBank(proposalDto, proposal);
			proposal = proposalRepository.save(proposal);
			return ProposalMapper.toProposalDataDto(proposal);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.PROPOSAL_NOT_FOUND);
		}
	}

	@Transactional
	public ProposalDataDTO updateCancelProposal(Long id) {
		try {
			Proposal proposal = proposalRepository.getReferenceById(id);
			proposal.setStatus(ProposalStatus.CANCELADA);
			proposal.setPayment(null);
			proposal = proposalRepository.save(proposal);
			return ProposalMapper.toProposalDataDto(proposal);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.PROPOSAL_NOT_FOUND);
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
			throw new IllegalParameterException(ErrorMessage.PROPOSAL_MULTIPLE_PARAMS);
		}
		if(!dateFieldName.equals("generation") && !dateFieldName.equals("payment")) {
			throw new IllegalParameterException(ErrorMessage.DATE_PARAMETERS);
		}
		if(beginDate == null || endDate == null) {
			throw new IllegalParameterException(ErrorMessage.DATE_PARAMETERS);
		}
		if(beginDate.isAfter(endDate)) {
			throw new IllegalParameterException(ErrorMessage.DATE_PARAMETERS);
		}
	}

	private void setEmployeeAndCustomerAndBank(ProposalCreateDTO proposalDto, Proposal proposal) {
		if(proposal.getEmployee() == null) {
			Employee employee = authorizationService.getLoggedUser().getEmployee();
			proposal.setEmployee(employee);
		}
		if(proposalDto.getCustomerId() != null) {
			Customer customer = customerRepository.findById(proposalDto.getCustomerId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND));
			proposal.setCustomer(customer);
		}
		if(proposalDto.getBankId() != null) {
			Bank bank = bankRepository.findById(proposalDto.getBankId())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.BANK_NOT_FOUND));
			proposal.setBank(bank);
		}
	}

	private void updateProposalStatus(Proposal proposal) {
		if(proposal.getPayment() == null && proposal.getStatus() == null) {
			proposal.setStatus(ProposalStatus.GERADA);
		}
		if(proposal.getPayment() != null) {
			proposal.setStatus(ProposalStatus.CONTRATADA);
		}
	}
}