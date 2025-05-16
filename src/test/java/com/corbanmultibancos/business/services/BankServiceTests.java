package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class BankServiceTests {
	
	@InjectMocks
	private BankService bankService;
	
	@Mock
	private BankRepository bankRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Integer existingCode;
	private Integer nonExistingCode;
	private String partialName;
	private String nonExistingName;
	private Bank bankEntity;
	private List<Bank> bankList;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		existingCode = 623;
		nonExistingCode = 999;
		partialName = "pa";
		nonExistingName = "banco inexistente";
		bankEntity = new Bank(existingId, existingCode, "PAN");
		bankList = List.of(bankEntity);
		Mockito.when(bankRepository.findById(existingId)).thenReturn(Optional.of(bankEntity));
		Mockito.when(bankRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(bankRepository.findByCode(existingCode)).thenReturn(Optional.of(bankEntity));
		Mockito.when(bankRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());
		Mockito.when(bankRepository.findByName(partialName)).thenReturn(bankList);
		Mockito.when(bankRepository.findByName(nonExistingName)).thenReturn(List.of());
		Mockito.when(bankRepository.findAll()).thenReturn(bankList);
		Mockito.when(bankRepository.save(any())).thenReturn(bankEntity);
		Mockito.when(bankRepository.getReferenceById(existingId)).thenReturn(bankEntity);
		Mockito.when(bankRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
	}
	
	@Test
	public void findByIdShouldReturnBankDTOWhenExistingId() {
		BankDTO bankDto = bankService.findById(existingId);
		Assertions.assertEquals(existingId, bankDto.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> bankService.findById(nonExistingId));
	}
	
	@Test
	public void findByCodeShouldReturnBankDTOWhenExistingCode() {
		BankDTO bankDto = bankService.findByCode(existingCode);
		Assertions.assertEquals(existingCode, bankDto.getCode());
	}
	
	@Test
	public void findByCodeShouldThrowResourceNotFoundExceptionWhenNonExistingCode() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> bankService.findByCode(nonExistingCode));
	}
	
	@Test
	public void findByNameShouldReturnBankDTOListWhenPartialName() {
		List<BankDTO> bankDtoList = bankService.findByName(partialName);
		Assertions.assertFalse(bankDtoList.isEmpty());
	}
	
	@Test
	public void findByNameShouldReturnBankDTOListWhenNonExistingName() {
		List<BankDTO> bankDtoList = bankService.findByName(nonExistingName);
		Assertions.assertTrue(bankDtoList.isEmpty());
	}
	
	@Test
	public void findAllShouldReturnBankDTOList() {
		List<BankDTO> bankDtoList = bankService.findAll();
		Assertions.assertFalse(bankDtoList.isEmpty());
	}
	
	@Test
	public void insertShouldReturnBankDTO() {
		BankDTO bankDto = bankService.insert(new BankDTO());
		Assertions.assertNotNull(bankDto.getId());
	}
	
	@Test
	public void updateShouldReturnBankDTOWhenExistingId() {
		BankDTO bankDto = bankService.update(existingId, new BankDTO());
		Assertions.assertEquals(existingId, bankDto.getId());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> bankService.update(nonExistingId, new BankDTO()));
	}
}
