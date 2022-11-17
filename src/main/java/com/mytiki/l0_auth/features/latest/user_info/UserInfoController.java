/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.user_info;

import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AUTH")
@RestController
@RequestMapping(value = UserInfoController.PATH_CONTROLLER)
public class UserInfoController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "userinfo";

    private final UserInfoService service;

    public UserInfoController(UserInfoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserInfoAO get(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return service.get(token.replace("Bearer ", ""));
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserInfoAO update(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                             @RequestBody UserInfoAOUpdate body) {
        if(body.getEmail() != null) {
            if (!EmailValidator.getInstance().isValid(body.getEmail()))
                throw new ApiExceptionBuilder(HttpStatus.BAD_REQUEST)
                        .message("Invalid email")
                        .build();
        }
        return service.update(token.replace("Bearer ", ""), body);
    }
}
