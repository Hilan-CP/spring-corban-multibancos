package com.corbanmultibancos.business.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.TeamDTO;

@Service
public class TeamCsvExporterService {

	public byte[] writeTeamsAsBytes(List<TeamDTO> teamDtoList) {
		ByteArrayOutputStream inMemoryOutput = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(inMemoryOutput);
		writer.println("ID;Nome");
		for(TeamDTO teamDto : teamDtoList) {
			writer.println(fieldsSeparatedBySemicolon(teamDto));
		}
		writer.flush();
		writer.close();
		return inMemoryOutput.toByteArray();
	}
	
	private String fieldsSeparatedBySemicolon(TeamDTO team) {
		StringJoiner joiner = new StringJoiner(";");
		joiner.add(Objects.toString(team.getId(), ""));
		joiner.add(Objects.toString(team.getName(), ""));
		return joiner.toString();
	}
}
