package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.repositories.CustomerRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerDTOValidator implements ConstraintValidator<CustomerDTOValid, CustomerDTO> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private CustomerRepository repository;

	@Override
	public boolean isValid(CustomerDTO customerDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if(isCpfUnavailable(customerDto)) {
			errors.put("cpf", "O CPF informado já está sendo usado por outro cliente");
		}
		context.disableDefaultConstraintViolation();
		errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation());
		return errors.isEmpty();
	}

	private boolean isCpfUnavailable(CustomerDTO customerDto) {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Long customerId = Long.parseLong(uriVariables.getOrDefault("id", "0"));
		Optional<Customer> customer = repository.findByCpf(customerDto.getCpf());
		return customer.isPresent() && customer.get().getId() != customerId;
	}
}
