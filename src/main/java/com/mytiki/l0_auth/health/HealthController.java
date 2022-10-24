/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.health;

import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.ApiError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "HEALTH")
@RestController
@RequestMapping(value = ApiConstants.HEALTH_ROUTE)
public class HealthController {
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class),
                    examples = @ExampleObject(value = "{\"message\":\"OK\"}")
            ))
    @RequestMapping(method = RequestMethod.GET)
    public ApiError get() {
        ApiError rsp = new ApiError();
        rsp.setMessage("OK");
        return rsp;
    }
}
