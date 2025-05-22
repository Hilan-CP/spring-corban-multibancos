package com.corbanmultibancos.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	List<Team> findByNameContainingIgnoreCase(String name);
}
