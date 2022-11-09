/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest;

import com.mytiki.l0_auth.features.latest.oauth.OauthConfig;
import com.mytiki.l0_auth.features.latest.otp.OtpConfig;
import com.mytiki.l0_auth.features.latest.refresh.RefreshConfig;
import org.springframework.context.annotation.Import;

@Import({
        OauthConfig.class,
        OtpConfig.class,
        RefreshConfig.class
})
public class FeaturesConfig {
}