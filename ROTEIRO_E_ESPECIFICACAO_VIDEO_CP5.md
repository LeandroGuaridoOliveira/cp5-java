# ESPECIFICAÇÃO TÉCNICA E ROTEIRO DE SINCRONIZAÇÃO DE VÍDEO
## Checkpoint 5 (Parte 1 e Parte 2) - Java Advanced (FIAP)
**Curso:** Tecnologia em Análise e Desenvolvimento de Sistemas (TDS) - Turma: **2TDSPV**  
**Professor Responsável:** Dr. Marcel Stefan Wagner  
**Alunos Integrantes:**
1. Leandro Guarido de Oliveira - RM: 561760
2. Kaiky Pereira Rodrigues Da Silva - RM: 564578
3. Gabriel Costa Solano - RM: 562325

**Repositório Oficial:** `https://github.com/LeandroGuaridoOliveira/cp5-java.git`  
**Deploy em Produção (Render):** `https://mercado-express-mvc-90ft.onrender.com`  
**Duração Alvo do Vídeo:** **5 a 7 minutos** (Critério eliminatório da rubrica).

---

## 1. REQUISITOS OFICIAIS DO CHECKPOINT (RUBRICA DE AVALIAÇÃO)

### Parte 1 — Spring Security MVC, Deploy e PostgreSQL:
- Interface Web responsiva para uma empresa do tipo "Mercado Express" com CRUD completo de produtos.
- Persistência em banco de dados relacional PostgreSQL (`mercado_db`) com mapeamento na tabela `TDS_Sec_MVC_TB_Mercado`.
- Autenticação com Spring Security, senhas criptografadas em BCrypt, tela de login customizada e tela de cadastro (Sign Up) com persistência na tabela `TDS_Users_Mercado`.
- Pelo menos 2 perfis de acesso: `ROLE_ADMIN` e `ROLE_USER`.
- Área Restrita (`/area-restrita`) acessível apenas para usuários autenticados.
- Landing page na rota base `index.html` (`/`).
- Deploy do sistema em ambiente de nuvem (Render) via container Docker.

### Parte 2 — Microsserviços com RabbitMQ, API Banco Tranquilo, E-mail e Dashboard:
- Broker de mensageria **RabbitMQ** configurado com Direct Exchange (`mercado.banco.exchange`) e duas filas:
  * Fila de Requisição: `banco.tranquilo.pagamento.requisicao.queue` (Routing Key: `banco.pagamento.requisicao`).
  * Fila de Resposta: `banco.tranquilo.pagamento.resposta.queue` (Routing Key: `banco.pagamento.resposta`).
- Microsserviço simulador da instituição financeira **Banco Tranquilo** (`BancoTranquiloService` e `BancoTranquiloApiController`):
  * Gera protocolo bancário oficial com prefixo `BT-XXXXXXXX`.
  * Regra de aprovação e recusa (ex: cartão com final `0000` retorna status `RECUSADO`).
- Serviço de Notificação por E-mail (`EmailNotificationService`):
  * Disparado automaticamente quando a compra é confirmada com sucesso pela API bancária.
  * Persistência de auditoria de todos os envios no PostgreSQL na tabela `TDS_TB_Emails_Enviados`.
- Tela de Carrinho e Checkout (`/carrinho` e `/carrinho/checkout`):
  * Visualização de itens, cálculo de subtotal/total, seleção de forma de pagamento e finalização.
  * Tela de comprovante oficial com protocolo bancário e baixa atômica de estoque.
- Script de testes automatizados da API (`script-testes-api.ps1`) e coleção Postman (`postman_collection_cp5_parte2.json`).
- **Dashboard Gerencial Executivo** (`/dashboard-gerencia`):
  * Acesso restrito a administradores.
  * 4 gráficos interativos em tempo real via Chart.js:
    1. Vendas vs. Lucros por transação (R$).
    2. Receita por Meio de Pagamento (PIX, Cartão de Crédito, Débito, Boleto).
    3. Estoque Atual por Produto (barras).
    4. Estoque Agrupado por Setor (Limpeza, Hortifruti, Vestuário, etc.).
  * Tabelas consolidadas de histórico de compras e auditoria de e-mails disparados.

