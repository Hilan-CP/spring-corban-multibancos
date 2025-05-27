package com.corbanmultibancos.business.dto;

public class UserDataDTO extends UserDTO {

	private Long employeeId;
	private RoleDTO role;

	public UserDataDTO() {
	}

	public UserDataDTO(String username, Long employeeId, RoleDTO role) {
		super(username);
		this.employeeId = employeeId;
		this.role = role;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public RoleDTO getRole() {
		return role;
	}

	public void setRole(RoleDTO role) {
		this.role = role;
	}
}
