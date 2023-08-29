package com.swk.myapp.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swk.myapp.auth.AuthUser;

import java.util.Date;

public class JwtUtil {

    public String secret = "alskmvllewkdkmslkmfe";
    public final long TOKEN_TIMEOUT = 1000 * 60 * 60 * 24 * 7;

    public String createToken(long id, String email, String nickname) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + TOKEN_TIMEOUT);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(String.valueOf(id))
                .withClaim("email", email)
                .withClaim("nickname", nickname)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public AuthUser validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
           Long id = Long.valueOf(decodedJWT.getSubject());
           String email = decodedJWT
                            .getClaim("email").asString();
           String nickname = decodedJWT
                            .getClaim("nickname").asString();


           return AuthUser.builder()
                   .id(id)
                   .email(email)
                   .nickname(nickname)
                   .build();

        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
