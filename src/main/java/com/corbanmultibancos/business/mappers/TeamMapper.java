package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.entities.Team;

public class TeamMapper {

	public static void copyDtoToEntity(TeamDTO dto, Team entity) {
		entity.setName(dto.getName());
	}
	
	public static TeamDTO toDto(Team entity) {
		TeamDTO dto = new TeamDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}
}
