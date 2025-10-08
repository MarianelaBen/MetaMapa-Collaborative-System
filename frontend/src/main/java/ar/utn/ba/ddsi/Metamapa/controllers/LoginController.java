package ar.utn.ba.ddsi.Metamapa.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class LoginController {

    @GetMapping("/login")
    public String verLogin(){
        System.out.println("al controller llega!!!");
        return "authentication/login";
    }

    @GetMapping("/signup")
    public String verSignup(){

        return "authentication/signup";
    }
/*
    @PostMapping("/signup")
    public String procesarSignup(@ModelAttribute RegisterRequestDTO dto,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        var tokens = api.signupAndGetTokens(dto); // llama a /auth/register en el auth-service
        // si tu /auth/register no devuelve tokens, cambiá el tipo de retorno y no guardes sesión acá
        session.setAttribute("accessToken", tokens.getAccessToken());
        session.setAttribute("refreshToken", tokens.getRefreshToken());
        ra.addFlashAttribute("mensaje", "Cuenta creada con éxito");
        return "redirect:/inicio";
    }*/
}
