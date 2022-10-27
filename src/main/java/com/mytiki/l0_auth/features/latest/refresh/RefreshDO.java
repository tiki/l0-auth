/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.refresh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "refresh")
public class RefreshDO implements Serializable {
    private String jti;
    private ZonedDateTime issued;
    private ZonedDateTime expires;

    @Id
    @Column(name = "jti")
    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
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
