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

![Imagem do teste1](image.png)

![Imagens do teste1](image-1.png)

**Levantamento de hipóteses:**
Como o sistema possui regras rígidas de negócio e integridade (ex: garantir que o CPF e o E-mail sejam únicos na base). A cada nova inserção, o motor do MySQL precisa verificar, bloquear e atualizar as árvores de índices destas colunas. À medida que a tabela crescer para milhões de registros, o custo computacional destas validações de Unique Constraint freará a vazão máxima de escritas por segundo (req/s).

**Nome do Serviço 2:** Listagem de Patentes (Vitrine)
**Tipo de operações:** Leitura (Consulta/Busca na base de dados)

**Arquivos envolvidos:**
* PatenteController.java
* PatenteService.java
* PatenteRepository.java

**Arquivos com o código fonte de medição do SLA:**
* teste-vitrine.js

**Data da medição:** 29/05/2026

**Descrição das configurações:**
* **Ambiente da Aplicação:** Servidor Tomcat embutido no Spring Boot rodando localmente (Porta 8080).
* **Persistência:** Banco de Dados Relacional MySQL local (v8.0.44), gerenciado via Hibernate/JPA (Versão 6.6.49) utilizando pool de conexões HikariCP.
* **Ambiente de Teste:** Máquina Windows local, disparos realizados via CLI pelo Grafana K6.

**Testes de carga (SLA):**
* **Latência (Tempo de Resposta Médio - p95):** 680.97 ms *(Ultrapassou o SLA estipulado de 500ms)*
* **Vazão:** 12.97 requisições por segundo (Total de 652 requisições completadas).
* **Concorrência:** 20 requisições simultâneas mantidas de forma constante no estágio principal (VUs).
* **Taxa de Erro:** 0.00% (Sucesso absoluto nas respostas HTTP 200).

![Imagem teste2](image-2.png)
![Imagem teste2](image-3.png)

**Levantamento de hipóteses (Potenciais gargalos do sistema que influenciam esta funcionalidade):**
Embora a API tenha suportado a carga sem erros, o tempo de resposta ultrapassou o limite do SLA. As hipóteses para este comportamento em operações de leitura massiva são:

1. **Overfetching e Carga de Rede (Payload Gigante):** Durante os 50 segundos de teste, o servidor trafegou 36 MB de dados apenas devolvendo JSONs. Isso indica que a consulta à Vitrine pode estar trazendo dados demais do banco (ex: carregando junto todos os dados do titular, pesquisadores e a string inteira do resumo da patente em cada item).

2. **Problema de "N+1 Queries" do Hibernate:** Como a entidade `Patente` possui relacionamentos com `Pesquisador` e `Titular`, o ORM pode estar realizando uma consulta inicial para buscar as patentes e, em seguida, disparando múltiplas consultas adicionais para buscar os relacionamentos de cada uma, o que multiplica o tempo de acesso ao banco.
3. **Ausência de Paginação e Cache:** A rota atual aparentemente busca todos os registros de uma vez. À medida que o banco crescer, essa rota sofrerá degradação de performance e risco de esgotamento de memória. Além disso, por ser uma vitrine (dados de alta leitura e baixa alteração), a ausência de uma camada de cache em memória obriga o motor do banco de dados a recalcular e ler o disco em toda requisição, configurando um gargalo claro de I/O.