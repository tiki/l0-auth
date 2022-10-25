/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.jwks;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth")
@RestController
@RequestMapping(value = "/jwks")
public class JWKSController {
    private final JWKSService service;

    public JWKSController(JWKSService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public JWKSAO get() {
        return service.getJwks();
    }
}
