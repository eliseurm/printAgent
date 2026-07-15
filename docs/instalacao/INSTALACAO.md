# Print Agent — Instalação, Execução e Atualização

## Guia para Windows e Linux

| Campo               | Valor                       |
| ------------------- | --------------------------- |
| Projeto             | Print Agent                 |
| Sistema relacionado | Presente                    |
| Documento           | INSTALACAO.md               |
| Versão do agente    | 1.0.0                       |
| Versão do documento | 1.0.0                       |
| Java                | 17 LTS                      |
| Namespace           | `br.eng.eliseu.printagent`  |
| Porta padrão        | `18181`                     |
| Status              | Aprovado para implementação |
| Última atualização  | Julho de 2026               |

---

# 1. Objetivo

Este documento descreve:

* pré-requisitos;
* compilação;
* instalação;
* configuração;
* inicialização;
* atualização;
* diagnóstico;
* desinstalação;

do Print Agent em Windows e Linux.

O Print Agent deverá ser instalado no computador que possui acesso local às impressoras utilizadas pelo usuário.

Cenário esperado:

```text
Sistema Presente na nuvem
          |
          | HTTPS
          v
Navegador do usuário
          |
          | HTTP localhost
          v
Print Agent
          |
          | CUPS ou Windows Print Service
          v
Impressora USB ou de rede instalada localmente
```

O backend hospedado no Google Cloud não acessa diretamente a impressora USB.

---

# 2. Localização no projeto Presente

O projeto deverá ser criado na raiz do repositório:

```text
presente/
├── backend/
├── frontend/
├── printAgent/
└── ...
```

Estrutura mínima esperada:

```text
printAgent/
├── docs/
├── installer/
├── src/
├── pom.xml
└── README.md
```

Este documento deverá permanecer em:

```text
printAgent/docs/instalacao/INSTALACAO.md
```

---

# 3. Requisitos gerais

## 3.1 Sistemas operacionais

A versão 1 deverá priorizar:

### Windows

```text
Windows 10
Windows 11
```

### Linux

```text
Debian
Ubuntu
Linux Mint
```

Distribuições Linux deverão utilizar CUPS.

---

## 3.2 Arquitetura

A arquitetura principal esperada é:

```text
x86_64 / amd64
```

Suporte a ARM poderá ser adicionado futuramente.

---

## 3.3 Java

A aplicação deverá utilizar:

```text
Java 17 LTS
```

Para desenvolvimento, deverá existir um JDK 17.

Para distribuição ao usuário final, recomenda-se empacotar um runtime Java com o instalador.

---

## 3.4 Maven

Para compilação:

```text
Maven 3.8 ou superior
```

Versão recomendada:

```text
Maven 3.9.x
```

---

## 3.5 Impressoras

A impressora deverá estar previamente:

* conectada ao computador;
* reconhecida pelo sistema operacional;
* instalada no spooler;
* apta a receber impressões.

O Print Agent não será responsável por instalar drivers de impressoras.

---

# 4. Portas e endereços

## 4.1 Porta padrão

```text
18181
```

---

## 4.2 Endereço padrão

```text
127.0.0.1
```

---

## 4.3 Interface Web

```text
http://localhost:18181
```

---

## 4.4 API

```text
http://localhost:18181/api/v1
```

---

## 4.5 Restrição de acesso

O agente deverá escutar somente no endereço local.

Configuração:

```yaml
server:
  address: 127.0.0.1
  port: 18181
```

Não utilizar:

```yaml
server:
  address: 0.0.0.0
```

A instalação não deverá abrir a porta `18181` no firewall para acesso pela rede.

---

# 5. Compilação do projeto

## 5.1 Acessar a pasta

A partir da raiz do projeto Presente:

```bash
cd printAgent
```

---

## 5.2 Validar o Java

```bash
java -version
```

Resultado esperado:

```text
openjdk version "17..."
```

Validar o compilador:

```bash
javac -version
```

Resultado esperado:

```text
javac 17...
```

---

## 5.3 Validar o Maven

```bash
mvn -version
```

