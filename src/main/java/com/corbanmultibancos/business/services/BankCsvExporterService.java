package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.BankDTO;

@Service
public class BankCsvExporterService {

	public byte[] writeBanksAsBytes(List<BankDTO> bankDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;Código;Nome");
		for (BankDTO bankDto : bankDtoList) {
			writer.println(fieldsSeparatedBySemicolon(bankDto));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
	}

	private String fieldsSeparatedBySemicolon(BankDTO bank) {
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(bank.getId(), ""));
		joiner.add(Objects.toString(bank.getCode(), ""));
		joiner.add(Objects.toString(bank.getName(), ""));
		return joiner.toString();
	}
}
