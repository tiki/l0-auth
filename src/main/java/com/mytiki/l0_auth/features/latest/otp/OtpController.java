/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.otp;

import com.mytiki.spring_rest_api.ApiConstants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = OtpController.PATH_CONTROLLER)
public class OtpController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "otp";
    public static final String PATH_ISSUE = "/issue";
    public static final String PATH_AUTHORIZE = "/authorize";

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_ISSUE)
    public OtpAOIssueRsp issue(@RequestBody OtpAOIssueReq body) {
        return otpService.issue(body);
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_AUTHORIZE)
    public OtpAOAuthorizeRsp authorize(@RequestBody OtpAOAuthorizeReq body) {
        return otpService.authorize(body);
    }
}
