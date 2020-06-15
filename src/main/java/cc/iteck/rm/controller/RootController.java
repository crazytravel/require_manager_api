package cc.iteck.rm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public void redirect2SwaggerUI(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

}
