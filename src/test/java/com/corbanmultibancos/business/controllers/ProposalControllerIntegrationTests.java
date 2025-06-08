package com.corbanmultibancos.business.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProposalControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long nonExistingId;
	private String existingCode;
	private String nonExistingCode;
	private String otherCode;
	private String partialEmployeeName;
	private Integer bankCode;
	private String dateField;
	private LocalDate beginDate;
	private LocalDate endDate;
	private ProposalCreateDTO proposalCreateDto;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 10000L;
		existingCode = "993";
		nonExistingCode = "nao existe";
		otherCode = "1430";
		partialEmployeeName = "jo";
		bankCode = 623;
		dateField = "generation";
		beginDate = LocalDate.of(2025, 1, 1);
		endDate = LocalDate.now();
		proposalCreateDto = new ProposalCreateDTO(null, "123", 100.0, LocalDate.now(), null, 1L, 1L, 1L);
	}

	@Test
	public void getProposalByIdShouldReturnProposalDataDTOWhenExistingId() throws Exception {
		mockMvc.perform(get("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
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
	public void getProposalByIdShouldReturnNotFoundWhenNonExistingId() throws Exception {
		mockMvc.perform(get("/proposals/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getProposalsShouldReturnPageOfSingleProposalDataDTOWhenExistingCode() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&dateField={field}&beginDate={begin}&endDate={end}",
				existingCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty())
			.andExpect(jsonPath("$.numberOfElements").value(1));
	}

	@Test
	public void getProposalsShouldReturnEmptyPageWhenNonExistingCode() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&dateField={field}&beginDate={begin}&endDate={end}",
				nonExistingCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndBankCodeNotNull() throws Exception {
		mockMvc.perform(get("/proposals?bankCode={bankCode}&dateField={field}&beginDate={begin}&endDate={end}",
				bankCode, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndEmployeeNameNotNull() throws Exception {
		mockMvc.perform(get("/proposals?employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateNotNull() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isNotEmpty());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenDateIsNull() throws Exception {
		mockMvc.perform(get("/proposals")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenInvalidDateField() throws Exception {
		mockMvc.perform(get("/proposals?dateField={field}&beginDate={begin}&endDate={end}",
				"cancel", beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenBankCodeAndEmployeeNameNotNull() throws Exception {
		mockMvc.perform(get("/proposals?bankCode={bankCode}&employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				bankCode, partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getProposalsShouldReturnBadRequestWhenAllParamters() throws Exception {
		mockMvc.perform(get("/proposals?code={code}&bankCode={bankCode}&employeeName={employeeName}&dateField={field}&beginDate={begin}&endDate={end}",
				existingCode, bankCode, partialEmployeeName, dateField, beginDate, endDate)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createProposalShouldReturnProposalDataDTOWhenValidData() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	public void createProposalShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		proposalCreateDto.setCode(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenValueIsNull() throws Exception {
		proposalCreateDto.setValue(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenGenerationDateIsNull() throws Exception {
		proposalCreateDto.setGeneration(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenEmployeeIdIsNull() throws Exception {
		proposalCreateDto.setEmployeeId(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenCustomerIdIsNull() throws Exception {
		proposalCreateDto.setCustomerId(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenBankIdIsNull() throws Exception {
		proposalCreateDto.setBankId(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void createProposalShouldReturnNotFoundWhenNonExistingEmployee() throws Exception {
		proposalCreateDto.setEmployeeId(100000L);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createProposalShouldReturnNotFoundWhenNonExistingCustomer() throws Exception {
		proposalCreateDto.setCustomerId(100000L);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createProposalShouldReturnNotFoundWhenNonExistingBank() throws Exception {
		proposalCreateDto.setBankId(100000L);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void createProposalShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		proposalCreateDto.setCode(otherCode);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(post("/proposals")
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
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
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnProposalDataDTOWhenExistingId() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
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
	public void updateProposalShouldReturnNotFoundWhenNonExistingId() throws Exception {
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenCodeIsNull() throws Exception {
		proposalCreateDto.setCode(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenValueIsNull() throws Exception {
		proposalCreateDto.setValue(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenGenerationIsNull() throws Exception {
		proposalCreateDto.setGeneration(null);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void updateProposalShouldReturnUnprocessableEntityWhenUniqueCodeViolation() throws Exception {
		proposalCreateDto.setCode(otherCode);
		String proposalJson = objectMapper.writeValueAsString(proposalCreateDto);
		mockMvc.perform(put("/proposals/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)
				.content(proposalJson)
				.contentType(MediaType.APPLICATION_JSON))
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
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void getProposalsAsCsvShouldReturnResource() throws Exception {
		mockMvc.perform(get("/proposals/csv?dateField={field}&beginDate={begin}&endDate={end}",
				dateField, beginDate, endDate))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
			.andExpect(content().contentType("text/csv;charset=UTF-8"))
			.andExpect(content().string(containsString("ID;Código;Valor;Data_Geração;Data_Pagamento;Status;Funcionário;Banco;CPF_Cliente;Nome_Cliente")))
			.andDo(print());
	}
}
