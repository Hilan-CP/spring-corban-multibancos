package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.dto.TeamDTO;

@Service
public class EmployeeCsvExporterService {

	public byte[] writeEmployeeAsBytes(List<EmployeeUserDTO> employeeDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;CPF;Nome;Usuário;Tipo_Usuário;ID_Equipe;Nome_Equipe");
		for (EmployeeUserDTO employeeDto : employeeDtoList) {
			writer.println(fieldsSeparatedBySemicolon(employeeDto));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
	}

	private String fieldsSeparatedBySemicolon(EmployeeUserDTO employee) {
		TeamDTO team = employee.getTeam() == null ? new TeamDTO() : employee.getTeam();
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(employee.getId(), ""));
		joiner.add(Objects.toString(employee.getCpf(), ""));
		joiner.add(Objects.toString(employee.getName(), ""));
		joiner.add(Objects.toString(employee.getUsername(), ""));
		joiner.add(Objects.toString(employee.getRoleName(), ""));
		joiner.add(Objects.toString(team.getId(), ""));
		joiner.add(Objects.toString(team.getName(), ""));
		return joiner.toString();
	}
}
