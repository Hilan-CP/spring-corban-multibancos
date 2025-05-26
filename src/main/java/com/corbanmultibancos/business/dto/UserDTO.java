package com.corbanmultibancos.business.dto;

import com.corbanmultibancos.business.validations.UserDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@UserDTOValid
public class UserDTO {

	@NotNull(message = "Obrigatório informar o ID do funcionário")
	private Long employeeId;
	
	@NotBlank(message = "Obrigatório informar o nome de usuário")
	private String username;
	
	private String password;
	
	@NotNull(message = "Obrigatório informar o tipo de usuário")
	private RoleDTO role;
	
	public UserDTO() {
	}

	public UserDTO(Long employeeId, String username, String password, RoleDTO role) {
		this.employeeId = employeeId;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long id) {
		this.employeeId = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RoleDTO getRole() {
		return role;
	}

	public void setRole(RoleDTO role) {
		this.role = role;
	}
}
