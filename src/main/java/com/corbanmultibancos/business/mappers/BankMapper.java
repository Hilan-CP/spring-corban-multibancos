package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;

public class BankMapper {

	public static void copyDtoToEntity(BankDTO dto, Bank entity) {
		entity.setCode(dto.getCode());
		entity.setName(dto.getName());
	}
	
	public static BankDTO toDto(Bank entity) {
		BankDTO dto = new BankDTO();
		dto.setId(entity.getId());
		dto.setCode(entity.getCode());
		dto.setName(entity.getName());
		return dto;
	}
}
