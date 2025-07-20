package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

public class ProposalFilterDTO {

	private String employeeName;
	private Integer bankCode;
	private LocalDate beginDate;
	private LocalDate endDate;
	private String dateField;
	private Long userId;

	public ProposalFilterDTO() {
	}

	public ProposalFilterDTO(String employeeName, Integer bankCode, LocalDate beginDate, LocalDate endDate,
			String dateField, Long userId) {
		this.employeeName = employeeName;
		this.bankCode = bankCode;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.dateField = dateField;
		this.userId = userId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Integer getBankCode() {
		return bankCode;
	}

	public void setBankCode(Integer bankCode) {
		this.bankCode = bankCode;
	}

	public LocalDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getDateField() {
		return dateField;
	}

	public void setDateField(String dateField) {
		this.dateField = dateField;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
