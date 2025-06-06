package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
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

	public byte[] getEmployeesAsCsvData(String cpf, String name) {
		Page<EmployeeUserDTO> result = getEmployees(cpf, name, Pageable.unpaged());
		List<EmployeeUserDTO> employeeDtoList = result.getContent();
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;CPF;Nome;Usuário;Tipo_Usuário;ID_Equipe;Nome_Equipe");
		for(EmployeeUserDTO employeeDto : employeeDtoList) {
			String teamId = "null";
			String teamName = "null";
			if(employeeDto.getTeam() != null) {
				teamId = employeeDto.getTeam().getId().toString();
				teamName = employeeDto.getTeam().getName();
			}
			writer.println(String.join(";",
					employeeDto.getId().toString(),
					employeeDto.getCpf(),
					employeeDto.getName(),
					employeeDto.getUsername(),
					employeeDto.getRoleName(),
					teamId,
					teamName));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
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
			throw new ResourceNotFoundException(EMPLOYEE_NOT_FOUND);
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
