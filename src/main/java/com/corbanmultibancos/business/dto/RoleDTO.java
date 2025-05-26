package com.corbanmultibancos.business.dto;

import jakarta.validation.constraints.NotNull;

public class RoleDTO {

	@NotNull(message = "Obrigatório informar o ID do tipo de usuário")
	private Long id;
	
	private String authority;
	
	public RoleDTO() {
	}

	public RoleDTO(Long id, String authority) {
		this.id = id;
		this.authority = authority;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
}
