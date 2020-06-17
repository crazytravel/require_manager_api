package cc.iteck.rm.security;

import cc.iteck.rm.model.account.PermissionDto;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.model.security.TokenWrapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.token-type}")
    private String tokenType;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;


    public String getUsernameFromToken(String token) {
        var claims = parseToken(token);
        return claims.getClaim("username").asString();
    }

    public Date getCreatedDateFromToken(String token) {
        var claims = parseToken(token);
        return claims.getIssuedAt();
    }

    public Date getExpirationDateFromToken(String token) {
        var claims = parseToken(token);
        return claims.getExpiresAt();
    }

    public TokenWrapper generateToken(JwtUserDetails jwtUserDetails) {
        final var createdDate = new Date();
        final var expirationDate = new Date(createdDate.getTime() + expiration * 1000);
        List<String> authorities = jwtUserDetails
                .getPermissions()
                .stream()
                .map(PermissionDto::getAuthority).collect(Collectors.toList());
        var algorithmHS = Algorithm.HMAC256(secret);
        String accessToken = JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(createdDate)
                .withSubject(jwtUserDetails.getUserId())
                .withClaim("username", jwtUserDetails.getUsername())
                .withClaim("authorities", authorities)
                .withExpiresAt(expirationDate)
                .sign(algorithmHS);
        return TokenWrapper.builder()
                .tokenType(tokenType)
                .accessToken(accessToken)
                .refreshToken("") // TODO implement me
                .expiresIn(expiration)
                .build();
    }

    public Boolean verifyToken(String token, UserDetails userDetails) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withClaim("username", userDetails.getUsername())
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt != null;
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            return false;
        }
    }

    private DecodedJWT parseToken(String token) {
        return JWT.decode(token);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        Date now = new Date();
        return expiration.before(now);
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }


    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token));
    }

//    public String refreshToken(String token) {
//        var claims = parseToken(token);
//        if (claims != null) {
//            return generateToken(claims.getSubject(), claims);
//        }
//        return null;
//    }


}


