/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.otp;

import com.mytiki.l0_auth.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@RequestMapping(value = OtpController.PATH_CONTROLLER)
public class OtpController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "otp";
    public static final String PATH_ISSUE = "/start";

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @Operation(operationId = Constants.PROJECT_DASH_PATH +  "-otp-start-post",
            summary = "Request OTP", description = "Start a new passwordless authorization flow")
    @RequestMapping(method = RequestMethod.POST, path = PATH_ISSUE)
    public OtpAOStartRsp issue(@RequestBody OtpAOStartReq body) {
        return otpService.start(body);
    }
}
