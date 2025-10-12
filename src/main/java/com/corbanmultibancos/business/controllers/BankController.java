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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.services.BankService;

import jakarta.validation.Valid;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content),
        @ApiResponse(responseCode = "403", description = "Logged user does not have permission", content = @Content)
})
@Tag(name = "Banks", description = "Bank endpoints")
@RestController
@RequestMapping("/banks")
public class BankController {

	@Autowired
	private BankService bankService;

    @Operation(summary = "Get a single bank by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric bank id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Bank found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<BankDTO> getBankById(@PathVariable Long id) {
		BankDTO bankDto = bankService.getBankById(id);
		return ResponseEntity.ok(bankDto);
	}

    @Operation(summary = "Get a list of banks",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "code", description = "unique numeric bank code", required = false, example = "336"),
                    @Parameter(name = "name", description = "partial bank name", required = false, example = "pa")
            },
            responses = {
                    @ApiResponse(description = "List of banks found (can be empty)", responseCode = "200")})
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<BankDTO>> getBanks(@RequestParam(defaultValue = "0") Integer code,
												@RequestParam(defaultValue = "") String name) {
		List<BankDTO> bankDtoList = bankService.getBanks(code, name);
		return ResponseEntity.ok(bankDtoList);
	}

    @Operation(summary = "CSV file of banks",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "code", description = "unique numeric bank code", required = false, example = "336"),
                    @Parameter(name = "name", description = "partial bank name", required = false, example = "pa")
            },
            responses = {
                    @ApiResponse(description = "File of banks found (can be empty)", responseCode = "200")})
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getBanksAsCsv(@RequestParam(defaultValue = "0") Integer code,
												@RequestParam(defaultValue = "") String name){
		byte[] csvData = bankService.getBanksAsCsvData(code, name);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=banks.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new bank",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Bank created", responseCode = "201"),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping(produces = "application/json")
	public ResponseEntity<BankDTO> createBank(@Valid @RequestBody BankDTO bankDto) {
		bankDto = bankService.createBank(bankDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(bankDto.getId());
		return ResponseEntity.created(uri).body(bankDto);
	}

    @Operation(summary = "Update a bank by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric bank id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Bank updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<BankDTO> updateBank(@PathVariable Long id, @Valid @RequestBody BankDTO bankDto) {
		bankDto = bankService.updateBank(id, bankDto);
		return ResponseEntity.ok(bankDto);
	}
}
