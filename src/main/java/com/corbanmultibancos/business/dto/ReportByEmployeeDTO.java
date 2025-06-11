package com.corbanmultibancos.business.dto;

public class ReportByEmployeeDTO {

	private String employee;
	private Long count;
	private Double sumGeneratedDay;
	private Double sumPaidDay;
	private Double sumPaidMonth;
	private Double monthTrend;

	public ReportByEmployeeDTO() {
	}

	public ReportByEmployeeDTO(String employee, Long count, Double sumGeneratedDay, Double sumPaidDay,
			Double sumPaidMonth, Double monthTrend) {
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

	public Double getSumGeneratedDay() {
		return sumGeneratedDay;
	}

	public void setSumGeneratedDay(Double sumGeneratedDay) {
		this.sumGeneratedDay = sumGeneratedDay;
	}

	public Double getSumPaidDay() {
		return sumPaidDay;
	}

	public void setSumPaidDay(Double sumPaidDay) {
		this.sumPaidDay = sumPaidDay;
	}

	public Double getSumPaidMonth() {
		return sumPaidMonth;
	}

	public void setSumPaidMonth(Double sumPaidMonth) {
		this.sumPaidMonth = sumPaidMonth;
	}

	public Double getMonthTrend() {
		return monthTrend;
	}

	public void setMonthTrend(Double monthTrend) {
		this.monthTrend = monthTrend;
	}
}
