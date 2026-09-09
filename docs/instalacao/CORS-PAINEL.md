# Acesso do painel ao Print Agent

Por solicitacao explicita do operador em 09/09/2026, a API /api/** aceita qualquer
origem com Access-Control-Allow-Origin: *. Esta decisao substitui, nesta
implementacao, a exigencia de lista explicita dos documentos anteriores.

WebConfig libera GET, POST, PUT, DELETE e OPTIONS e todos os cabecalhos de
requisicao. Credenciais CORS (cookies/autenticacao do navegador) nao sao aceitas.
O interceptor Angular nao envia o JWT do Presente ao agente.

A propriedade legada print-agent.cors.origens-permitidas permanece para
compatibilidade, mas nao restringe mais as origens. Configuracoes externas
antigas e PRINT_AGENT_ALLOWED_ORIGINS nao alteram esta politica aberta.

Nao e necessario cadastrar o dominio quando o painel for publicado ou mudar de
endereco. O navegador continua acessando http://localhost:18181 na maquina do
operador. O agente permanece limitado ao endereco de loopback.

Qualquer site aberto no navegador pode tentar consultar a API local e enviar
trabalhos de impressao. A politica aberta nao contorna permissoes de acesso a
rede local ou outras restricoes de seguranca impostas pelo navegador.

Reinicie o Print Agent com o JAR atualizado para aplicar a alteracao.
Os testes automatizados exercitam preflight de GET e POST JSON e aceitacao
de origem externa, sem enviar trabalhos de impressao.
