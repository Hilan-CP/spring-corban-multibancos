package com.corbanmultibancos.business.entities;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_proposal")
public class Proposal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String code;

	private Double rawValue;
	private LocalDate generation;
	private LocalDate payment;

	@Enumerated(EnumType.STRING)
	private ProposalStatus status;

	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "bank_id", nullable = false)
	private Bank bank;

	public Proposal() {
	}

	public Proposal(Long id, String code, Double rawValue, LocalDate generation, LocalDate payment,
			ProposalStatus status, Employee employee, Customer customer, Bank bank) {
		this.id = id;
		this.code = code;
		this.rawValue = rawValue;
		this.generation = generation;
		this.payment = payment;
		this.status = status;
		this.employee = employee;
		this.customer = customer;
		this.bank = bank;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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
