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

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.services.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/{id}")
	public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
		CustomerDTO customerDto = customerService.getCustomerById(id);
		return ResponseEntity.ok(customerDto);
	}

	@GetMapping
	public ResponseEntity<Page<CustomerDTO>> getCustomers(@RequestParam(defaultValue = "") String cpf,
											@RequestParam(defaultValue = "") String name,
											@RequestParam(defaultValue = "") String phone,
											Pageable pageable) {
		Page<CustomerDTO> page = customerService.getCustomers(cpf, name, phone, pageable);
		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDto) {
		customerDto = customerService.createCustomer(customerDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(customerDto.getId());
		return ResponseEntity.created(uri).body(customerDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDto) {
		customerDto = customerService.updateCustomer(id, customerDto);
		return ResponseEntity.ok(customerDto);
	}
}
