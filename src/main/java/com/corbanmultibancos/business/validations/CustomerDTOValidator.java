package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.repositories.CustomerRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerDTOValidator implements ConstraintValidator<CustomerDTOValid, CustomerDTO> {

	@Autowired
	private ValidatorUtil validatorUtil;

	@Autowired
	private CustomerRepository repository;

	@Override
	public boolean isValid(CustomerDTO customerDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isCpfUnavailable(customerDto)) {
			errors.put("cpf", "O CPF informado já está sendo usado por outro cliente");
		}
		validatorUtil.buildConstraintViolations(errors, context);
		return errors.isEmpty();
	}

	private boolean isCpfUnavailable(CustomerDTO customerDto) {
		Long customerId = validatorUtil.getIdPathVariable();
		Optional<Customer> customer = repository.findByCpf(customerDto.getCpf());
		return customer.isPresent() && customer.get().getId() != customerId;
	}
}
