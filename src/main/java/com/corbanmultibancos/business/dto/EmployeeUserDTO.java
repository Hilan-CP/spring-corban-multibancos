package com.corbanmultibancos.business.dto;

public class EmployeeUserDTO {

	private Long id;
	private String cpf;
	private String name;
	private String username;
	private String roleName;
	private TeamDTO teamDto;

	public EmployeeUserDTO() {
	}

	public EmployeeUserDTO(Long id, String cpf, String name, String username, String roleName, TeamDTO teamDto) {
		this.id = id;
		this.cpf = cpf;
		this.name = name;
		this.username = username;
		this.roleName = roleName;
		this.teamDto = teamDto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public TeamDTO getTeamDto() {
		return teamDto;
	}

	public void setTeamDto(TeamDTO teamDto) {
		this.teamDto = teamDto;
	}
}
