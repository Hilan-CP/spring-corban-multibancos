package com.corbanmultibancos.business.dto;

import com.corbanmultibancos.business.validations.UserDTOValid;

@UserDTOValid
public class UserUpdateDTO extends UserDTO {

	private String password;
	private Long roleId;
	
	public UserUpdateDTO() {
	}

	public UserUpdateDTO(String username, String password, Long roleId) {
		super(username);
		this.password = password;
		this.roleId = roleId;
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