O resultado deverá informar:

* Maven 3.8 ou superior;
* Java 17.

---

## 5.4 Executar testes

```bash
mvn clean test
```

---

## 5.5 Gerar pacote

```bash
mvn clean package
```

Artefato esperado:

```text
target/print-agent-1.0.0.jar
```

---

## 5.6 Gerar sem testes

Permitido apenas para diagnóstico:

```bash
mvn clean package -DskipTests
```

Não deverá ser utilizado no processo normal de publicação.

---

# 6. Execução em desenvolvimento

## 6.1 Spring Boot Maven Plugin

```bash
mvn spring-boot:run
```

---

## 6.2 Perfil de desenvolvimento

```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=dev
```

---

## 6.3 Execução do JAR

```bash
java -jar target/print-agent-1.0.0.jar
```

---

## 6.4 Configuração externa

```bash
java \
  -jar target/print-agent-1.0.0.jar \
  --spring.config.additional-location=file:./config/application.yml
```

---

## 6.5 Porta alternativa

```bash
java \
  -jar target/print-agent-1.0.0.jar \
  --server.port=18182
```

A aplicação cliente deverá utilizar a mesma porta configurada.

---

## 6.6 Validar execução

Abrir:

```text
http://localhost:18181
```

Testar a API:

```bash
curl http://localhost:18181/api/v1/status
```

---

# 7. Configuração inicial

## 7.1 Arquivo principal

```text
application.yml
```

---

## 7.2 Exemplo completo

```yaml
server:
  address: 127.0.0.1
  port: 18181

spring:
  application:
    name: print-agent

  jackson:
    deserialization:
      fail-on-unknown-properties: true

print-agent:
  historico:
    tamanho-maximo: 100

  trabalho:
    timeout-sincrono-segundos: 30

  provider:
    timeout-comando-segundos: 15

  impressao:
    configuracoes-desconhecidas: IGNORAR

  impressoras:
    atualizar-ao-iniciar: true

  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"

  atualizacao:
    habilitada: true
    verificar-ao-iniciar: true
    intervalo-horas: 24
    url-manifesto: "https://downloads.exemplo.com/print-agent/manifesto.json"

  encerramento:
    timeout-segundos: 30

  limites:
    requisicao-maxima-bytes: 20971520
    zpl-maximo-bytes: 2097152
    raw-maximo-bytes: 2097152
    pdf-maximo-bytes: 15728640
    configuracoes-maximas: 50
    tamanho-chave-configuracao: 200
    tamanho-valor-configuracao: 200

logging:
  level:
    root: INFO
    br.eng.eliseu.printagent: INFO
```

---

## 7.3 CORS para desenvolvimento

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "http://localhost:4200"
      - "https://presente.exemplo.com.br"
```

A origem do frontend em produção deverá ser configurada explicitamente.

Não utilizar:

```yaml
origens-permitidas:
  - "*"
```

---

## 7.4 Configurações desconhecidas

Valores permitidos:

```text
IGNORAR
REJEITAR
```

Valor recomendado:

```text
IGNORAR
```

No modo `IGNORAR`, propriedades não suportadas são desconsideradas e registradas,
e a impressão continua quando as propriedades restantes e os padrões do driver
forem suficientes. Se não forem suficientes para concluir a impressão, o trabalho
falha com mensagem clara. No modo `REJEITAR`, qualquer propriedade não suportada
faz o trabalho falhar antes do envio.

---

# 8. Instalação no Linux

## 8.1 Pré-requisitos

Instalar CUPS e ferramentas de cliente:

```bash
sudo apt update
sudo apt install cups cups-client
```

Opcionalmente:

```bash
sudo apt install system-config-printer
```

---

## 8.2 Ativar CUPS

```bash
sudo systemctl enable cups
sudo systemctl start cups
```

Verificar:

```bash
systemctl status cups
```

---

## 8.3 Verificar o agendador

```bash
lpstat -r
```

Resultado esperado:

```text
Agendador está em execução
```

---

## 8.4 Listar impressoras

```bash
lpstat -p -d
```

---

## 8.5 Listar dispositivos

```bash
lpinfo -v
```

---

## 8.6 Consultar opções de uma impressora

```bash
lpoptions -p NOME_DA_IMPRESSORA -l
```

Exemplo:

```bash
lpoptions -p ElginL42 -l
```

---

## 8.7 Testar uma impressão pelo CUPS

Texto simples:

```bash
echo "TESTE" | lp -d NOME_DA_IMPRESSORA
```

Arquivo PDF:

```bash
lp -d NOME_DA_IMPRESSORA arquivo.pdf
```

ZPL RAW:

```bash
lp \
  -d NOME_DA_IMPRESSORA \
  -o raw \
  etiqueta.zpl
