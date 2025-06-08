package com.corbanmultibancos.business.services;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.ProposalStatus;

@ExtendWith(SpringExtension.class)
public class ProposalCsvExporterServiceTests {

	@InjectMocks
	private ProposalCsvExporterService exporterService;

	private List<ProposalDataDTO> proposalDtoList;
	private String fileHeader;
	private String proposalDtoAsString;

	@BeforeEach
	void setUp() {
		proposalDtoList = List.of(new ProposalDataDTO(1L, "1000", 150.0, LocalDate.of(2025, 6, 1), null,
				ProposalStatus.GERADA, "José", "BMG", "01234567890", "Ronaldo"));
		fileHeader = "ID;Código;Valor;Data_Geração;Data_Pagamento;Status;Funcionário;Banco;CPF_Cliente;Nome_Cliente";
		proposalDtoAsString = "1;1000;150.0;2025-06-01;;GERADA;José;BMG;01234567890;Ronaldo";
	}

	@Test
	public void writeProposalsAsBytesShouldReturnByteArray() {
		byte[] csvData = exporterService.writeProposalsAsBytes(proposalDtoList);
		String result = new String(csvData);
		Assertions.assertTrue(result.contains(fileHeader));
		Assertions.assertTrue(result.contains(proposalDtoAsString));
	}
}
