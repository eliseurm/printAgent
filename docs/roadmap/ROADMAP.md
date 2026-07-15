# Print Agent — Roadmap

## Planejamento de evolução do produto

| Campo                  | Valor                      |
| ---------------------- | -------------------------- |
| Projeto                | Print Agent                |
| Sistema relacionado    | Presente                   |
| Documento              | ROADMAP.md                 |
| Versão atual planejada | 1.0.0                      |
| Java                   | 17 LTS                     |
| Namespace              | `br.eng.eliseu.printagent` |
| Status                 | Planejamento inicial       |
| Última atualização     | Julho de 2026              |

---

# 1. Objetivo

Este documento registra a evolução planejada do Print Agent.

O Roadmap não substitui a especificação técnica da versão 1.

A implementação inicial deverá obedecer aos documentos:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
docs/api/API.md
docs/api/JSON.md
docs/web/WEBUI.md
docs/instalacao/INSTALACAO.md
```

Funcionalidades listadas para versões futuras não deverão ser implementadas na versão 1 sem aprovação e atualização da documentação.

---

# 2. Princípios de evolução

A evolução do Print Agent deverá preservar:

* simplicidade;
* funcionamento local;
* baixo acoplamento;
* independência do sistema Presente;
* compatibilidade com Windows e Linux;
* estabilidade da API;
* segurança do acesso local;
* facilidade de instalação;
* ausência de regras de negócio no agente.

Novas funcionalidades não deverão transformar o Print Agent em:

* sistema de gestão de documentos;
* editor de etiquetas;
* servidor central de impressão;
* repositório de arquivos;
* aplicação específica do sistema Presente.

---

# 3. Versionamento

O projeto deverá utilizar versionamento semântico:

```text
MAJOR.MINOR.PATCH
```

Exemplos:

```text
1.0.0
1.1.0
1.1.1
2.0.0
```

## 3.1 MAJOR

Incrementado em alterações incompatíveis.

Exemplos:

* remoção de endpoint;
* alteração incompatível de JSON;
* mudança de comportamento central;
* nova versão obrigatória da API;
* alteração de requisitos mínimos de plataforma.

## 3.2 MINOR

Incrementado em novas funcionalidades compatíveis.

Exemplos:

* novo endpoint;
* novo tipo de documento;
* nova tela;
* novo recurso opcional;
* nova capacidade de diagnóstico.

## 3.3 PATCH

Incrementado em correções compatíveis.

Exemplos:

* correção de falha;
* ajuste de log;
* melhoria de mensagens;
* correção de instalador;
* melhoria interna sem alteração de contrato.

---

# 4. Versão 1.0.0 — Primeira versão funcional

## 4.1 Objetivo

Disponibilizar um agente local capaz de receber trabalhos de impressão via API REST e encaminhá-los para impressoras instaladas no computador do usuário.

---

## 4.2 Escopo obrigatório

A versão 1.0.0 deverá incluir:

* Java 17;
* Spring Boot 3.x compatível;
* Maven;
* API REST local;
* interface Web administrativa;
* fila FIFO em memória;
* histórico em memória;
* descoberta de impressoras;
* Provider Linux com CUPS;
* Provider Windows;
* impressão ZPL;
* impressão PDF no Linux;
* impressão RAW;
* configurações por trabalho;
* consulta de capacidades;
* cancelamento de trabalhos pendentes;
* impressão de teste;
* logs;
* configuração externa;
* instalação Windows;
* pacote `.deb`;
* inicialização automática;
* verificação de atualização;
* CORS configurável;
* acesso somente por loopback.

---

## 4.3 Tipos de conteúdo

```text
application/zpl
application/pdf
text/plain
```

---

## 4.4 Endpoints obrigatórios

```text
GET    /api/v1/status
POST   /api/v1/trabalhos
GET    /api/v1/trabalhos
GET    /api/v1/trabalhos/{id}
DELETE /api/v1/trabalhos/{id}
GET    /api/v1/impressoras
POST   /api/v1/impressoras/atualizar
GET    /api/v1/impressoras/{nome}
GET    /api/v1/impressoras/{nome}/capacidades
POST   /api/v1/testes/impressao
GET    /api/v1/configuracoes
PUT    /api/v1/configuracoes
GET    /api/v1/logs
GET    /api/v1/logs/arquivos
GET    /api/v1/logs/arquivos/{nome}
GET    /api/v1/atualizacoes
POST   /api/v1/atualizacoes/verificar
```

Todos os endpoints acima fazem parte da versão 1.0.0. O contrato detalhado e a
fonte de verdade dos endpoints são definidos em `API.md`.

---

## 4.5 Fora do escopo da versão 1.0.0

Não implementar:

* autenticação;
* banco de dados;
* fila persistente;
* histórico persistente;
* plugins;
* editor de etiquetas;
* perfis de impressora;
* múltiplos agentes;
* painel central em nuvem;
* acesso remoto pela rede;
* imagens PNG ou JPEG;
* WebUSB;
* extensão de navegador;
* internacionalização;
* tema escuro obrigatório;
* prioridades de fila;
* múltiplas filas internas;
* repetição automática de impressão.

---

# 5. Marcos da versão 1.0.0

## Marco 1 — Fundação

Objetivos:

* criar projeto Maven;
* configurar Java 17;
* definir namespace;
* configurar Spring Boot;
* implementar envelope padrão;
* implementar endpoint de status;
* configurar CORS;
* restringir servidor ao loopback;
* configurar logs.

Critério de conclusão:

```text
GET /api/v1/status
```

deverá responder corretamente.

---

## Marco 2 — Modelo e fila

Objetivos:

* criar DTOs;
* criar modelo interno de trabalho;
* criar enums;
* criar máquina de estados;
* implementar fila FIFO;
* implementar histórico em memória;
* implementar cancelamento.

Critério de conclusão:

trabalhos simulados deverão percorrer a fila sem impressora física.

---

## Marco 3 — Impressoras

Objetivos:

* criar `PrinterProvider`;
* detectar sistema operacional;
* implementar listagem no Linux;
* implementar listagem no Windows;
* identificar impressora padrão;
* consultar capacidades;
* atualizar catálogo.

Critério de conclusão:

```text
GET /api/v1/impressoras
```

deverá listar as filas disponíveis.

---

## Marco 4 — Impressão ZPL e RAW

Objetivos:

* implementar estratégia ZPL;
* implementar estratégia RAW;
* enviar bytes sem conversão;
* aplicar configurações;
* respeitar cópias;
* registrar erros.

Critério de conclusão:

uma etiqueta ZPL deverá ser impressa em impressora compatível.

---

## Marco 5 — Impressão PDF no Linux

Objetivos:

* validar Base64;
* decodificar conteúdo;
* usar arquivo temporário quando necessário;
* enviar PDF ao spooler;
* remover arquivo temporário;
* rejeitar PDF no Provider Windows com erro claro nesta versão.

Critério de conclusão:

um PDF válido deverá ser aceito por pelo menos uma impressora homologada no
Linux; no Windows, deverá ser rejeitado com o código documentado até a versão
1.2.0.

---

## Marco 6 — API completa

Objetivos:

* implementar endpoints de trabalhos;
* implementar histórico;
* implementar impressoras;
* implementar testes;
* implementar configurações;
* implementar logs;
* implementar atualizações.

Critério de conclusão:

os exemplos de `API.md` deverão funcionar.

---

## Marco 7 — Web UI

Objetivos:

* Dashboard;
* Impressoras;
* Trabalhos;
* Histórico;
* Teste de impressão;
* Configurações;
* Logs;
* Atualizações;
* Sobre.

Critério de conclusão:

a administração básica deverá funcionar sem Internet.

---

## Marco 8 — Instalação

Objetivos:

* serviço systemd;
* pacote `.deb`;
* serviço Windows;
* instalador Windows;
* runtime Java empacotado;
* configuração externa;
* atalhos;
* desinstalação.

Critério de conclusão:

instalação limpa e atualização deverão preservar configurações.

---

## Marco 9 — Homologação

Objetivos:

* testes automatizados;
* teste Linux;
* teste Windows;
* teste Elgin L42 Pro Full;
* teste PDF;
* teste RAW;
* teste de CORS;
* teste de atualização;
* teste de desinstalação.

Critério de conclusão:

todos os critérios obrigatórios da especificação deverão ser atendidos.

---

# 6. Versão 1.0.1 — Correções iniciais

## Objetivo

Corrigir problemas identificados após a primeira homologação ou implantação.

Possíveis itens:

* correções de descoberta de impressoras;
* ajustes de permissões no Linux;
* correções do serviço Windows;
* melhoria de logs;
* ajustes de CORS;
* correções de instalação;
* correções de timeout;
* melhoria de mensagens;
* correções em arquivos temporários.

Nenhuma nova funcionalidade relevante deverá ser adicionada nessa versão.

---

# 7. Versão 1.1.0 — Estabilidade e diagnóstico

## Objetivo

Aprimorar suporte técnico e diagnóstico sem alterar a arquitetura principal.

Itens candidatos:

* diagnóstico detalhado do Provider;
* verificação guiada do CUPS;
* verificação do spooler do Windows;
* relatório técnico para suporte;
* exportação de diagnóstico;
* melhoria da tela de logs;
* indicadores de ambiente degradado;
* teste de permissões;
* teste de diretório temporário;
* teste de acesso às impressoras;
* mensagens de instalação mais detalhadas.

---

## 7.1 Relatório de diagnóstico

Possível endpoint futuro:

```text
GET /api/v1/diagnostico
```

Possíveis informações:

* versão;
* sistema operacional;
* Java;
* Provider;
* CUPS ou spooler;
* impressoras;
* permissões;
* diretórios;
* porta;
* CORS;
* últimos erros.

O relatório não deverá incluir conteúdo dos documentos.

---

# 8. Versão 1.2.0 — Melhorias de impressão

## Objetivo

Ampliar compatibilidade com drivers e tipos de impressora.

Itens candidatos:

* implementar e homologar impressão PDF no Windows;
* melhor mapeamento de atributos no Windows;
* melhor leitura das capacidades;
* suporte ampliado a cópias;
* suporte a cancelamento no spooler;
* melhoria do modo RAW;
* novos testes internos;
* tratamento avançado de impressoras offline;
* cache controlado de capacidades;
* atualização automática do catálogo;
* normalização ampliada de status.

---

## 8.1 Novos tipos de teste

Possíveis tipos:

```text
ZPL
RAW
PDF
PAGINA_PADRAO
CONFIGURACAO
```

Qualquer novo enum deverá ser documentado.

---

# 9. Versão 1.3.0 — Novos formatos opcionais

## Objetivo

Adicionar formatos de impressão somente quando houver necessidade confirmada.

Tipos candidatos:

```text
image/png
image/jpeg
application/epl
application/cpcl
application/escpos
```

Esses tipos não deverão ser implementados antecipadamente sem caso real.

---

## 9.1 PNG e JPEG

A implementação poderá:

* encaminhar diretamente quando o driver suportar;
* rasterizar por estratégia própria;
* rejeitar quando não suportado.

O comportamento deverá ser documentado por Provider.

---

## 9.2 EPL e CPCL

Poderão ser tratados como conteúdo RAW com tipos explícitos.

Exemplos futuros:

```text
application/epl
application/cpcl
```

---

## 9.3 ESC/POS

Poderá ser adicionado para impressoras de cupom.

Tipo futuro sugerido:

```text
application/escpos
```

---

# 10. Versão 1.4.0 — Perfis de impressão

## Objetivo

Reduzir a necessidade de repetir configurações em todas as requisições.

Esta funcionalidade não pertence à versão 1.

---

## 10.1 Conceito

Um perfil poderá associar:

* nome lógico;
* impressora física;
* configurações padrão;
* tipo de conteúdo;
* observações.

Exemplo futuro:

```json
{
    "nome": "ETIQUETA_PATRIMONIO",
    "impressora": "ElginL42",
    "configuracoesImpressao": {
        "PageSize": "w100h60",
        "PrintDarkness": "20",
        "PrintSpeed": "4"
    }
}
```

---

## 10.2 Uso futuro

```json
{
    "perfil": "ETIQUETA_PATRIMONIO",
    "trabalho": {},
    "documento": {}
}
```

A adoção exigirá revisão dos contratos.

---

# 11. Versão 1.5.0 — Persistência opcional

## Objetivo

Permitir recuperação da fila e do histórico após reinicialização.

Não deverá ser implementada na versão 1.

Possíveis tecnologias:

* arquivo local;
* banco embarcado;
* SQLite;
* armazenamento orientado a eventos.

A escolha deverá priorizar:

* baixa manutenção;
* consistência;
* instalação simples;
* recuperação segura;
* proteção do conteúdo.

---

## 11.1 Regras futuras

A persistência não deverá armazenar documentos indefinidamente.

Poderá armazenar:

* metadados;
* status;
* datas;
* erros;
* configurações.

O conteúdo poderá ser removido após o envio.

---

# 12. Versão 2.0.0 — Evolução maior

A versão 2 somente deverá ser criada quando existirem alterações incompatíveis ou mudança relevante do produto.

Itens candidatos, não aprovados:

* API v2;
* autenticação local;
* múltiplas filas;
* prioridades;
* múltiplos usuários;
* comunicação segura por HTTPS local;
* gerenciamento centralizado;
* descoberta de agentes;
* painel de administração em nuvem;
* execução em contêiner para impressoras de rede;
* gerenciamento de frota;
* telemetria opcional;
* políticas corporativas.

Nenhum desses itens está aprovado automaticamente.

---

# 13. Itens deliberadamente não planejados

Os seguintes itens não deverão ser presumidos como evolução natural:

* editor visual ZPL;
* substituição do JasperReports;
* armazenamento de relatórios;
* gestão de documentos;
* envio de arquivos para a nuvem;
* cadastro de usuários;
* login;
* permissões por departamento;
* regras específicas de prefeitura ou câmara;
* relatórios do sistema Presente;
* geração de código de barras;
* geração de QR Code;
* criação de layout de etiqueta.

Essas responsabilidades pertencem ao sistema cliente.

---

# 14. Dívida técnica aceitável na versão 1

Para priorizar uma versão funcional, poderão ser aceitos temporariamente:

* interface Web simples;
* histórico sem paginação;
* cache simples de impressoras;
* suporte parcial a capacidades no Windows;
* atualização sem instalação totalmente silenciosa;
* apenas pacote `.deb` no Linux;
* suporte inicial apenas a `amd64`;
* logs sem painel avançado;
* ausência de tema escuro;
* ausência de persistência.

Esses itens deverão ser documentados no `CHANGELOG.md` quando relevantes.

---

# 15. Prioridades

## Prioridade P0 — Obrigatória

* API local;
* fila;
* ZPL;
* RAW;
* impressoras;
* CUPS;
* Windows;
* logs;
* segurança local;
* instalação;
* configuração externa.

---

## Prioridade P1 — Importante

* PDF;
* interface Web;
* capacidades;
* histórico;
* atualização;
* testes de impressão;
* pacote `.deb`;
* instalador Windows.

---

## Prioridade P2 — Pode aguardar

* diagnóstico avançado;
* exportação de suporte;
* melhorias visuais;
* atualização totalmente automática;
* formatos adicionais.

---

## Prioridade P3 — Futuro

* perfis;
* persistência;
* autenticação;
* painel central;
* múltiplos agentes;
* plugins.

---

# 16. Critérios para incluir uma funcionalidade

Uma nova funcionalidade somente deverá ser aprovada quando:

1. resolver um problema real;
2. não puder ser resolvida adequadamente pelo sistema cliente;
3. preservar o caráter genérico do agente;
4. não introduzir acoplamento ao Presente;
5. possuir impacto de manutenção aceitável;
6. possuir contrato documentado;
7. possuir testes;
8. funcionar em Windows e Linux, ou declarar claramente a limitação;
9. possuir estratégia de atualização;
10. não enfraquecer a segurança local.

---

# 17. Critérios para rejeitar uma funcionalidade

Uma solicitação deverá ser rejeitada ou movida para o sistema cliente quando:

* depender de regra de negócio;
* depender de entidade do sistema Presente;
* exigir conhecimento do layout;
* transformar o agente em editor;
* exigir armazenamento permanente sem necessidade;
* duplicar função do sistema operacional;
* aumentar significativamente a superfície de segurança;
* funcionar somente para uma impressora sem abstração;
* exigir atualizações frequentes do agente para cada etiqueta.

---

# 18. Compatibilidade da API

A API v1 deverá permanecer estável durante toda a linha 1.x.

Alterações compatíveis:

* novo endpoint;
* novo campo opcional;
* novo código de erro;
* novo tipo de documento opcional;
* nova capacidade;
* novo recurso da interface.

Alterações incompatíveis exigem:

```text
/api/v2
```

Exemplos:

* renomear propriedade;
* remover campo;
* alterar tipo;
* alterar envelope;
* tornar campo opcional obrigatório;
* mudar significado de status.

---

# 19. Compatibilidade do Java

A versão inicial utilizará:

```text
Java 17
```

A atualização para Java 21 ou superior deverá ocorrer somente quando:

* houver benefício concreto;
* o Spring Boot utilizado justificar;
* os instaladores forem atualizados;
* o runtime empacotado for atualizado;
* houver testes em Windows e Linux;
* o impacto de compatibilidade for documentado.

A troca de versão do Java não deverá ser feita apenas por disponibilidade de uma versão mais recente.

---

# 20. Compatibilidade de sistemas operacionais

## 20.1 Windows

Linha 1.x:

```text
Windows 10
Windows 11
```

Versões futuras poderão remover suporte somente em versão principal ou quando o próprio sistema deixar de ser suportado.

---

## 20.2 Linux

Linha 1.x deverá priorizar distribuições com:

* Java 17;
* systemd;
* CUPS;
* arquitetura amd64.

Distribuições-alvo:

```text
Debian
Ubuntu
Linux Mint
```

---

# 21. Homologação por impressora

A homologação deverá registrar:

* fabricante;
* modelo;
* conexão;
* sistema operacional;
* driver;
* linguagem;
* tipos testados;
* capacidades;
* limitações.

Modelo inicial prioritário:

```text
Elgin L42 Pro Full
```

Outros modelos deverão ser adicionados conforme uso real.

---

## 21.1 Registro de homologação sugerido

```text
Modelo: Elgin L42 Pro Full
Conexão: USB
Sistema: Linux Mint
Driver: Elgin/CUPS
Linguagem: ZPL
ZPL: Aprovado
RAW: Aprovado
PDF: A validar
Capacidades: Parcial
Observações: utilizar fila RAW para ZPL
```

Esses registros poderão permanecer em documentação própria no futuro.

---

# 22. Roadmap de documentação

## Versão 1.0.0

Documentos obrigatórios:

```text
ESPECIFICACAO_PRINT_AGENT.md
API.md
JSON.md
WEBUI.md
INSTALACAO.md
ROADMAP.md
CHANGELOG.md
README.md
```

---

## Evoluções futuras

Poderão ser adicionados:

```text
DIAGNOSTICO.md
HOMOLOGACAO_IMPRESSORAS.md
ATUALIZACAO.md
SEGURANCA.md
```

Somente quando o volume de informação justificar documentos separados.

---

# 23. Indicadores de sucesso da versão 1

A versão 1 será considerada bem-sucedida quando:

* puder ser instalada por usuário não desenvolvedor;
* iniciar automaticamente;
* localizar impressoras;
* imprimir ZPL sem janela do navegador;
* funcionar com a Elgin L42 Pro Full;
* permitir integração pelo frontend do Presente;
* funcionar em Windows e Linux;
* não exigir alteração a cada novo layout;
* não exigir geração de PDF para etiquetas ZPL;
* possuir diagnóstico suficiente para suporte;
* preservar configurações em atualizações.

---

# 24. Riscos do Roadmap

## 24.1 PDF no Windows

O suporte não faz parte da versão 1.0.0 e está planejado para a versão 1.2.0.
Sua implementação pode variar conforme driver e Java Print Service.

Mitigação:

* homologar cedo;
* documentar limitações;
* manter ZPL e RAW como caminhos prioritários para térmicas.

---

## 24.2 Serviço sem acesso às impressoras

Serviços podem não enxergar impressoras instaladas apenas para o usuário.

Mitigação:

* testar conta do serviço;
* avaliar execução no contexto do usuário;
* documentar instalação.

---

## 24.3 HTTPS para HTTP local

Navegadores podem impor restrições a aplicações Web HTTPS acessando serviço HTTP local.

Mitigação:

* homologar nos navegadores;
* configurar CORS;
* avaliar alternativas futuras somente se necessário.

---

## 24.4 Drivers inconsistentes

As capacidades podem variar muito.

Mitigação:

* preservar nomes externos;
* não criar lista fixa;
* permitir modo `IGNORAR`;
* documentar por impressora.

---

## 24.5 Atualização automática

Instalação privilegiada pode ser difícil.

Mitigação:

* separar verificação, download e instalação;
* iniciar com atualização assistida;
* evoluir posteriormente.

---

# 25. Processo de revisão do Roadmap

O Roadmap deverá ser revisado:

* antes de iniciar nova versão;
* após homologação;
* após incidentes relevantes;
* quando surgir novo sistema operacional;
* quando mudar a versão mínima do Java;
* quando um item futuro for aprovado.

Cada alteração deverá ser registrada em:

```text
docs/roadmap/CHANGELOG.md
```

---

# 26. Estado dos itens

Valores utilizados:

```text
PLANEJADO
EM_ANALISE
APROVADO
EM_DESENVOLVIMENTO
EM_TESTE
CONCLUIDO
ADIADO
CANCELADO
```

---

## 26.1 Tabela inicial

| Item                 | Versão | Estado     |
| -------------------- | ------ | ---------- |
| Núcleo da API        | 1.0.0  | APROVADO   |
| Fila em memória      | 1.0.0  | APROVADO   |
| Provider Linux       | 1.0.0  | APROVADO   |
| Provider Windows     | 1.0.0  | APROVADO   |
| ZPL                  | 1.0.0  | APROVADO   |
| RAW                  | 1.0.0  | APROVADO   |
| PDF                  | 1.0.0  | APROVADO   |
| Web UI               | 1.0.0  | APROVADO   |
| Instalador Windows   | 1.0.0  | APROVADO   |
| Pacote `.deb`        | 1.0.0  | APROVADO   |
| Diagnóstico avançado | 1.1.0  | PLANEJADO  |
| Novos formatos       | 1.3.0  | EM_ANALISE |
| Perfis               | 1.4.0  | ADIADO     |
| Persistência         | 1.5.0  | ADIADO     |
| Autenticação         | 2.0.0  | ADIADO     |
| Painel central       | 2.0.0  | EM_ANALISE |

---

# 27. Referências

Especificação:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

API:

```text
docs/api/API.md
```

Contratos:

```text
docs/api/JSON.md
```

Interface:

```text
docs/web/WEBUI.md
```

Instalação:

```text
docs/instalacao/INSTALACAO.md
```

Histórico de versões:

```text
docs/roadmap/CHANGELOG.md
```

---
