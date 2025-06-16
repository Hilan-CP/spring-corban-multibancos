package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.mappers.EmployeeMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;
import com.corbanmultibancos.business.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeCsvExporterService exporterService;

	@Transactional(readOnly = true)
	public EmployeeUserDTO getEmployeeById(Long id) {
		Optional<Employee> result = employeeRepository.findById(id);
		Employee employee = result.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND));
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

	public byte[] getEmployeesAsCsvData(String cpf, String name) {
		Page<EmployeeUserDTO> result = getEmployees(cpf, name, Pageable.unpaged());
		return exporterService.writeEmployeeAsBytes(result.getContent());
	}

	@Transactional
	public EmployeeCreateDTO createEmployee(EmployeeCreateDTO employeeDto) {
		Employee employee = new Employee();
		EmployeeMapper.copyEmployeeCreationDtoToEntity(employeeDto, employee);
		employee = employeeRepository.save(employee);
		return EmployeeMapper.toEmployeeCreationDto(employee);
	}

	@Transactional
	public EmployeeCreateDTO updateEmployee(Long id, EmployeeCreateDTO employeeDto) {
		try {
			Employee employee = employeeRepository.getReferenceById(id);
			EmployeeMapper.copyEmployeeCreationDtoToEntity(employeeDto, employee);
			employee = employeeRepository.save(employee);
			return EmployeeMapper.toEmployeeCreationDto(employee);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND);
		}
	}

	private void validateParameters(String cpf, String name) {
		if(!cpf.isBlank() && !name.isBlank()) {
			throw new IllegalParameterException(ErrorMessage.MULTIPLE_PARAMS);
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
