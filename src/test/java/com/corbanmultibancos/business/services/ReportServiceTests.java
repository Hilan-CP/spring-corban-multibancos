package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.ReportItemDTO;
import com.corbanmultibancos.business.dto.ReportTotalDTO;
import com.corbanmultibancos.business.repositories.ReportRepository;

@ExtendWith(SpringExtension.class)
public class ReportServiceTests {

	@InjectMocks
	private ReportService reportService;

	@Mock
	private ReportRepository reportRepository;

	List<ReportItemDTO> report;
	List<Long> teamIds;
	LocalDate today;

	@BeforeEach
	void setUp() {
		report = new ArrayList<>();
		report.add(new ReportItemDTO("Equipe A", "Ana Silva", 15L, new BigDecimal("1250.75"), new BigDecimal("1000.00"),
				new BigDecimal("15000.00")));
		report.add(new ReportItemDTO("Equipe B", "Carlos Souza", 10L, new BigDecimal("980.50"),
				new BigDecimal("850.00"), new BigDecimal("12000.00")));
		report.add(new ReportItemDTO("Equipe A", "Beatriz Costa", 20L, new BigDecimal("1500.00"),
				new BigDecimal("1300.00"), new BigDecimal("18000.00")));
		report.add(new ReportItemDTO("Equipe B", "Daniel Pereira", 8L, new BigDecimal("750.25"),
				new BigDecimal("600.00"), new BigDecimal("9000.00")));
		report.add(new ReportItemDTO("Equipe A", "Fernanda Santos", 12L, new BigDecimal("1100.00"),
				new BigDecimal("950.00"), new BigDecimal("14000.00")));
		teamIds = List.of(1L,2L);
		today = LocalDate.now();
		Mockito.when(reportRepository.findReportByDates(any(), any(), any(), eq(List.of()))).thenReturn(List.of());
		Mockito.when(reportRepository.findReportByDates(any(), any(), any(), eq(teamIds))).thenReturn(report);
	}

	@Test
	public void getReportShouldReturnEmptyReportWhenEmptyTeamList() {
		ReportTotalDTO finalReport = reportService.getReport(List.of());
		Assertions.assertTrue(finalReport.getResultByTeam().isEmpty());
		Assertions.assertEquals(0L, finalReport.getTotalCount());
		Assertions.assertEquals(0.0, finalReport.getTotalSumGeneratedDay());
		Assertions.assertEquals(0.0, finalReport.getTotalSumPaidDay());
		Assertions.assertEquals(0.0, finalReport.getTotalSumPaidMonth());
		Assertions.assertEquals(0.0, finalReport.getTotalMonthTrend());
	}

	@Test
	public void getReportShouldReturnReportWhenNotEmptyTeamList() {
		ReportTotalDTO finalReport = reportService.getReport(teamIds);
		Double trend = (finalReport.getTotalSumPaidMonth() / today.getDayOfMonth()) * today.lengthOfMonth();
		Assertions.assertFalse(finalReport.getResultByTeam().isEmpty());
		Assertions.assertEquals(65L, finalReport.getTotalCount());
		Assertions.assertEquals(5581.50, finalReport.getTotalSumGeneratedDay());
		Assertions.assertEquals(4700.00, finalReport.getTotalSumPaidDay());
		Assertions.assertEquals(68000.00, finalReport.getTotalSumPaidMonth());
		Assertions.assertEquals(trend, finalReport.getTotalMonthTrend());
	}
}
