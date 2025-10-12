package com.corbanmultibancos.business.controllers;

import java.net.URI;

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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.services.EmployeeService;

import jakarta.validation.Valid;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content),
        @ApiResponse(responseCode = "403", description = "Logged user does not have permission", content = @Content)
})
@Tag(name = "Employees", description = "Employee endpoints")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

    @Operation(summary = "Get an employee by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric employee id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Employee found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<EmployeeUserDTO> getEmployeeById(@PathVariable Long id) {
		EmployeeUserDTO employeeDto = employeeService.getEmployeeById(id);
		return ResponseEntity.ok(employeeDto);
	}

    @Operation(summary = "Get a list of employees",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "cpf", description = "11 digit cpf", required = false, example = "01234567890"),
                    @Parameter(name = "name", description = "partial employee name", required = false, example = "jo")
            },
            responses = {
                    @ApiResponse(description = "List of employees found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(produces = "application/json")
	public ResponseEntity<Page<EmployeeUserDTO>> getEmployees(@RequestParam(defaultValue = "") String cpf,
														@RequestParam(defaultValue = "") String name,
														@ParameterObject Pageable pageable) {
		Page<EmployeeUserDTO> page = employeeService.getEmployees(cpf, name, pageable);
		return ResponseEntity.ok(page);
	}


    @Operation(summary = "CSV file of employees",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "cpf", description = "11 digit cpf", required = false, example = "01234567890"),
                    @Parameter(name = "name", description = "partial employee name", required = false, example = "jo")
            },
            responses = {
                    @ApiResponse(description = "File of employees found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getEmployeesAsCsv(@RequestParam(defaultValue = "") String cpf,
												@RequestParam(defaultValue = "") String name){
		byte[] csvData = employeeService.getEmployeesAsCsvData(cpf, name);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=employees.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new employee",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Employee created", responseCode = "201"),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping(produces = "application/json")
	public ResponseEntity<EmployeeCreateDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO employeeDto) {
		employeeDto = employeeService.createEmployee(employeeDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(employeeDto.getId());
		return ResponseEntity.created(uri).body(employeeDto);
	}

    @Operation(summary = "Update an employee by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric employee id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Employee updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<EmployeeCreateDTO> updateEmployee(@PathVariable Long id,
														@Valid @RequestBody EmployeeCreateDTO employeeDto) {
		employeeDto = employeeService.updateEmployee(id, employeeDto);
		return ResponseEntity.ok(employeeDto);
	}
}
