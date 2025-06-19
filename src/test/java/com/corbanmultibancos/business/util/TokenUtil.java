package com.corbanmultibancos.business.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Component
public class TokenUtil {

	public String logInAndGetToken(MockMvc mockMvc, String username, String password) throws Exception {
		ResultActions result = mockMvc.perform(post("/login")
				.accept(MediaType.APPLICATION_JSON)
				.with(httpBasic(username, password)));
		String response = result.andReturn().getResponse().getContentAsString();
		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(response).get("token").toString();
	}
}
