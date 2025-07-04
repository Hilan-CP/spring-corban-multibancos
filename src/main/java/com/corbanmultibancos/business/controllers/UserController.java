package com.corbanmultibancos.business.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;
import com.corbanmultibancos.business.services.UserService;
import com.corbanmultibancos.business.validations.CreateGroup;

import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping("/{id}")
	public ResponseEntity<UserDataDTO> getUserById(@PathVariable Long id) {
		UserDataDTO userDto = userService.getUserById(id);
		return ResponseEntity.ok(userDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping
	public ResponseEntity<Page<UserDataDTO>> getUsers(@RequestParam(defaultValue = "") String username,
												Pageable pageable) {
		Page<UserDataDTO> userDtoPage = userService.getUsers(username, pageable);
		return ResponseEntity.ok(userDtoPage);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping("/csv")
	public ResponseEntity<Resource> getUsersAsCsv(@RequestParam(defaultValue = "") String username){
		byte[] csvData = userService.getUsersAsCsvData(username);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=users.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PostMapping
	public ResponseEntity<UserDataDTO> createUser(@Validated({Default.class, CreateGroup.class}) @RequestBody UserCreateDTO userCreateDto) {
		UserDataDTO userDto = userService.createUser(userCreateDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(userDto.getEmployeeId());
		return ResponseEntity.created(uri).body(userDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@PutMapping("/{id}")
	public ResponseEntity<UserDataDTO> updateUser(@PathVariable Long id, @Validated(Default.class) @RequestBody UserCreateDTO userCreateDto) {
		UserDataDTO userDto = userService.updateUser(id, userCreateDto);
		return ResponseEntity.ok(userDto);
	}

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
