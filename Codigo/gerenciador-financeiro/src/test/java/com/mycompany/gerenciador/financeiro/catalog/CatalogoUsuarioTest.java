package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Usuario;

class CatalogoUsuarioTest {

    private CatalogoUsuario catalogo;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoUsuario();
    }

    @Test
    void deveAdicionarNovoUsuario() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        
        boolean resultado = catalogo.criar(usuario);
        
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarUsuarios()).contains(usuario);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        catalogo.criar(usuario);
        
        Usuario encontrado = catalogo.buscarPorEmail("joao@email.com");
        
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveRetornarNullQuandoUsuarioNaoExiste() {
        Usuario encontrado = catalogo.buscarPorEmail("naoexiste@email.com");
        
        assertThat(encontrado).isNull();
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisCorretas() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        catalogo.criar(usuario);
        
        Usuario autenticado = catalogo.autenticar("joao@email.com", "senha123");
        
        assertThat(autenticado).isNotNull();
        assertThat(autenticado.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void naoDeveAutenticarUsuarioComSenhaIncorreta() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        catalogo.criar(usuario);
        
        Usuario autenticado = catalogo.autenticar("joao@email.com", "senhaErrada");
        
        assertThat(autenticado).isNull();
    }

    @Test
    void naoDeveAutenticarUsuarioInexistente() {
        Usuario autenticado = catalogo.autenticar("naoexiste@email.com", "senha123");
        
        assertThat(autenticado).isNull();
    }

    @Test
    void deveListarTodosOsUsuarios() {
        Usuario usuario1 = new Usuario("João", "joao@email.com", "senha123");
        Usuario usuario2 = new Usuario("Maria", "maria@email.com", "senha456");
        
        catalogo.criar(usuario1);
        catalogo.criar(usuario2);
        
        List<Usuario> usuarios = catalogo.listarUsuarios();
        
        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).contains(usuario1, usuario2);
    }

    @Test
    void naoDeveAdicionarUsuarioDuplicado() {
        Usuario usuario1 = new Usuario("João", "joao@email.com", "senha123");
        Usuario usuario2 = new Usuario("João Silva", "joao@email.com", "senha456");
        
        catalogo.criar(usuario1);
        boolean resultado = catalogo.criar(usuario2);
        
        assertThat(resultado).isFalse();
        assertThat(catalogo.listarUsuarios()).hasSize(1);
    }

    @Test
    void deveChamarSalvarAoEncerrar() throws IOException {
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        catalogo.criar(usuario);
        
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
