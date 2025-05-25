package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.entities.Role;
import com.corbanmultibancos.business.entities.User;

public class UserMapper {

	public static void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setUsername(dto.getUsername());
		entity.setPassword(dto.getPassword());
		Role role = new Role();
		RoleMapper.copyDtoToEntity(dto.getRole(), role);
		entity.setRole(role);
	}
	
	public static UserDTO toDto(User entity) {
		UserDTO dto = new UserDTO();
		dto.setEmployeeId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setRole(RoleMapper.toDto(entity.getRole()));
		return dto;
	}
}
