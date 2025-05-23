package com.corbanmultibancos.business.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.services.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/{id}")
	public ResponseEntity<EmployeeUserDTO> getEmployeeById(){
		return null;
	}
	
	@GetMapping
	public ResponseEntity<EmployeeUserDTO> getEmployees(){
		return null;
	}
	
	@PostMapping
	public ResponseEntity<EmployeeUserDTO> createEmployee(){
		return null;
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<EmployeeUserDTO> updateEmployee(){
		return null;
	}
}
