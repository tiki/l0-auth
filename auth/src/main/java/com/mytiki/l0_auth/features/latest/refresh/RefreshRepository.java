/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.refresh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshDO, String> {
    Optional<RefreshDO> findByJti(String jti);

    void deleteByJti(String jti);

    List<RefreshDO> findAllByExpiresBefore(ZonedDateTime before);
}
