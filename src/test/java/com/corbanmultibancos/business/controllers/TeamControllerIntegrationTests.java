package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.dto.TeamDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TeamControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private Long emptyTeamId;
	private String partialName;
	private TeamDTO teamDto;


	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		emptyTeamId = 5L;
		partialName = "nior";
		teamDto = new TeamDTO(null, "Nova equipe");
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getTeamByIdShouldReturnTeamDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getTeamByIdShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getTeamByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getTeamsShouldReturnTeamDTOListWhenPartialName() throws Exception {
		mockMvc.perform(get("/teams?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getTeamsShouldReturnTeamDTOListWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getTeamsShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamsShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createTeamShouldReturnTeamDTOWhenValidDataAndRoleGestor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").value(teamDto.getName()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void createTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createTeamShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		teamDto.setName(null);
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(post("/teams")
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateTeamShouldReturnTeamDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.name").value(teamDto.getName()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void updateTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateTeamShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		teamDto.setName(null);
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void deleteTeamShouldReturnNoContentWhenEmptyTeamAndRoleGestor() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void deleteTeamShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteTeamShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void deleteTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(delete("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void deleteTeamShouldReturnConflictWhenNotEmptyTeam() throws Exception {
		mockMvc.perform(delete("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getTeamsAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/teams/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF8"))
			.andExpect(content().string(containsString("ID;Nome")));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getTeamsAsCsvShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/teams/csv"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getTeamsAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/teams/csv"))
			.andExpect(status().isUnauthorized());
	}
}
