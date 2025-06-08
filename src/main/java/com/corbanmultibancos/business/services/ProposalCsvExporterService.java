package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.ProposalDataDTO;

@Service
public class ProposalCsvExporterService {

	public byte[] writeProposalsAsBytes(List<ProposalDataDTO> proposalDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;Código;Valor;Data_Geração;Data_Pagamento;Status;Funcionário;Banco;CPF_Cliente;Nome_Cliente");
		for(ProposalDataDTO proposalDto : proposalDtoList) {
			writer.println(fieldsSeparatedBySemicolon(proposalDto));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
	}

	private String fieldsSeparatedBySemicolon(ProposalDataDTO proposal) {
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(proposal.getId(), ""));
		joiner.add(Objects.toString(proposal.getCode(), ""));
		joiner.add(Objects.toString(proposal.getValue(), ""));
		joiner.add(Objects.toString(proposal.getGeneration(), ""));
		joiner.add(Objects.toString(proposal.getPayment(), ""));
		joiner.add(Objects.toString(proposal.getStatus(), ""));
		joiner.add(Objects.toString(proposal.getEmployeeName(), ""));
		joiner.add(Objects.toString(proposal.getBankName(), ""));
		joiner.add(Objects.toString(proposal.getCustomerCpf(), ""));
		joiner.add(Objects.toString(proposal.getCustomerName(), ""));
		return joiner.toString();
	}
}
