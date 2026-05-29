**Nome do Serviço 1:** Cadastro de Pesquisador
**Tipo de operações:** Inserção (Escrita Transacional na base de dados)

**Arquivos envolvidos:**
* UsuarioController.java
* PesquisadorRequestDTO.java
* PesquisadorService.java
* PesquisadorRepository.java

**Arquivos com o código fonte de medição do SLA:**
* teste-cadastro.js

**Data da medição:** 29/05/2026

**Descrição das configurações:**
* **Ambiente da Aplicação:** Servidor Tomcat embutido no Spring Boot rodando localmente (Porta 8080).
* **Persistência:** Banco de Dados Relacional MySQL local (v8.0.44), gerenciado via Hibernate/JPA (Versão 6.6.49) utilizando pool de conexões HikariCP.
* **Ambiente de Teste:** Máquina Windows local, disparos realizados via CLI pelo Grafana K6.

**Testes de carga (SLA):**
* **Latência (Tempo de Resposta Médio - p95):** 194.11 ms
* **Vazão:** 14.63 requisições por segundo (Total de 740 requisições completadas e inseridas).
* **Concorrência:** 20 requisições simultâneas mantidas de forma constante no estágio principal (VUs).
* **Taxa de Erro:** 0.00% (Sucesso absoluto em todas as tentativas).

![Imagem dos testes1](image.png)

![Imagens dos testes1](image-1.png)

**Levantamento de hipóteses:**
Como o sistema possui regras rígidas de negócio e integridade (ex: garantir que o CPF e o E-mail sejam únicos na base). A cada nova inserção, o motor do MySQL precisa verificar, bloquear e atualizar as árvores de índices destas colunas. À medida que a tabela crescer para milhões de registros, o custo computacional destas validações de Unique Constraint freará a vazão máxima de escritas por segundo (req/s).