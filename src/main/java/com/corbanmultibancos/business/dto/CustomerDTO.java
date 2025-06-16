package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

import com.corbanmultibancos.business.validations.CPF;
import com.corbanmultibancos.business.validations.CustomerDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@CustomerDTOValid
public class CustomerDTO {

	private Long id;

	@CPF
	@NotBlank(message = "Obrigatório informar o CPF do cliente")
	private String cpf;

	@NotBlank(message = "Obrigatório informar o nome do cliente")
	private String name;

	@NotBlank(message = "Obrigatório informar o telefone do cliente")
	private String phone;

	@NotNull(message = "Obrigatório informar a data de nascimento do cliente")
	private LocalDate birthDate;

	public CustomerDTO() {
	}

	public CustomerDTO(Long id, String cpf, String name, String phone, LocalDate birthDate) {
		this.id = id;
		this.cpf = cpf;
		this.name = name;
		this.phone = phone;
		this.birthDate = birthDate;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
}
