package com.corbanmultibancos.business.controllers;

import static org.hamcrest.CoreMatchers.containsString;
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

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BankControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;

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

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
	}

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
	public void getBankByIdShouldReturnBankDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").exists())
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void getBankByIdShouldReturnBankDTOWhenExistingIdAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void getBankByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/banks/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenExistingCode() throws Exception {
		mockMvc.perform(get("/banks?code={code}", existingCode)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getBanksShouldReturnEmptyListWhenNonExistingCode() throws Exception {
		mockMvc.perform(get("/banks?code={code}", nonExistingCode)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenPartialName() throws Exception {
		mockMvc.perform(get("/banks?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenNoParameterAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void getBanksShouldReturnBadRequestWhenBothParameters() throws Exception {
		mockMvc.perform(get("/banks?code={code}&name={name}", existingCode, partialName)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBankShouldReturnBankDTOWhenValidDataAndRoleGestor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.code").value(bankDto.getCode()))
			.andExpect(jsonPath("$.name").value(bankDto.getName()));
	}

	@Test
	public void createBankShouldReturnForbiddenWhenValidDataAndRoleConsultor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void createBankShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		bankDto.setName("");
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createBankShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		bankDto.setCode(null);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createBankShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		bankDto.setCode(existingCode);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(post("/banks")
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateBankShouldReturnBankDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").value(bankDto.getCode()))
			.andExpect(jsonPath("$.name").value(bankDto.getName()));
	}

	@Test
	public void updateBankShouldReturnForbiddenWhenExistingIdAndValidDataAndRoleConsultor() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
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
	public void updateBankShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateBankShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		bankDto.setName("");
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateBankShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		bankDto.setCode(null);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateBankShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		bankDto.setCode(otherExistingCode);
		String bankJson = objectMapper.writeValueAsString(bankDto);
		mockMvc.perform(put("/banks/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(bankJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void getBankAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/banks/csv")
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;Código;Nome")));
	}

	@Test
	public void getBankAsCsvShouldReturnResourceWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/banks/csv")
				.header("Authorization", "Bearer " + consultorToken))
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
