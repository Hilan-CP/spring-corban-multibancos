package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.UserDataDTO;

@Service
public class UserCsvExporterService {

	public byte[] writeUsersAsBytes(List<UserDataDTO> userDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID_Funcionário;Usuário;ID_Tipo;Tipo_Usuário");
		for (UserDataDTO userDto : userDtoList) {
			writer.println(fieldsSeparatedBySemicolon(userDto));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
	}

	private String fieldsSeparatedBySemicolon(UserDataDTO user) {
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(user.getEmployeeId(), ""));
		joiner.add(Objects.toString(user.getUsername(), ""));
		joiner.add(Objects.toString(user.getRole().getId(), ""));
		joiner.add(Objects.toString(user.getRole().getAuthority(), ""));
		return joiner.toString();
	}
}
