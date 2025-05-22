package com.corbanmultibancos.business.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.TeamDTO;

@RestController
@RequestMapping("/teams")
public class TeamController {

	@GetMapping("/{id}")
	public ResponseEntity<TeamDTO> getTeamById(){
		return null;
	}
	
	@GetMapping
	public ResponseEntity<List<TeamDTO>> getTeams(){
		return null;
	}
	
	@PostMapping
	public ResponseEntity<TeamDTO> createTeam(){
		return null;
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TeamDTO> updateTeam(){
		return null;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTeam(){
		return null;
	}
}
