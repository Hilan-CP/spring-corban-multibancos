package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.dto.BankDTO;
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
public class BankControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private Integer existingCode;
	private Integer nonExistingCode;
	private Integer otherExistingCode;
	private String partialName;
	private BankDTO bankDto;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 100L;
		existingCode = 623;
		nonExistingCode = 999;
		otherExistingCode = 336;
		partialName = "pa";
		bankDto = new BankDTO(null, 1000, "Banco de teste");
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBankByIdShouldReturnBankDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/banks/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(existingId))
				.andExpect(jsonPath("$.code").exists())
				.andExpect(jsonPath("$.name").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getBankByIdShouldReturnBankDTOWhenExistingIdAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").exists())
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void getBankByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBankByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/banks/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBanksShouldReturnBankDTOListWhenExistingCode() throws Exception {
		mockMvc.perform(get("/banks?code={code}", existingCode)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBanksShouldReturnEmptyListWhenNonExistingCode() throws Exception {
		mockMvc.perform(get("/banks?code={code}", nonExistingCode)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBanksShouldReturnBankDTOListWhenPartialName() throws Exception {
		mockMvc.perform(get("/banks?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBanksShouldReturnBankDTOListWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/banks")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getBanksShouldReturnBankDTOListWhenNoParameterAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getBanksShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/banks")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBanksShouldReturnBadRequestWhenBothParameters() throws Exception {
		mockMvc.perform(get("/banks?code={code}&name={name}", existingCode, partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createBankShouldReturnBankDTOWhenValidDataAndRoleGestor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.code").value(bankDto.getCode()))
			.andExpect(jsonPath("$.name").value(bankDto.getName()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void createBankShouldReturnForbiddenWhenValidDataAndRoleConsultor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void createBankShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createBankShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		bankDto.setName("");
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createBankShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		bankDto.setCode(null);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createBankShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		bankDto.setCode(existingCode);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateBankShouldReturnBankDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").value(bankDto.getCode()))
			.andExpect(jsonPath("$.name").value(bankDto.getName()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void updateBankShouldReturnForbiddenWhenExistingIdAndValidDataAndRoleConsultor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateBankShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateBankShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateBankShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		bankDto.setName("");
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateBankShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		bankDto.setCode(null);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateBankShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		bankDto.setCode(otherExistingCode);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getBankAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/banks/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;Código;Nome")));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getBankAsCsvShouldReturnResourceWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;Código;Nome")));
	}

	@Test
	public void getBankAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/banks/csv"))
			.andExpect(status().isUnauthorized());
	}
}
