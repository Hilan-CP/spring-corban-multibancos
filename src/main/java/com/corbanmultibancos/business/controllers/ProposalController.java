package com.corbanmultibancos.business.controllers;

import java.net.URI;
import java.time.LocalDate;

import com.corbanmultibancos.business.controllers.exceptions.CustomError;
import com.corbanmultibancos.business.controllers.exceptions.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.services.ProposalService;
import com.corbanmultibancos.business.validations.CreateGroup;

import jakarta.validation.groups.Default;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content)
})
@Tag(name = "Proposals", description = "Proposal endpoints")
@RestController
@RequestMapping("/proposals")
public class ProposalController {

	@Autowired
	private ProposalService proposalService;

    @Operation(summary = "Get a proposal by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric proposal id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Proposal found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Logged user does not have permission", responseCode = "403", content = @Content)})
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<ProposalDataDTO> getProposalById(@PathVariable Long id){
		ProposalDataDTO proposalDataDto = proposalService.getProposalById(id);
		return ResponseEntity.ok(proposalDataDto);
	}

    @Operation(summary = "Get a proposal by code",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "code", description = "string proposal code", required = true, example = "993abc"),
            responses = {
                    @ApiResponse(description = "Proposal found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Logged user does not have permission", responseCode = "403", content = @Content)})
	@GetMapping(value = "/code/{code}", produces = "application/json")
	public ResponseEntity<ProposalDataDTO> getProposalByCode(@PathVariable String code){
		ProposalDataDTO proposalDataDto = proposalService.getProposalByCode(code);
		return ResponseEntity.ok(proposalDataDto);
	}

    @Operation(summary = "Get a list of proposals",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "employeeName", description = "partial name of the employee", required = false, example = "jo"),
                    @Parameter(name = "bankCode", description = "numeric bank code", required = false, example = "336"),
                    @Parameter(name = "dateField", description = "name of the date field", required = true, examples = {
                            @ExampleObject(value = "generation"),
                            @ExampleObject(value = "payment")
                    }),
                    @Parameter(name = "beginDate", description = "initial date (inclusive)", required = true, example = "2025-01-20"),
                    @Parameter(name = "endDate", description = "final date (inclusive)", required = true, example = "2025-09-23")
            },
            responses = {
                    @ApiResponse(description = "List of proposals found (can be empty)", responseCode = "200")})
	@GetMapping(produces = "application/json")
	public ResponseEntity<Page<ProposalDataDTO>> getProposals(
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(required = true) String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate,
			@ParameterObject Pageable pageable){
		Page<ProposalDataDTO> page = proposalService.getProposals(employeeName, bankCode, dateField, beginDate, endDate, pageable);
		return ResponseEntity.ok(page);
	}

    @Operation(summary = "CSV file of proposals",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "employeeName", description = "partial name of the employee", required = false, example = "jo"),
                    @Parameter(name = "bankCode", description = "numeric bank code", required = false, example = "336"),
                    @Parameter(name = "dateField", description = "name of the date field", required = true, example = "payment"),
                    @Parameter(name = "beginDate", description = "initial date (inclusive)", required = true, example = "2025-01-20"),
                    @Parameter(name = "endDate", description = "final date (inclusive)", required = true, example = "2025-09-23")
            },
            responses = {
                    @ApiResponse(description = "File of proposals found (can be empty)", responseCode = "200")})
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getProposalsAsCsv(@RequestParam(defaultValue = "") String code,
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(required = true) String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate){
		byte[] csvData = proposalService.getProposalsAsCsvData(code, employeeName, bankCode, dateField, beginDate, endDate);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=proposals.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new proposal",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Proposal created", responseCode = "201"),
                    @ApiResponse(description = "Bank or customer were not found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PostMapping(produces = "application/json")
	public ResponseEntity<ProposalDataDTO> createProposal(@Validated(value = {Default.class, CreateGroup.class}) @RequestBody ProposalCreateDTO createDto){
		ProposalDataDTO proposalDataDto = proposalService.createProposal(createDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(proposalDataDto.getId());
		return ResponseEntity.created(uri).body(proposalDataDto);
	}

    @Operation(summary = "Update a proposal by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric proposal id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Proposal updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class))),
                    @ApiResponse(description = "Logged user does not have permission", responseCode = "403", content = @Content)})
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<ProposalDataDTO> updateProposal(@PathVariable Long id, @Validated(Default.class) @RequestBody ProposalCreateDTO createDto){
		ProposalDataDTO proposalDataDto = proposalService.updateProposal(id, createDto);
		return ResponseEntity.ok(proposalDataDto);
	}

    @Operation(summary = "Update a proposal status to canceled",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric proposal id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Proposal updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Logged user does not have permission", responseCode = "403", content = @Content)})
	@PutMapping(value = "/{id}/cancel", produces = "application/json")
	public ResponseEntity<ProposalDataDTO> updateCancelProposal(@PathVariable Long id){
		ProposalDataDTO proposalDataDto = proposalService.updateCancelProposal(id);
		return ResponseEntity.ok(proposalDataDto);
	}
}