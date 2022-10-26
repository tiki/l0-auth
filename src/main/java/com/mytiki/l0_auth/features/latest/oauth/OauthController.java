/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.oauth;

import com.mytiki.l0_auth.features.latest.otp.OtpService;
import com.mytiki.spring_rest_api.ApiConstants;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = OauthController.PATH_CONTROLLER)
public class OauthController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "oauth";
    private final OtpService otpService;

    public OauthController(OtpService otpService) {
        this.otpService = otpService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            params = {"username", "password"})
    public OAuth2AccessTokenResponse tokenGrantPw(
            @RequestParam(name = "grant_type") AuthorizationGrantType grantType,
            @RequestParam(required = false) String scope,
            @RequestParam(name = "username") String deviceId,
            @RequestParam(name = "password") String code) {
        if (!grantType.equals(AuthorizationGrantType.PASSWORD))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        return otpService.authorize(deviceId, code);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            params = {"refresh_token"})
    public OAuth2AccessTokenResponse tokenGrantRefresh(
            @RequestParam(name = "grant_type") AuthorizationGrantType grantType,
            @RequestParam(required = false) String scope,
            @RequestParam(name = "refresh_token") String refreshToken) {
        if (!grantType.equals(AuthorizationGrantType.REFRESH_TOKEN))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        return OAuth2AccessTokenResponse
                .withToken(refreshToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            params = {"assertion"})
    public OAuth2AccessTokenResponse tokenGrantJwt(
            @RequestParam(name = "grant_type") AuthorizationGrantType grantType,
            @RequestParam(required = false) String scope,
            @RequestParam String assertion) {
        if (!grantType.equals(AuthorizationGrantType.JWT_BEARER))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        return OAuth2AccessTokenResponse
                .withToken(assertion)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/revoke",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public OAuth2AccessTokenResponse revoke(@RequestParam String token) {
        return null;
    }

}