```

---

# 9. Instalação manual no Linux

## 9.1 Criar diretórios

```bash
sudo mkdir -p /opt/print-agent
sudo mkdir -p /etc/print-agent
sudo mkdir -p /var/log/print-agent
sudo mkdir -p /var/tmp/print-agent
```

---

## 9.2 Criar usuário

```bash
sudo useradd \
  --system \
  --no-create-home \
  --shell /usr/sbin/nologin \
  printagent
```

Se o usuário já existir, o comando poderá retornar erro sem comprometer a instalação.

---

## 9.3 Copiar o JAR

```bash
sudo cp \
  target/print-agent-1.0.0.jar \
  /opt/print-agent/print-agent.jar
```

---

## 9.4 Copiar configuração

```bash
sudo cp \
  src/main/resources/application.yml \
  /etc/print-agent/application.yml
```

Não sobrescrever uma configuração existente durante atualização sem criar backup.

---

## 9.5 Permissões

```bash
sudo chown -R root:root /opt/print-agent
sudo chmod -R 755 /opt/print-agent

sudo chown -R root:printagent /etc/print-agent
sudo chmod 750 /etc/print-agent
sudo chmod 640 /etc/print-agent/application.yml

sudo chown -R printagent:printagent /var/log/print-agent
sudo chown -R printagent:printagent /var/tmp/print-agent
```

---

## 9.6 Acesso ao CUPS

Adicionar o usuário ao grupo apropriado quando necessário.

Exemplo:

```bash
sudo usermod -aG lp printagent
```

Em algumas distribuições, poderá ser necessário:

```bash
sudo usermod -aG lpadmin printagent
```

O grupo `lpadmin` oferece permissões administrativas sobre impressoras e não deverá ser usado sem necessidade.

Priorizar o grupo `lp`.

---

# 10. Serviço systemd

## 10.1 Arquivo

Criar:

```text
/etc/systemd/system/print-agent.service
```

---

## 10.2 Conteúdo sugerido

```ini
[Unit]
Description=Print Agent
Documentation=http://localhost:18181
After=network.target cups.service
Wants=cups.service

[Service]
Type=simple

User=printagent
Group=printagent

WorkingDirectory=/opt/print-agent

ExecStart=/usr/bin/java \
  -jar /opt/print-agent/print-agent.jar \
  --spring.config.additional-location=file:/etc/print-agent/application.yml

SuccessExitStatus=143

Restart=on-failure
RestartSec=5

NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

Caso o runtime Java seja empacotado:

```ini
ExecStart=/opt/print-agent/runtime/bin/java \
  -jar /opt/print-agent/print-agent.jar \
  --spring.config.additional-location=file:/etc/print-agent/application.yml
```

---

## 10.3 Recarregar systemd

```bash
sudo systemctl daemon-reload
```

---

## 10.4 Habilitar inicialização

```bash
sudo systemctl enable print-agent
```

---

## 10.5 Iniciar

```bash
sudo systemctl start print-agent
```

---

## 10.6 Consultar status

```bash
systemctl status print-agent
```

---

## 10.7 Reiniciar

```bash
sudo systemctl restart print-agent
```

---

## 10.8 Parar

```bash
sudo systemctl stop print-agent
```

---

## 10.9 Logs do systemd

```bash
journalctl -u print-agent
```

