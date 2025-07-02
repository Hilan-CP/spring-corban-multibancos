package com.corbanmultibancos.business.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportItemDTO {

	private String team;
	private String employee;
	private Long count;
	private BigDecimal sumGeneratedDay;
	private BigDecimal sumPaidDay;
	private BigDecimal sumPaidMonth;

	public ReportItemDTO() {
	}

	public ReportItemDTO(String team, String employee, Long count, BigDecimal sumGeneratedDay, BigDecimal sumPaidDay,
			BigDecimal sumPaidMonth) {
		this.team = team;
		this.employee = employee;
		this.count = count;
		this.sumGeneratedDay = sumGeneratedDay;
		this.sumPaidDay = sumPaidDay;
		this.sumPaidMonth = sumPaidMonth;
	}
	
	public ReportItemDTO(String team, String employee, Long count, Double sumGeneratedDay, Double sumPaidDay,
			Double sumPaidMonth) {
		this.team = team;
		this.employee = employee;
		this.count = count;
		this.sumGeneratedDay = BigDecimal.valueOf(sumGeneratedDay).setScale(2, RoundingMode.HALF_EVEN);
		this.sumPaidDay = BigDecimal.valueOf(sumPaidDay).setScale(2, RoundingMode.HALF_EVEN);
		this.sumPaidMonth = BigDecimal.valueOf(sumPaidMonth).setScale(2, RoundingMode.HALF_EVEN);
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public BigDecimal getSumGeneratedDay() {
		return sumGeneratedDay;
	}

	public void setSumGeneratedDay(BigDecimal sumGeneratedDay) {
		this.sumGeneratedDay = sumGeneratedDay;
	}

	public BigDecimal getSumPaidDay() {
		return sumPaidDay;
	}

	public void setSumPaidDay(BigDecimal sumPaidDay) {
		this.sumPaidDay = sumPaidDay;
	}

	public BigDecimal getSumPaidMonth() {
		return sumPaidMonth;
	}

	public void setSumPaidMonth(BigDecimal sumPaidMonth) {
		this.sumPaidMonth = sumPaidMonth;
	}
}
