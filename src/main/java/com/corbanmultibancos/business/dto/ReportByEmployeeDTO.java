package com.corbanmultibancos.business.dto;

import java.math.BigDecimal;

public class ReportByEmployeeDTO {

	private String employee;
	private Long count;
	private BigDecimal sumGeneratedDay;
	private BigDecimal sumPaidDay;
	private BigDecimal sumPaidMonth;
	private BigDecimal monthTrend;

	public ReportByEmployeeDTO() {
	}

	public ReportByEmployeeDTO(String employee, Long count, BigDecimal sumGeneratedDay, BigDecimal sumPaidDay,
			BigDecimal sumPaidMonth, BigDecimal monthTrend) {
		this.employee = employee;
		this.count = count;
		this.sumGeneratedDay = sumGeneratedDay;
		this.sumPaidDay = sumPaidDay;
		this.sumPaidMonth = sumPaidMonth;
		this.monthTrend = monthTrend;
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

	public BigDecimal getMonthTrend() {
		return monthTrend;
	}

	public void setMonthTrend(BigDecimal monthTrend) {
		this.monthTrend = monthTrend;
	}
}
