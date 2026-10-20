package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.dto.CustomerDTO;
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

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerIntegrationTests {

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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomerByIdShouldReturnCustomerDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.phone").exists())
			.andExpect(jsonPath("$.birthDate").exists());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getCustomerByIdShouldReturnCustomerDTOWhenExistingIdAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomerByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/customers/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnPageOfCustomerDTOWhenExistingCpf() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}", existingCpf)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements", is(1)));
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnEmptyPageWhenNonExistingCpf() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}", nonExistingCpf)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnPageOfCustomerDTOWhenPartialName() throws Exception {
		mockMvc.perform(get("/customers?name={name}", partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnPageOfCustomerDTOWhenPartialPhone() throws Exception {
		mockMvc.perform(get("/customers?phone={phone}", partialPhone)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnPageOfCustomerDTOWhenNoParameterAndRoleGestor() throws Exception {
		mockMvc.perform(get("/customers")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getCustomersShouldReturnPageOfCustomerDTOWhenNoParameterAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers")
				.accept(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnBadRequestWhenAllParameters() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&name={name}&phone={phone}", existingCpf, partialName, partialPhone)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnBadRequestWhenExistingCpfAndName() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&name={name}", existingCpf, partialName)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnBadRequestWhenExistingCpfAndPhone() throws Exception {
		mockMvc.perform(get("/customers?cpf={cpf}&phone={phone}", existingCpf, partialPhone)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersShouldReturnBadRequestWhenExistingNameAndPhone() throws Exception {
		mockMvc.perform(get("/customers?name={name}&phone={phone}", partialName, partialPhone)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnCustomerDTOWhenValidDataAndRoleGestor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void createCustomerShouldReturnCustomerDTOWhenValidDataAndRoleConsultor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenCpfIsBlank() throws Exception {
		customerDto.setCpf(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenInvalidCpf() throws Exception {
		customerDto.setCpf("12345678900");
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		customerDto.setName(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenPhoneIsBlank() throws Exception {
		customerDto.setPhone(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenBirthDateIsNull() throws Exception {
		customerDto.setBirthDate(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void createCustomerShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		customerDto.setCpf(otherCpf);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(post("/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnCustomerDTOWhenExistingIdAndValidDataAndRoleGestor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.cpf").value(customerDto.getCpf()))
			.andExpect(jsonPath("$.name").value(customerDto.getName()))
			.andExpect(jsonPath("$.phone").value(customerDto.getPhone()))
			.andExpect(jsonPath("$.birthDate").value(customerDto.getBirthDate().toString()));
	}
	
	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void updateCustomerShouldReturnCustomerDTOWhenExistingIdAndValidDataAndRoleConsultor() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnUnprocessableEntityWhenCpfIsBlank() throws Exception {
		customerDto.setCpf(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnUnprocessableEntityWhenNameIsBlank() throws Exception {
		customerDto.setName(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnUnprocessableEntityWhenPhoneIsBlank() throws Exception {
		customerDto.setPhone(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnUnprocessableEntityWhenBirthDateIsNull() throws Exception {
		customerDto.setBirthDate(null);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void updateCustomerShouldReturnUnprocessableEntityWhenUniqueCpfViolation() throws Exception {
		customerDto.setCpf(otherCpf);
		String customerJson = objectMapper.writeValueAsString(customerDto);
		mockMvc.perform(put("/customers/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(customerJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	@WithMockUser(username = "zenobia", authorities = "SCOPE_GESTOR")
	public void getCustomersAsCsvShouldReturnResourceWhenRoleGestor() throws Exception {
		mockMvc.perform(get("/customers/csv"))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;CPF;Nome;Telefone;Nascimento")));
	}

	@Test
	@WithMockUser(username = "florinda", authorities = "SCOPE_CONSULTOR")
	public void getCustomersAsCsvShouldReturnResourceWhenRoleConsultor() throws Exception {
		mockMvc.perform(get("/customers/csv"))
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
