package com.corbanmultibancos.business.entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_oauth_client")
public class OAuthClient {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String clientId;

    private String clientSecret;

    @Column(nullable = false)
    private String clientName;

    // texto separado por vírgula
    @Column(nullable = false, columnDefinition = "TEXT")
    private String redirectUris;

    // texto separado por vírgula
    @Column(columnDefinition = "TEXT")
    private String postLogoutRedirectUri;

    // texto separado por vírgula
    @Column(nullable = false)
    private String scopes;

    // texto separado por vírgula
    @Column(nullable = false)
    private String authorizedGrantTypes;

    // texto separado por vírgula
    @Column(nullable = false)
    private String clientAuthenticationMethods;

    @Column(nullable = false)
    private long accessTokenTTLSeconds = 3600L;

    @Column(nullable = false)
    private long refreshTokenTTLSeconds = 2592000L;

    @Column(nullable = false)
    private boolean requireConsent = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getPostLogoutRedirectUri() {
        return postLogoutRedirectUri;
    }

    public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public void setClientAuthenticationMethods(String clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
    }

    public long getAccessTokenTTLSeconds() {
        return accessTokenTTLSeconds;
    }

    public void setAccessTokenTTLSeconds(long accessTokenTTLSeconds) {
        this.accessTokenTTLSeconds = accessTokenTTLSeconds;
    }

    public long getRefreshTokenTTLSeconds() {
        return refreshTokenTTLSeconds;
    }

    public void setRefreshTokenTTLSeconds(long refreshTokenTTLSeconds) {
        this.refreshTokenTTLSeconds = refreshTokenTTLSeconds;
    }

    public boolean isRequireConsent() {
        return requireConsent;
    }

    public void setRequireConsent(boolean requireConsent) {
        this.requireConsent = requireConsent;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OAuthClient that = (OAuthClient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
