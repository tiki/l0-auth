/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth;

import com.mytiki.l0_auth.features.latest.refresh.RefreshDO;
import com.mytiki.l0_auth.features.latest.refresh.RefreshRepository;
import com.mytiki.l0_auth.features.latest.refresh.RefreshService;
import com.mytiki.l0_auth.main.App;
import com.mytiki.l0_auth.utilities.Constants;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefreshTest {

    @Autowired
    private RefreshService service;

    @Autowired
    private RefreshRepository repository;

    @Autowired
    private JWSSigner signer;

    @Test
    public void Test_Token_Success() throws JOSEException, ParseException {
        String jwt = service.token();
        assertNotNull(jwt);

        JWSObject jws = JWSObject.parse(jwt);
        String jti = (String) jws.getPayload().toJSONObject().get("jti");
        Optional<RefreshDO> found = repository.findByJti(jti);

        assertTrue(found.isPresent());
        assertNotNull(found.get().getIssued());
        assertTrue(found.get().getExpires().isAfter(ZonedDateTime.now()));
    }

    @Test
    public void Test_Revoke_Success() throws JOSEException, ParseException {
        String jwt = service.token();
        service.revoke(jwt);

        JWSObject jws = JWSObject.parse(jwt);
        String jti = (String) jws.getPayload().toJSONObject().get("jti");
        Optional<RefreshDO> found = repository.findByJti(jti);

        assertTrue(found.isEmpty());
    }

    @Test
    public void Test_Revoke_Replay_Success() throws JOSEException, ParseException {
        String jwt = service.token();
        service.revoke(jwt);
        service.revoke(jwt);

        JWSObject jws = JWSObject.parse(jwt);
        String jti = (String) jws.getPayload().toJSONObject().get("jti");
        Optional<RefreshDO> found = repository.findByJti(jti);

        assertTrue(found.isEmpty());
    }

    @Test
    public void Test_Authorize_Success() throws JOSEException, ParseException {
        String jwt = service.token();

        OAuth2AccessTokenResponse token = service.authorize(jwt);
        assertNotNull(token.getAccessToken().getTokenValue());
        assertEquals(OAuth2AccessToken.TokenType.BEARER, token.getAccessToken().getTokenType());
        assertNotNull(token.getRefreshToken());
        assertNotNull(token.getRefreshToken().getTokenValue());
        assertNotNull(token.getAccessToken().getExpiresAt());
        assertTrue(token.getAccessToken().getExpiresAt().isAfter(Instant.now()));

        JWSObject jws = JWSObject.parse(jwt);
        String jti = (String) jws.getPayload().toJSONObject().get("jti");
        Optional<RefreshDO> found = repository.findByJti(jti);

        assertTrue(found.isEmpty());
    }

    @Test
    public void Test_Authorize_Revoked_Success() throws JOSEException, ParseException {
        String jwt = service.token();
        service.revoke(jwt);

        OAuth2AuthorizationException ex = assertThrows(OAuth2AuthorizationException.class,
                () -> service.authorize(jwt));
        assertEquals(ex.getError().getErrorCode(), OAuth2ErrorCodes.INVALID_GRANT);
    }

    @Test
    public void Test_Authorize_Expired_Success() throws JOSEException, ParseException, InterruptedException {
        JWSObject jws = new JWSObject(
                new JWSHeader
                        .Builder(JWSAlgorithm.ES256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .issuer(Constants.MODULE_DOT_PATH)
                                .issueTime(Date.from(Instant.now()))
                                .expirationTime(Date.from(Instant.now().minusSeconds(100)))
                                .jwtID(UUID.randomUUID().toString())
                                .build()
                                .toJSONObject()
                ));
        jws.sign(signer);
        String jwt = jws.serialize();

        OAuth2AuthorizationException ex = assertThrows(OAuth2AuthorizationException.class,
                () -> service.authorize(jwt));
        assertEquals(ex.getError().getErrorCode(), OAuth2ErrorCodes.INVALID_GRANT);
    }
}
