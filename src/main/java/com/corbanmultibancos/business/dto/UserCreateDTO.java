package com.corbanmultibancos.business.dto;

import com.corbanmultibancos.business.validations.CreateGroup;
import com.corbanmultibancos.business.validations.UserDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@UserDTOValid
public class UserCreateDTO extends UserDTO {

	@NotNull(message = "Obrigatório informar o ID do funcionário", groups = CreateGroup.class)
	private Long employeeId;

	@NotBlank(message = "Obrigatório informar a senha", groups = CreateGroup.class)
	private String password;

	@NotNull(message = "Obrigatório informar o tipo de usuário", groups = CreateGroup.class)
	private Long roleId;

	public UserCreateDTO() {
	}

	public UserCreateDTO(Long employeeId, String username, String password, Long roleId) {
		super(username);
		this.employeeId = employeeId;
		this.password = password;
		this.roleId = roleId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
