package com.corbanmultibancos.business.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;

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
	private String partialPhone;
	private CustomerDTO customerDto;

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
	}

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 1000L;
		existingCpf = "00066098645";
		nonExistingCpf = "00011122233";
		otherCpf = "00384722288";
		partialName = "se";
		partialPhone = "119";
		customerDto = new CustomerDTO(null, "01234567890", "José", "44987654321", LocalDate.of(1999, 12, 30));
	}

	@Test
	public void getCustomerByIdShouldReturnCustomerDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.phone").exists())
			.andExpect(jsonPath("$.birthDate").exists());
	}

	@Test
	public void getCustomerByIdShouldReturnCustomerDTOWhenExistingIdAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.phone").exists())
			.andExpect(jsonPath("$.birthDate").exists());
	}

	@Test
	public void getCustomerByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getCustomerByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/customers/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getCustomersShouldReturnPageOfCustomerDTOWhenExistingCpf() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}", existingCpf)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements", is(1)));
	}

	@Test
	public void getCustomersShouldReturnEmptyPageWhenNonExistingCpf() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}", nonExistingCpf)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	public void getCustomersShouldReturnPageOfCustomerDTOWhenPartialName() throws Exception {
		mockMvc.perform(get("/customers?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getCustomersShouldReturnPageOfCustomerDTOWhenPartialPhone() throws Exception {
		mockMvc.perform(get("/customers?phone={phone}", partialPhone)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getCustomersShouldReturnPageOfCustomerDTOWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getCustomersShouldReturnPageOfCustomerDTOWhenNoParameterAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getCustomersShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/customers")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getCustomersShouldReturnBadRequestWhenAllParameters() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&name={name}&phone={phone}", existingCpf, partialName, partialPhone)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getCustomersShouldReturnBadRequestWhenExistingCpfAndName() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&name={name}", existingCpf, partialName)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getCustomersShouldReturnBadRequestWhenExistingCpfAndPhone() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&phone={phone}", existingCpf, partialPhone)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getCustomersShouldReturnBadRequestWhenExistingNameAndPhone() throws Exception {
		mockMvc.perform(get("/customers?name={name}&phone={phone}", partialName, partialPhone)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createCustomerShouldReturnCustomerDTOWhenValidDataAndRoleGestor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}

	@Test
	public void createCustomerShouldReturnCustomerDTOWhenValidDataAndRoleConsultor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}

	@Test
	public void createCustomerShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenCpfIsBlank() throws Exception {
		customerDto.setCpf(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenInvalidCpf() throws Exception {
		customerDto.setCpf("12345678900");
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		customerDto.setName(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenPhoneIsBlank() throws Exception {
		customerDto.setPhone(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenBirthDateIsNull() throws Exception {
		customerDto.setBirthDate(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createCustomerShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		customerDto.setCpf(otherCpf);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCustomerShouldReturnCustomerDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}
	
	@Test
	public void updateCustomerShouldReturnCustomerDTOWhenExistingIdAndValidDataAndRoleConsultor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}
	
	@Test
	public void updateCustomerShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void updateCustomerShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateCustomerShouldReturnUnprocessableEntityWhenCpfIsBlank() throws Exception {
		customerDto.setCpf(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCustomerShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		customerDto.setName(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCustomerShouldReturnUnprocessableEntityWhenPhoneIsBlank() throws Exception {
		customerDto.setPhone(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCustomerShouldReturnUnprocessableEntityWhenBirthDateIsNull() throws Exception {
		customerDto.setBirthDate(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCustomerShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		customerDto.setCpf(otherCpf);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void getCustomersAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/customers/csv")
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;CPF;Nome;Telefone;Nascimento")));
	}

	@Test
	public void getCustomersAsCsvShouldReturnResourceWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers/csv")
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;CPF;Nome;Telefone;Nascimento")));
	}

	@Test
	public void getCustomersAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/customers/csv"))
			.andExpect(status().isUnauthorized());
	}
}