Últimas linhas:

```bash
journalctl \
  -u print-agent \
  -n 100 \
  --no-pager
```

Acompanhamento:

```bash
journalctl \
  -u print-agent \
  -f
```

---

# 11. Pacote Debian

## 11.1 Nome sugerido

```text
print-agent_1.0.0_amd64.deb
```

---

## 11.2 Conteúdo esperado

```text
/opt/print-agent/
/etc/print-agent/
/var/log/print-agent/
/var/tmp/print-agent/
/etc/systemd/system/print-agent.service
```

---

## 11.3 Instalação

```bash
sudo apt install ./print-agent_1.0.0_amd64.deb
```

Alternativa:

```bash
sudo dpkg -i print-agent_1.0.0_amd64.deb
sudo apt --fix-broken install
```

Preferir `apt`.

---

## 11.4 Scripts do pacote

O pacote poderá possuir:

```text
preinst
postinst
prerm
postrm
```

---

## 11.5 postinst

Deverá:

1. criar usuário de serviço;
2. ajustar permissões;
3. preservar configuração existente;
4. recarregar systemd;
5. habilitar serviço;
6. iniciar ou reiniciar serviço.

---

## 11.6 prerm

Deverá:

1. interromper o serviço;
2. desabilitar o serviço quando houver remoção definitiva.

---

## 11.7 Atualização do pacote

```bash
sudo apt install ./print-agent_1.1.0_amd64.deb
```

A atualização deverá preservar:

```text
/etc/print-agent/application.yml
```

e:

```text
/var/log/print-agent/
```

---

## 11.8 Configuração como conffile

O arquivo:

```text
/etc/print-agent/application.yml
```

deverá ser tratado como arquivo de configuração do pacote.

Alterações feitas pelo usuário não deverão ser sobrescritas silenciosamente.

---

# 12. Diagnóstico no Linux

## 12.1 Agente não inicia

```bash
systemctl status print-agent
```

```bash
journalctl \
  -u print-agent \
  -n 200 \
  --no-pager
```

---

## 12.2 Porta não responde

```bash
curl http://localhost:18181/api/v1/status
```

Verificar processo:

```bash
ss -ltnp | grep 18181
```

---

## 12.3 CUPS indisponível

```bash
systemctl status cups
```

```bash
lpstat -r
```

---

## 12.4 Nenhuma impressora encontrada

```bash
lpstat -p -d
```

Executar como o usuário do serviço:

```bash
sudo -u printagent lpstat -p -d
```

Se o usuário comum listar impressoras, mas o usuário `printagent` não listar, existe problema de permissão ou sessão.

---

## 12.5 Testar impressão como usuário do serviço

```bash
echo "TESTE" |
sudo -u printagent \
lp -d NOME_DA_IMPRESSORA
```

---

## 12.6 Verificar opções

```bash
sudo -u printagent \
lpoptions \
-p NOME_DA_IMPRESSORA \
-l
```

---

## 12.7 Erro de acesso ao log

```bash
ls -ld /var/log/print-agent
```

Corrigir:

```bash
sudo chown -R printagent:printagent /var/log/print-agent
```

---

## 12.8 Erro no diretório temporário

```bash
ls -ld /var/tmp/print-agent
```

Corrigir:

```bash
sudo chown -R printagent:printagent /var/tmp/print-agent
sudo chmod 700 /var/tmp/print-agent
```

---

# 13. Impressora Elgin L42 Pro Full no Linux

## 13.1 Detecção USB

```bash
lsusb
```

Exemplo esperado:

```text
ELGIN L42PRO FULL
```

---

## 13.2 Detecção pelo CUPS

```bash
lpinfo -v
```

Exemplo:

```text
direct usb://ELGIN/L42PRO%20FULL?serial=XXXXXXXXXX
```

---

## 13.3 Driver

A impressora deverá possuir uma fila no CUPS.

Exemplo:

```text
ElginL42
```

---

## 13.4 Consultar capacidades

```bash
lpoptions -p ElginL42 -l
```

---

## 13.5 Teste ZPL

