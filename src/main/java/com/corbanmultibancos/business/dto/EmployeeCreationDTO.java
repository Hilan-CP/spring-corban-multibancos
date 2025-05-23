package com.corbanmultibancos.business.dto;

public class EmployeeCreationDTO {

	private Long id;
	private String cpf;
	private String name;
	private Long teamId;

	public EmployeeCreationDTO() {
	}

	public EmployeeCreationDTO(Long id, String cpf, String name, Long teamId) {
		this.id = id;
		this.cpf = cpf;
		this.name = name;
		this.teamId = teamId;
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

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
}
