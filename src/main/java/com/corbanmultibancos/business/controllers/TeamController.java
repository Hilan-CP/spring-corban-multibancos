package com.corbanmultibancos.business.controllers;

import java.net.URI;
import java.util.List;

import com.corbanmultibancos.business.controllers.exceptions.CustomError;
import com.corbanmultibancos.business.controllers.exceptions.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content),
        @ApiResponse(responseCode = "403", description = "Logged user does not have permission", content = @Content)
})
@Tag(name = "Teams", description = "Team endpoints")
@RestController
@RequestMapping("/teams")
public class TeamController {

	@Autowired
	private TeamService teamService;

    @Operation(summary = "Get a single team by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric team id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Team found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
		TeamDTO teamDto = teamService.getTeamById(id);
		return ResponseEntity.ok(teamDto);
	}

    @Operation(summary = "Get a list of teams",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "name", description = "partial team name", required = false, example = "nior"),
            responses = {
                    @ApiResponse(description = "List of teams found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<TeamDTO>> getTeams(@RequestParam(defaultValue = "") String name) {
		List<TeamDTO> teamDtoList = teamService.getTeams(name);
		return ResponseEntity.ok(teamDtoList);
	}

    @Operation(summary = "CSV file of teams",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "name", description = "partial team name", required = false, example = "nior"),
            responses = {
                    @ApiResponse(description = "File of teams found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getTeamsAsCsv(@RequestParam(defaultValue = "") String name){
		byte[] csvData = teamService.getTeamsAsCsvData(name);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=teams.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new team",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Team created", responseCode = "201"),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping(produces = "application/json")
	public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDto) {
		teamDto = teamService.createTeam(teamDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(teamDto.getId());
		return ResponseEntity.created(uri).body(teamDto);
	}

    @Operation(summary = "Update a team by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric team id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Team updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamDTO teamDto) {
		teamDto = teamService.updateTeam(id, teamDto);
		return ResponseEntity.ok(teamDto);
	}

    @Operation(summary = "Delete a team by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric team id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Team found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
		teamService.deleteTeam(id);
		return ResponseEntity.noContent().build();
	}
}
