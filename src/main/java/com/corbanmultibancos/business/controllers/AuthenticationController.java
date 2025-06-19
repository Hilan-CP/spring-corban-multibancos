package com.corbanmultibancos.business.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.services.JwtService;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

	@Autowired
	private JwtService jwtService;

	@PostMapping
	public ResponseEntity<Map<String, String>> login(Authentication authentication) {
		Map<String, String> response = jwtService.getToken(authentication);
		return ResponseEntity.ok(response);
	}
}
