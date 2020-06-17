package cc.iteck.rm.controller;

import cc.iteck.rm.model.account.LoginForm;
import cc.iteck.rm.model.account.UserDto;
import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.model.security.TokenWrapper;
import cc.iteck.rm.security.JwtTokenProvider;
import cc.iteck.rm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenWrapper> authToken(@RequestBody LoginForm loginForm) {
        UserDto userDto = userService.findUsernameAndPassword(loginForm.getUsername(), loginForm.getPassword());
        var jwtUserDetails = JwtUserDetails.builder()
                .userId(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .permissions(userDto.getPermissions())
                .build();
        var token = jwtTokenProvider.generateToken(jwtUserDetails);
        return ResponseEntity.ok(token);
    }

}
