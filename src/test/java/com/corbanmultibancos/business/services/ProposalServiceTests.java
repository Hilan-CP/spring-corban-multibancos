package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.entities.ProposalStatus;
import com.corbanmultibancos.business.mappers.ProposalMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.ProposalRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProposalServiceTests {

	@InjectMocks
	private ProposalService proposalService;

	@Mock
	private ProposalRepository proposalRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private BankRepository bankRepository;
	
	@Mock
	private ProposalCsvExporterService exporterService;

	private Long existingId;
	private Long nonExistingId;
	private String existingCode;
	private String nonExistingCode;
	private String partialEmployeeName;
	private Integer bankCode;
	private String dateFieldName;
	private LocalDate beginDate;
	private LocalDate endDate;
	private Proposal proposalEntity;
	private Employee employee;
	private Bank bank;
	private Customer customer;
	private Page<ProposalDataDTO> proposalDtoPage;
	private ProposalCreateDTO proposalCreateDto;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 1000L;
		existingCode = "993";
		nonExistingCode = "does not exist";
		partialEmployeeName = "jo";
		bankCode = 623;
		dateFieldName = "generation";
		beginDate = LocalDate.of(2025, 6, 1);
		endDate = LocalDate.of(2025, 6, 1);
		employee = new Employee(existingId, "67661033020", "Jose", null, null);
		bank = new Bank(existingId, bankCode, "PAN");
		customer = new Customer(existingId, "00066098645", "Sergio", "44987654321", LocalDate.now());
		proposalEntity = new Proposal(existingId, existingCode, 1000.0, beginDate, endDate, ProposalStatus.CONTRATADA,
				employee, customer, bank);
		proposalDtoPage = new PageImpl<>(List.of(ProposalMapper.toProposalDataDto(proposalEntity)));
		proposalCreateDto = new ProposalCreateDTO(null, "123ABC", 100.0, beginDate, endDate, employee.getId(),
				customer.getId(), bank.getId());
		pageable = PageRequest.of(0, 10);
		Mockito.when(proposalRepository.findById(existingId)).thenReturn(Optional.of(proposalEntity));
		Mockito.when(proposalRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(proposalRepository.getReferenceById(existingId)).thenReturn(proposalEntity);
		Mockito.when(proposalRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(proposalRepository.save(any())).thenReturn(proposalEntity);
		Mockito.when(proposalRepository.findByCode(existingCode)).thenReturn(Optional.of(proposalEntity));
		Mockito.when(proposalRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());
		Mockito.when(proposalRepository.findBy(anyMap(), any(Pageable.class))).thenReturn(proposalDtoPage);
		Mockito.when(bankRepository.findById(existingId)).thenReturn(Optional.of(bank));
		Mockito.when(bankRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Mockito.when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
		Mockito.when(customerRepository.findById(nonExistingId)).thenReturn(Optional.empty());
	}

	@Test
	public void getProposalByIdShouldReturnProposalDataDTOWhenExistingId() {
		ProposalDataDTO proposalDto = proposalService.getProposalById(existingId);
		Assertions.assertEquals(existingId, proposalDto.getId());
	}

	@Test
	public void getProposalByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> proposalService.getProposalById(nonExistingId));
	}

	@Test
	public void getProposalsShouldReturnPageOfSingleProposalDataDTOWhenExistingCode() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals(existingCode, "", 0, dateFieldName, beginDate,
				endDate, pageable);
		Assertions.assertEquals(1, dtoPage.getSize());
	}

	@Test
	public void getProposalsShouldReturnEmptyPageWhenNonExistingCode() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals(nonExistingCode, "", 0, dateFieldName, beginDate,
				endDate, pageable);
		Assertions.assertTrue(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndBankCodeNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", bankCode, dateFieldName, beginDate,
				endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndEmployeeNameNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", partialEmployeeName, 0, dateFieldName,
				beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", 0, dateFieldName, beginDate, endDate,
				pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenBankCodeAndEmployeNameNotNull() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> proposalService.getProposals("", partialEmployeeName, bankCode, dateFieldName, beginDate, endDate, pageable));
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenDateIsNull() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> proposalService.getProposals("", "", bankCode, dateFieldName, null, null, pageable));
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenInvalidDateField() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> proposalService.getProposals("", "", bankCode, "invalid field name", beginDate, endDate, pageable));
	}
	
	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenBeginDateIsAfterEndDate() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> proposalService.getProposals("", "", bankCode, "invalid field name", endDate.plusDays(1L), endDate, pageable));
	}

	@Test
	public void createProposalShouldReturnProposalDataDTOWhenValidData() {
		ProposalDataDTO proposalDto = proposalService.createProposal(proposalCreateDto);
		Assertions.assertNotNull(proposalDto.getId());
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingEmployee() {
		proposalCreateDto.setEmployeeId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingBank() {
		proposalCreateDto.setBankId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingCustomer() {
		proposalCreateDto.setCustomerId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void updateProposalShouldReturnProposalDataDTOWhenExistingId() {
		ProposalDataDTO proposalDto = proposalService.updateProposal(existingId, proposalCreateDto);
		Assertions.assertNotNull(proposalDto.getId());
	}

	@Test
	public void updateProposalShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> proposalService.updateProposal(nonExistingId, proposalCreateDto));
	}

	@Test
	public void updateCancelProposalShouldReturnProposalDataDTOWhenExistingId() {
		ProposalDataDTO proposalDto = proposalService.updateCancelProposal(existingId);
		Assertions.assertEquals(null, proposalDto.getPayment());
		Assertions.assertEquals(ProposalStatus.CANCELADA, proposalDto.getStatus());
	}

	@Test
	public void updateCancelProposalShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> proposalService.updateCancelProposal(nonExistingId));
	}

	@Test
	public void getProposalsAsCsvDataShouldReturnByteArray() {
		Page<ProposalDataDTO> page = new PageImpl<>(List.of(ProposalMapper.toProposalDataDto(proposalEntity)));
		String data = String.join("\n", "ID;Código;Valor;Data_Geração;Data_Pagamento;Status;Funcionário;Banco;CPF_Cliente;Nome_Cliente",
				"1;993;1000.0;2025-06-01;2025-06-01;CONTRATADA;Jose;PAN;00066098645;Sergio");
		ProposalService serviceSpy = Mockito.spy(proposalService);
		Mockito.doReturn(page).when(serviceSpy).getProposals("", "", 0, dateFieldName, beginDate, endDate, Pageable.unpaged());
		Mockito.doReturn(data.getBytes()).when(exporterService).writeProposalsAsBytes(anyList());
		byte[] csvData = serviceSpy.getProposalsAsCsvData("", "", 0, dateFieldName, beginDate, endDate);
		Assertions.assertEquals(data, new String(csvData));
	}
}
