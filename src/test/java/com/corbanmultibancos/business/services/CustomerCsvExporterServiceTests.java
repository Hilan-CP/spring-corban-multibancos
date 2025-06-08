package com.corbanmultibancos.business.services;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.CustomerDTO;

@ExtendWith(SpringExtension.class)
public class CustomerCsvExporterServiceTests {

	@InjectMocks
	private CustomerCsvExporterService exporterService;

	private List<CustomerDTO> customerDtoList;
	private String fileHeader;
	private String customerDtoAsString;

	@BeforeEach
	void setUp() {
		customerDtoList = List.of(new CustomerDTO(1L, "01234567890", "Ana", "44987654321", LocalDate.of(1999, 1, 1)));
		fileHeader = "ID;CPF;Nome;Telefone;Nascimento";
		customerDtoAsString = "1;01234567890;Ana;44987654321;1999-01-01";
	}

	@Test
	public void writeCustomerAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeCustomerAsBytes(customerDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(customerDtoAsString));
	}
}
