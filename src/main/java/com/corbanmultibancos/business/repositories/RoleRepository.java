package com.corbanmultibancos.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.corbanmultibancos.business.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
