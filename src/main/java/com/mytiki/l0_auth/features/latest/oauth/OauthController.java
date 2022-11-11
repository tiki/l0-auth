/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.oauth;

import com.mytiki.l0_auth.features.latest.otp.OtpService;
import com.mytiki.l0_auth.features.latest.refresh.RefreshService;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AUTH")
@RestController
@RequestMapping(value = OauthController.PATH_CONTROLLER)
public class OauthController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "oauth";
    private final OtpService otpService;
    private final RefreshService refreshService;

    public OauthController(OtpService otpService, RefreshService refreshService) {
        this.otpService = otpService;
        this.refreshService = refreshService;
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
            @RequestParam(name = "password") String code,
            @RequestParam List<String> audience) {
        if (!grantType.equals(AuthorizationGrantType.PASSWORD))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        return otpService.authorize(deviceId, code, audience);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            params = {"refresh_token"})
    public OAuth2AccessTokenResponse tokenGrantRefresh(
            @RequestParam(name = "grant_type") AuthorizationGrantType grantType,
            @RequestParam(required = false) String scope,
            @RequestParam(name = "refresh_token") String refreshToken,
            @RequestParam List<String> audience) {
        if (!grantType.equals(AuthorizationGrantType.REFRESH_TOKEN))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        return refreshService.authorize(refreshToken);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            params = {"assertion"})
    public OAuth2AccessTokenResponse tokenGrantJwt(
            @RequestParam(name = "grant_type") AuthorizationGrantType grantType,
            @RequestParam(required = false) String scope,
            @RequestParam String assertion,
            @RequestParam List<String> audience) {
        if (!grantType.equals(AuthorizationGrantType.JWT_BEARER))
            throw new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));

        //TODO NEEDS IMPLEMENTATION
        throw  new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
    }

    @ApiResponse(responseCode = "200")
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/revoke",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void revoke(@RequestParam String token) {
        refreshService.revoke(token);
    }

}
