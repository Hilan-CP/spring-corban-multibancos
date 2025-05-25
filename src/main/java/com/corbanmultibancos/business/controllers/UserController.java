package com.corbanmultibancos.business.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById() {
		return null;
	}

	@GetMapping
	public ResponseEntity<Page<UserDTO>> getUsers() {
		return null;
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser() {
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser() {
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser() {
		return null;
	}
}
