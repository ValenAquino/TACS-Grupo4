package app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorPing {

    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }

}
