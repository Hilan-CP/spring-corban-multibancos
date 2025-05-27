package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Team;

public class EmployeeMapper {

	public static void copyEmployeeCreationDtoToEntity(EmployeeCreateDTO dto, Employee entity) {
		entity.setCpf(dto.getCpf());
		entity.setName(dto.getName());
		if(dto.getTeamId() != null) {
			entity.setTeam(new Team(dto.getTeamId(), null));
		}
	}

	public static EmployeeCreateDTO toEmployeeCreationDto(Employee entity) {
		EmployeeCreateDTO dto = new EmployeeCreateDTO();
		dto.setId(entity.getId());
		dto.setCpf(entity.getCpf());
		dto.setName(entity.getName());
		if(entity.getTeam() != null) {
			dto.setTeamId(entity.getTeam().getId());
		}
		return dto;
	}

	public static EmployeeUserDTO toEmployeeUserDTO(Employee entity) {
		EmployeeUserDTO dto = new EmployeeUserDTO();
		dto.setId(entity.getId());
		dto.setCpf(entity.getCpf());
		dto.setName(entity.getName());
		if(entity.getUser() != null) {
			dto.setUsername(entity.getUser().getUsername());
			dto.setRoleName(entity.getUser().getRole().getAuthority());
		}
		if(entity.getTeam() != null) {
			dto.setTeam(TeamMapper.toDto(entity.getTeam()));
		}
		return dto;
	}
}
