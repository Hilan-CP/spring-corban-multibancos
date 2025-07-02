package com.corbanmultibancos.business.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.config.ClockConfig;
import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.util.DateUtil;
import com.corbanmultibancos.business.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(ClockConfig.class)
public class ProposalControllerIntegrationTests {

	private static String gestorToken;
	private static String consultorToken;
	private static String ownerConsultorToken;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DateUtil dateUtil;

	private Long existingId;
	private Long nonExistingId;
	private String existingCode;
	private String otherCode;
	private String partialEmployeeName;
	private Integer bankCode;
	private String dateField;
	private LocalDate beginDate;
	private LocalDate endDate;
	private ProposalCreateDTO proposalCreateDto;

	@BeforeAll
	static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired TokenUtil tokenUtil) throws Exception {
		gestorToken = tokenUtil.logInAndGetToken(mockMvc, "zenobia", "zenobia123");
		consultorToken = tokenUtil.logInAndGetToken(mockMvc, "florinda", "florinda123");
		ownerConsultorToken = tokenUtil.logInAndGetToken(mockMvc, "ricardo", "ricardo123");
	}

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 10000L;
		existingCode = "993";
		otherCode = "1430";
		partialEmployeeName = "jo";
		bankCode = 623;
		dateField = "generation";
		beginDate = dateUtil.getDateOfFirstDayOfMonth();
		endDate = dateUtil.getDateOfLastDayOfMonth();
		proposalCreateDto = new ProposalCreateDTO(null, "123", 100.0, LocalDate.now(), null, 1L, 1L);
	}

	@Test
	public void getProposalByIdShouldReturnProposalDataDTOWhenExistingIdAndRoleGestor() throws Exception {
		mockMvc.perform(get("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").exists())
			.andExpect(jsonPath("$.value").exists())
			.andExpect(jsonPath("$.generation").exists())
			.andExpect(jsonPath("$.payment").hasJsonPath())
			.andExpect(jsonPath("$.status").exists())
			.andExpect(jsonPath("$.employeeName").exists())
			.andExpect(jsonPath("$.bankName").exists())
			.andExpect(jsonPath("$.customerCpf").exists())
			.andExpect(jsonPath("$.customerName").exists());
	}

	@Test
	public void getProposalByIdShouldReturnForbiddenWhenExistingIdAndRoleConsultorNotOwner() throws Exception {
		mockMvc.perform(get("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void getProposalByIdShouldReturnProposalDataDTOWhenExistingIdAndRoleConsultorOwner() throws Exception {
		mockMvc.perform(get("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + ownerConsultorToken))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getProposalByIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getProposalByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/proposals/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getProposalsShouldReturnPageOfSingleProposalDataDTOWhenExistingCodeAndRoleGestor() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&dateField={field}&beginDate={begin}&endDate={end}",
				existingCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements").value(1));
	}

	@Test
	public void getProposalsShouldReturnEmptyPageWhenExistingCodeAndRoleConsultorNotOwner() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&dateField={field}&beginDate={begin}&endDate={end}",
				existingCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndBankCodeNotNull() throws Exception {
		mockMvc.perform(get("/proposals?bankCode={bankCode}&dateField={field}&beginDate={begin}&endDate={end}",
				bankCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndEmployeeNameNotNull() throws Exception {
		mockMvc.perform(get("/proposals?employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateNotNullAndRoleGestor() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}
	
	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateNotNullAndRoleConsultor() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.content[0].employeeName").value("Florinda Flores"));
	}

	@Test
	public void getProposalsShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenDateIsNull() throws Exception {
		mockMvc.perform(get("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenInvalidDateField() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				"cancel", beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenBankCodeAndEmployeeNameNotNull() throws Exception {
		mockMvc.perform(get("/proposals?bankCode={bankCode}&employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				bankCode, partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenAllParamters() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&bankCode={bankCode}&employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				existingCode, bankCode, partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createProposalShouldReturnProposalDataDTOWhenValidData() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.code").value(proposalCreateDto.getCode()))
			.andExpect(jsonPath("$.value").value(proposalCreateDto.getValue()))
			.andExpect(jsonPath("$.generation").exists())
			.andExpect(jsonPath("$.payment").hasJsonPath())
			.andExpect(jsonPath("$.status").value("GERADA"))
			.andExpect(jsonPath("$.employeeName").exists())
			.andExpect(jsonPath("$.bankName").exists())
			.andExpect(jsonPath("$.customerCpf").exists())
			.andExpect(jsonPath("$.customerName").exists());
	}

	@Test
	public void createProposalShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		proposalCreateDto.setCode(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenValueIsNull() throws Exception {
		proposalCreateDto.setValue(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenGenerationDateIsNull() throws Exception {
		proposalCreateDto.setGeneration(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenCustomerIdIsNull() throws Exception {
		proposalCreateDto.setCustomerId(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenBankIdIsNull() throws Exception {
		proposalCreateDto.setBankId(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnNotFoundWhenNonExistingCustomer() throws Exception {
		proposalCreateDto.setCustomerId(100000L);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createProposalShouldReturnNotFoundWhenNonExistingBank() throws Exception {
		proposalCreateDto.setBankId(100000L);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		proposalCreateDto.setCode(otherCode);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenGenerationIsAfterPayment() throws Exception {
		proposalCreateDto.setGeneration(LocalDate.now());
		proposalCreateDto.setPayment(LocalDate.now().minusDays(1L));
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnProposalDataDTOWhenExistingIdAndRoleGestor() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingId))
			.andExpect(jsonPath("$.code").value(proposalCreateDto.getCode()))
			.andExpect(jsonPath("$.value").value(proposalCreateDto.getValue()))
			.andExpect(jsonPath("$.generation").exists())
			.andExpect(jsonPath("$.payment").hasJsonPath())
			.andExpect(jsonPath("$.status").exists())
			.andExpect(jsonPath("$.employeeName").exists())
			.andExpect(jsonPath("$.bankName").exists())
			.andExpect(jsonPath("$.customerCpf").exists())
			.andExpect(jsonPath("$.customerName").exists());
	}

	@Test
	public void updateProposalShouldReturnForbiddenWhenExistingIdAndRoleConsultorNotOwner() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + consultorToken))
			.andExpect(status().isForbidden());
	}

	@Test
	public void updateProposalShouldReturnProposalDataDTOWhenExistingIdAndRoleConsultorOwner() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + ownerConsultorToken))
			.andExpect(status().isOk());
	}
	
	@Test
	public void updateProposalShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void updateProposalShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		proposalCreateDto.setCode(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenValueIsNull() throws Exception {
		proposalCreateDto.setValue(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenGenerationIsNull() throws Exception {
		proposalCreateDto.setGeneration(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		proposalCreateDto.setCode(otherCode);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenGenerationIsAfterPayment() throws Exception {
		proposalCreateDto.setGeneration(LocalDate.now());
		proposalCreateDto.setPayment(LocalDate.now().minusDays(1L));
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateCancelProposalShouldReturnProposalDataDTOWhenExistingId() throws Exception {
		mockMvc.perform(put("/proposals/{id}/cancel", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("CANCELADA"))
			.andExpect(jsonPath("$.payment", nullValue()));
	}

	@Test
	public void updateCancelProposalShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(put("/proposals/{id}/cancel", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getProposalsAsCsvShouldReturnResourceWhenUserLogged() throws Exception {
		mockMvc.perform(get("/proposals/csv?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate)
				.header("Authorization", "Bearer " + gestorToken))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;Código;Valor;Data_Geração;Data_Pagamento;Status;Funcionário;Banco;CPF_Cliente;Nome_Cliente")));
	}

	@Test
	public void getProposalsAsCsvShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/proposals/csv?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate))
			.andExpect(status().isUnauthorized());
	}
}
