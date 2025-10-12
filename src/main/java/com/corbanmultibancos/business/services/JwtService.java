package com.corbanmultibancos.business.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.corbanmultibancos.business.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	@Autowired
	private JwtEncoder jwtEncoder;

	public TokenDTO generateToken(Authentication authentication) {
		Instant now = Instant.now();
		long durationSeconds = 3600L;
		String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer("corban-multibancos")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(durationSeconds))
			.subject(authentication.getName())
			.claim("scope", role)
			.build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenDTO(token);
	}
}
