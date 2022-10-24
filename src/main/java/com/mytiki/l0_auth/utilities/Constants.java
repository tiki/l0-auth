/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.utilities;

public interface Constants {
    String MODULE_DOT_PATH = "com.mytiki.l0_auth";
    String MODULE_SLASH_PATH = "com/mytiki/l0_auth";

    String SLICE_FEATURES = "features";
    String SLICE_LATEST = "latest";

    String PACKAGE_FEATURES_LATEST_DOT_PATH = MODULE_DOT_PATH + "." + SLICE_FEATURES + "." + SLICE_LATEST;
    String PACKAGE_FEATURES_LATEST_SLASH_PATH = MODULE_SLASH_PATH + "/" + SLICE_FEATURES + "/" + SLICE_LATEST;

    String API_DOCS_PATH = "/v3/api-docs.yaml";
}
