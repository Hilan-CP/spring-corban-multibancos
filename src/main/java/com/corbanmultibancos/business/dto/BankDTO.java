package com.corbanmultibancos.business.dto;

import com.corbanmultibancos.business.validations.BankDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@BankDTOValid
public class BankDTO {

	private Long id;
	
	@NotNull(message = "Obrigatório informar o código do banco")
	private Integer code;
	
	@NotBlank(message = "Obrigatório informar nome do banco")
	private String name;

	public BankDTO() {
	}

	public BankDTO(Long id, Integer code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
