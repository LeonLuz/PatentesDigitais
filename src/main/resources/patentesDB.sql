create schema if not exists patentesdb;
use patentesdb;

-- Observação: usamos binary(16) para UUID por não existir nativamente no MySQL

create table if not exists USUARIO (
    id_usuario binary(16) PRIMARY KEY,
    email varchar(100) not null,
    senha varchar(255) not null,
    telefone varchar(20),
    endereco varchar(250),
    tipo_perfil enum('PESQUISADOR', 'ORGANIZACAO', 'NIT') not null,
    status_auth boolean,
    status_ativo boolean,
    data_criacao timestamp
    );

create table if not exists ORGANIZACAO (
    id_usuario binary(16) PRIMARY KEY,
    CNPJ char(14) not null UNIQUE KEY,
    razao_social varchar(150) not null,
    CONSTRAINT fk_usuario_organizacao
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
    ON DELETE CASCADE
    );


create table if not exists NIT (
    id_usuario binary(16) PRIMARY KEY,
    CNPJ char(14) not null UNIQUE KEY,
    razao_social varchar(150) not null,
    CONSTRAINT fk_usuario_nit
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
    ON DELETE CASCADE
    );

create table if not exists PESQUISADOR (
    id_usuario binary(16) PRIMARY KEY,
    id_nit binary(16),
    CPF char(11) not null UNIQUE KEY,
    nome varchar(150),
    disponibilidade_consultoria boolean,
    CONSTRAINT fk_usuario_pesquisador
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario),
    CONSTRAINT fk_nit_pesquisador
    FOREIGN KEY (id_nit) references NIT(id_usuario)
    );

create table if not exists PATENTE (
    id_patente binary(16) PRIMARY KEY,
    id_titular binary(16) not null,
    titulo varchar(200) not null,
    num_deposito varchar(17) not null,
    resumo TEXT,
    area varchar(150),
    valor decimal(10,2) not null,
    pesquisadores text,
    documento varchar(255),
    status enum('RASCUNHO', 'REPROVADA', 'DISPONIVEL',
                'EM_PROCESSO_DE_COMPRA', 'CEDIDA', 'LICENCIADA', 'EXCLUIDA') not null,
    CONSTRAINT fk_titular_patente
    FOREIGN KEY (id_titular) references USUARIO(id_usuario)
    ON DELETE CASCADE
    );

create table if not exists PATENTE_PESQUISADOR (
    id_patente binary(16) not null,
    id_pesquisador binary(16) not null,
    CONSTRAINT pk_patente_pesquisador
    PRIMARY KEY (id_patente, id_pesquisador),
    CONSTRAINT fk_patente_pesquisador
    FOREIGN KEY (id_patente) references PATENTE(id_patente)
    ON DELETE CASCADE,
    CONSTRAINT fk_pesquisador_patente
    FOREIGN KEY (id_pesquisador) references PESQUISADOR(id_usuario)
    ON DELETE CASCADE
    );

create table if not exists AQUISICAO (
    id_aquisicao binary(16) PRIMARY KEY,
    id_usuario binary(16) not null,
    data_aquisicao date not null,
    data_expiracao date,
    status enum('AGUARDANDO_PAGAMENTO','ANALISE_NIT', 'CONCLUIDA',
                'CANCELADA') not null,
    CONSTRAINT fk_usuario_aquisicao
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
    );

create table if not exists ITEM_AQUISICAO (
    id_item binary(16) PRIMARY KEY,
    id_aquisicao binary(16) not null,
    id_patente binary(16) not null,
    tipo_aquisicao enum('LICENCIAMENTO', 'CESSAO') not null,
    fim_licenca date,
    CONSTRAINT fk_aquisicao_item_aquisicao
    FOREIGN KEY (id_aquisicao) references AQUISICAO(id_aquisicao)
    ON DELETE CASCADE,
    CONSTRAINT fk_patente_item_aquisicao
    FOREIGN KEY (id_patente) references PATENTE(id_patente)
    );

create table if not exists CARRINHO (
    id_carrinho binary(16) PRIMARY KEY,
    id_usuario binary(16) not null UNIQUE KEY,
    CONSTRAINT fk_usuario_carrinho
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
    );