Criar:

```text
teste.zpl
```

Conteúdo:

```zpl
^XA
^PW800
^LL480
^FO20,20
^A0N,35,35
^FDTESTE PRINT AGENT^FS
^FO20,80
^A0N,25,25
^FDELGIN L42 PRO FULL^FS
^XZ
```

Enviar:

```bash
lp \
  -d ElginL42 \
  -o raw \
  teste.zpl
```

Se o ZPL não for interpretado, verificar o modo de linguagem configurado na impressora.

---

# 14. Instalação no Windows

## 14.1 Formato

A distribuição deverá utilizar:

```text
.msi
```

ou:

```text
.exe
```

O formato recomendado é `.msi` quando o processo de empacotamento permitir.

---

## 14.2 Estrutura sugerida

Aplicação:

```text
C:\Program Files\Print Agent\
```

Configuração e logs:

```text
C:\ProgramData\PrintAgent\
```

Estrutura:

```text
C:\Program Files\Print Agent\
├── app\
│   └── print-agent.jar
└── runtime\
    └── bin\
        └── java.exe

C:\ProgramData\PrintAgent\
├── config\
│   └── application.yml
├── logs\
└── temp\
```

---

## 14.3 Instalação guiada

O instalador deverá:

1. validar a arquitetura;
2. instalar a aplicação;
3. instalar o runtime Java 17 empacotado;
4. criar configuração inicial;
5. preservar configuração existente;
6. criar diretórios de log e temporários;
7. registrar o serviço;
8. iniciar o serviço;
9. testar o endpoint de status;
10. criar atalho para a interface Web.

---

# 15. Serviço do Windows

## 15.1 Nome interno

```text
PrintAgent
```

---

## 15.2 Nome de exibição

```text
Print Agent
```

---

## 15.3 Descrição

```text
Agente local responsável por receber e encaminhar trabalhos de impressão.
```

---

## 15.4 Conta do serviço

A conta utilizada deverá possuir acesso às impressoras necessárias.

Essa decisão deverá ser validada em ambiente real.

Impressoras instaladas somente para um usuário podem não ficar visíveis a serviços executados como `LocalSystem`.

Quando necessário, o agente deverá ser executado:

* com uma conta de serviço específica; ou
* no contexto do usuário conectado.

A estratégia final deverá ser testada no instalador.

---

## 15.5 Inicialização automática

O serviço deverá utilizar:

```text
Automatic
```

ou:

```text
Automatic (Delayed Start)
```

O início atrasado poderá ajudar quando drivers ou spooler demorarem para iniciar.

---

## 15.6 Recuperação

Configurar:

```text
Primeira falha: reiniciar serviço
Segunda falha: reiniciar serviço
Falhas seguintes: reiniciar serviço
```

---

## 15.7 Serviço via WinSW

Uma opção de implementação é utilizar WinSW.

Estrutura:

```text
PrintAgent.exe
PrintAgent.xml
```

Exemplo conceitual:

```xml
<service>
    <id>PrintAgent</id>

    <name>Print Agent</name>

    <description>
        Agente local de impressão.
    </description>

    <executable>
        C:\Program Files\Print Agent\runtime\bin\java.exe
    </executable>

    <arguments>
        -jar
        "C:\Program Files\Print Agent\app\print-agent.jar"
        --spring.config.additional-location=file:C:\ProgramData\PrintAgent\config\application.yml
    </arguments>

    <logpath>
        C:\ProgramData\PrintAgent\logs
    </logpath>

    <startmode>Automatic</startmode>
</service>
```

Os caminhos deverão ser validados e escapados corretamente.

---

## 15.8 Serviço via outro mecanismo

O projeto poderá utilizar:

* Apache Commons Daemon Procrun;
* jpackage;
* instalador com wrapper próprio.

A escolha deverá preservar os requisitos deste documento.

---

# 16. Instalação manual no Windows

## 16.1 Copiar arquivos

Criar:

