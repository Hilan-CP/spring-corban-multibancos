package com.corbanmultibancos.business.dto;

public class UserDTO {

	private Long employeeId;
	private String username;
	private String password;
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
