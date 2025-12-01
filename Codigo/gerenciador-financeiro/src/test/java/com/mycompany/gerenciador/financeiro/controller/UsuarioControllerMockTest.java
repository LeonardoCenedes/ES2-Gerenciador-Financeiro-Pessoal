package com.mycompany.gerenciador.financeiro.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoUsuario;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Teste UNITÁRIO com MOCKS - UsuarioController
 * Testa o UsuarioController SEM executar código real do CatalogoUsuario
 * Usa injeção de dependência via construtor para passar mocks
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerMockTest {

    @Mock
    private CatalogoUsuario catalogoMock;  // Mock: não executa código real
    
    private ControladorUsuario controlador;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        controlador = new ControladorUsuario(catalogoMock); // Injeção do mock
    }

    /**
     * Teste unitário mockado - Registro com sucesso
     */
    @Test
    void deveCriarUsuarioComSucesso_Mock() {
        // Arrange: preparar o mock para retornar true quando criar usuário
        when(catalogoMock.criar(any(Usuario.class))).thenReturn(true);
        
        // Act: criar usuário
        boolean resultado = controlador.criarUsuario("João", "joao@test.com", "senha123");
        
        // Assert: verificar que o método do mock foi chamado
        assertThat(resultado).isTrue();
        verify(catalogoMock, times(1)).criar(any(Usuario.class));
    }

    /**
     * Teste unitário mockado - Login com sucesso
     */
    @Test
    void deveAutenticarUsuarioComSucesso_Mock() {
        // Arrange: configurar mock para retornar usuário quando autenticar
        Usuario usuarioEsperado = new Usuario("João", "joao@test.com", "senha123");
        when(catalogoMock.autenticar("joao@test.com", "senha123")).thenReturn(usuarioEsperado);
        
        // Act: autenticar
        Usuario usuario = controlador.autenticar("joao@test.com", "senha123");
        
        // Assert: verificar resultado e chamada
        assertThat(usuario).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("joao@test.com");
        verify(catalogoMock, times(1)).autenticar("joao@test.com", "senha123");
    }

    /**
     * Teste unitário mockado - Login com credenciais inválidas
     */
    @Test
    void naoDeveAutenticarComSenhaErrada_Mock() {
        // Arrange: configurar mock para retornar null (falha de autenticação)
        when(catalogoMock.autenticar("joao@test.com", "senhaErrada")).thenReturn(null);
        
        // Act: tentar autenticar com senha errada
        Usuario usuario = controlador.autenticar("joao@test.com", "senhaErrada");
        
        // Assert: verificar que retornou null
        assertThat(usuario).isNull();
        verify(catalogoMock, times(1)).autenticar("joao@test.com", "senhaErrada");
    }

    /**
     * Teste unitário mockado - Registro com email duplicado
     */
    @Test
    void naoDeveCriarUsuarioComEmailDuplicado_Mock() {
        // Arrange: configurar mock para retornar false (email já existe)
        when(catalogoMock.criar(any(Usuario.class))).thenReturn(false);
        
        // Act: tentar criar usuário com email duplicado
        boolean resultado = controlador.criarUsuario("João", "duplicado@test.com", "senha123");
        
        // Assert: verificar que falhou
        assertThat(resultado).isFalse();
        verify(catalogoMock, times(1)).criar(any(Usuario.class));
    }

    /**
     * Teste unitário mockado - Buscar usuário por email
     */
    @Test
    void deveBuscarUsuarioPorEmail_Mock() {
        // Arrange: Mock retorna usuário específico
        Usuario usuarioEsperado = new Usuario("Maria", "maria@test.com", "senha");
        when(catalogoMock.buscarPorEmail("maria@test.com")).thenReturn(usuarioEsperado);
        
        // Act: buscar usuário
        Usuario usuario = controlador.buscarUsuarioPorEmail("maria@test.com");
        
        // Assert: verifica retorno correto
        assertThat(usuario).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("maria@test.com");
        assertThat(usuario.getNome()).isEqualTo("Maria");
        verify(catalogoMock, times(1)).buscarPorEmail("maria@test.com");
    }

    /**
     * Teste unitário mockado - Buscar usuário inexistente
     */
    @Test
    void deveRetornarNullParaUsuarioInexistente_Mock() {
        // Arrange: Mock retorna null para email não cadastrado
        when(catalogoMock.buscarPorEmail("inexistente@test.com")).thenReturn(null);
        
        // Act: buscar usuário inexistente
        Usuario usuario = controlador.buscarUsuarioPorEmail("inexistente@test.com");
        
        // Assert: verifica que retornou null
        assertThat(usuario).isNull();
        verify(catalogoMock, times(1)).buscarPorEmail("inexistente@test.com");
    }
}
