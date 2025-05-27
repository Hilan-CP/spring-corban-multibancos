package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;
import com.corbanmultibancos.business.dto.UserUpdateDTO;
import com.corbanmultibancos.business.entities.User;

public class UserMapper {

	public static void copyUserCreateDtoToEntity(UserCreateDTO dto, User entity) {
		entity.setUsername(dto.getUsername());
		entity.setPassword(dto.getPassword());
	}

	public static void copyUserUpdateDtoToEntity(UserUpdateDTO dto, User entity) {
		entity.setUsername(dto.getUsername());
		entity.setPassword(dto.getPassword());
	}

	public static UserDataDTO toUserDataDto(User entity) {
		UserDataDTO dto = new UserDataDTO();
		dto.setEmployeeId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setRole(RoleMapper.toDto(entity.getRole()));
		return dto;
	}
}
