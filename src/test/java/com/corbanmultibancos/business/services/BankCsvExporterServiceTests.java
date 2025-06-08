package com.corbanmultibancos.business.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.BankDTO;

@ExtendWith(SpringExtension.class)
public class BankCsvExporterServiceTests {

	@InjectMocks
	private BankCsvExporterService exporterService;

	private List<BankDTO> bankDtoList;
	private String bankDtoAsString;
	private String fileHeader;

	@BeforeEach
	void setUp() {
		bankDtoList = List.of(new BankDTO(1L, 123, "ABC"));
		bankDtoAsString = "1;123;ABC";
		fileHeader = "ID;Código;Nome";
	}

	@Test
	public void writeBanksAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeBanksAsBytes(bankDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(bankDtoAsString));
	}
}
