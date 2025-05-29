package com.corbanmultibancos.business.mappers;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.entities.ProposalStatus;

public class ProposalMapper {

	public static void copyProposalCreateDtoToEntity(ProposalCreateDTO dto, Proposal entity) {
		entity.setCode(dto.getCode());
		entity.setRawValue(dto.getValue());
		entity.setGeneration(dto.getGeneration());
		entity.setPayment(dto.getPayment());
		entity.setStatus(ProposalStatus.GERADA);
	}

	public static ProposalDataDTO toProposalDataDto(Proposal entity) {
		ProposalDataDTO dto = new ProposalDataDTO();
		dto.setId(entity.getId());
		dto.setCode(entity.getCode());
		dto.setValue(entity.getRawValue());
		dto.setGeneration(entity.getGeneration());
		dto.setPayment(entity.getPayment());
		dto.setStatus(entity.getStatus());
		dto.setEmployeeName(entity.getEmployee().getName());
		dto.setBankName(entity.getBank().getName());
		dto.setCustomerCpf(entity.getCustomer().getCpf());
		dto.setCustomerName(entity.getCustomer().getName());
		return dto;
	}
}
