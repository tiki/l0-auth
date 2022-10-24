/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest;

import com.mytiki.l0_auth.features.latest.otp.OtpConfig;
import org.springframework.context.annotation.Import;

@Import({
        OtpConfig.class
})
public class FeaturesConfig {
}