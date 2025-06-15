package com.corbanmultibancos.business.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReportTotalDTO {

	private List<ReportByTeamDTO> resultByTeam = new ArrayList<>();
	private Long totalCount;
	private BigDecimal totalSumGeneratedDay;
	private BigDecimal totalSumPaidDay;
	private BigDecimal totalSumPaidMonth;
	private BigDecimal totalMonthTrend;

	public ReportTotalDTO() {
	}

	public ReportTotalDTO(List<ReportByTeamDTO> resultByTeam, Long totalCount, BigDecimal totalSumGeneratedDay,
			BigDecimal totalSumPaidDay, BigDecimal totalSumPaidMonth, BigDecimal totalMonthTrend) {
		this.resultByTeam = resultByTeam;
		this.totalCount = totalCount;
		this.totalSumGeneratedDay = totalSumGeneratedDay;
		this.totalSumPaidDay = totalSumPaidDay;
		this.totalSumPaidMonth = totalSumPaidMonth;
		this.totalMonthTrend = totalMonthTrend;
	}

	public List<ReportByTeamDTO> getResultByTeam() {
		return resultByTeam;
	}

	public void setResultByTeam(List<ReportByTeamDTO> resultByTeam) {
		this.resultByTeam = resultByTeam;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getTotalSumGeneratedDay() {
		return totalSumGeneratedDay;
	}

	public void setTotalSumGeneratedDay(BigDecimal totalSumGeneratedDay) {
		this.totalSumGeneratedDay = totalSumGeneratedDay;
	}

	public BigDecimal getTotalSumPaidDay() {
		return totalSumPaidDay;
	}

	public void setTotalSumPaidDay(BigDecimal totalSumPaidDay) {
		this.totalSumPaidDay = totalSumPaidDay;
	}

	public BigDecimal getTotalSumPaidMonth() {
		return totalSumPaidMonth;
	}

	public void setTotalSumPaidMonth(BigDecimal totalSumPaidMonth) {
		this.totalSumPaidMonth = totalSumPaidMonth;
	}

	public BigDecimal getTotalMonthTrend() {
		return totalMonthTrend;
	}

	public void setTotalMonthTrend(BigDecimal totalMonthTrend) {
		this.totalMonthTrend = totalMonthTrend;
	}
}
