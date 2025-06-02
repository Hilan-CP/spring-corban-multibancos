package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

import com.corbanmultibancos.business.entities.ProposalStatus;

public class ProposalDataDTO extends ProposalDTO {

	private ProposalStatus status;
	private String employeeName;
	private String bankName;
	private String customerCpf;
	private String customerName;

	public ProposalDataDTO() {
	}

	public ProposalDataDTO(Long id, String code, Double value, LocalDate generation, LocalDate payment,
			ProposalStatus status, String employeeName, String bankName, String customerCpf, String customerName) {
		super(id, code, value, generation, payment);
		this.status = status;
		this.employeeName = employeeName;
		this.bankName = bankName;
		this.customerCpf = customerCpf;
		this.customerName = customerName;
	}

	public ProposalStatus getStatus() {
		return status;
	}

	public void setStatus(ProposalStatus status) {
		this.status = status;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCustomerCpf() {
		return customerCpf;
	}

	public void setCustomerCpf(String customerCpf) {
		this.customerCpf = customerCpf;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
