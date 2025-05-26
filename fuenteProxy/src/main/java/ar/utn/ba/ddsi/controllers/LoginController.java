package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.LoginDTO;
import ar.utn.ba.ddsi.models.dtos.input.LoginResponseDTO;
import ar.utn.ba.ddsi.services.impl.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/login")
public class LoginController {

  private final LoginService loginService;

  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping
  public Mono<ResponseEntity<LoginResponseDTO>> login(@RequestBody LoginDTO creds) {
    return loginService
        .loginAndStore(creds)
        .map(ResponseEntity::ok)
        .onErrorResume(e ->
            Mono.just(ResponseEntity.status(401).build())
        );
  }
}
