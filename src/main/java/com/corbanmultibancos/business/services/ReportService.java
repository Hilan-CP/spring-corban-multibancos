package com.corbanmultibancos.business.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ReportByEmployeeDTO;
import com.corbanmultibancos.business.dto.ReportByTeamDTO;
import com.corbanmultibancos.business.dto.ReportItemDTO;
import com.corbanmultibancos.business.dto.ReportTotalDTO;
import com.corbanmultibancos.business.repositories.ReportRepository;
import com.corbanmultibancos.business.util.DateUtil;

@Service
public class ReportService {

	private ReportRepository reportRepository;
	private DateUtil dateUtil;

	public ReportService(ReportRepository reportRepository, DateUtil dateUtil) {
		this.reportRepository = reportRepository;
		this.dateUtil = dateUtil;
	}

	@Transactional(readOnly = true)
	public ReportTotalDTO getReport(List<Long> teamIds) {
		List<ReportItemDTO> report = reportRepository.findReportByDates(dateUtil.getDateOfFirstDayOfMonth(),
				dateUtil.getDateOfLastDayOfMonth(), dateUtil.getDateOfCurrentDay(), teamIds);
		List<String> teams = report.stream().map(item -> item.getTeam()).distinct().toList();
		List<ReportByTeamDTO> teamReport = new ArrayList<>();
		for (String team : teams) {
			List<ReportItemDTO> filteredReport = report.stream().filter(item -> team.equals(item.getTeam())).toList();
			teamReport.add(createReportByTeam(team, filteredReport));
		}
		return createFinalReport(teamReport);
	}

	private ReportByEmployeeDTO createReportByEmployee(ReportItemDTO report) {
		ReportByEmployeeDTO employeeReport = new ReportByEmployeeDTO();
		employeeReport.setEmployee(report.getEmployee());
		employeeReport.setCount(report.getCount());
		employeeReport.setSumGeneratedDay(report.getSumGeneratedDay());
		employeeReport.setSumPaidDay(report.getSumPaidDay());
		employeeReport.setSumPaidMonth(report.getSumPaidMonth());
		employeeReport.setMonthTrend(calculateTrend(employeeReport.getSumPaidMonth()));
		return employeeReport;
	}

	private ReportByTeamDTO createReportByTeam(String team, List<ReportItemDTO> filteredReport) {
		ReportByTeamDTO teamReport = new ReportByTeamDTO();
		teamReport.setTeam(team);
		teamReport.getResultByEmployee().addAll(filteredReport.stream().map(item -> createReportByEmployee(item)).toList());
		teamReport.setSubtotalCount(filteredReport.stream().mapToLong(item -> item.getCount()).sum());
		teamReport.setSubtotalSumGeneratedDay(filteredReport.stream().map(item -> item.getSumGeneratedDay()).reduce(BigDecimal.ZERO, BigDecimal::add));
		teamReport.setSubtotalSumPaidDay(filteredReport.stream().map(item -> item.getSumPaidDay()).reduce(BigDecimal.ZERO, BigDecimal::add));
		teamReport.setSubtotalSumPaidMonth(filteredReport.stream().map(item -> item.getSumPaidMonth()).reduce(BigDecimal.ZERO, BigDecimal::add));
		teamReport.setSubtotalMonthTrend(calculateTrend(teamReport.getSubtotalSumPaidMonth()));
		return teamReport;
	}

	private ReportTotalDTO createFinalReport(List<ReportByTeamDTO> teamReport) {
		ReportTotalDTO finalReport = new ReportTotalDTO();
		finalReport.getResultByTeam().addAll(teamReport);
		finalReport.setTotalCount(teamReport.stream().mapToLong(item -> item.getSubtotalCount()).sum());
		finalReport.setTotalSumGeneratedDay(teamReport.stream().map(item -> item.getSubtotalSumGeneratedDay()).reduce(BigDecimal.ZERO, BigDecimal::add));
		finalReport.setTotalSumPaidDay(teamReport.stream().map(item -> item.getSubtotalSumPaidDay()).reduce(BigDecimal.ZERO, BigDecimal::add));
		finalReport.setTotalSumPaidMonth(teamReport.stream().map(item -> item.getSubtotalSumPaidMonth()).reduce(BigDecimal.ZERO, BigDecimal::add));
		finalReport.setTotalMonthTrend(calculateTrend(finalReport.getTotalSumPaidMonth()));
		return finalReport;
	}

	private BigDecimal calculateTrend(BigDecimal value) {
		LocalDate today = dateUtil.getDateOfCurrentDay();
		int elapsedDays = today.getDayOfMonth();
		int totalDays = today.lengthOfMonth();
		return value.divide(new BigDecimal(elapsedDays), 2, RoundingMode.HALF_EVEN).multiply(new BigDecimal(totalDays));
	}
}
