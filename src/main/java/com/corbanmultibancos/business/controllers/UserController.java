package com.corbanmultibancos.business.controllers;

import com.corbanmultibancos.business.controllers.exceptions.CustomError;
import com.corbanmultibancos.business.controllers.exceptions.ValidationError;
import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;
import com.corbanmultibancos.business.services.UserService;
import com.corbanmultibancos.business.validations.CreateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "No user logged", content = @Content),
        @ApiResponse(responseCode = "403", description = "Logged user does not have permission", content = @Content)
})
@Tag(name = "Users", description = "User endpoints")
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

    @Operation(summary = "Get a single user by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric user id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "User found", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UserDataDTO> getUserById(@PathVariable Long id) {
		UserDataDTO userDto = userService.getUserById(id);
		return ResponseEntity.ok(userDto);
	}

    @Operation(summary = "Get a page of users",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "username", description = "complete username to be queried", required = false, example = "florinda"),
            responses = {
                    @ApiResponse(description = "Page of users found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(produces = "application/json")
	public ResponseEntity<Page<UserDataDTO>> getUsers(@RequestParam(defaultValue = "") String username, @ParameterObject Pageable pageable) {
		Page<UserDataDTO> userDtoPage = userService.getUsers(username, pageable);
		return ResponseEntity.ok(userDtoPage);
	}

    @Operation(summary = "CSV file of users",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "username", description = "complete username to be queried", required = false, example = "florinda"),
            responses = {
                    @ApiResponse(description = "File of users found (can be empty)", responseCode = "200")})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<Resource> getUsersAsCsv(@RequestParam(defaultValue = "") String username){
		byte[] csvData = userService.getUsersAsCsvData(username);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=users.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

    @Operation(summary = "Create a new user",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "User created", responseCode = "201"),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping(produces = "application/json")
	public ResponseEntity<UserDataDTO> createUser(@Validated({Default.class, CreateGroup.class}) @RequestBody UserCreateDTO userCreateDto) {
		UserDataDTO userDto = userService.createUser(userCreateDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(userDto.getEmployeeId());
		return ResponseEntity.created(uri).body(userDto);
	}

    @Operation(summary = "Update a user by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric user id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "User updated", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class))),
                    @ApiResponse(description = "Invalid input data", responseCode = "422", content = @Content(schema = @Schema(implementation = ValidationError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UserDataDTO> updateUser(@PathVariable Long id, @Validated(Default.class) @RequestBody UserCreateDTO userCreateDto) {
		UserDataDTO userDto = userService.updateUser(id, userCreateDto);
		return ResponseEntity.ok(userDto);
	}

    @Operation(summary = "Delete a user by id",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "numeric user id", required = true, example = "1"),
            responses = {
                    @ApiResponse(description = "User deleted", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content(schema = @Schema(implementation = CustomError.class)))})
	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