create table if not exists ITEM_CARRINHO (
    id_item binary(16) PRIMARY KEY,
    id_carrinho binary(16) not null,
    id_patente binary(16) not null,
    CONSTRAINT fk_carrinho_item_carrinho
    FOREIGN KEY (id_carrinho) references CARRINHO(id_carrinho)
    ON DELETE CASCADE,
    CONSTRAINT fk_patente_item_carrinho
    FOREIGN KEY (id_patente) references PATENTE(id_patente)
    );

-- CARGA INICIAL

-- GERAÇÃO DE UUIDs NAS VARIÁVEIS

-- NIT
SET @nit_unirio = UUID_TO_BIN(UUID());

-- Pesquisadores
SET @pesq01 = UUID_TO_BIN(UUID()); SET @pesq02 = UUID_TO_BIN(UUID()); SET @pesq03 = UUID_TO_BIN(UUID()); SET @pesq04 = UUID_TO_BIN(UUID()); SET @pesq05 = UUID_TO_BIN(UUID());
SET @pesq06 = UUID_TO_BIN(UUID()); SET @pesq07 = UUID_TO_BIN(UUID()); SET @pesq08 = UUID_TO_BIN(UUID()); SET @pesq09 = UUID_TO_BIN(UUID()); SET @pesq10 = UUID_TO_BIN(UUID());

-- Organizações
SET @org01 = UUID_TO_BIN(UUID()); SET @org02 = UUID_TO_BIN(UUID()); SET @org03 = UUID_TO_BIN(UUID()); SET @org04 = UUID_TO_BIN(UUID()); SET @org05 = UUID_TO_BIN(UUID());
SET @org06 = UUID_TO_BIN(UUID()); SET @org07 = UUID_TO_BIN(UUID()); SET @org08 = UUID_TO_BIN(UUID()); SET @org09 = UUID_TO_BIN(UUID()); SET @org10 = UUID_TO_BIN(UUID());

-- Patentes
SET @pat01 = UUID_TO_BIN(UUID()); SET @pat02 = UUID_TO_BIN(UUID()); SET @pat03 = UUID_TO_BIN(UUID()); SET @pat04 = UUID_TO_BIN(UUID()); SET @pat05 = UUID_TO_BIN(UUID());
SET @pat06 = UUID_TO_BIN(UUID()); SET @pat07 = UUID_TO_BIN(UUID()); SET @pat08 = UUID_TO_BIN(UUID()); SET @pat09 = UUID_TO_BIN(UUID()); SET @pat10 = UUID_TO_BIN(UUID());

-- Aquisições
SET @aq01 = UUID_TO_BIN(UUID()); SET @aq02 = UUID_TO_BIN(UUID()); SET @aq03 = UUID_TO_BIN(UUID()); SET @aq04 = UUID_TO_BIN(UUID()); SET @aq05 = UUID_TO_BIN(UUID());
SET @aq06 = UUID_TO_BIN(UUID()); SET @aq07 = UUID_TO_BIN(UUID()); SET @aq08 = UUID_TO_BIN(UUID()); SET @aq09 = UUID_TO_BIN(UUID()); SET @aq10 = UUID_TO_BIN(UUID());

-- Itens Aquisição
SET @ia01 = UUID_TO_BIN(UUID()); SET @ia02 = UUID_TO_BIN(UUID()); SET @ia03 = UUID_TO_BIN(UUID());

-- Carrinhos
SET @car01 = UUID_TO_BIN(UUID()); SET @car02 = UUID_TO_BIN(UUID()); SET @car03 = UUID_TO_BIN(UUID()); SET @car04 = UUID_TO_BIN(UUID()); SET @car05 = UUID_TO_BIN(UUID());
SET @car06 = UUID_TO_BIN(UUID()); SET @car07 = UUID_TO_BIN(UUID()); SET @car08 = UUID_TO_BIN(UUID()); SET @car09 = UUID_TO_BIN(UUID()); SET @car10 = UUID_TO_BIN(UUID());

-- Itens Carrinho
SET @ic01 = UUID_TO_BIN(UUID()); SET @ic02 = UUID_TO_BIN(UUID());

