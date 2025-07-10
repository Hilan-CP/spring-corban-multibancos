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

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;

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

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
	}

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
	public void getUserByIdShouldReturnUserDataDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.employeeId").value(existingId))
			.andExpect(jsonPath("$.username").exists())
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").exists())
			.andExpect(jsonPath("$.role.authority").exists());
	}
	
	@Test
	public void getUserByIdShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUserByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getUserByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getUsersShouldReturnPageOfSingleUserDataDTOWhenExistingUsername() throws Exception {
		mockMvc.perform(get("/users?username={username}", existingUsername)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements").value(1));
	}

	@Test
	public void getUsersShouldReturnEmptyPageWhenNonExistingUsername() throws Exception {
		mockMvc.perform(get("/users?username={username}", nonExistingUsername)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	public void getUsersShouldReturnPageOfUserDataDTOWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getUsersShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUsersShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createUserShouldReturnUserDataDTOWhenValidDataAndRoleGestor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.employeeId").value(userCreateDto.getEmployeeId()))
			.andExpect(jsonPath("$.username").value(userCreateDto.getUsername()))
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").value(userCreateDto.getRoleId()))
			.andExpect(jsonPath("$.role.authority").exists());
	}

	@Test
	public void createUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void createUserShouldReturnUnprocessableEntityWhenEmployeeIdIsNull() throws Exception {
		userCreateDto.setEmployeeId(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createUserShouldReturnUnprocessableEntityWhenUsernameIsBlank() throws Exception {
		userCreateDto.setUsername(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createUserShouldReturnUnprocessableEntityWhenPasswordIsBlank() throws Exception {
		userCreateDto.setPassword(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createUserShouldReturnUnprocessableEntityWhenRoleIsNull() throws Exception {
		userCreateDto.setRoleId(null);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createUserShouldReturnNotFoundWhenNonExistingRole() throws Exception {
		userCreateDto.setRoleId(1000L);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createUserShouldReturnUnprocessableEntityWhenUniqueEmployeeIdViolation() throws Exception {
		userCreateDto.setEmployeeId(existingId);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createUserShouldReturnUnprocessableEntityWhenUniqueUsernameViolation() throws Exception {
		userCreateDto.setUsername(existingUsername);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateUserShouldReturnUserDataDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.employeeId").value(existingId))
			.andExpect(jsonPath("$.username").value(userCreateDto.getUsername()))
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.role.id").exists())
			.andExpect(jsonPath("$.role.authority").exists());
	}

	@Test
	public void updateUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void updateUserShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateUserShouldReturnUnprocessableEntityWhenUniqueUsernameViolation() throws Exception {
		userCreateDto.setUsername(otherUsername);
		String userJson = objectMapper.writeValueAsString(userCreateDto);
		mockMvc.perform(put("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void deleteUserShouldReturnNoContentWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNoContent());
	}

	@Test
	public void deleteUserShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteUserShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(delete("/users/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void deleteUserShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(delete("/users/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getUsersAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/users/csv")
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("")));
	}

	@Test
	public void getUsersAsCsvShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/users/csv")
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getUsersAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/users/csv"))
			.andExpect(status().isUnauthorized());
	}
}
