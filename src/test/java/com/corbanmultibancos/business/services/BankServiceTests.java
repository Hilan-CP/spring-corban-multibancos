package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;
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
import com.corbanmultibancos.business.mappers.BankMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
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
	public void getBankByIdShouldReturnBankDTOWhenExistingId() {
		BankDTO bankDto = bankService.getBankById(existingId);
		Assertions.assertEquals(existingId, bankDto.getId());
	}

	@Test
	public void getBankByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> bankService.getBankById(nonExistingId));
	}

	@Test
	public void getBanksShouldReturnListOfSingleBankDTOWhenExistingCode() {
		List<BankDTO> bankDtoList = bankService.getBanks(existingCode, "");
		Assertions.assertEquals(1, bankDtoList.size());
		Mockito.verify(bankRepository, times(1)).findByCode(any());
		Mockito.verify(bankRepository, never()).findByName(any());
		Mockito.verify(bankRepository, never()).findAll();
	}

	@Test
	public void getBanksShouldReturnEmptyListWhenNonExistingCode() {
		List<BankDTO> bankDtoList = bankService.getBanks(nonExistingCode, "");
		Assertions.assertTrue(bankDtoList.isEmpty());
		Mockito.verify(bankRepository, times(1)).findByCode(any());
		Mockito.verify(bankRepository, never()).findByName(any());
		Mockito.verify(bankRepository, never()).findAll();
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenPartialName() {
		List<BankDTO> bankDtoList = bankService.getBanks(0, partialName);
		Assertions.assertFalse(bankDtoList.isEmpty());
		Mockito.verify(bankRepository, never()).findByCode(any());
		Mockito.verify(bankRepository, times(1)).findByName(any());
		Mockito.verify(bankRepository, never()).findAll();
	}

	@Test
	public void getBanksShouldReturnEmptyListWhenNonExistingName() {
		List<BankDTO> bankDtoList = bankService.getBanks(0, nonExistingName);
		Assertions.assertTrue(bankDtoList.isEmpty());
		Mockito.verify(bankRepository, never()).findByCode(any());
		Mockito.verify(bankRepository, times(1)).findByName(any());
		Mockito.verify(bankRepository, never()).findAll();
	}

	@Test
	public void getBanksShouldReturnBankDTOListWhenNoParameter() {
		List<BankDTO> bankDtoList = bankService.getBanks(0, "");
		Assertions.assertFalse(bankDtoList.isEmpty());
		Mockito.verify(bankRepository, never()).findByCode(any());
		Mockito.verify(bankRepository, never()).findByName(any());
		Mockito.verify(bankRepository, times(1)).findAll();
	}

	@Test
	public void getBanksShouldThrowIllegalParameterExceptionWhenBothParameters() {
		Assertions.assertThrows(IllegalParameterException.class, () -> bankService.getBanks(existingCode, partialName));
	}

	@Test
	public void createBankShouldReturnBankDTO() {
		BankDTO bankDto = bankService.createBank(new BankDTO());
		Assertions.assertNotNull(bankDto.getId());
	}

	@Test
	public void updateBankShouldReturnBankDTOWhenExistingId() {
		BankDTO bankDto = bankService.updateBank(existingId, new BankDTO());
		Assertions.assertEquals(existingId, bankDto.getId());
	}

	@Test
	public void updateBankShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> bankService.updateBank(nonExistingId, new BankDTO()));
	}

	@Test
	public void getBanksAsCsvDataShouldReturnByteArray() {
		List<BankDTO> bankDtoList = List.of(BankMapper.toDto(bankEntity));
		BankService serviceSpy = Mockito.spy(bankService);
		Mockito.doReturn(bankDtoList).when(serviceSpy).getBanks(0, "");
		byte[] csvData = serviceSpy.getBanksAsCsvData(0, "");
		String content = new String(csvData, StandardCharsets.UTF_8);
		Assertions.assertNotNull(csvData);
		Assertions.assertTrue(content.contains("ID;Código;Nome"));
		Assertions.assertTrue(content.contains(bankDtoList.get(0).getId().toString()));
		Assertions.assertTrue(content.contains(bankDtoList.get(0).getCode().toString()));
		Assertions.assertTrue(content.contains(bankDtoList.get(0).getName()));
	}
}
