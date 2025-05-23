package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.EmployeeCreationDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.mappers.EmployeeMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EmployeeService {
	private static final String EMPLOYEE_NOT_FOUND = "Funcionário não encontrado";
	private static final String MULTIPLE_PARAMS = "Não é permitida a busca usando mais de um parâmetro";

	@Autowired
	private EmployeeRepository employeeRepository;

	@Transactional(readOnly = true)
	public EmployeeUserDTO getEmployeeById(Long id) {
		Optional<Employee> result = employeeRepository.findById(id);
		Employee employee = result.orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND));
		return EmployeeMapper.toEmployeeUserDTO(employee);
	}

	@Transactional(readOnly = true)
	public Page<EmployeeUserDTO> getEmployees(String cpf, String name, Pageable pageable) {
		validateParameters(cpf, name);
		Page<Employee> page;
		if(!cpf.isBlank()) {
			page = getEmployeeByCpf(cpf);
		}
		else if(!name.isBlank()) {
			page = employeeRepository.findByNameContainingIgnoreCase(name, pageable);
		}
		else {
			page = employeeRepository.findAll(pageable);
		}
		return page.map(employee -> EmployeeMapper.toEmployeeUserDTO(employee));
	}

	@Transactional
	public EmployeeCreationDTO createEmployee(EmployeeCreationDTO employeeDto) {
		Employee employee = new Employee();
		EmployeeMapper.copyEmployeeCreationDtoToEntity(employeeDto, employee);
		employee = employeeRepository.save(employee);
		return EmployeeMapper.toEmployeeCreationDto(employee);
	}

	@Transactional
	public EmployeeCreationDTO updateEmployee(Long id, EmployeeCreationDTO employeeDto) {
		try {
			Employee employee = employeeRepository.getReferenceById(id);
			EmployeeMapper.copyEmployeeCreationDtoToEntity(employeeDto, employee);
			employee = employeeRepository.save(employee);
			return EmployeeMapper.toEmployeeCreationDto(employee);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(MULTIPLE_PARAMS);
		}
	}

	private void validateParameters(String cpf, String name) {
		if(!cpf.isBlank() && !name.isBlank()) {
			throw new IllegalParameterException(MULTIPLE_PARAMS);
		}
	}

	private Page<Employee> getEmployeeByCpf(String cpf) {
		Optional<Employee> result = employeeRepository.findByCpf(cpf);
		if(result.isPresent()) {
			return new PageImpl<>(List.of(result.get()));
		}
		return Page.empty();
	}
}
