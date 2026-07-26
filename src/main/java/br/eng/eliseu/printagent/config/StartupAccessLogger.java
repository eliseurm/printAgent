package br.eng.eliseu.printagent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupAccessLogger {
    private static final Logger log = LoggerFactory.getLogger(StartupAccessLogger.class);

    private final Environment ambiente;

    public StartupAccessLogger(Environment ambiente) {
        this.ambiente = ambiente;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void exibirOpcoesDeAcesso(ApplicationReadyEvent evento) {
        if (!(evento.getApplicationContext() instanceof WebServerApplicationContext contexto)) {
            return;
        }
        int porta = contexto.getWebServer().getPort();
        String enderecoConfigurado = ambiente.getProperty("server.address", "localhost");
        String host = enderecoConfigurado.equals("0.0.0.0")
            || enderecoConfigurado.equals("127.0.0.1")
            || enderecoConfigurado.equals("::")
            || enderecoConfigurado.equals("::1")
            ? "localhost"
            : enderecoConfigurado;
        String base = "http://" + host + ":" + porta;

        log.info("""

            ============================================================
            Print Agent iniciado com sucesso.

            Painel administrativo: {}/printAgent
            API REST:              {}/api/v1
            Estado do agente:      {}/api/v1/status
            Impressoras:           {}/api/v1/impressoras
            ============================================================
            """, base, base, base, base);
    }
}
