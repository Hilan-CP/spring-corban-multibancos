package com.corbanmultibancos.business.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.corbanmultibancos.business.dto.TeamDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void getTeamByIdShouldReturnTeamDTOWhenExistingId() throws Exception {
		mockMvc.perform(get("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void getTeamByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getTeamsShouldReturnTeamDTOListWhenPartialName() throws Exception {
		mockMvc.perform(get("/teams?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getTeamsShouldReturnTeamDTOListWhenNoParameter() throws Exception {
		mockMvc.perform(get("/teams")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void createTeamShouldReturnTeamDTOWhenValidData() throws Exception {
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
	public void updateTeamShouldReturnTeamDTOWhenExistingId() throws Exception {
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
	public void updateTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String teamJson = objectMapper.writeValueAsString(teamDto);
		mockMvc.perform(put("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(teamJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void deleteTeamShouldReturnNoContentWhenEmptyTeam() throws Exception {
		mockMvc.perform(delete("/teams/{id}", emptyTeamId))
			.andExpect(status().isNoContent());
	}

	@Test
	public void deleteTeamShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(delete("/teams/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void deleteTeamShouldReturnConflictWhenNotEmptyTeam() throws Exception {
		mockMvc.perform(delete("/teams/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict());
	}
}
