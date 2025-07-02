package com.corbanmultibancos.business.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.config.ClockConfig;
import com.corbanmultibancos.business.util.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(ClockConfig.class)
public class ReportControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;

	@Autowired
	private MockMvc mockMvc;

	private String teamIds;

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
	}

	@BeforeEach
	void setUp() {
		teamIds = "1,2,3,4,5";
	}

	@Test
	public void getReportShouldReturnEmptyReportWhenEmptyTeamListAndRoleGestor() throws Exception {
		mockMvc.perform(get("/report?teamIds=")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultByTeam").isEmpty())
			.andExpect(jsonPath("$.totalCount").value(0))
			.andExpect(jsonPath("$.totalSumGeneratedDay").value(0))
			.andExpect(jsonPath("$.totalSumPaidDay").value(0))
			.andExpect(jsonPath("$.totalSumPaidMonth").value(0))
			.andExpect(jsonPath("$.totalMonthTrend").value(0));
	}

	@Test
	public void getReportShouldReturnReportWhenNotEmptyTeamListAndRoleGestor() throws Exception {
		mockMvc.perform(get("/report?teamIds={teamIds}", teamIds)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultByTeam").isNotEmpty())
			.andExpect(jsonPath("$.totalCount").value(8))
			.andExpect(jsonPath("$.totalSumGeneratedDay").value(303.59))
			.andExpect(jsonPath("$.totalSumPaidDay").value(656.86))
			.andExpect(jsonPath("$.totalSumPaidMonth").value(8414.52))
			.andExpect(jsonPath("$.totalMonthTrend").value(12421.39));
	}

	@Test
	public void getReportShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/report?teamIds={teamIds}", teamIds)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getReportShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/report?teamIds={teamIds}", teamIds)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}
}