```text
C:\Program Files\Print Agent\app
C:\Program Files\Print Agent\runtime
C:\ProgramData\PrintAgent\config
C:\ProgramData\PrintAgent\logs
C:\ProgramData\PrintAgent\temp
```

---

## 16.2 Configuração

Copiar:

```text
application.yml
```

para:

```text
C:\ProgramData\PrintAgent\config\application.yml
```

---

## 16.3 Teste em console

PowerShell:

```powershell
& "C:\Program Files\Print Agent\runtime\bin\java.exe" `
  -jar "C:\Program Files\Print Agent\app\print-agent.jar" `
  "--spring.config.additional-location=file:C:\ProgramData\PrintAgent\config\application.yml"
```

---

## 16.4 Verificar status

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:18181/api/v1/status"
```

---

# 17. Diagnóstico no Windows

## 17.1 Serviço

PowerShell:

```powershell
Get-Service PrintAgent
```

---

## 17.2 Iniciar

```powershell
Start-Service PrintAgent
```

---

## 17.3 Reiniciar

```powershell
Restart-Service PrintAgent
```

---

## 17.4 Parar

```powershell
Stop-Service PrintAgent
```

---

## 17.5 Verificar porta

```powershell
Get-NetTCPConnection `
  -LocalPort 18181 `
  -ErrorAction SilentlyContinue
```

---

## 17.6 Testar API

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:18181/api/v1/status" `
  -Method Get
