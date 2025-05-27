package com.corbanmultibancos.business.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.services.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeUserDTO> getEmployeeById(@PathVariable Long id) {
		EmployeeUserDTO employeeDto = employeeService.getEmployeeById(id);
		return ResponseEntity.ok(employeeDto);
	}

	@GetMapping
	public ResponseEntity<Page<EmployeeUserDTO>> getEmployees(@RequestParam(defaultValue = "") String cpf,
														@RequestParam(defaultValue = "") String name,
														Pageable pageable) {
		Page<EmployeeUserDTO> page = employeeService.getEmployees(cpf, name, pageable);
		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<EmployeeCreateDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO employeeDto) {
		employeeDto = employeeService.createEmployee(employeeDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(employeeDto.getId());
		return ResponseEntity.created(uri).body(employeeDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmployeeCreateDTO> updateEmployee(@PathVariable Long id,
														@Valid @RequestBody EmployeeCreateDTO employeeDto) {
		employeeDto = employeeService.updateEmployee(id, employeeDto);
		return ResponseEntity.ok(employeeDto);
	}
}
