package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.repositories.BankRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BankDTOValidator implements ConstraintValidator<BankDTOValid, BankDTO> {

	@Autowired
	private ValidatorUtil validatorUtil;

	@Autowired
	private BankRepository repository;

	@Override
	public boolean isValid(BankDTO bankDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isCodeUnavailable(bankDto)) {
			errors.put("code", "O código informado já está em uso por outro banco");
		}
		validatorUtil.buildConstraintViolations(errors, context);
		return errors.isEmpty();
	}

	private boolean isCodeUnavailable(BankDTO bankDto) {
		Long bankId = validatorUtil.getIdPathVariable();
		Optional<Bank> bank = repository.findByCode(bankDto.getCode());
		return bank.isPresent() && bank.get().getId() != bankId;
	}
}
