package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.dto.UserCreateDTO;
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
public class UserControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private Long newUserId;
	private String existingUsername;
	private String nonExistingUsername;
	private String otherUsername;
	private UserCreateDTO userCreateDto;


	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		newUserId = 9L;
		existingUsername = "florinda";
		nonExistingUsername = "inexistente";
		otherUsername = "zenobia";
		userCreateDto = new UserCreateDTO(newUserId, "george", "george123", 1L);
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUserByIdShouldReturnUserDataDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.employeeId").value(existingId))
			.andExpect(jsonPath("$.username").exists())
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").exists())
			.andExpect(jsonPath("$.role.authority").exists());
	}
	
	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getUserByIdShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUserByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUserByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUsersShouldReturnPageOfSingleUserDataDTOWhenExistingUsername() throws Exception {
		mockMvc.perform(get("/users?username={username}", existingUsername)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements").value(1));
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUsersShouldReturnEmptyPageWhenNonExistingUsername() throws Exception {
		mockMvc.perform(get("/users?username={username}", nonExistingUsername)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUsersShouldReturnPageOfUserDataDTOWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getUsersShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUsersShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUserDataDTOWhenValidDataAndRoleGestor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.employeeId").value(userCreateDto.getEmployeeId()))
			.andExpect(jsonPath("$.username").value(userCreateDto.getUsername()))
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").value(userCreateDto.getRoleId()))
			.andExpect(jsonPath("$.role.authority").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void createUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void createUserShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenEmployeeIdIsNull() throws Exception {
		userCreateDto.setEmployeeId(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenUsernameIsBlank() throws Exception {
		userCreateDto.setUsername(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenPasswordIsBlank() throws Exception {
		userCreateDto.setPassword(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenRoleIsNull() throws Exception {
		userCreateDto.setRoleId(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnNotFoundWhenNonExistingRole() throws Exception {
		userCreateDto.setRoleId(1000L);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenUniqueEmployeeIdViolation() throws Exception {
		userCreateDto.setEmployeeId(existingId);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createUserShouldReturnUnprocessableEntityWhenUniqueUsernameViolation() throws Exception {
		userCreateDto.setUsername(existingUsername);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateUserShouldReturnUserDataDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.employeeId").value(existingId))
			.andExpect(jsonPath("$.username").value(userCreateDto.getUsername()))
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").exists())
			.andExpect(jsonPath("$.role.authority").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void updateUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateUserShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateUserShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateUserShouldReturnUnprocessableEntityWhenUniqueUsernameViolation() throws Exception {
		userCreateDto.setUsername(otherUsername);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void deleteUserShouldReturnNoContentWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void deleteUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteUserShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void deleteUserShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(delete("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getUsersAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/users/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("")));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getUsersAsCsvShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users/csv"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUsersAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users/csv"))
			.andExpect(status().isUnauthorized());
	}
}
