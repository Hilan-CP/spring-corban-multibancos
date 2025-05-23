package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByTeamId(Long teamId);

	Optional<Employee> findByCpf(String cpf);

	Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
