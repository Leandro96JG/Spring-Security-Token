package com.spring_security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //return jwtToken
        return JWT.create()
                //Emisor del token, osea desde el backend
                .withIssuer(this.userGenerator)
                //especificar el sujeto del token
                .withSubject(username)
                //reclamaciones clave-valor proporcionan info adic sobre el token
                .withClaim("authorities",authorities)
                //agregar la fecha y hora en la que se emitió el token
                .withIssuedAt(new Date())
                //en cuanto tiempo expira el token
                .withExpiresAt(new Date(System.currentTimeMillis()+1800000))
                //id unico por cada token emitido
                .withJWTId(UUID.randomUUID().toString())
                //especificar la fecha y hora antes de la cual el token no debe ser aceptado
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();
            //DecodedJWT
           return verifier.verify(token);
        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalid, not authorized");
        }
    }

    public String extractUserName(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

    public String returnClaim(DecodedJWT decodedJWT, String clainName){
        return decodedJWT.getClaim(clainName).toString();
    }
}