---

## 2. ROTEIRO CENA A CENA: AÇÕES VISUAIS E LOCUÇÃO (AUDIO-VIDEO SYNC)

### 🎬 CENA 1: Abertura e Identificação Institucional
- **Janela de Tempo Alvo:** `00:00 - 00:45` (~45s)
- **Elemento Visual em Tela:** IntelliJ IDEA com o arquivo `integrantes.txt` aberto na raiz do projeto, ou a página inicial do sistema no navegador (`http://localhost:8082/`).
- **Texto da Locução:**
> *"Olá, Professor Doutor Marcel Wagner e a todos que estão assistindo. Meu nome é Leandro Guarido de Oliveira, RM 561760, e hoje venho apresentar a entrega unificada do Checkpoint 5 - Parte 1 e Parte 2 da disciplina de Java Advanced, da turma 2TDSPV do curso de Análise e Desenvolvimento de Sistemas da FIAP. O nosso grupo é formado por mim, Leandro Guarido, pelo Kaiky Pereira Rodrigues Da Silva, RM 564578, e pelo Gabriel Costa Solano, RM 562325. O nosso projeto é o Mercado Express, desenvolvido na IDE IntelliJ IDEA utilizando Spring Boot 3, Spring Security, banco de dados relacional PostgreSQL, mensageria assíncrona com RabbitMQ, microsserviço com a API do Banco Tranquilo, serviço de disparo e auditoria de e-mails, e um Dashboard Gerencial completo com gráficos interativos em Chart.js."*

---

### 🎬 CENA 2: Arquitetura de Software e Código-Fonte no IntelliJ IDEA
- **Janela de Tempo Alvo:** `00:45 - 02:05` (~1m 20s)
- **Elementos Visuais em Tela (Caminhos Exatos no IntelliJ):**
  1. `cp5-java/src/main/resources/application.yml`: Mostrar conexão PostgreSQL (`mercado_db`) e broker RabbitMQ (`localhost:5672`).
  2. `cp5-java/src/main/java/br/com/fiap/mercadomvc/config/RabbitMQConfig.java`: Destacar a direct exchange `mercado.banco.exchange` e as queues de requisição e resposta.
  3. `cp5-java/src/main/java/br/com/fiap/mercadomvc/service/BancoTranquiloService.java`: Mostrar a lógica de autorização bancária e geração de protocolo `BT-`.
  4. `cp5-java/src/main/java/br/com/fiap/mercadomvc/service/PagamentoMensageriaService.java`: Mostrar os métodos `@RabbitListener`, atualização de estoque e persistência.
- **Texto da Locução:**
> *"Aqui no IntelliJ IDEA, temos a estrutura técnica do projeto. No caminho `src/main/resources/application.yml`, configuramos a conexão com o banco de dados PostgreSQL `mercado_db`, onde mapeamos as quatro tabelas: a tabela de produtos e estoque `TDS_Sec_MVC_TB_Mercado`, a de usuários `TDS_Users_Mercado`, a de compras `TDS_TB_Compras_Mercado` e a de auditoria de e-mails `TDS_TB_Emails_Enviados`. No pacote `config`, no arquivo `RabbitMQConfig.java`, implementamos a arquitetura de mensageria: criamos a Direct Exchange `mercado.banco.exchange` e as duas filas solicitadas no PDF do Checkpoint: a fila de requisição de pagamento e a fila de resposta do Banco Tranquilo, com suas respectivas routing keys. No pacote `service`, o arquivo `BancoTranquiloService.java` atua como a API da instituição bancária, autorizando transações, gerando os protocolos bancários com prefixo BT e aplicando a regra de recusa de cartões. Já no `PagamentoMensageriaService.java`, temos o orquestrador completo que publica mensagens na fila, consome as respostas com a anotação `@RabbitListener`, atualiza atomicamente o estoque no banco relacional e dispara a notificação de confirmação por e-mail."*

