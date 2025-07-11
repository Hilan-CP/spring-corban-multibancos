package com.corbanmultibancos.business.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.services.TeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/teams")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping("/{id}")
	public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
		TeamDTO teamDto = teamService.getTeamById(id);
		return ResponseEntity.ok(teamDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping
	public ResponseEntity<List<TeamDTO>> getTeams(@RequestParam(defaultValue = "") String name) {
		List<TeamDTO> teamDtoList = teamService.getTeams(name);
		return ResponseEntity.ok(teamDtoList);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping("/csv")
	public ResponseEntity<Resource> getTeamsAsCsv(@RequestParam(defaultValue = "") String name){
		byte[] csvData = teamService.getTeamsAsCsvData(name);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=teams.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping
	public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDto) {
		teamDto = teamService.createTeam(teamDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(teamDto.getId());
		return ResponseEntity.created(uri).body(teamDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping("/{id}")
	public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamDTO teamDto) {
		teamDto = teamService.updateTeam(id, teamDto);
		return ResponseEntity.ok(teamDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
		teamService.deleteTeam(id);
		return ResponseEntity.noContent().build();
	}
}
