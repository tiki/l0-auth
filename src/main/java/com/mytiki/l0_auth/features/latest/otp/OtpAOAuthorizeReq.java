/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.otp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OtpAOAuthorizeReq {
    private String deviceId;
    private String code;

    public OtpAOAuthorizeReq(
            @JsonProperty(required = true) String deviceId,
            @JsonProperty(required = true) String code) {
        this.deviceId = deviceId;
        this.code = code;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
