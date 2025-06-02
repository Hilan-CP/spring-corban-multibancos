package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

import com.corbanmultibancos.business.validations.ProposalDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ProposalDTOValid
public abstract class ProposalDTO {

	private Long id;

	@NotBlank(message = "Obrigatório informar o código da proposta")
	private String code;

	@NotNull(message = "Obrigatório informar o valor da proposta")
	@Positive(message = "O valor da proposta precisa ser maior do que zero")
	private Double value;

	@NotNull(message = "Obrigatório informar a data de geração da proposta")
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
