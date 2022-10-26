/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.utilities;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/.well-known/jwks.json")
public class JWKS {
    private final JWKSet jwkSet;

    public JWKS(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> get() {
        return jwkSet.toJSONObject(true);
    }
}
