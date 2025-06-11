package com.corbanmultibancos.business.dto;

import java.util.ArrayList;
import java.util.List;

public class ReportByTeamDTO {

	private String team;
	private List<ReportByEmployeeDTO> resultByEmployee = new ArrayList<>();
	private Long subtotalCount;
	private Double subtotalSumGeneratedDay;
	private Double subtotalSumPaidDay;
	private Double subtotalSumPaidMonth;
	private Double subtotalMonthTrend;

	public ReportByTeamDTO() {
	}

	public ReportByTeamDTO(String team, Long subtotalCount, Double subtotalSumGeneratedDay, Double subtotalSumPaidDay,
			Double subtotalSumPaidMonth, Double subtotalMonthTrend) {
		this.team = team;
		this.subtotalCount = subtotalCount;
		this.subtotalSumGeneratedDay = subtotalSumGeneratedDay;
		this.subtotalSumPaidDay = subtotalSumPaidDay;
		this.subtotalSumPaidMonth = subtotalSumPaidMonth;
		this.subtotalMonthTrend = subtotalMonthTrend;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Long getSubtotalCount() {
		return subtotalCount;
	}

	public void setSubtotalCount(Long subtotalCount) {
		this.subtotalCount = subtotalCount;
	}

	public Double getSubtotalSumGeneratedDay() {
		return subtotalSumGeneratedDay;
	}

	public void setSubtotalSumGeneratedDay(Double subtotalSumGeneratedDay) {
		this.subtotalSumGeneratedDay = subtotalSumGeneratedDay;
	}

	public Double getSubtotalSumPaidDay() {
		return subtotalSumPaidDay;
	}

	public void setSubtotalSumPaidDay(Double subtotalSumPaidDay) {
		this.subtotalSumPaidDay = subtotalSumPaidDay;
	}

	public Double getSubtotalSumPaidMonth() {
		return subtotalSumPaidMonth;
	}

	public void setSubtotalSumPaidMonth(Double subtotalSumPaidMonth) {
		this.subtotalSumPaidMonth = subtotalSumPaidMonth;
	}

	public Double getSubtotalMonthTrend() {
		return subtotalMonthTrend;
	}

	public void setSubtotalMonthTrend(Double subtotalMonthTrend) {
		this.subtotalMonthTrend = subtotalMonthTrend;
	}

	public List<ReportByEmployeeDTO> getResultByEmployee() {
		return resultByEmployee;
	}
}
