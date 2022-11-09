/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.refresh;

import com.mytiki.l0_auth.utilities.Constants;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import javax.transaction.Transactional;
import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class RefreshService {
    private final RefreshRepository repository;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public RefreshService(RefreshRepository repository, JWSSigner signer, JWSVerifier verifier) {
        this.repository = repository;
        this.signer = signer;
        this.verifier = verifier;
    }

    public String token() throws JOSEException {
        RefreshDO refreshDO = new RefreshDO();
        ZonedDateTime now = ZonedDateTime.now();

        refreshDO.setJti(UUID.randomUUID().toString());
        refreshDO.setIssued(now);
        refreshDO.setExpires(now.plusSeconds(Constants.REFRESH_EXPIRY_DURATION_SECONDS));
        repository.save(refreshDO);

        JWSObject jwsObject = new JWSObject(
                new JWSHeader
                        .Builder(JWSAlgorithm.ES256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .issuer(Constants.MODULE_DOT_PATH)
                                .issueTime(Date.from(refreshDO.getIssued().toInstant()))
                                .expirationTime(Date.from(refreshDO.getExpires().toInstant()))
                                .jwtID(refreshDO.getJti())
                                .build()
                                .toJSONObject()
                ));

        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    public OAuth2AccessTokenResponse authorize(String jwt) {
        try {
            JWSObject jws = JWSObject.parse(jwt);
            if (!verify(jws))
                throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));

            String jti = (String) jws.getPayload().toJSONObject().get("jti");
            Optional<RefreshDO> found = repository.findByJti(jti);
            if (found.isPresent()) {
                repository.delete(found.get());
                Instant iat = Instant.now();
                JWSObject accessToken = new JWSObject(
                        new JWSHeader
                                .Builder(JWSAlgorithm.ES256)
                                .type(JOSEObjectType.JWT)
                                .build(),
                        new Payload(
                                new JWTClaimsSet.Builder()
                                        .issuer(Constants.MODULE_DOT_PATH)
                                        .issueTime(Date.from(iat))
                                        .expirationTime(Date.from(iat.plusSeconds(Constants.TOKEN_EXPIRY_DURATION_SECONDS)))
                                        .build()
                                        .toJSONObject()
                        ));
                accessToken.sign(signer);
                return OAuth2AccessTokenResponse
                        .withToken(accessToken.serialize())
                        .tokenType(OAuth2AccessToken.TokenType.BEARER)
                        .refreshToken(token())
                        .expiresIn(Constants.TOKEN_EXPIRY_DURATION_SECONDS)
                        .build();
            } else
                throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
        } catch (ParseException | JOSEException | BadJWTException e) {
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), e);
        }
    }

    @Transactional
    public void revoke(String jwt) {
        try {
            JWSObject jws = JWSObject.parse(jwt);
            if (!verify(jws))
                throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));

            String jti = (String) jws.getPayload().toJSONObject().get("jti");
            repository.deleteByJti(jti);
        } catch (ParseException | JOSEException | BadJWTException e) {
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT), e);
        }
    }

    private boolean verify(JWSObject jws) throws JOSEException, ParseException, BadJWTException {
        DefaultJWTClaimsVerifier<?> claimsVerifier = new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder()
                        .issuer(Constants.MODULE_DOT_PATH)
                        .build(),
                new HashSet<>(Arrays.asList("exp", "jti", "iat")));
        claimsVerifier.verify(JWTClaimsSet.parse(jws.getPayload().toJSONObject()), null);
        return jws.verify(verifier);
    }
}
