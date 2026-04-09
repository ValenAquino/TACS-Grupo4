package app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerPrueba {

  @GetMapping("/hola")
  public String hola() {
    return "Hola mundo";
  }
}