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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.services.CustomerService;

import jakarta.validation.Valid;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content)
})
@Tag(name = "Customers", description = "Customer endpoints")
@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

    @Operation(summary = "Get a customer by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric customer id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Customer found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
		CustomerDTO customerDto = customerService.getCustomerById(id);
		return ResponseEntity.ok(customerDto);
	}

    @Operation(summary = "Get a list of customers",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "cpf", description = "11 digit cpf", required = false, example = "01234567890"),
                    @Parameter(name = "name", description = "partial customer name", required = false, example = "jo"),
                    @Parameter(name = "phone", description = "partial phone", required = false, example = "4498765")
            },
            responses = {
                    @ApiResponse(description = "List of customers found (can be empty)", responseCode = "200")})
	@GetMapping(produces = "application/json")
	public ResponseEntity<Page<CustomerDTO>> getCustomers(@RequestParam(defaultValue = "") String cpf,
											@RequestParam(defaultValue = "") String name,
											@RequestParam(defaultValue = "") String phone,
											@ParameterObject Pageable pageable) {
		Page<CustomerDTO> page = customerService.getCustomers(cpf, name, phone, pageable);
		return ResponseEntity.ok(page);
	}

    @Operation(summary = "CSV file of customers",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "cpf", description = "11 digit cpf", required = false, example = "01234567890"),
                    @Parameter(name = "name", description = "partial customer name", required = false, example = "jo"),
                    @Parameter(name = "phone", description = "partial phone", required = false, example = "4498765")
            },
            responses = {
                    @ApiResponse(description = "File of customers found (can be empty)", responseCode = "200")})
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getCustomersAsCsv(@RequestParam(defaultValue = "") String cpf,
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") String phone){
		byte[] csvData = customerService.getCustomersAsCsvData(cpf, name, phone);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=customers.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new customer",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Customer created", responseCode = "201"),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PostMapping(produces = "application/json")
	public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDto) {
		customerDto = customerService.createCustomer(customerDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(customerDto.getId());
		return ResponseEntity.created(uri).body(customerDto);
	}

    @Operation(summary = "Update a customer by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric customer id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "Customer found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDto) {
		customerDto = customerService.updateCustomer(id, customerDto);
		return ResponseEntity.ok(customerDto);
	}
}
