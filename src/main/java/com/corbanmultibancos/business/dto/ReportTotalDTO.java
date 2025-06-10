package com.corbanmultibancos.business.dto;

import java.util.ArrayList;
import java.util.List;

public class ReportTotalDTO {

	private List<ReportByTeamDTO> resultByTeam = new ArrayList<>();
	private Long totalCount;
	private Double totalSumGeneratedDay;
	private Double totalSumPaidDay;
	private Double totalSumPaidMonth;
	private Double totalMonthTrend;

	public ReportTotalDTO() {
	}

	public ReportTotalDTO(Long totalCount, Double totalSumGeneratedDay, Double totalSumPaidDay,
			Double totalSumPaidMonth, Double totalMonthTrend) {
		this.totalCount = totalCount;
		this.totalSumGeneratedDay = totalSumGeneratedDay;
		this.totalSumPaidDay = totalSumPaidDay;
		this.totalSumPaidMonth = totalSumPaidMonth;
		this.totalMonthTrend = totalMonthTrend;
	}

	public List<ReportByTeamDTO> getResultByTeam() {
		return resultByTeam;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Double getTotalSumGeneratedDay() {
		return totalSumGeneratedDay;
	}

	public void setTotalSumGeneratedDay(Double totalSumGeneratedDay) {
		this.totalSumGeneratedDay = totalSumGeneratedDay;
	}

	public Double getTotalSumPaidDay() {
		return totalSumPaidDay;
	}

	public void setTotalSumPaidDay(Double totalSumPaidDay) {
		this.totalSumPaidDay = totalSumPaidDay;
	}

	public Double getTotalSumPaidMonth() {
		return totalSumPaidMonth;
	}

	public void setTotalSumPaidMonth(Double totalSumPaidMonth) {
		this.totalSumPaidMonth = totalSumPaidMonth;
	}

	public Double getTotalMonthTrend() {
		return totalMonthTrend;
	}

	public void setTotalMonthTrend(Double totalMonthTrend) {
		this.totalMonthTrend = totalMonthTrend;
	}
}