-- NIT (UNIRIO)
INSERT INTO USUARIO (id_usuario, email, senha, telefone, endereco, tipo_perfil, status_auth, status_ativo, data_criacao) VALUES
    (@nit_unirio, 'nit@unirio.br', 'nit_unirio_sec', '2125420000', 'Av. Pasteur, 296 - Urca, RJ', 'NIT', true, true, '2024-01-01 09:00:00');

-- Pesquisadores
INSERT INTO USUARIO (id_usuario, email, senha, telefone, endereco, tipo_perfil, status_auth, status_ativo, data_criacao) VALUES
    (@pesq01, 'izabel.paixao@unirio.br', 'hash_sec_01', '11988880001', 'Rua das Flores, 123, SP', 'PESQUISADOR', false, true, '2024-01-10 10:00:00'),
    (@pesq02, 'marcia.feijo@unirio.br', 'hash_sec_02', '21977770002', 'Av. Pasteur, 250, RJ', 'PESQUISADOR', true, true, '2024-01-12 11:00:00'),
    (@pesq03, 'mariano.pimentel@unirio.br', 'hash_sec_03', '11966660003', 'Rua do Matão, 101, SP', 'PESQUISADOR', true, true, '2024-01-15 09:30:00'),
    (@pesq04, 'cristina.takeiti@unirio.br', 'hash_sec_04', '19955550004', 'Rua Sérgio Buarque, 12, Campinas', 'PESQUISADOR', true, true, '2024-01-18 14:20:00'),
    (@pesq05, 'julia.matheus@unirio.br', 'hash_sec_05', '31944440005', 'Av. Antônio Carlos, 6627, BH', 'PESQUISADOR', false, true, '2024-01-20 08:00:00'),
    (@pesq06, 'elvino.santana@unirio.br', 'hash_sec_06', '11933330006', 'Rua Pedro Vicente, 625, SP', 'PESQUISADOR', true, true, '2024-01-22 16:45:00'),
    (@pesq07, 'danielle.paula@unirio.br', 'hash_sec_07', '41922220007', 'Rua XV de Novembro, 1299, Curitiba', 'PESQUISADOR', false, true, '2024-01-25 10:15:00'),
    (@pesq08, 'maria.koblitz@unirio.br', 'hash_sec_08', '51911110008', 'Av. Paulo Gama, 110, Porto Alegre', 'PESQUISADOR', true, true, '2024-01-28 13:00:00'),
    (@pesq09, 'guilherme.silva@unirio.br', 'hash_sec_09', '71900000009', 'Rua Augusto Viana, s/n, Salvador', 'PESQUISADOR', true, true, '2024-02-01 15:30:00'),
    (@pesq10, 'tatiane.gravino@unirio.br', 'hash_sec_10', '61999990010', 'Campus Darcy Ribeiro, Brasília', 'PESQUISADOR', false, true, '2024-02-03 09:00:00');

-- Organizações
INSERT INTO USUARIO (id_usuario, email, senha, telefone, endereco, tipo_perfil, status_auth, status_ativo, data_criacao) VALUES
    (@org01, 'contato@techverde.com.br', 'pass_org_01', '1140040001', 'Polo Industrial, Manaus', 'ORGANIZACAO', true, true, '2024-02-05 10:00:00'),
    (@org02, 'inovacao@mineracaovale.com', 'pass_org_02', '3130303030', 'Av. Vale, 500, Nova Lima', 'ORGANIZACAO', false, true, '2024-02-06 11:00:00'),
    (@org03, 'compras@farmabrasil.com.br', 'pass_org_03', '1155554444', 'Distrito Industrial, Anápolis', 'ORGANIZACAO', true, true, '2024-02-07 14:00:00'),
    (@org04, 'diretoria@agroforte.agr.br', 'pass_org_04', '6634343434', 'Rodovia MT-130, Rondonópolis', 'ORGANIZACAO', false, true, '2024-02-08 09:00:00'),
    (@org05, 'pnd@solarenergy.com', 'pass_org_05', '4832323232', 'Parque Tecnológico, Florianópolis', 'ORGANIZACAO', true, true, '2024-02-09 16:00:00'),
    (@org06, 'juridico@construmais.com', 'pass_org_06', '2125252525', 'Av. Rio Branco, RJ', 'ORGANIZACAO', false, true, '2024-02-10 10:30:00'),
    (@org07, 'suporte@nanotech.ind.br', 'pass_org_07', '1938383838', 'Tecnoparque, Campinas', 'ORGANIZACAO', true, true, '2024-02-11 11:15:00'),
    (@org08, 'sac@automotivax.com', 'pass_org_08', '1143434343', 'S. Bernardo do Campo, SP', 'ORGANIZACAO', true, true, '2024-02-12 13:45:00'),
    (@org09, 'ceo@softstream.io', 'pass_org_09', '1191234567', 'Av. Paulista, 1000, SP', 'ORGANIZACAO', false, true, '2024-02-13 17:00:00'),
    (@org10, 'parcerias@biovid.com.br', 'pass_org_10', '3132323232', 'Av. Brasil, BH', 'ORGANIZACAO', true, true, '2024-02-14 08:30:00');

