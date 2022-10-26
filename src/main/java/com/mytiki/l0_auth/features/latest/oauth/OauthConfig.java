/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.oauth;

import com.mytiki.l0_auth.features.latest.otp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;

public class OauthConfig {

    @Bean
    public OauthController oauthController(
            @Autowired OtpService otpService) {
        return new OauthController(otpService);
    }

    @Bean
    public HttpMessageConverter<OAuth2AccessTokenResponse> oAuth2AccessTokenResponseHttpMessageConverter() {
        return new OAuth2AccessTokenResponseHttpMessageConverter();
    }

    @Bean
    public HttpMessageConverter<OAuth2Error> oAuth2ErrorHttpMessageConverter() {
        return new OAuth2ErrorHttpMessageConverter();
    }
}
