package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes do UsuarioController - focado em casos de uso de Login e Registro
 */
class UsuarioControllerTest {

    private ControladorUsuario controller;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        controller = new ControladorUsuario();
    }

    @Test
    void deveCriarNovoUsuarioComSucesso() throws IOException {
        boolean resultado = controller.criarUsuario("João Silva", "joao@email.com", "senha123");
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarUsuarioComNomeVazio() throws IOException {
        boolean resultado = controller.criarUsuario("", "joao@email.com", "senha123");
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarUsuarioComEmailVazio() throws IOException {
        boolean resultado = controller.criarUsuario("João", "", "senha123");
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarUsuarioComSenhaVazia() throws IOException {
        boolean resultado = controller.criarUsuario("João", "joao@email.com", "");
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarUsuarioComEmailInvalido() throws IOException {
        boolean resultado = controller.criarUsuario("João", "emailinvalido", "senha123");
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() throws IOException {
        controller.criarUsuario("João", "joao@email.com", "senha123");
        
        Usuario usuario = controller.autenticar("joao@email.com", "senha123");
        
        assertThat(usuario).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void naoDeveAutenticarUsuarioComSenhaInvalida() throws IOException {
        controller.criarUsuario("João", "joao@email.com", "senha123");
        
        Usuario usuario = controller.autenticar("joao@email.com", "senhaErrada");
        
        assertThat(usuario).isNull();
    }

    @Test
    void naoDeveAutenticarUsuarioInexistente() {
        Usuario usuario = controller.autenticar("naoexiste@email.com", "senha123");
        
        assertThat(usuario).isNull();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
