package br.eng.eliseu.printagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final PrintAgentProperties properties;
    public WebConfig(PrintAgentProperties properties){this.properties=properties;}
    @Override public void addCorsMappings(CorsRegistry registry){
        var origins=properties.getCors().getOrigensPermitidas();
        if(!origins.isEmpty()) registry.addMapping("/api/**").allowedOrigins(origins.toArray(String[]::new)).allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
}
