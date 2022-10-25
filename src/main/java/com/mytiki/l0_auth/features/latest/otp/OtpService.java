/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_auth.features.latest.otp;

import com.mytiki.l0_auth.utilities.Mustache;
import com.mytiki.l0_auth.utilities.Sendgrid;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class OtpService {
    private static final Long EXPIRY_DURATION_MINUTES = 30L;
    private final OtpRepository repository;
    private final Mustache templates;
    private final Sendgrid sendgrid;

    public OtpService(OtpRepository repository, Mustache templates, Sendgrid sendgrid) {
        this.repository = repository;
        this.templates = templates;
        this.sendgrid = sendgrid;
    }

    public OtpAOIssueRsp issue(OtpAOIssueReq req) {
        String deviceId = randomB64(32);
        String code = randomAlphanumeric(6);

        /*String path = "https://mytiki.com/app/bouncer?otp=" + newOtpMap.get(KEY_OTP);
        HashMap<String, String> templateDataMap = new HashMap<>(1);
        templateDataMap.put("dynamic-link",
                "https://mytiki.app/?link=" + URLEncoder.encode(path, StandardCharsets.UTF_8) +
                        "&apn=com.mytiki.app" +
                        "&ibi=com.mytiki.app");*/

        Map<String, String> input = new HashMap<>(1);
        input.put("dynamic-link", code);

        boolean sent = sendgrid.send(req.getEmail(),
                templates.resovle(OtpConfig.TEMPLATE_SUBJECT, null),
                templates.resovle(OtpConfig.TEMPLATE_BODY_HTML, input),
                templates.resovle(OtpConfig.TEMPLATE_BODY_TXT, input));
        if (sent) {
            OtpDO otpDO = new OtpDO();
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            ZonedDateTime expires = now.plusMinutes(EXPIRY_DURATION_MINUTES);
            otpDO.setOtpHashed(hashedOtp(deviceId, code));
            otpDO.setIssued(now);
            otpDO.setExpires(expires);
            repository.save(otpDO);
            OtpAOIssueRsp rsp = new OtpAOIssueRsp();
            rsp.setDeviceId(deviceId);
            rsp.setExpires(expires);
            return rsp;
        } else {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message("One-time Password (OTP) failed")
                    .detail("Issue with sending email")
                    .build();
        }
    }

    public OtpAOAuthorizeRsp authorize(OtpAOAuthorizeReq req) {
        String hashedOtp = hashedOtp(req.getDeviceId(), req.getCode());
        Optional<OtpDO> found = repository.findByOtpHashed(hashedOtp);
        if (found.isEmpty())
            throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                    .message("Invalid One-time Password (OTP)")
                    .detail("Device id and/or code invalid")
                    .help("Check your email")
                    .build();
        repository.delete(found.get());
        if (ZonedDateTime.now(ZoneOffset.UTC).isAfter(found.get().getExpires()))
            throw new ApiExceptionBuilder(HttpStatus.UNAUTHORIZED)
                    .message("Invalid One-time Password (OTP)")
                    .detail("Expired")
                    .help("Reissue for a new OTP")
                    .build();
        return new OtpAOAuthorizeRsp();
    }

    private String hashedOtp(String deviceId, String code) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(code.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(md.digest(deviceId.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message("One-time Password (OTP) failed")
                    .detail("Issue with SHA256")
                    .cause(e)
                    .build();
        }
    }

    private String randomB64(int len) {
        try {
            byte[] bytes = new byte[len];
            SecureRandom.getInstanceStrong().nextBytes(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message("One-time Password (OTP) failed")
                    .detail("Issue with SecureRandom generation")
                    .cause(e)
                    .build();
        }
    }

    private String randomAlphanumeric(int len) {
        try {
            int[] ints = SecureRandom.getInstanceStrong().ints(len, 0, 36).toArray();
            StringBuilder result = new StringBuilder();
            Arrays.stream(ints).forEach(i -> result.append(Integer.toString(i, 36)));
            return result.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message("One-time Password (OTP) failed")
                    .detail("Issue with SecureRandom generation")
                    .cause(e)
                    .build();
        }
    }
}
