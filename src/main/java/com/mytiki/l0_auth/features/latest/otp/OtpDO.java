/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.otp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "otp")
public class OtpDO implements Serializable {
    private String otpHashed;
    private ZonedDateTime issued;
    private ZonedDateTime expires;

    @Id
    @Column(name = "otp_hashed")
    public String getOtpHashed() {
        return otpHashed;
    }

    public void setOtpHashed(String otpHashed) {
        this.otpHashed = otpHashed;
    }

    @Column(name = "issued_utc")
    public ZonedDateTime getIssued() {
        return issued;
    }

    public void setIssued(ZonedDateTime issued) {
        this.issued = issued;
    }

    @Column(name = "expires_utc")
    public ZonedDateTime getExpires() {
        return expires;
    }

    public void setExpires(ZonedDateTime expires) {
        this.expires = expires;
    }
}
