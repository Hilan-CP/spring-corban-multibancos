package com.corbanmultibancos.business.services;

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

@Service
public class ReportService {

	private ReportRepository reportRepository;
	private LocalDate today;
	private LocalDate firstDayOfMonth;
	private LocalDate lastDayOfMonth;

	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
		this.today = LocalDate.now();
		this.firstDayOfMonth = today.withDayOfMonth(1);
		this.lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
	}

	@Transactional(readOnly = true)
	public ReportTotalDTO getReport(List<Long> teamIds) {
		List<ReportItemDTO> report = reportRepository.findReportByDates(firstDayOfMonth, lastDayOfMonth, today, teamIds);
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
		employeeReport.setSumGeneratedDay(report.getSumGeneratedDay().doubleValue());
		employeeReport.setSumPaidDay(report.getSumPaidDay().doubleValue());
		employeeReport.setSumPaidMonth(report.getSumPaidMonth().doubleValue());
		employeeReport.setMonthTrend(calculateTrend(employeeReport.getSumPaidMonth()));
		return employeeReport;
	}

	private ReportByTeamDTO createReportByTeam(String team, List<ReportItemDTO> filteredReport) {
		ReportByTeamDTO teamReport = new ReportByTeamDTO();
		teamReport.setTeam(team);
		teamReport.getResultByEmployee().addAll(filteredReport.stream().map(item -> createReportByEmployee(item)).toList());
		teamReport.setSubtotalCount(filteredReport.stream().mapToLong(item -> item.getCount()).sum());
		teamReport.setSubtotalSumGeneratedDay(filteredReport.stream().mapToDouble(item -> item.getSumGeneratedDay().doubleValue()).sum());
		teamReport.setSubtotalSumPaidDay(filteredReport.stream().mapToDouble(item -> item.getSumPaidDay().doubleValue()).sum());
		teamReport.setSubtotalSumPaidMonth(filteredReport.stream().mapToDouble(item -> item.getSumPaidMonth().doubleValue()).sum());
		teamReport.setSubtotalMonthTrend(calculateTrend(teamReport.getSubtotalSumPaidMonth()));
		return teamReport;
	}

	private ReportTotalDTO createFinalReport(List<ReportByTeamDTO> teamReport) {
		ReportTotalDTO finalReport = new ReportTotalDTO();
		finalReport.getResultByTeam().addAll(teamReport);
		finalReport.setTotalCount(teamReport.stream().mapToLong(item -> item.getSubtotalCount()).sum());
		finalReport.setTotalSumGeneratedDay(teamReport.stream().mapToDouble(item -> item.getSubtotalSumGeneratedDay()).sum());
		finalReport.setTotalSumPaidDay(teamReport.stream().mapToDouble(item -> item.getSubtotalSumPaidDay()).sum());
		finalReport.setTotalSumPaidMonth(teamReport.stream().mapToDouble(item -> item.getSubtotalSumPaidMonth()).sum());
		finalReport.setTotalMonthTrend(calculateTrend(finalReport.getTotalSumPaidMonth()));
		return finalReport;
	}

	private double calculateTrend(double value) {
		int elapsedDays = today.getDayOfMonth();
		int totalDays = today.lengthOfMonth();
		return (value / elapsedDays) * totalDays;
	}
}
