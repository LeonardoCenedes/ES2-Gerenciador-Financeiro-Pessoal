package com.mycompany.gerenciador.financeiro.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComConstrutorVazio() {
        Usuario usuario = new Usuario();
        assertThat(usuario).isNotNull();
    }

    @Test
    void deveCriarUsuarioComTodosOsDados() {
        Usuario usuario = new Usuario("João Silva", "joao@email.com", "senha123");
        
        assertThat(usuario.getNome()).isEqualTo("João Silva");
        assertThat(usuario.getEmail()).isEqualTo("joao@email.com");
        assertThat(usuario.getSenha()).isEqualTo("senha123");
    }

    @Test
    void deveAlterarNome() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        usuario.setNome("João Silva");
        
        assertThat(usuario.getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveAlterarEmail() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        usuario.setEmail("joao.silva@email.com");
        
        assertThat(usuario.getEmail()).isEqualTo("joao.silva@email.com");
    }

    @Test
    void deveAlterarSenha() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        usuario.setSenha("novaSenha456");
        
        assertThat(usuario.getSenha()).isEqualTo("novaSenha456");
    }

    @Test
    void deveTerEmailUnico() {
        Usuario usuario1 = new Usuario("João", "joao@email.com", "senha123");
        Usuario usuario2 = new Usuario("Maria", "maria@email.com", "senha456");
        
        assertThat(usuario1.getEmail()).isNotEqualTo(usuario2.getEmail());
    }
}
