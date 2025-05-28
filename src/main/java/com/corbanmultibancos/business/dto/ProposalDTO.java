package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

public abstract class ProposalDTO {

	private Long id;
	private String code;
	private Double value;
	private LocalDate generation;
	private LocalDate payment;

	public ProposalDTO() {
	}

	public ProposalDTO(Long id, String code, Double value, LocalDate generation, LocalDate payment) {
		this.id = id;
		this.code = code;
		this.value = value;
		this.generation = generation;
		this.payment = payment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public LocalDate getGeneration() {
		return generation;
	}

	public void setGeneration(LocalDate generation) {
		this.generation = generation;
	}

	public LocalDate getPayment() {
		return payment;
	}

	public void setPayment(LocalDate payment) {
		this.payment = payment;
	}
}
