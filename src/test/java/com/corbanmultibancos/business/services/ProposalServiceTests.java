package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
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
		beginDate = LocalDate.now();
		endDate = LocalDate.now();
		employee = new Employee(existingId, "67661033020", "Jose", null, null);
		bank = new Bank(existingId, bankCode, "PAN");
		customer = new Customer(existingId, "00066098645", "Sergio", "44987654321", LocalDate.now());
		proposalEntity = new Proposal(existingId, existingCode, 1000.0, beginDate, endDate, ProposalStatus.CONTRATADA,
				employee, customer, bank);
		proposalDtoPage = new PageImpl<>(List.of(ProposalMapper.toProposalDataDto(proposalEntity)));
		proposalCreateDto = new ProposalCreateDTO(null, "123ABC", 100.0, beginDate, endDate,
				employee.getId(), customer.getId(), bank.getId());
		pageable = PageRequest.of(0, 10);
		Mockito.when(proposalRepository.findById(existingId)).thenReturn(Optional.of(proposalEntity));
		Mockito.when(proposalRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(proposalRepository.getReferenceById(existingId)).thenReturn(proposalEntity);
		Mockito.when(proposalRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(proposalRepository.save(any())).thenReturn(proposalEntity);
		Mockito.when(proposalRepository.findByCode(existingCode)).thenReturn(Optional.of(proposalEntity));
		Mockito.when(proposalRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());
		Mockito.when(proposalRepository.findBy(anyMap(), any(Pageable.class))).thenReturn(proposalDtoPage);
		Mockito.when(bankRepository.getReferenceById(existingId)).thenReturn(bank);
		Mockito.when(bankRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(employeeRepository.getReferenceById(existingId)).thenReturn(employee);
		Mockito.when(employeeRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(customerRepository.getReferenceById(existingId)).thenReturn(customer);
		Mockito.when(customerRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
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
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals(existingCode, "", 0, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertEquals(1, dtoPage.getSize());
	}

	@Test
	public void getProposalsShouldReturnEmptyPageWhenNonExistingCode() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals(nonExistingCode, "", 0, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertTrue(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndBankCodeNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", bankCode, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateAndEmployeeNameNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", partialEmployeeName, 0, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnPageOfProposalDataDTOWhenDateNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", 0, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenBankCodeAndEmployeNameNotNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", partialEmployeeName, bankCode, dateFieldName, beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenDateIsNull() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", bankCode, dateFieldName, null, null, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void getProposalsShouldReturnIllegalParameterExceptionWhenInvalidDateField() {
		Page<ProposalDataDTO> dtoPage = proposalService.getProposals("", "", bankCode, "invalid field name", beginDate, endDate, pageable);
		Assertions.assertFalse(dtoPage.isEmpty());
	}

	@Test
	public void createProposalShouldReturnProposalDataDTOWhenValidData() {
		ProposalDataDTO proposalDto = proposalService.createProposal(proposalCreateDto);
		Assertions.assertNotNull(proposalDto.getId());
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingEmployee() {
		proposalCreateDto.setEmployeeId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingBank() {
		proposalCreateDto.setBankId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void createProposalShouldThrowResourceNotFoundExceptionWhenNonExistingCustomer() {
		proposalCreateDto.setCustomerId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> proposalService.createProposal(proposalCreateDto));
	}

	@Test
	public void updateProposalShouldReturnProposalDataDTOWhenExistingId() {
		ProposalDataDTO proposalDto = proposalService.updateProposal(existingId, proposalCreateDto);
		Assertions.assertNotNull(proposalDto.getId());
	}

	@Test
	public void updateProposalShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> proposalService.updateProposal(nonExistingId, proposalCreateDto));
	}
}
