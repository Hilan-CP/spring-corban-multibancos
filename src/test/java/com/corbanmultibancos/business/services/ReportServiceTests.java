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
import com.corbanmultibancos.business.util.DateUtil;

@ExtendWith(SpringExtension.class)
public class ReportServiceTests {

	@InjectMocks
	private ReportService reportService;

	@Mock
	private ReportRepository reportRepository;
	
	@Mock
	private DateUtil dateUtil;

	List<ReportItemDTO> report;
	List<Long> teamIds;
	LocalDate today;
	LocalDate firstDayOfMonth;
	LocalDate lastDayOfMonth;

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
		teamIds = List.of(1L, 2L);
		today = LocalDate.of(2025, 6, 21);
		firstDayOfMonth = LocalDate.of(2025, 6, 1);
		lastDayOfMonth = LocalDate.of(2025, 6, 30);
		Mockito.when(reportRepository.findReportByDates(any(), any(), any(), eq(List.of()))).thenReturn(List.of());
		Mockito.when(reportRepository.findReportByDates(any(), any(), any(), eq(teamIds))).thenReturn(report);
		Mockito.when(dateUtil.getDateOfCurrentDay()).thenReturn(today);
		Mockito.when(dateUtil.getDateOfFirstDayOfMonth()).thenReturn(firstDayOfMonth);
		Mockito.when(dateUtil.getDateOfLastDayOfMonth()).thenReturn(lastDayOfMonth);
	}

	@Test
	public void getReportShouldReturnEmptyReportWhenEmptyTeamList() {
		ReportTotalDTO finalReport = reportService.getReport(List.of());
		Assertions.assertTrue(finalReport.getResultByTeam().isEmpty());
		Assertions.assertEquals(0L, finalReport.getTotalCount());
		Assertions.assertEquals(BigDecimal.ZERO, finalReport.getTotalSumGeneratedDay());
		Assertions.assertEquals(BigDecimal.ZERO, finalReport.getTotalSumPaidDay());
		Assertions.assertEquals(BigDecimal.ZERO, finalReport.getTotalSumPaidMonth());
		Assertions.assertEquals(BigDecimal.ZERO.setScale(2), finalReport.getTotalMonthTrend());
	}

	@Test
	public void getReportShouldReturnReportWhenNotEmptyTeamList() {
		ReportTotalDTO finalReport = reportService.getReport(teamIds);
		BigDecimal generatedDay = new BigDecimal(5581.5).setScale(2);
		BigDecimal paidDay = new BigDecimal(4700.0).setScale(2);
		BigDecimal paidMonth = new BigDecimal(68000.0).setScale(2);
		BigDecimal trend = new BigDecimal(97143.0).setScale(2);
		Assertions.assertFalse(finalReport.getResultByTeam().isEmpty());
		Assertions.assertEquals(65L, finalReport.getTotalCount());
		Assertions.assertEquals(generatedDay, finalReport.getTotalSumGeneratedDay());
		Assertions.assertEquals(paidDay, finalReport.getTotalSumPaidDay());
		Assertions.assertEquals(paidMonth, finalReport.getTotalSumPaidMonth());
		Assertions.assertEquals(trend, finalReport.getTotalMonthTrend());
	}
}
