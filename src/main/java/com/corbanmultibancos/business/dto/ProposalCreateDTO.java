package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

public class ProposalCreateDTO extends ProposalDTO {

	private Long employeeId;
	private Long customerId;
	private Long bankId;

	public ProposalCreateDTO() {
	}

	public ProposalCreateDTO(Long id, String code, Double value, LocalDate generation, LocalDate payment,
			Long employeeId, Long customerId, Long bankId) {
		super(id, code, value, generation, payment);
		this.employeeId = employeeId;
		this.customerId = customerId;
		this.bankId = bankId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
}
