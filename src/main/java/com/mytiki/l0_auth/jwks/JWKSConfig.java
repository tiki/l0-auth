/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.jwks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class JWKSConfig {
    @Bean
    public JWKSController jwksController(@Autowired JWKSService service) {
        return new JWKSController(service);
    }

    @Bean
    public JWKSService jwksService(
            @Value("${com.mytiki.l0_auth.jwt.private_key}") String privateKey) {
        return new JWKSService(privateKey);
    }
}
