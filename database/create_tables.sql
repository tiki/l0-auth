/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- ONE-TIME PASSWORD
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS otp(
    otp_hashed TEXT NOT NULL,
    issued_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(otp_hashed)
);
