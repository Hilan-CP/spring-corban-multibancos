package com.corbanmultibancos.business.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.TeamDTO;

@ExtendWith(SpringExtension.class)
public class TeamCsvExporterServiceTests {

	@InjectMocks
	private TeamCsvExporterService exporterService;

	private List<TeamDTO> teamDtoList;
	private String fileHeader;
	private String teamDtoAsString;

	@BeforeEach
	void setUp() {
		teamDtoList = List.of(new TeamDTO(1L, "Temporários"));
		fileHeader = "ID;Nome";
		teamDtoAsString = "1;Temporários";
	}

	@Test
	public void writeTeamsAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeTeamsAsBytes(teamDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(teamDtoAsString));
	}
}
