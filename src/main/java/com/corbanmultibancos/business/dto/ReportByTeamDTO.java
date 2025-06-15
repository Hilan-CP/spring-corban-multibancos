package com.corbanmultibancos.business.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReportByTeamDTO {

	private String team;
	private List<ReportByEmployeeDTO> resultByEmployee = new ArrayList<>();
	private Long subtotalCount;
	private BigDecimal subtotalSumGeneratedDay;
	private BigDecimal subtotalSumPaidDay;
	private BigDecimal subtotalSumPaidMonth;
	private BigDecimal subtotalMonthTrend;

	public ReportByTeamDTO() {
	}

	public ReportByTeamDTO(String team, List<ReportByEmployeeDTO> resultByEmployee, Long subtotalCount,
			BigDecimal subtotalSumGeneratedDay, BigDecimal subtotalSumPaidDay, BigDecimal subtotalSumPaidMonth,
			BigDecimal subtotalMonthTrend) {
		this.team = team;
		this.resultByEmployee = resultByEmployee;
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

	public List<ReportByEmployeeDTO> getResultByEmployee() {
		return resultByEmployee;
	}

	public void setResultByEmployee(List<ReportByEmployeeDTO> resultByEmployee) {
		this.resultByEmployee = resultByEmployee;
	}

	public Long getSubtotalCount() {
		return subtotalCount;
	}

	public void setSubtotalCount(Long subtotalCount) {
		this.subtotalCount = subtotalCount;
	}

	public BigDecimal getSubtotalSumGeneratedDay() {
		return subtotalSumGeneratedDay;
	}

	public void setSubtotalSumGeneratedDay(BigDecimal subtotalSumGeneratedDay) {
		this.subtotalSumGeneratedDay = subtotalSumGeneratedDay;
	}

	public BigDecimal getSubtotalSumPaidDay() {
		return subtotalSumPaidDay;
	}

	public void setSubtotalSumPaidDay(BigDecimal subtotalSumPaidDay) {
		this.subtotalSumPaidDay = subtotalSumPaidDay;
	}

	public BigDecimal getSubtotalSumPaidMonth() {
		return subtotalSumPaidMonth;
	}

	public void setSubtotalSumPaidMonth(BigDecimal subtotalSumPaidMonth) {
		this.subtotalSumPaidMonth = subtotalSumPaidMonth;
	}

	public BigDecimal getSubtotalMonthTrend() {
		return subtotalMonthTrend;
	}

	public void setSubtotalMonthTrend(BigDecimal subtotalMonthTrend) {
		this.subtotalMonthTrend = subtotalMonthTrend;
	}
}