---

### 🎬 CENA 3: Fluxo Web - Catálogo, Carrinho, Checkout e Baixa de Estoque
- **Janela de Tempo Alvo:** `02:05 - 03:35` (~1m 30s)
- **Elementos Visuais em Tela (Navegador Chrome):**
  1. `http://localhost:8082/` (Home Page).
  2. `http://localhost:8082/produtos` (Catálogo com indicação com o cursor do mouse na coluna "Estoque").
  3. `http://localhost:8082/carrinho` (Tabela de itens do carrinho com subtotal e total).
  4. Seleção da opção **PIX**, preenchimento dos dados do comprador e clique em **"Finalizar Pedido com Banco Tranquilo"**.
  5. `http://localhost:8082/carrinho/comprovante/...` (Comprovante com status APROVADO, protocolo `BT-XXXXXXXX` e aviso de e-mail).
  6. Retorno em `http://localhost:8082/produtos` demonstrando o decremento do estoque.
- **Texto da Locução:**
> *"Agora vamos acompanhar o fluxo completo da compra no navegador. Na rota `/produtos`, temos o catálogo do Mercado Express exibindo os itens cadastrados e o estoque atual de cada um. Acessando a tela de Carrinho de Compras em `/carrinho`, o cliente visualiza os produtos adicionados, os subtotais e o valor total calculado. Vamos selecionar o pagamento via PIX, preencher os dados de contato e clicar em finalizar. Neste momento, a aplicação publica a transação na fila do RabbitMQ. A API do Banco Tranquilo consome a requisição, aprova o pagamento e devolve a resposta pela fila de mensageria. Como vemos no comprovante na tela, o pedido foi APROVADO, gerando o protocolo oficial com prefixo BT e confirmando o envio do e-mail. Ao retornarmos ao catálogo de produtos, podemos ver que a baixa no estoque foi efetuada no banco de dados com total consistência."*

---

### 🎬 CENA 4: Execução dos Testes de API Automatizados e Mensageria
- **Janela de Tempo Alvo:** `03:35 - 04:50` (~1m 15s)
- **Elementos Visuais em Tela (PowerShell / Terminal):**
  1. Execução dos comandos:
     ```powershell
     cd E:\Faculdade\cp5\cp5-java
     .\script-testes-api.ps1
     ```
  2. Destaque dos 4 blocos de resposta:
     - Bloco 1 (Ciano): Status `ONLINE` da API do banco e queues RabbitMQ.
     - Bloco 2 (Verde): Compra via PIX aprovada com protocolo e e-mail.
     - Bloco 3 (Amarelo): Compra recusada com cartão `0000` (sem e-mail).
     - Bloco 4 (Tabela): Auditoria de e-mails persistidos na tabela `TDS_TB_Emails_Enviados`.
- **Texto da Locução:**
> *"Agora no PowerShell, entramos no diretório do projeto e executamos o script automatizado `script-testes-api.ps1`, que valida os requisitos de API e testes solicitados pelo professor. Como podemos ver no primeiro teste, o endpoint `/api/banco-tranquilo/status` confirma que a API do banco está online e integrada ao RabbitMQ com a exchange `mercado.banco.exchange` e as filas ativas. No segundo teste, enviamos uma requisição de compra em JSON via PIX. A transação foi processada com sucesso, recebendo o status APROVADO, gerando o protocolo bancário oficial com prefixo BT e disparando a notificação. No terceiro teste, simulamos um cenário de recusa: enviamos uma compra com cartão de crédito terminado em 0000. O Banco Tranquilo rejeita a operação com o status RECUSADO e, seguindo a regra de negócio, nenhum e-mail de confirmação é enviado. Por fim, no quarto teste, consultamos o endpoint de e-mails, comprovando que todas as mensagens enviadas foram devidamente salvas na tabela `TDS_TB_Emails_Enviados` do banco PostgreSQL para fins de auditoria."*

