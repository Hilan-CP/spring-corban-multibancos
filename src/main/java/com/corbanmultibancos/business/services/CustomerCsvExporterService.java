package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.CustomerDTO;

@Service
public class CustomerCsvExporterService {

	public byte[] writeCustomerAsBytes(List<CustomerDTO> customerDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;CPF;Nome;Telefone;Nascimento");
		for (CustomerDTO customerDto : customerDtoList) {
			writer.println(fieldsSeparatedBySemicolon(customerDto));
		}
		writer.flush();
		writer.flush();
		return inMemoryOutput.toByteArray();
	}

	private String fieldsSeparatedBySemicolon(CustomerDTO customer) {
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(customer.getId(), ""));
		joiner.add(Objects.toString(customer.getCpf(), ""));
		joiner.add(Objects.toString(customer.getName(), ""));
		joiner.add(Objects.toString(customer.getPhone(), ""));
		joiner.add(Objects.toString(customer.getBirthDate(), ""));
		return joiner.toString();
	}
}
