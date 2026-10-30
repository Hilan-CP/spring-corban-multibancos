package com.corbanmultibancos.business.repositories;

import com.corbanmultibancos.business.entities.OAuthClient;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OAuthRegisteredClientRepository implements RegisteredClientRepository {

    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        oAuthClientRepository.save(mapToOAuthClient(registeredClient));
    }

    @Override
    public @Nullable RegisteredClient findById(String id) {
        return oAuthClientRepository.findById(UUID.fromString(id)).map(this::mapToRegisteredClient).orElse(null);
    }

    @Override
    public @Nullable RegisteredClient findByClientId(String clientId) {
        return oAuthClientRepository.findByClientId(clientId).map(this::mapToRegisteredClient).orElse(null);
    }

    private RegisteredClient mapToRegisteredClient(OAuthClient oAuthClient) {
        List<AuthorizationGrantType> authorizationGrantTypes = Arrays.stream(oAuthClient.getAuthorizedGrantTypes().split(","))
                .map(AuthorizationGrantType::new)
                .toList();
        List<ClientAuthenticationMethod> authenticationMethods = Arrays.stream(oAuthClient.getClientAuthenticationMethods().split(","))
                .map(ClientAuthenticationMethod::new)
                .toList();
        List<String> clientScopes = Arrays.stream(oAuthClient.getScopes().split(","))
                .map(String::trim)
                .toList();
        List<String> redirectUris = Arrays.stream(oAuthClient.getRedirectUris().split(","))
                .map(String::trim)
                .toList();
        List<String> postLogoutRedirectUris = Arrays.stream(oAuthClient.getPostLogoutRedirectUri().split(","))
                .map(String::trim)
                .filter(uri -> !uri.isEmpty())
                .toList();

        return RegisteredClient.withId(oAuthClient.getId().toString())
                .clientId(oAuthClient.getClientId())
                .clientSecret(oAuthClient.getClientSecret())
                .clientName(oAuthClient.getClientName())
                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(authorizationGrantTypes))
                .clientAuthenticationMethods(methods -> methods.addAll(authenticationMethods))
                .redirectUris(uris -> uris.addAll(redirectUris))
                .postLogoutRedirectUris(postLogoutUris -> {
                    if (!postLogoutRedirectUris.isEmpty()) {
                        postLogoutUris.addAll(postLogoutRedirectUris);
                    }
                })
                .scopes(scopes -> scopes.addAll(clientScopes))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(oAuthClient.isRequireConsent()).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(oAuthClient.getAccessTokenTTLSeconds()))
                        .refreshTokenTimeToLive(Duration.ofSeconds(oAuthClient.getRefreshTokenTTLSeconds()))
                        .build())
                .build();
    }

    private OAuthClient mapToOAuthClient(RegisteredClient registeredClient) {
        OAuthClient  oAuthClient = new OAuthClient();
        oAuthClient.setId(UUID.fromString(registeredClient.getId()));
        oAuthClient.setClientId(registeredClient.getClientId());
        oAuthClient.setClientSecret(registeredClient.getClientSecret());
        oAuthClient.setClientName(registeredClient.getClientName());
        oAuthClient.setRequireConsent(registeredClient.getClientSettings().isRequireAuthorizationConsent());
        oAuthClient.setAccessTokenTTLSeconds(registeredClient.getTokenSettings().getAccessTokenTimeToLive().getSeconds());
        oAuthClient.setRefreshTokenTTLSeconds(registeredClient.getTokenSettings().getRefreshTokenTimeToLive().getSeconds());
        oAuthClient.setAuthorizedGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.joining(",")));
        oAuthClient.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.joining(",")));
        oAuthClient.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
        oAuthClient.setPostLogoutRedirectUri(String.join(",", registeredClient.getPostLogoutRedirectUris()));
        oAuthClient.setScopes(String.join(",", registeredClient.getScopes()));
        return oAuthClient;
    }
}
