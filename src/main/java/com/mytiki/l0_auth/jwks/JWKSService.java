/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

/*
 * (h/t) plajko
 * https://stackoverflow.com/questions/42639620/generate-ecpublickey-from-ecprivatekey
 */

package com.mytiki.l0_auth.jwks;

import com.nimbusds.jose.jwk.Curve;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;

public class JWKSService {

    private final JWKSAO jwks;

    public JWKSService(String privateKey) {
        ECPublicKey key = publicKey(privateKey);

        JWKSAOEC ec = new JWKSAOEC();
        ec.setAlg("EC");
        ec.setUse("sig");
        ec.setCrv(Curve.P_256.getName());
        ec.setX(Base64.getUrlEncoder().encodeToString(key.getW().getAffineX().toByteArray()));
        ec.setY(Base64.getUrlEncoder().encodeToString(key.getW().getAffineY().toByteArray()));

        jwks = new JWKSAO();
        jwks.setJwk(Collections.singletonList(ec));
    }

    public JWKSAO getJwks() {
        return jwks;
    }

    private ECPublicKey publicKey(String pkcs8) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pkcs8));
            ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(encodedKeySpec);

            ECParameterSpec keyParams = EC5Util.convertSpec(privateKey.getParams());
            ECPoint q = keyParams.getG().multiply(privateKey.getS());
            ECPoint bcW = keyParams.getCurve().decodePoint(q.getEncoded(false));
            java.security.spec.ECPoint w = new java.security.spec.ECPoint(
                    bcW.getAffineXCoord().toBigInteger(),
                    bcW.getAffineYCoord().toBigInteger());


            ECNamedCurveParameterSpec curveParams = ECNamedCurveTable.getParameterSpec(Curve.P_256.getStdName());
            ECNamedCurveSpec curveSpec = new ECNamedCurveSpec(curveParams.getName(), curveParams.getCurve(),
                    curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());

            ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(w, curveSpec);
            return (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
