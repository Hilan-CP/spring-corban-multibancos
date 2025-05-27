package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsById(Long id);
	
	Optional<User> findByUsername(String username);
}
