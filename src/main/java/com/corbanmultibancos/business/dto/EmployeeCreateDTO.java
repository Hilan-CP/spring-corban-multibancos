package com.corbanmultibancos.business.dto;

import com.corbanmultibancos.business.validations.EmployeeCreationDTOValid;

import jakarta.validation.constraints.NotBlank;

@EmployeeCreationDTOValid
public class EmployeeCreateDTO {

	private Long id;
	
	@NotBlank(message = "Obrigatório informar o CPF do funcionário")
	private String cpf;
	
	@NotBlank(message = "Obrigatório informar o nome do funcionário")
	private String name;
	
	private Long teamId;

	public EmployeeCreateDTO() {
	}

	public EmployeeCreateDTO(Long id, String cpf, String name, Long teamId) {
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
