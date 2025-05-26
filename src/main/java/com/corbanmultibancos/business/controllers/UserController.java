package com.corbanmultibancos.business.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		UserDTO userDto = userService.getUserById(id);
		return ResponseEntity.ok(userDto);
	}

	@GetMapping
	public ResponseEntity<Page<UserDTO>> getUsers(@RequestParam(defaultValue = "") String username,
												Pageable pageable) {
		Page<UserDTO> userDtoPage = userService.getUsers(username, pageable);
		return ResponseEntity.ok(userDtoPage);
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDto) {
		userDto = userService.createUser(userDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(userDto.getEmployeeId());
		return ResponseEntity.created(uri).body(userDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
		userDto = userService.updateUser(id, userDto);
		return ResponseEntity.ok(userDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
