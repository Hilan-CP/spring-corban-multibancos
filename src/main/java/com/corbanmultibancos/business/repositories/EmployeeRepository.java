package com.corbanmultibancos.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByTeamId(Long teamId);
}
