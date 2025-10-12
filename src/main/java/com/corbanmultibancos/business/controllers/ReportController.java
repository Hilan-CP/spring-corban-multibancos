package com.corbanmultibancos.business.controllers;

import java.util.List;

import com.corbanmultibancos.business.controllers.exceptions.CustomError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.ReportTotalDTO;
import com.corbanmultibancos.business.services.ReportService;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content),
        @ApiResponse(responseCode = "403", description = "Logged user does not have permission", content = @Content)
})
@Tag(name = "Report", description = "Report endpoints")
@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

    @Operation(summary = "Get daily report",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "teamIds", description = "list of team id", required = true, example = "1,2,3"),
            responses = @ApiResponse(description = "Report generated", responseCode = "200"))
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(produces = "application/json")
	public ResponseEntity<ReportTotalDTO> getReport(@RequestParam(required = true) List<Long> teamIds) {
		ReportTotalDTO report = reportService.getReport(teamIds);
		return ResponseEntity.ok(report);
	}
}
