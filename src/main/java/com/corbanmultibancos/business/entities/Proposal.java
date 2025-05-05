package com.corbanmultibancos.business.entities;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_proposal")
public class Proposal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Double rawValue;
	private LocalDate generation;
	private LocalDate payment;
	
	@Enumerated(EnumType.STRING)
	private ProposalStatus status;

	public Proposal() {
	}

	public Proposal(Long id, Double rawValue, LocalDate generation, LocalDate payment, ProposalStatus status) {
		this.id = id;
		this.rawValue = rawValue;
		this.generation = generation;
		this.payment = payment;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getRawValue() {
		return rawValue;
	}

	public void setRawValue(Double value) {
		this.rawValue = value;
	}

	public LocalDate getGeneration() {
		return generation;
	}

	public void setGeneration(LocalDate generation) {
		this.generation = generation;
	}

	public LocalDate getPayment() {
		return payment;
	}

	public void setPayment(LocalDate payment) {
		this.payment = payment;
	}

	public ProposalStatus getStatus() {
		return status;
	}

	public void setStatus(ProposalStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Proposal other = (Proposal) obj;
		return Objects.equals(id, other.id);
	}
}
