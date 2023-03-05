package com.helia.gateway.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping(path = "/login2")
    public String index() {
        return "login";
    }
}
