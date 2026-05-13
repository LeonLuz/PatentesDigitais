
create table USUARIO (
    id_usuario char(36) PRIMARY KEY,
    email varchar(100),
    senha varchar(255),
    telefone varchar(20),
    endereco varchar(250),
    tipo_perfil enum('PESQUISADOR', 'ORGANIZACAO', 'NIT') not null,
    status_auth boolean,
    status_ativo boolean,
    data_criacao timestamp
);

create table ORGANIZACAO (
    id_usuario char(36) PRIMARY KEY,
    CNPJ char(14) not null UNIQUE,
    razao_social varchar(150) not null,
    CONSTRAINT fk_usuario_organizacao
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
);


create table NIT (
    id_usuario char(36) PRIMARY KEY,
    CNPJ char(14) not null UNIQUE,
    razao_social varchar(150) not null,
    CONSTRAINT fk_usuario_nit
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
);

create table PESQUISADOR (
    id_usuario char(36) PRIMARY KEY,
    CPF char(11) not null UNIQUE,
    nome varchar(150),
    disponibilidade_consultoria boolean,
    CONSTRAINT fk_usuario_pesquisador
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
);

create table PATENTE
(
    id_patente    char(36) PRIMARY KEY,
    id_titular    char(36)       not null,
    titulo        varchar(200)   not null,
    num_deposito  varchar(17)    not null,
    resumo        TEXT,
    area          varchar(150),
    valor         decimal(10, 2) not null,
    pesquisadores text,
    status        enum('RASCUNHO', 'REPROVADA', 'DISPONIVEL',
                'EM_PROCESSO_DE_COMPRA', 'CEDIDA', 'LICENCIADA', 'EXCLUIDA') not null,
    CONSTRAINT fk_titular_patente
        FOREIGN KEY (id_titular) references USUARIO (id_usuario)
);

create table PATENTE_PESQUISADOR (
    id_patente char(36) not null,
    id_pesquisador char(36) not null,
    CONSTRAINT pk_patente_pesquisador
    PRIMARY KEY (id_patente, id_pesquisador),
    CONSTRAINT fk_patente_pesquisador
    FOREIGN KEY (id_patente) references PATENTE(id_patente),
    CONSTRAINT fk_pesquisador_patente
    FOREIGN KEY (id_pesquisador) references PESQUISADOR(id_usuario)
);

create table AQUISICAO (
    id_aquisicao char(36) PRIMARY KEY,
    id_usuario char(36) not null,
    data_aquisicao date not null,
    data_expiracao date,
    status enum('AGUARDANDO_PAGAMENTO','ANALISE_NIT', 'CONCLUIDA',
                'CANCELADA') not null,
    CONSTRAINT fk_usuario_aquisicao
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
);

create table ITEM_AQUISICAO (
    id_item char(36) PRIMARY KEY,
    id_aquisicao char(36) not null,
    id_patente char(36) not null,
    tipo_aquisicao enum('LICENCIAMENTO', 'CESSAO') not null,
    fim_licenca date,
    CONSTRAINT fk_aquisicao_item_aquisicao
    FOREIGN KEY (id_aquisicao) references AQUISICAO(id_aquisicao),
    CONSTRAINT fk_patente_item_aquisicao
    FOREIGN KEY (id_patente) references PATENTE(id_patente)
);

create table CARRINHO (
    id_carrinho char(36) PRIMARY KEY,
    id_usuario char(36) not null UNIQUE,
    CONSTRAINT fk_usuario_carrinho
    FOREIGN KEY (id_usuario) references USUARIO(id_usuario)
);

create table ITEM_CARRINHO (
    id_item char(36) PRIMARY KEY,
    id_carrinho char(36) not null,
    id_patente char(36) not null,
    CONSTRAINT fk_carrinho_item_carrinho
    FOREIGN KEY (id_carrinho) references CARRINHO(id_carrinho),
    CONSTRAINT fk_patente_item_carrinho
    FOREIGN KEY (id_patente) references PATENTE(id_patente)
);