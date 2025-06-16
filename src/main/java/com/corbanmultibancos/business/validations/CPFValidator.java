package com.corbanmultibancos.business.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

	@Override
	public boolean isValid(String cpf, ConstraintValidatorContext context) {
		if(cpf == null || cpf.isBlank()) {
			return true; //null ou em branco validado por @NotBlank
		}
		if(cpf.length() != 11) {
			return false;
		}
		if(cpf.matches("\\D+")) {
			return false;
		}
		String validDigits = calculateDigit(10, cpf) + calculateDigit(11, cpf);
		if(cpf.endsWith(validDigits)) {
			return true;
		}
		return false;
	}

	private String calculateDigit(int nthDigit, String cpf) {
		int digit;
		int weight = 9;
		int sum = 0;
		for(int i = nthDigit - 2; i >= 0; --i) {
			digit = Character.getNumericValue(cpf.charAt(i));
			sum = sum + (digit * weight);
			--weight;
		}
		int remainder = sum % 11;
		if(remainder == 10) {
			return "0";
		}
		else {
			return String.valueOf(remainder);
		}
	}
}