-- 2. NIT
INSERT INTO NIT (id_usuario, CNPJ, razao_social) VALUES
    (@nit_unirio, '34023077000107', 'Universidade Federal do Estado do Rio de Janeiro - NIT UNIRIO');

-- 3. ORGANIZACAO
INSERT INTO ORGANIZACAO (id_usuario, CNPJ, razao_social) VALUES
    (@org01, '12345678000190', 'TechVerde Soluções Sustentáveis Ltda'),
    (@org02, '98765432000110', 'Mineração Vale do Sol S.A.'),
    (@org03, '45612378000144', 'FarmaBrasil Laboratórios Médicos'),
    (@org04, '32165498000155', 'AgroForte Tecnologia do Campo'),
    (@org05, '14725836000122', 'SolarEnergy Painéis Fotovoltaicos'),
    (@org06, '96385274000133', 'ConstruMais Engenharia Civil'),
    (@org07, '85296374000111', 'NanoTech Indústria de Componentes'),
    (@org08, '74185296000100', 'Automotiva X Componentes Ltda'),
    (@org09, '15926348000188', 'SoftStream Soluções Digitais'),
    (@org10, '35795146000177', 'BioVid Biotecnologia Aplicada');

-- 4. PESQUISADOR
INSERT INTO PESQUISADOR (id_usuario, id_nit, CPF, nome, disponibilidade_consultoria) VALUES
    (@pesq01, @nit_unirio, '11122233344', 'Izabel Christina Paixão', true),
    (@pesq02, @nit_unirio, '22233344455', 'Marcia Barreto Feijó', false),
    (@pesq03, @nit_unirio, '33344455566', 'Mariano Gomes Pimentel', true),
    (@pesq04, @nit_unirio, '44455566677', 'Cristina Yoshie Takeiti', true),
    (@pesq05, @nit_unirio, '55566677788', 'Julia Rabelo Vaz Matheus', false),
    (@pesq06, @nit_unirio, '66677788899', 'Elvino de Castro Santana', true),
    (@pesq07, @nit_unirio, '77788899900', 'Daniele Galdino De Paula', true),
    (@pesq08, @nit_unirio, '88899900011', 'Maria Gabriela Koblitz', false),
    (@pesq09, @nit_unirio, '99900011122', 'Guilherme de Souza Silva', true),
    (@pesq10, @nit_unirio, '10011122233', 'Tatiane Veiga Gravino', true);