```

---

## 17.7 Listar impressoras

PowerShell:

```powershell
Get-Printer
```

A lista deverá ser comparada com:

```text
GET /api/v1/impressoras
```

---

## 17.8 Impressora não aparece

Verificar:

* se foi instalada para todos os usuários;
* qual conta executa o serviço;
* se a conta do serviço possui acesso;
* se o spooler está ativo;
* se o driver está instalado.

---

## 17.9 Spooler do Windows

Consultar:

```powershell
Get-Service Spooler
```

Reiniciar:

```powershell
Restart-Service Spooler
```

---

## 17.10 Logs

Diretório:

```text
C:\ProgramData\PrintAgent\logs
```

---

# 18. Atalho da interface

## 18.1 Nome

```text
Abrir Print Agent
```

---

## 18.2 Destino

```text
http://localhost:18181
```

O atalho poderá utilizar o navegador padrão.

---

## 18.3 Área de trabalho

A criação de atalho na área de trabalho deverá ser opcional.

---

## 18.4 Menu Iniciar

O instalador deverá criar grupo:

```text
Print Agent
```

Com itens:

```text
Abrir Print Agent
Documentação
Desinstalar
```

---

# 19. Atualização

## 19.1 Versionamento

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

---

## 19.2 Manifesto

A aplicação deverá consultar um manifesto HTTPS.

Exemplo:

```json
{
    "produto": "print-agent",
    "versao": "1.1.0",
    "canal": "estavel",
    "publicadoEm": "2026-09-01T10:00:00-03:00",
    "notas": "Correções de impressão no Windows.",
    "artefatos": {
        "windows": {
            "url": "https://downloads.exemplo.com/print-agent/1.1.0/print-agent.msi",
            "sha256": "HASH"
        },
        "linux-deb": {
            "url": "https://downloads.exemplo.com/print-agent/1.1.0/print-agent.deb",
            "sha256": "HASH"
        }
    }
}
```

---

## 19.3 Validação

Antes de instalar:

* confirmar HTTPS;
* baixar arquivo;
* calcular SHA-256;
* comparar com manifesto;
* rejeitar arquivo divergente.

---

## 19.4 Atualização no Linux

```bash
sudo apt install ./print-agent_1.1.0_amd64.deb
```

---

## 19.5 Atualização no Windows

Executar o novo instalador.

O instalador deverá:

1. parar o serviço;
2. substituir executáveis;
3. preservar configuração;
4. preservar logs;
5. iniciar o serviço;
6. validar o endpoint.

---

## 19.6 Trabalhos pendentes

Antes da atualização, informar:

```text
Os trabalhos ainda não concluídos poderão ser perdidos durante a atualização.
```

A instalação automática deverá preferir aguardar a fila ficar vazia.

---

## 19.7 Falha de atualização

A versão atual deverá permanecer instalada sempre que possível.

Falha na verificação remota não deverá interromper impressões.

---

# 20. Backup da configuração

## 20.1 Antes da atualização

Criar cópia:

```text
application.yml.bak
```

ou:

```text
application.2026-07-15T184000.yml.bak
```

---

## 20.2 Linux

```text
/etc/print-agent/backup/
```

---

## 20.3 Windows

```text
C:\ProgramData\PrintAgent\config\backup\
```

---

## 20.4 Retenção

Manter quantidade limitada de backups.

Valor sugerido:

```text
5
```

---

# 21. Desinstalação no Linux

## 21.1 Remover pacote

```bash
sudo apt remove print-agent
```

---

## 21.2 Remover também configurações

```bash
sudo apt purge print-agent
```

A diferença entre `remove` e `purge` deverá ser respeitada.

---

## 21.3 Remoção manual

```bash
sudo systemctl stop print-agent
sudo systemctl disable print-agent
sudo rm /etc/systemd/system/print-agent.service
sudo systemctl daemon-reload
sudo rm -rf /opt/print-agent
```

Remoção opcional:

```bash
sudo rm -rf /etc/print-agent
sudo rm -rf /var/log/print-agent
sudo rm -rf /var/tmp/print-agent
```

Não remover configuração e logs sem confirmação explícita.

---

# 22. Desinstalação no Windows

O desinstalador deverá:

1. parar o serviço;
2. remover o serviço;
3. remover atalhos;
4. remover executáveis;
5. perguntar se deve preservar dados.

Dados preserváveis:

```text
C:\ProgramData\PrintAgent\config
C:\ProgramData\PrintAgent\logs
```

---

# 23. Verificação pós-instalação

## 23.1 Serviço

Confirmar que está em execução.

---

## 23.2 API

```bash
curl http://localhost:18181/api/v1/status
```

---

## 23.3 Interface

Abrir:

```text
http://localhost:18181
```

---

## 23.4 Impressoras

Consultar:

```bash
curl http://localhost:18181/api/v1/impressoras
```

---

## 23.5 Teste

Realizar impressão pela tela:

```text
Teste de impressão
```

---

## 23.6 CORS

Testar a partir do frontend autorizado.

---

## 23.7 Acesso externo

Confirmar que outro computador não consegue acessar:

```text
http://IP_DA_MAQUINA:18181
```

---

# 24. Checklist de instalação Linux

```text
[ ] Java 17 ou runtime empacotado disponível
[ ] CUPS instalado
[ ] CUPS ativo
[ ] Impressora cadastrada
[ ] lpstat lista a impressora
[ ] Usuário do serviço criado
[ ] Usuário possui acesso à impressora
[ ] Diretório da aplicação criado
[ ] Configuração externa instalada
[ ] Diretório de logs gravável
[ ] Diretório temporário gravável
[ ] Serviço systemd instalado
[ ] Serviço habilitado
[ ] Serviço iniciado
[ ] Porta 18181 responde
[ ] Interface Web abre
[ ] API lista impressoras
[ ] Impressão de teste funciona
```

---

# 25. Checklist de instalação Windows

```text
[ ] Sistema Windows suportado
[ ] Runtime Java 17 instalado ou empacotado
[ ] Impressora instalada
[ ] Spooler ativo
[ ] Aplicação copiada
[ ] Configuração externa criada
[ ] Diretório de logs gravável
[ ] Diretório temporário gravável
[ ] Serviço registrado
[ ] Conta do serviço enxerga as impressoras
[ ] Serviço iniciado
[ ] Porta 18181 responde
[ ] Interface Web abre
[ ] API lista impressoras
[ ] Impressão de teste funciona
```

---

# 26. Problemas comuns

## 26.1 Porta em uso

Sintoma:

```text
Address already in use
```

Solução:

* identificar o processo;
* alterar a porta;
* atualizar o frontend;
* reiniciar o serviço.

Linux:

```bash
ss -ltnp | grep 18181
```

Windows:

```powershell
Get-NetTCPConnection -LocalPort 18181
```

---

## 26.2 Nenhuma impressora localizada

Verificar:

* instalação da impressora;
* usuário do serviço;
* spooler;
* CUPS;
* driver;
* sessão do usuário.

---

## 26.3 Impressora aparece, mas não imprime

Verificar:

* status;
* fila pausada;
* driver;
* modo RAW;
* linguagem ZPL;
* permissões;
* teste fora do agente.

---

## 26.4 ZPL impresso como texto

A impressora ou a fila não está interpretando ZPL.

Verificar:

* linguagem configurada;
* driver;
* envio RAW;
* emulação ZPL;
* firmware.

---

## 26.5 PDF sai rotacionado ou dimensionado incorretamente

Verificar:

* tamanho real do PDF;
* `PageSize`;
* orientação;
* área imprimível;
* driver;
* capacidades retornadas.

O Print Agent não deverá corrigir automaticamente o layout.

---

## 26.6 Navegador não conecta

Verificar:

* serviço;
* porta;
* CORS;
* URL;
* console do navegador;
* HTTPS da aplicação chamando HTTP local.

A integração de uma página HTTPS com um serviço HTTP local deverá ser validada nos navegadores suportados.

---

## 26.7 Erro de CORS

Adicionar exatamente a origem do frontend.

Exemplo:

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"
```

