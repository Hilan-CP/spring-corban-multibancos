package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.mappers.CustomerMapper;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {
	private static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado";
	private static final String MULTIPLE_PARAMS = "Não é permitida a busca usando mais de um parâmetro";

	@Autowired
	private CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public CustomerDTO getCustomerById(Long id) {
		Optional<Customer> result = customerRepository.findById(id);
		Customer customer = result.orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND));
		return CustomerMapper.toDto(customer);
	}

	@Transactional(readOnly = true)
	public Page<CustomerDTO> getCustomers(String cpf, String name, String phone, Pageable pageable) {
		validateParameters(cpf, name, phone);
		Page<Customer> customerPage;
		if(!cpf.isBlank()) {
			customerPage = getCustomerByCpf(cpf);
		}
		else if(!name.isBlank()) {
			customerPage = customerRepository.findByNameContainingIgnoreCase(name, pageable);
		}
		else if(!phone.isBlank()) {
			customerPage = customerRepository.findByPhoneContaining(phone, pageable);
		}
		else {
			customerPage = customerRepository.findAll(pageable);
		}
		return customerPage.map(customer -> CustomerMapper.toDto(customer));
	}

	@Transactional
	public CustomerDTO createCustomer(CustomerDTO customerDto) {
		Customer customer = new Customer();
		CustomerMapper.copyDtoToEntity(customerDto, customer);
		customer = customerRepository.save(customer);
		return CustomerMapper.toDto(customer);
	}

	@Transactional
	public CustomerDTO updateCustomer(Long id, CustomerDTO customerDto) {
		try {
			Customer customer = customerRepository.getReferenceById(id);
			CustomerMapper.copyDtoToEntity(customerDto, customer);
			customer = customerRepository.save(customer);
			return CustomerMapper.toDto(customer);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(CUSTOMER_NOT_FOUND);
		}
	}

	private void validateParameters(String cpf, String name, String phone) {
		int countParams = 0;
		if(!cpf.isBlank()) {
			countParams++;
		}
		if(!name.isBlank()) {
			countParams++;
		}
		if(!phone.isBlank()) {
			countParams++;
		}
		if(countParams > 1) {
			throw new IllegalParameterException(MULTIPLE_PARAMS);
		}
	}

	private Page<Customer> getCustomerByCpf(String cpf) {
		Optional<Customer> result = customerRepository.findByCpf(cpf);
		if(result.isPresent()) {
			return new PageImpl<>(List.of(result.get()));
		}
		return Page.empty();
	}
}
