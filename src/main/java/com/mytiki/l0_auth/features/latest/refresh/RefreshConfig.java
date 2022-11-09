/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.refresh;

import com.mytiki.l0_auth.utilities.Constants;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(RefreshConfig.PACKAGE_PATH)
@EntityScan(RefreshConfig.PACKAGE_PATH)
public class RefreshConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".refresh";

    @Bean
    public RefreshService refreshService(
            @Autowired RefreshRepository repository,
            @Autowired JWSSigner signer,
            @Autowired JWSVerifier verifier) {
        return new RefreshService(repository, signer, verifier);
    }
}
