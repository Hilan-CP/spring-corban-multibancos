package com.corbanmultibancos.business.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsById(Long id);

	@EntityGraph(attributePaths = "role")
	Optional<User> findByUsername(String username);

	@EntityGraph(attributePaths = "role")
	Page<User> findAll(Pageable pageable);
}
