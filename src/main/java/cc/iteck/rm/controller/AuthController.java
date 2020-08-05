package cc.iteck.rm.controller;

import cc.iteck.rm.exception.AuthenticationException;
import cc.iteck.rm.model.ErrorWrapper;
import cc.iteck.rm.model.account.SignForm;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.model.security.TokenWrapper;
import cc.iteck.rm.security.JwtTokenProvider;
import cc.iteck.rm.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<TokenWrapper> authToken(@RequestBody SignForm loginForm) {
        UserDto userDto = userService.findUsernameAndPassword(loginForm.getUsername(), loginForm.getPassword());
        var jwtUserDetails = JwtUserDetails.builder()
                .userId(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .permissions(userDto.getPermissions())
                .build();
        var token = jwtTokenProvider.generateToken(jwtUserDetails);
        return setCookie(token);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignForm loginForm) {
        UserDto userDto = userService.findUserByUsername(loginForm.getUsername());
        if (userDto != null) {
            ErrorWrapper errorWrapper = ErrorWrapper.builder().code(400).error(HttpStatus.BAD_REQUEST.toString())
                    .errorDescription("已存在用户，请直接登录").build();
            return ResponseEntity.badRequest().body(errorWrapper);
        }
        UserDto newUser = UserDto.builder()
                .username(loginForm.getUsername())
                .password(loginForm.getPassword())
                .nickname(loginForm.getNickname())
                .build();
        UserDto user = userService.createUser(newUser);
        var jwtUserDetails = JwtUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .permissions(user.getPermissions())
                .build();
        var token = jwtTokenProvider.generateToken(jwtUserDetails);
        return setCookie(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenWrapper> refreshToken(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthenticationException("cannot found access_token from cookie");
        }
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
        if (accessToken == null) {
            throw new AuthenticationException("cannot found access_token from cookie");
        }
        var username = jwtTokenProvider.getUsernameFromToken(accessToken);
        var userDto = userService.findUserByUsername(username);
        if (userDto == null) {
            throw new AuthenticationException("无效的登录信息，请重新登录");
        }
        var lastModifyDate = Date.from(userDto.getModifiedAt().atZone(ZoneId.systemDefault())
                .toInstant());
        TokenWrapper tokenWrapper = jwtTokenProvider.refreshToken(accessToken, refreshToken, lastModifyDate);
        return setCookie(tokenWrapper);
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserDto> userInfo(HttpServletRequest request) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (accessTokenCookieName.equals(cookie.getName())) {
                accessToken = cookie.getValue();
                break;
            }
        }
        if (accessToken == null) {
            throw new AuthenticationException("cannot found access_token from cookie");
        }
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        UserDto userDto = userService.findUserWithPermissionsByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<TokenWrapper> logout() {
        var token = TokenWrapper.builder()
                .accessToken(null)
                .refreshToken(null)
                .refreshExpiresIn(0L)
                .expiresIn(0L)
                .build();
        return setCookie(token);
    }

    private ResponseEntity<TokenWrapper> setCookie(TokenWrapper token) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(accessTokenCookieName, token.getAccessToken())
                .sameSite("Strict")
                .httpOnly(true)
                .secure(false)  // TODO for production env should be true
                .maxAge(token.getRefreshExpiresIn())
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieName, token.getRefreshToken())
                .sameSite("Strict")
                .httpOnly(true)
                .secure(false) // TODO for production env should be true
                .maxAge(token.getRefreshExpiresIn())
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(token);
    }

}
