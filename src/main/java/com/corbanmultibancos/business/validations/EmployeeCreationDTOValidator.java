package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.repositories.EmployeeRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmployeeCreationDTOValidator implements ConstraintValidator<EmployeeCreationDTOValid, EmployeeCreateDTO> {

	@Autowired
	private ValidatorUtil validatorUtil;

	@Autowired
	private EmployeeRepository repository;

	@Override
	public boolean isValid(EmployeeCreateDTO employeeDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isCpfUnavailable(employeeDto)) {
			errors.put("cpf", "O CPF informado já está sendo usado por outro funcionário");
		}
		validatorUtil.buildConstraintViolations(errors, context);
		return errors.isEmpty();
	}

	private boolean isCpfUnavailable(EmployeeCreateDTO employeeDto) {
		Long employeeId = validatorUtil.getIdPathVariable();
		Optional<Employee> employee = repository.findByCpf(employeeDto.getCpf());
		return employee.isPresent() && employee.get().getId() != employeeId;
	}
}
