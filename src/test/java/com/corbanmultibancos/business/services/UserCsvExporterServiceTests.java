package com.corbanmultibancos.business.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.RoleDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;

@ExtendWith(SpringExtension.class)
public class UserCsvExporterServiceTests {

	@InjectMocks
	private UserCsvExporterService exporterService;

	private List<UserDataDTO> userDtoList;
	private String fileHeader;
	private String userDtoAsString;

	@BeforeEach
	void setUp() {
		userDtoList = List.of(new UserDataDTO("florinda", 1L, new RoleDTO(1L, "GESTOR")));
		fileHeader = "ID_Funcionário;Usuário;ID_Tipo;Tipo_Usuário";
		userDtoAsString = "1;florinda;1;GESTOR";
	}

	@Test
	public void writeUsersAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeUsersAsBytes(userDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(userDtoAsString));
	}
}
