package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.corbanmultibancos.business.dto.EmployeeCreationDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.repositories.EmployeeRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmployeeCreationDTOValidator implements ConstraintValidator<EmployeeCreationDTOValid, EmployeeCreationDTO> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private EmployeeRepository repository;

	@Override
	public boolean isValid(EmployeeCreationDTO employeeDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if(isCpfUnavailable(employeeDto)) {
			errors.put("cpf", "O CPF informado já está sendo usado por outro funcionário");
		}
		context.disableDefaultConstraintViolation();
		errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation());
		return errors.isEmpty();
	}
	
	private boolean isCpfUnavailable(EmployeeCreationDTO employeeDto) {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Long employeeId = Long.parseLong(uriVariables.getOrDefault("id", "0"));
		Optional<Employee> employee = repository.findByCpf(employeeDto.getCpf());
		return employee.isPresent() && employee.get().getId() != employeeId;
	}
}
