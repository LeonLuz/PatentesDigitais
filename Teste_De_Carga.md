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

![Imagem do teste1 grafana](image-5.png)
![Imagem do teste1](image.png)
![Imagens do teste1](image-1.png)

**Levantamento de hipóteses:**
Como o sistema possui regras rígidas de negócio e integridade (ex: garantir que o CPF e o E-mail sejam únicos na base). A cada nova inserção, o motor do MySQL precisa verificar, bloquear e atualizar as árvores de índices destas colunas. À medida que a tabela crescer para milhões de registros, o custo computacional destas validações de Unique Constraint freará a vazão máxima de escritas por segundo (req/s).

---

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

![Imagem teste2 grafana](image-4.png)
![Imagem teste2](image-2.png)
![Imagem teste2](image-3.png)

**Levantamento de hipóteses (Potenciais gargalos do sistema que influenciam esta funcionalidade):**
Embora a API tenha suportado a carga sem erros, o tempo de resposta ultrapassou o limite do SLA. As hipóteses para este comportamento em operações de leitura massiva são:

1. **Overfetching e Carga de Rede (Payload Gigante):** Durante os 50 segundos de teste, o servidor trafegou 36 MB de dados apenas devolvendo JSONs. Isso indica que a consulta à Vitrine pode estar trazendo dados demais do banco (ex: carregando junto todos os dados do titular, pesquisadores e a string inteira do resumo da patente em cada item).

2. **Problema de "N+1 Queries" do Hibernate:** Como a entidade `Patente` possui relacionamentos com `Pesquisador` e `Titular`, o ORM pode estar realizando uma consulta inicial para buscar as patentes e, em seguida, disparando múltiplas consultas adicionais para buscar os relacionamentos de cada uma, o que multiplica o tempo de acesso ao banco.
3. **Ausência de Paginação e Cache:** A rota atual aparentemente busca todos os registros de uma vez. À medida que o banco crescer, essa rota sofrerá degradação de performance e risco de esgotamento de memória. Além disso, por ser uma vitrine (dados de alta leitura e baixa alteração), a ausência de uma camada de cache em memória obriga o motor do banco de dados a recalcular e ler o disco em toda requisição, configurando um gargalo claro de I/O.

**Otimização:** Após a análise dos primeiros testes de carga gerados pelo K6, identificamos gargalos críticos de latência e um alto consumo de banda na rota principal da aplicação (`/api/patentes/vitrine`). O tráfego excessivo de rede e o esgotamento do banco de dados elevaram nosso `p(95)` para níveis inaceitáveis.

Para garantir alta disponibilidade e atingir nossa meta de SLA (abaixo de 500ms), refatoramos a arquitetura da rota aplicando os seguintes padrões de projeto:

* **Data Transfer Objects (DTO) e Surrogate Keys:** Criamos o `PatenteVitrineDTO` para blindar a porta de saída da API. Em vez de expor a entidade completa do banco, a API agora devolve apenas os campos essenciais (título, resumo curto) e oculta as chaves primárias numéricas em favor de UUIDs. Isso reduziu o tamanho do payload em mais de 90% e adicionou uma camada extra de segurança contra ataques de enumeração.
* **Mitigação do Problema de N+1 Queries:** O uso de lógicas avançadas de herança (`instanceof` para descobrir o nome real do titular) estava bombardeando o MySQL com múltiplas requisições sequenciais. Resolvemos isso na camada do `Repository` forçando o Spring Data a utilizar um `LEFT JOIN FETCH` via JPQL, resolvendo todo o cruzamento de dados em uma única viagem ao banco.
* **Paginação Estruturada:** Implementamos a interface `Pageable` do Spring Boot para fatiar a entrega dos dados. Em vez de carregar a tabela inteira na memória RAM do servidor Java, a aplicação agora processa e entrega lotes enxutos de 10 itens por vez, garantindo previsibilidade no consumo de recursos.

![Imagem teste2 otimizado](image-10.png)
![Imagem teste2 otimizado](image-11.png)
![Imagem teste2 grafana otimizado](image-12.png)

---

**Nome do Serviço 3:** Realizar Checkout (Aquisição)
**Tipo de operações:** Leitura e Escrita

**Arquivos envolvidos:**

* UsuarioController.java
* PatenteController.java
* CarrinhoController.java
* AquisicaoController.java

**Arquivos com o código fonte de medição do SLA:**

* teste-aquisicao-checkout.js

**Data da medição:** 01/06/2026

**Descrição das configurações:**

* **Ambiente da Aplicação:** Servidor Tomcat embutido no Spring Boot rodando localmente (Porta 8080).

* **Persistência:** Banco de Dados Relacional MySQL local (v8.0.44), gerenciado via Hibernate/JPA (Versão 6.6.49) utilizando pool de conexões HikariCP.

* **Ambiente de Teste:** Máquina Windows local, disparos realizados via Windows PowerShell, com monitoramento em tempo real via InfluxDB 1.8.

**Testes de carga (SLA):**

* **Latência (Tempo de Resposta Médio - p95):** 18.35 ms

* **Vazão:** 21.47 requisições por segundo (Total de 4199 checks realizados).

* **Concorrência:** 20 requisições simultâneas mantidas de forma constante (VUs).

* **Taxa de Erro:** 3.17% (Falhas na validação da transação de aquisição).

![Imagem teste3 grafana](image-6.png)
![Imagem teste3 PowerShell](image-7.png)

**LEVANTAMENTO DE HIPÓTESES dos potenciais gargalos do sistema que influenciam esta funcionalidade:**

**Concorrência no Controle de Estoque (Race Condition):** A taxa de falha de 3.17% pode indicar que o sistema está falhando ao tentar reservar itens em estoque simultaneamente.

**Saturação do Pool de Conexões:** Como a operação de checkout envolve múltiplas tabelas (Carrinho, Estoque, Transação), se o HikariCP estiver com um limite de conexões baixo, as threads do Tomcat podem ficar em estado de WAITING, esperando uma conexão livre com o MySQL, o que explica a variação na latência em cenários de alta concorrência.

**Latência de Escrita (I/O do Banco):** A necessidade de garantir o ACID em múltiplas inserções de tabelas distintas exige que o MySQL faça o flush dos logs de transação para o disco a cada checkout. Tornando-se um gargalo que limita a vazão (Throughput).