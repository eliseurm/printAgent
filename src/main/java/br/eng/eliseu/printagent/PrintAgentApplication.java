package br.eng.eliseu.printagent;

import br.eng.eliseu.printagent.config.PrintAgentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PrintAgentProperties.class)
public class PrintAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrintAgentApplication.class, args);
    }
}
