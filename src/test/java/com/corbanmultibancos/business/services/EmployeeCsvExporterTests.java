package com.corbanmultibancos.business.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.EmployeeUserDTO;

@ExtendWith(SpringExtension.class)
public class EmployeeCsvExporterTests {

	@InjectMocks
	private EmployeeCsvExporterService exporterService;

	private List<EmployeeUserDTO> employeeDtoList;
	private String fileHeader;
	private String customerDtoAsString;

	@BeforeEach
	void setUp() {
		employeeDtoList = List.of(new EmployeeUserDTO(1L, "01234567890", "Jose", null, null, null));
		fileHeader = "ID;CPF;Nome;Usuário;Tipo_Usuário;ID_Equipe;Nome_Equipe";
		customerDtoAsString = "1;01234567890;Jose;;;";
	}

	@Test
	public void writeEmployeeAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeEmployeeAsBytes(employeeDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(customerDtoAsString));
	}
}
