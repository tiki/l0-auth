/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.jwks;

import java.util.List;

public class JWKSAO {
    private List<JWKSAOJWK> jwk;

    public List<JWKSAOJWK> getJwk() {
        return jwk;
    }

    public void setJwk(List<JWKSAOJWK> jwk) {
        this.jwk = jwk;
    }
}
