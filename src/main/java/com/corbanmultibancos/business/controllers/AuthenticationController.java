package com.corbanmultibancos.business.controllers;

import java.util.Map;

import com.corbanmultibancos.business.controllers.exceptions.CustomError;
import com.corbanmultibancos.business.dto.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.services.JwtService;

@Tag(name = "Login", description = "Authentication endpoints")
@RestController
@RequestMapping("/login")
public class AuthenticationController {

	@Autowired
	private JwtService jwtService;

    @Operation(summary = "Log in and get token",
            description = "HTTP Basic authentication with user and password",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(description = "User logged successfully", responseCode = "200"),
                    @ApiResponse(description = "Invalid credentials", responseCode = "401", content = @Content)})
	@PostMapping(produces = "application/json")
	public ResponseEntity<TokenDTO> login(Authentication authentication) {
		TokenDTO response = jwtService.generateToken(authentication);
		return ResponseEntity.ok(response);
	}
}
