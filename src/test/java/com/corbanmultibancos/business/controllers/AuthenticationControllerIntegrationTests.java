package com.corbanmultibancos.business.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerIntegrationTests {

	@Autowired
	private JwtDecoder jwtDecoder;

	@Autowired
	private MockMvc mockMvc;

	private String existingUsername;
	private String existingPassword;
	private String invalidUsername;
	private String invalidPassword;

	@BeforeEach
	void setUp() {
		existingUsername = "florinda";
		existingPassword = "florinda123";
		invalidUsername = "does not exist";
		invalidPassword = "wrong password";
	}

	@Test
	public void loginShouldReturnTokenWhenValidCredentials() throws Exception {
		ResultActions result = mockMvc.perform(post("/login")
				.accept(MediaType.APPLICATION_JSON)
				.with(httpBasic(existingUsername, existingPassword)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists());
		String response = result.andReturn().getResponse().getContentAsString();
		JacksonJsonParser jsonParser = new JacksonJsonParser();
		String token = jsonParser.parseMap(response).get("token").toString();
		String subject = jwtDecoder.decode(token).getSubject();
		Assertions.assertEquals(existingUsername, subject);
	}

	@Test
	public void loginShouldReturnUnauthorizedWhenNonExistingUsername() throws Exception {
		mockMvc.perform(post("/login")
				.accept(MediaType.APPLICATION_JSON)
				.with(httpBasic(invalidUsername, existingPassword)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void loginShouldReturnUnauthorizedWhenWrongPassword() throws Exception {
		mockMvc.perform(post("/login")
				.accept(MediaType.APPLICATION_JSON)
				.with(httpBasic(existingUsername, invalidPassword)))
			.andExpect(status().isUnauthorized());
	}
}
