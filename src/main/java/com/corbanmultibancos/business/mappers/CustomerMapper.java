package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.entities.Customer;

public class CustomerMapper {

	public static void copyDtoToEntity(CustomerDTO dto, Customer entity) {
		entity.setCpf(dto.getCpf());
		entity.setName(dto.getName());
		entity.setPhone(dto.getPhone());
		entity.setBirthDate(dto.getBirthDate());
	}
	
	public static CustomerDTO toDto(Customer entity) {
		CustomerDTO dto = new CustomerDTO();
		dto.setId(entity.getId());
		dto.setCpf(entity.getCpf());
		dto.setName(entity.getName());
		dto.setPhone(entity.getPhone());
		dto.setBirthDate(entity.getBirthDate());
		return dto;
	}
}
