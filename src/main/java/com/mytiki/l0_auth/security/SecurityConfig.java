/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String FEATURE_POLICY = "accelerometer" + " 'none'" + "ambient-light-sensor" + " 'none'" +
            "autoplay" + " 'none'" + "battery" + " 'none'" + "camera" + " 'none'" + "display-capture" + " 'none'" +
            "document-domain" + " 'none'" + "encrypted-media" + " 'none'" + "execution-while-not-rendered" + " 'none'" +
            "execution-while-out-of-viewport" + " 'none'" + "fullscreen" + " 'none'" + "geolocation" + " 'none'" +
            "gyroscope" + " 'none'" + "layout-animations" + " 'none'" + "legacy-image-formats" + " 'none'" +
            "magnetometer" + " 'none'" + "microphone" + " 'none'" + "midi" + " 'none'" + "navigation-override" + " 'none'" +
            "oversized-images" + " 'none'" + "payment" + " 'none'" + "picture-in-picture" + " 'none'" + "publickey-credentials-get" + " 'none'" +
            "sync-xhr" + " 'none'" + "usb" + " 'none'" + "vr wake-lock" + " 'none'" + "xr-spatial-tracking" + " 'none'";

    private static final String CONTENT_SECURITY_POLICY = "default-src" + "' self'";
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String REMOTE_WORKER_ROLE = "REMOTE";

    public SecurityConfig(@Autowired ObjectMapper objectMapper) {
        super(true);
        this.accessDeniedHandler = new AccessDeniedHandler(objectMapper);
        this.authenticationEntryPoint = new AuthenticationEntryPoint(objectMapper);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .servletApi().and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .securityContext().and()
                .headers()
                .cacheControl().and()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity().and()
                .frameOptions().and()
                .xssProtection().and()
                .referrerPolicy().and()
                .permissionsPolicy().policy(FEATURE_POLICY).and()
                .httpPublicKeyPinning().and()
                .contentSecurityPolicy(CONTENT_SECURITY_POLICY).and()
                .and()
                .anonymous().and()
                .cors().configurationSource(corsConfigurationSource()).and()
                .authorizeRequests().anyRequest().permitAll();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
