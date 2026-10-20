package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
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
public class EmployeeControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private String existingCpf;
	private String nonExistingCpf;
	private String otherCpf;
	private String partialName;
	private EmployeeCreateDTO employeeCreationDto;


	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		existingCpf = "10975759000";
		nonExistingCpf = "00011122233";
		otherCpf = "33701848009";
		partialName = "jo";
		employeeCreationDto = new EmployeeCreateDTO(null, "01234567890", "Novo Funcionário", 1L);
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeeByIdShouldReturnEmployeeUserDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.username").exists())
			.andExpect(jsonPath("$.roleName").exists())
			.andExpect(jsonPath("$.team").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getEmployeeByIdShouldReturnForbiddenWhenExistingIdAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getEmployeeByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeeByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/employees/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesShouldReturnPageOfSingleEmployeeUserDTOWhenExistingCpf() throws Exception {
		mockMvc.perform(get("/employees?cpf={cpf}", existingCpf)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements").value(1));
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesShouldReturnEmptyPageWhenNonExistingCpf() throws Exception {
		mockMvc.perform(get("/employees?cpf={cpf}", nonExistingCpf)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenPartialName() throws Exception {
		mockMvc.perform(get("/employees?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/employees")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getEmployeesShouldReturnForbiddenWhenNoParameterAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/employees")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getEmployeesShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/employees")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesShouldReturnBadRequestWhenAllParameters() throws Exception {
		mockMvc.perform(get("/employees?cpf={cpf}&name={name}", existingCpf, partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createEmployeeShouldReturnEmployeeCreationDTOWhenValidDataAndRoleGestor() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.cpf").value(employeeCreationDto.getCpf()))
			.andExpect(jsonPath("$.name").value(employeeCreationDto.getName()))
			.andExpect(jsonPath("$.teamId").value(employeeCreationDto.getTeamId()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void createEmployeeShouldReturnForbiddenWhenValidDataAndRoleConsultor() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void createEmployeeShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createEmployeeShouldReturnUnprocessableEntityWhenCpfIsBlank() throws Exception {
		employeeCreationDto.setCpf(null);
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createEmployeeShouldReturnUnprocessableEntityWhenInvalidCpf() throws Exception {
		employeeCreationDto.setCpf("12345678900");
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createEmployeeShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		employeeCreationDto.setName(null);
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createEmployeeShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		employeeCreationDto.setCpf(otherCpf);
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(post("/employees")
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateEmployeeShouldReturnEmployeeCreationDTOWhenExistingIdAndRoleGestor() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(put("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").value(employeeCreationDto.getCpf()))
			.andExpect(jsonPath("$.name").value(employeeCreationDto.getName()))
			.andExpect(jsonPath("$.teamId").value(employeeCreationDto.getTeamId()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void updateEmployeeShouldReturnForbiddenWhenExistingIdAndRoleConsultor() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(put("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateEmployeeShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(put("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateEmployeeShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(put("/employees/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateEmployeeShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		employeeCreationDto.setCpf(otherCpf);
		String employeeJson = objectMapper.writeValueAsString(employeeCreationDto);
		mockMvc.perform(put("/employees/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(employeeJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getEmployeesAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/employees/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;CPF;Nome;Usuário;Tipo_Usuário;ID_Equipe;Nome_Equipe")));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getEmployeesAsCsvShouldReturnForbiddenWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/employees/csv"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getEmployeesAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/employees/csv"))
			.andExpect(status().isUnauthorized());
	}
}
