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

    @Value("${jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;


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
        var accessToken = generateAccessToken(jwtUserDetails);
        var refreshToken = generateRefreshToken();
        return TokenWrapper.builder()
                .tokenType(tokenType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration)
                .refreshExpiresIn(refreshTokenExpiration)
                .build();
    }

    public Boolean verifyToken(String token, UserDetails userDetails) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withClaim("username", userDetails.getUsername())
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt != null;
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            log.info("verification token info: {}", exception.getMessage());
            return false;
        }
    }

    public TokenWrapper refreshToken(String accessToken, String refreshToken, Date lastPasswordReset) {
        canTokenBeRefreshed(refreshToken, lastPasswordReset);
        var currentTimeMillis = System.currentTimeMillis();
        final var createdDate = new Date(currentTimeMillis);
        final var expirationDate = new Date(currentTimeMillis + accessTokenExpiration);
        var claims = parseToken(accessToken);
        var algorithmHS = Algorithm.HMAC256(accessTokenSecret);
        accessToken = JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(createdDate)
                .withSubject(claims.getSubject())
                .withClaim("username", claims.getClaim("username").asString())
                .withClaim("authorities", claims.getClaim("authorities").asList(String.class))
                .withExpiresAt(expirationDate)
                .sign(algorithmHS);
        refreshToken = generateRefreshToken();
        return TokenWrapper.builder()
                .tokenType(tokenType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration)
                .refreshExpiresIn(refreshTokenExpiration)
                .build();
    }

    private String generateAccessToken(JwtUserDetails jwtUserDetails) {
        var currentTimeMillis = System.currentTimeMillis();
        final var createdDate = new Date(currentTimeMillis);
        final var expirationDate = new Date(currentTimeMillis + accessTokenExpiration * 1000);
        List<String> authorities = jwtUserDetails
                .getPermissions()
                .stream()
                .map(PermissionDto::getAuthority).collect(Collectors.toList());
        var algorithmHS = Algorithm.HMAC256(accessTokenSecret);
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(createdDate)
                .withSubject(jwtUserDetails.getUserId())
                .withClaim("username", jwtUserDetails.getUsername())
                .withClaim("authorities", authorities)
                .withExpiresAt(expirationDate)
                .sign(algorithmHS);
    }

    private String generateRefreshToken() {
        var currentTimeMillis = System.currentTimeMillis();
        final var createdDate = new Date(currentTimeMillis);
        final var expirationDate = new Date(currentTimeMillis + refreshTokenExpiration * 1000);
        var algorithmHS = Algorithm.HMAC256(refreshTokenSecret);
        return JWT.create().withIssuer(issuer)
                .withIssuedAt(createdDate)
                .withExpiresAt(expirationDate)
                .sign(algorithmHS);
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


    private Boolean canTokenBeRefreshed(String refreshToken, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(refreshToken);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(refreshToken));
    }


}