---

### 🎬 CENA 5: Autenticação Administrativa e Dashboard Gerencial Executivo
- **Janela de Tempo Alvo:** `04:50 - 05:50` (~1m 00s)
- **Elementos Visuais em Tela (Navegador Chrome):**
  1. `http://localhost:8082/login` (Login com `admin` e `admin123`).
  2. Redirecionamento para `http://localhost:8082/area-restrita`.
  3. Acesso à rota `http://localhost:8082/dashboard-gerencia`.
  4. Rolagem da página exibindo os 4 gráficos em Chart.js:
     - Gráfico de Vendas vs. Lucro.
     - Gráfico de Receita por Meio de Pagamento.
     - Gráfico de Estoque por Produto.
     - Gráfico de Estoque por Setor.
  5. Apresentação das tabelas de Compras Recentes e Histórico de E-mails Enviados.
- **Texto da Locução:**
> *"Retornando ao navegador, autenticamos com perfil de Administrador para acessar a Área Restrita e o Dashboard Gerencial do Mercado Express, localizado na rota `/dashboard-gerencia`. Este painel executivo foi desenvolvido com Chart.js e consolida indicadores em tempo real através de quatro gráficos interativos: o primeiro compara o volume total de vendas com a margem de lucro realizada em cada pedido. O segundo apresenta a divisão de faturamento pelos meios de pagamento, destacando PIX e cartões. O terceiro gráfico detalha o saldo de estoque atual de cada item do catálogo. E o quarto gráfico agrupa os volumes de estoque por setor da loja, como Limpeza, Hortifruti e Vestuário. Mais abaixo, temos a listagem auditável completa de todas as compras realizadas com os seus respectivos protocolos do Banco Tranquilo, e a tabela de controle de envio de e-mails para os clientes, gravada diretamente na tabela do PostgreSQL."*

---

### 🎬 CENA 6: Encerramento, Repositório GitHub e Deploy
- **Janela de Tempo Alvo:** `05:50 - 06:25` (~35s)
- **Elementos Visuais em Tela:**
  1. `https://github.com/LeandroGuaridoOliveira/cp5-java` (README com prints e documentação).
  2. `https://mercado-express-mvc-90ft.onrender.com` (Aplicação rodando no Render).
  3. Arquivo `integrantes.txt` no IntelliJ.
- **Texto da Locução:**
> *"Para finalizar, todo o código do projeto, os scripts de teste e as instruções de execução estão disponíveis no nosso repositório do GitHub, junto com o link da aplicação hospedada em produção na plataforma Render. Comprovamos assim o cumprimento integral de todos os requisitos do Checkpoint 5, cobrindo Spring Security, PostgreSQL, broker RabbitMQ, microsserviço da API Banco Tranquilo, envio de e-mails e Dashboard Gerencial. Agradecemos a atenção do Professor Doutor Marcel Wagner e nos colocamos à disposição. Muito obrigado!"*

---

## 3. CHECKLIST DE CONFORMIDADE PARA O EDITOR DE VÍDEO
- [ ] Duração total final entre **05:00 e 07:00 minutos**.
- [ ] Áudio da locução perfeitamente sincronizado com as ações visuais do mouse e telas correspondentes.
- [ ] Sem lacunas de silêncio superiores a 2 segundos entre blocos.
- [ ] Transições de cena suaves entre o IntelliJ IDEA, Navegador e PowerShell.
- [ ] Resolução de saída: 1080p (Full HD, 1920x1080), áudio estéreo claro e nivelado.
