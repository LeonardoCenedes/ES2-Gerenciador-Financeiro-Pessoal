package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoContas;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes UNITÁRIOS com MOCKS - ContaController
 * Usa Mockito para isolar a lógica do controller
 */
@ExtendWith(MockitoExtension.class)
class ContaControladorMockTest {

    @Mock
    private CatalogoContas catalogoMock;
    
    @Mock
    private ControladorTransacao transacaoControllerMock;

    private ControladorConta controller;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        controller = new ControladorConta(catalogoMock, transacaoControllerMock);
    }

    @Test
    void deveCriarContaComSucesso() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        
        // Act
        controller.criarConta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        
        // Assert
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).salvar(any(Conta.class));
    }

    @Test
    void naoDeveCriarContaComNomeVazio() {
        // Act & Assert
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        try {
            controller.criarConta("", "Corrente", 1000.0f, "BRL", usuario);
            org.junit.jupiter.api.Assertions.fail("Deveria lançar exceção");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Nome");
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail("Não deveria lançar IOException");
        }
    }

    @Test
    void deveListarContasDoUsuario() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta1 = new Conta("Conta 1", "Corrente", 1000.0f, "BRL", usuario);
        Conta conta2 = new Conta("Conta 2", "Poupança", 2000.0f, "BRL", usuario);
        List<Conta> contasEsperadas = Arrays.asList(conta1, conta2);
        
        when(catalogoMock.buscarPorUsuario(usuario)).thenReturn(contasEsperadas);
        
        // Act
        List<Conta> resultado = controller.buscarContasUsuario(usuario);
        
        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(conta1, conta2);
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).buscarPorUsuario(usuario);
    }

    @Test
    void deveEditarContaComSucesso() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Antiga", "Corrente", 1000.0f, "BRL", usuario);
        String nomeOriginal = conta.getNome();
        
        // Act
        controller.editarConta(conta, "Conta Nova", "Poupança", 2000.0f);
        
        // Assert
        assertThat(conta.getNome()).isEqualTo("Conta Nova");
        assertThat(conta.getTipo()).isEqualTo("Poupança");
        assertThat(conta.getSaldoInicial()).isEqualTo(2000.0f);
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).atualizar(conta, nomeOriginal);
    }

    @Test
    void deveExcluirContaComSucesso() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        
        // Act
        controller.excluirConta(conta);
        
        // Assert
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).excluir(conta);
    }
}
