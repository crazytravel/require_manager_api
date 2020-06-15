package cc.iteck.rm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/token")
    public ResponseEntity<String> authToken() {
        // TODO implement me
        return ResponseEntity.ok("token");
    }

}
