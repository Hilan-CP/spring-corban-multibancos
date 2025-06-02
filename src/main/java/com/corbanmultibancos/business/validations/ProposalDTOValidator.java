package com.corbanmultibancos.business.validations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.corbanmultibancos.business.dto.ProposalDTO;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.repositories.ProposalRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProposalDTOValidator implements ConstraintValidator<ProposalDTOValid, ProposalDTO> {
	
	@Autowired
	private ValidatorUtil validatorUtil;
	
	@Autowired
	private ProposalRepository repository;

	@Override
	public boolean isValid(ProposalDTO proposalDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isCodeUnavailable(proposalDto)) {
			errors.put("code", "O código de proposta informado já está cadastrado em outra proposta");
		}
		if(isGenerationAfterPayment(proposalDto)) {
			errors.put("generation", "A data de geração não pode ser posterior à data de pagamento");
		}
		validatorUtil.buildConstraintViolations(errors, context);
		return errors.isEmpty();
	}

	private boolean isCodeUnavailable(ProposalDTO proposalDto) {
		Long proposalId = validatorUtil.getIdPathVariable();
		Optional<Proposal> result = repository.findByCode(proposalDto.getCode());
		return result.isPresent() && result.get().getId() != proposalId;
	}

	private boolean isGenerationAfterPayment(ProposalDTO proposalDto) {
		LocalDate generation = proposalDto.getGeneration();
		LocalDate payment = proposalDto.getPayment();
		if(generation == null || payment == null) {
			return false;
		}
		return generation.isAfter(payment);
	}
}
