package com.corbanmultibancos.business.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReportControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	private String teamIds;

	@BeforeEach
	void setUp() {
		teamIds = "1,2,3,4,5";
	}

	@Test
	public void getReportShouldReturnEmptyReportWhenEmptyTeamList() throws Exception {
		mockMvc.perform(get("/report?teamIds=")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultByTeam").isEmpty())
			.andExpect(jsonPath("$.totalCount").value(0))
			.andExpect(jsonPath("$.totalSumGeneratedDay").value(0))
			.andExpect(jsonPath("$.totalSumPaidDay").value(0))
			.andExpect(jsonPath("$.totalSumPaidMonth").value(0))
			.andExpect(jsonPath("$.totalMonthTrend").value(0));
	}

	@Test
	public void getReportShouldReturnReportWhenNotEmptyTeamList() throws Exception {
		
		mockMvc.perform(get("/report?teamIds={teamIds}", teamIds)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultByTeam").hasJsonPath())
			.andExpect(jsonPath("$.totalCount").hasJsonPath())
			.andExpect(jsonPath("$.totalSumGeneratedDay").hasJsonPath())
			.andExpect(jsonPath("$.totalSumPaidDay").hasJsonPath())
			.andExpect(jsonPath("$.totalSumPaidMonth").hasJsonPath())
			.andExpect(jsonPath("$.totalMonthTrend").hasJsonPath());
	}
}
