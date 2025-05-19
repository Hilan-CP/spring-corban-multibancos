package com.corbanmultibancos.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.repositories.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public CustomerDTO getCustomerById(Long id) {
		return null;
	}

	@Transactional(readOnly = true)
	public Page<CustomerDTO> getCustomers(String cpf, String name, String phone, Pageable pageable) {
		return null;
	}

	@Transactional
	public CustomerDTO createCustomer(CustomerDTO customerDto) {
		return null;
	}

	@Transactional
	public CustomerDTO updateCustomer(Long id, CustomerDTO customerDto) {
		return null;
	}
}
