package com.corbanmultibancos.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.corbanmultibancos.business.entities.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {

	Optional<Bank> findByCode(Integer code);
	
	@Query("SELECT b FROM Bank b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name,'%'))")
	List<Bank> findByName(String name);
}
