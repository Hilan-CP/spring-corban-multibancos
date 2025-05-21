package com.corbanmultibancos.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.repositories.TeamRepository;

@Service
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	@Transactional(readOnly = true)
	public TeamDTO getTeamById(Long id) {
		return null;
	}

	@Transactional(readOnly = true)
	public List<TeamDTO> getTeams(String name) {
		return null;
	}

	@Transactional
	public TeamDTO createTeam(TeamDTO teamDto) {
		return null;
	}

	@Transactional
	public TeamDTO updateTeam(Long id, TeamDTO teamDto) {
		return null;
	}

	@Transactional
	public TeamDTO deleteTeam(Long id) {
		return null;
	}
}
