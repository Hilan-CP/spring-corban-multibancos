package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.repositories.BankRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BankDTOValidator implements ConstraintValidator<BankDTOValid, BankDTO> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private BankRepository repository;

	@Override
	public boolean isValid(BankDTO bankDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isCodeUnavailable(bankDto)) {
			errors.put("code", "O código informado já está em uso por outro banco");
		}
		context.disableDefaultConstraintViolation();
		errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation());
		return errors.isEmpty();
	}

	private boolean isCodeUnavailable(BankDTO bankDto) {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Long bankId = Long.parseLong(uriVariables.getOrDefault("id", "0"));
		Optional<Bank> bank = repository.findByCode(bankDto.getCode());
		return bank.isPresent() && bank.get().getId() != bankId;
	}
}
