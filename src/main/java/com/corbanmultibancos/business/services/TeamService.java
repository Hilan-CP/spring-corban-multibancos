package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.entities.Team;
import com.corbanmultibancos.business.mappers.TeamMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.TeamRepository;
import com.corbanmultibancos.business.services.exceptions.DataIntegrityException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;
import com.corbanmultibancos.business.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private TeamCsvExporterService exporterService;

	@Transactional(readOnly = true)
	public TeamDTO getTeamById(Long id) {
		Optional<Team> result = teamRepository.findById(id);
		Team team = result.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.TEAM_NOT_FOUND));
		return TeamMapper.toDto(team);
	}

	@Transactional(readOnly = true)
	public List<TeamDTO> getTeams(String name) {
		List<Team> teamList;
		if(!name.isBlank()) {
			teamList = teamRepository.findByNameContainingIgnoreCase(name);
		}
		else {
			teamList = teamRepository.findAll();
		}
		return teamList.stream().map(team -> TeamMapper.toDto(team)).toList();
	}

	public byte[] getTeamsAsCsvData(String name) {
		List<TeamDTO> teamDtoList = getTeams(name);
		return exporterService.writeTeamsAsBytes(teamDtoList);
	}

	@Transactional
	public TeamDTO createTeam(TeamDTO teamDto) {
		Team team = new Team();
		TeamMapper.copyDtoToEntity(teamDto, team);
		team = teamRepository.save(team);
		return TeamMapper.toDto(team);
	}

	@Transactional
	public TeamDTO updateTeam(Long id, TeamDTO teamDto) {
		try {
			Team team = teamRepository.getReferenceById(id);
			TeamMapper.copyDtoToEntity(teamDto, team);
			team = teamRepository.save(team);
			return TeamMapper.toDto(team);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.TEAM_NOT_FOUND);
		}
	}

	@Transactional
	public void deleteTeam(Long id) {
		if(!teamRepository.existsById(id)) {
			throw new ResourceNotFoundException(ErrorMessage.TEAM_NOT_FOUND);
		}
		if(employeeRepository.existsByTeamId(id)) {
			throw new DataIntegrityException(ErrorMessage.FOREIGN_KEY_VIOLATION);
		}
		teamRepository.deleteById(id);
	}
}
