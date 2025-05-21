package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByCpf(String cpf);

	Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Customer> findByPhoneContaining(String phone, Pageable pageable);
}
