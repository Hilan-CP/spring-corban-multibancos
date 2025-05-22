package com.corbanmultibancos.business.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamDTO {

	private Long id;
	
	@NotBlank(message = "Obrigatório informar nome do time")
	private String name;
	
	public TeamDTO() {
	}

	public TeamDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
