package com.corbanmultibancos.business.dto;

import jakarta.validation.constraints.NotBlank;

public abstract class UserDTO {

	@NotBlank(message = "Obrigatório informar o nome de usuário")
	private String username;
	
	public UserDTO() {
	}

	public UserDTO(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