Reiniciar o agente quando necessário.

---

## 26.8 Serviço inicia, mas impressoras não aparecem

Executar os comandos sob a mesma conta do serviço.

Linux:

```bash
sudo -u printagent lpstat -p -d
```

Windows:

validar a conta configurada no serviço.

---

# 27. Logs

## 27.1 Linux

```text
/var/log/print-agent/print-agent.log
```

---

## 27.2 Windows

```text
C:\ProgramData\PrintAgent\logs\print-agent.log
```

---

## 27.3 Rotação

Configuração sugerida:

```text
20 MB por arquivo
30 dias
500 MB no total
```

---

## 27.4 Informações para suporte

Ao abrir um chamado, coletar:

* versão;
* sistema operacional;
* Java;
* nome da impressora;
* status da impressora;
* UUID do trabalho;
* horário aproximado;
* arquivo de log;
* resultado de `/api/v1/status`;
* resultado de `/api/v1/impressoras`.

Não compartilhar documentos impressos sem necessidade.

---

# 28. Segurança de instalação

## 28.1 Privilégios

O agente deverá executar com o menor privilégio possível.

---

## 28.2 Rede

Não abrir porta para a rede.

---

## 28.3 Configuração

O arquivo deverá ser protegido contra alteração por usuários não autorizados.

---

## 28.4 Atualização

Somente artefatos HTTPS e com hash válido deverão ser aceitos.

---

## 28.5 Diretório temporário

Apenas o usuário do serviço deverá possuir acesso.

---

## 28.6 Comandos

A aplicação não deverá executar comandos fornecidos pelo cliente.

---

# 29. Critérios de aceite da instalação

A instalação será considerada concluída quando:

1. existir instalador Windows;
2. existir pacote `.deb`;
3. o runtime Java 17 estiver disponível;
4. o serviço iniciar automaticamente;
5. a configuração estiver fora do JAR;
6. os logs estiverem em diretório externo;
7. a porta responder somente em loopback;
8. a interface Web abrir;
9. a API responder;
10. impressoras forem listadas;
11. uma impressão de teste funcionar;
12. a atualização preservar configuração;
13. a desinstalação remover o serviço;
14. configuração e logs puderem ser preservados;
15. os checklists deste documento forem atendidos.

---

# 30. Referências

Especificação principal:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

API:

```text
docs/api/API.md
```

Contratos JSON:

```text
docs/api/JSON.md
```

Interface Web:

```text
docs/web/WEBUI.md
```

Roadmap:

```text
docs/roadmap/ROADMAP.md
```

Histórico:

```text
docs/roadmap/CHANGELOG.md
```

---
