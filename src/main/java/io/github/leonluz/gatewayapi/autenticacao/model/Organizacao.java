    package io.github.leonluz.gatewayapi.autenticacao.model;

    import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoRequestDTO;
    import jakarta.persistence.*;

    @Entity
    @Table(name = "ORGANIZACAO")
    @PrimaryKeyJoinColumn(name = "id_usuario")
    @DiscriminatorValue("ORGANIZACAO")
    public class Organizacao extends Usuario {

        @Column(name = "CNPJ", unique = true)
        private String cnpj;

        @Column(name = "razao_social", nullable = false)
        private String razaoSocial;

        public Organizacao(OrganizacaoRequestDTO dto) {
            this.setEmail(dto.email());
            this.setSenha(dto.senha());
            this.setTelefone(dto.telefone());
            this.setEndereco(dto.endereco());
            this.cnpj = dto.cnpj();
            this.razaoSocial = dto.razaoSocial();
        }

        public Organizacao() {

        }

        public void setCnpj(String cnpj) {
            this.cnpj = cnpj;
        }

        public void setRazaoSocial(String razaoSocial) {
            this.razaoSocial = razaoSocial;
        }

        public String getCnpj() {
            return cnpj;
        }

        public String getRazaoSocial() {
            return razaoSocial;
        }

        @Override
        public String toString() {
            return "Organizacao{" +
                    "cnpj='" + cnpj + '\'' +
                    ", razaoSocial='" + razaoSocial + '\'' +
                    '}';
        }
    }
