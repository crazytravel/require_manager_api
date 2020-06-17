package cc.iteck.rm.controller;

import cc.iteck.rm.model.account.LoginForm;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.model.security.TokenWrapper;
import cc.iteck.rm.security.JwtTokenProvider;
import cc.iteck.rm.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.ZoneId;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.access-token.cookie-name}")
    private String accessTokenCookieName;
    @Value("${jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;
    @Value("${jwt.token-type}")
    private String tokenType;

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenWrapper> authToken(@RequestBody LoginForm loginForm, HttpServletResponse response) {
        UserDto userDto = userService.findUsernameAndPassword(loginForm.getUsername(), loginForm.getPassword());
        var jwtUserDetails = JwtUserDetails.builder()
                .userId(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .permissions(userDto.getPermissions())
                .build();
        var token = jwtTokenProvider.generateToken(jwtUserDetails);
        Cookie accessTokenCookie = new Cookie(accessTokenCookieName, token.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(token.getExpiresIn().intValue());
//        accessTokenCookie.setSecure(true);
        Cookie refreshTokenCookie = new Cookie(refreshTokenCookieName, token.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(token.getRefreshExpiresIn().intValue());
//        refreshTokenCookie.setSecure(true);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenWrapper> refreshToken(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (accessToken != null && refreshToken != null) {
                break;
            }
            if (accessTokenCookieName.equals(cookie.getName())) {
                accessToken = cookie.getValue();
                continue;
            }
            if (refreshTokenCookieName.equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }
        var username = jwtTokenProvider.getUsernameFromToken(accessToken);
        var userDto = userService.findUserByUsername(username);
        var lastModifyDate = Date.from(userDto.getModifiedAt().atZone(ZoneId.systemDefault())
                .toInstant());
        TokenWrapper tokenWrapper = jwtTokenProvider.refreshToken(accessToken, refreshToken, lastModifyDate);
        return ResponseEntity.ok(tokenWrapper);
    }

}
