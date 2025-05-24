package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.RoleDTO;
import com.corbanmultibancos.business.entities.Role;

public class RoleMapper {

	public static void copyDtoToEntity(RoleDTO dto, Role entity) {
		entity.setId(dto.getId());
		entity.setAuthority(dto.getAuthority());
	}
	
	public static RoleDTO toDto(Role entity) {
		RoleDTO dto = new RoleDTO();
		dto.setId(entity.getId());
		dto.setAuthority(entity.getAuthority());
		return dto;
	}
}
