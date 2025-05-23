package com.corbanmultibancos.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.EmployeeCreationDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.repositories.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Transactional(readOnly = true)
	public EmployeeUserDTO getEmployeeById(Long id) {
		return null;
	}

	@Transactional(readOnly = true)
	public Page<EmployeeUserDTO> getEmployees(String cpf, String name, Pageable pageable) {
		return null;
	}

	@Transactional
	public EmployeeCreationDTO createEmployee(EmployeeCreationDTO employeeDto) {
		return null;
	}

	@Transactional
	public EmployeeCreationDTO updateEmployee(Long id, EmployeeCreationDTO employeeDto) {
		return null;
	}
}
