package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByTeamId(Long teamId);
	
	boolean existsById(Long id);

	@EntityGraph(attributePaths = {"team","user","user.role"})
	Optional<Employee> findByCpf(String cpf);

	@EntityGraph(attributePaths = {"team","user","user.role"})
	Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

	@EntityGraph(attributePaths = {"team","user","user.role"})
	Page<Employee> findAll(Pageable pageable);
}
