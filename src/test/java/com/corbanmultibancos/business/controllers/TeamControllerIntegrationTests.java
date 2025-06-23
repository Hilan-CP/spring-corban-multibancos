package com.corbanmultibancos.business.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TeamControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private Long emptyTeamId;
	private String partialName;
	private TeamDTO teamDto;

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
	}

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		emptyTeamId = 5L;
		partialName = "nior";
		teamDto = new TeamDTO(null, "Nova equipe");
	}

	@Test
	public void getTeamByIdShouldReturnTeamDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void getTeamByIdShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getTeamByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getTeamsShouldReturnTeamDTOListWhenPartialName() throws Exception {
		mockMvc.perform(get("/teams?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getTeamsShouldReturnTeamDTOListWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getTeamsShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamsShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createTeamShouldReturnTeamDTOWhenValidDataAndRoleGestor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").value(teamDto.getName()));
	}

	@Test
	public void createTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void createTeamShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createTeamShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		teamDto.setName(null);
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateTeamShouldReturnTeamDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.name").value(teamDto.getName()));
	}

	@Test
	public void updateTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateTeamShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void updateTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateTeamShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		teamDto.setName(null);
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void deleteTeamShouldReturnNoContentWhenEmptyTeamAndRoleGestor() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNoContent());
	}

	@Test
	public void deleteTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteTeamShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void deleteTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(delete("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void deleteTeamShouldReturnConflictWhenNotEmptyTeam() throws Exception {
		mockMvc.perform(delete("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isConflict());
	}

	@Test
	public void getTeamsAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/teams/csv")
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF8"))
			.andExpect(content().string(containsString("ID;Nome")));
	}

	@Test
	public void getTeamsAsCsvShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams/csv")
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamsAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams/csv"))
			.andExpect(status().isUnauthorized());
	}
}
