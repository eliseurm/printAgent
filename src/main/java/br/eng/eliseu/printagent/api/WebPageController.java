package br.eng.eliseu.printagent.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {
    @GetMapping({"/printAgent", "/printAgent/"})
    public String painel() {
        return "forward:/printAgent/index.html";
    }

    @GetMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/printAgent/favicon.svg";
    }
}
