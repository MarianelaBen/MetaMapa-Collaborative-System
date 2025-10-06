package ar.utn.ba.ddsi.Metamapa.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

    @GetMapping("/login")
    public String verLogin(){

        return "authentication/login";
    }

    @GetMapping("/signup")
    public String verSignup(){

        return "authentication/signup";
    }
}