-- 5. PATENTE
INSERT INTO PATENTE (id_patente, id_titular, titulo, num_deposito, resumo, area, valor, pesquisadores, status) VALUES
    (@pat01, @nit_unirio, 'EXTRATO MARINHO CONTRA HIV-1', 'PI 1012416-0', 'Extrato de produtos marinhos com ação inibitória da replicação do vírus HIV-1.', 'Biotecnologia', 1500000.00, 'Izabel Christina Paixão', 'CEDIDA'),
    (@pat02, @nit_unirio,'BIOMASSA DE FRUTA-PÃO', 'BR 10 2015 018850', 'Processo de obtenção de biomassa de fruta-pão sob pressão.', 'Alimentos', 450000.00, 'Marcia Barreto Feijó', 'DISPONIVEL'),
    (@pat03, @nit_unirio, 'SISTEMA DE PRODUÇÃO SONORA', 'BR 10 2016 028373', 'Sistema portátil útil no auxílio da educação musical.', 'Tecnologia Assistiva', 120000.00, 'Mariano Gomes Pimentel', 'EM_PROCESSO_DE_COMPRA'),
    (@pat04, @nit_unirio, 'PREBIÓTICO POTENCIALIZADO', 'BR 10 2018 002657', 'Produto capaz de potencializar o efeito probiótico.', 'Nutracêuticos', 890000.00, 'Cristina Yoshie Takeiti', 'LICENCIADA'),
    (@pat05, @nit_unirio, 'FILMES DE CAQUI', 'BR 10 2019 025723', 'Filme biodegradável a base de caqui para alimentos.', 'Sustentabilidade', 320000.00, 'Julia Rabelo Vaz Matheus', 'RASCUNHO'),
    (@pat06, @nit_unirio, 'ILUMINADOR SUBCUTÂNEO', 'BR 20 2020 004562', 'Iluminador para visualização da rede venosa.', 'Equipamento Médico', 210000.00, 'Elvino De Castro Santana', 'DISPONIVEL'),
    (@pat07, @nit_unirio, 'PROTETOR DE TRAQUEOSTOMIA', 'BR 20 2020 004690', 'Dispositivo de baixo custo para higiene pessoal.', 'Saúde', 75000.00, 'Danielle Galdino De Paula', 'REPROVADA'),
    (@pat08, @nit_unirio, 'BAUNILHAS DA MATA ATLÂNTICA', 'BR 10 2022 024314', 'Agentes aromatizantes à base de baunilhas selvagens.', 'Cosméticos', 580000.00, 'Maria Gabriela Koblitz', 'DISPONIVEL'),
    (@pat09, @nit_unirio,'MICROSCÓPIO RECICLADO', 'BR 20 2023 007137', 'Microscópio digital produzido com material reciclável.', 'Educação', 45000.00, 'Guilherme De Souza Silva', 'EXCLUIDA'),
    (@pat10, @nit_unirio, 'FARINHAS DE PINHÃO', 'BR 10 2024 016408', 'Metodologia para obtenção de farinhas nutritivas.', 'Agronegócio', 185000.00, 'Tatiane Veiga Gravino', 'EM_PROCESSO_DE_COMPRA');

-- 6. PATENTE_PESQUISADOR
INSERT INTO PATENTE_PESQUISADOR (id_patente, id_pesquisador) VALUES
    (@pat01, @pesq01),
    (@pat02, @pesq02),
    (@pat03, @pesq03),
    (@pat04, @pesq04),
    (@pat05, @pesq05),
    (@pat06, @pesq06),
    (@pat07, @pesq07),
    (@pat08, @pesq08),
    (@pat09, @pesq09),
    (@pat10, @pesq10);

-- 7. AQUISICAO
INSERT INTO AQUISICAO (id_aquisicao, id_usuario, data_aquisicao, data_expiracao, status) VALUES
    (@aq01, @org10, '2024-03-01', NULL, 'CONCLUIDA'),
    (@aq02, @org03, '2024-03-05', '2026-03-05', 'CONCLUIDA'),
    (@aq03, @org04, '2024-03-10', NULL, 'ANALISE_NIT');

-- 8. ITEM_AQUISICAO
INSERT INTO ITEM_AQUISICAO (id_item, id_aquisicao, id_patente, tipo_aquisicao, fim_licenca) VALUES
    (@ia01, @aq01, @pat01, 'CESSAO', NULL),
    (@ia02, @aq02, @pat06, 'LICENCIAMENTO', '2026-03-05'),
    (@ia03, @aq03, @pat10, 'CESSAO', NULL);

-- 9. CARRINHO
INSERT INTO CARRINHO (id_carrinho, id_usuario) VALUES
    (@car03, @org04),
    (@car04, @org09);

-- 10. ITEM_CARRINHO
INSERT INTO ITEM_CARRINHO (id_item, id_carrinho, id_patente) VALUES
    (@ic01, @car03, @pat10),
    (@ic02, @car04, @pat03);

COMMIT;