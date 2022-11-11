/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.user_info;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfoDO, String> {
    Optional<UserInfoDO> findByUid(String uid);
    Optional<UserInfoDO> findByEmail(String email);
}
