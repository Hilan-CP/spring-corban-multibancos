package com.corbanmultibancos.business.dto;

import java.time.LocalDate;

public class CustomerDTO {

	private Long id;
	private String cpf;
	private String name;
	private String phone;
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
