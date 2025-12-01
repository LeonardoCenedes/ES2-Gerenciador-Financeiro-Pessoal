package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoTransacao;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes UNITÁRIOS com MOCKS - TransacaoController
 * Usa Mockito para isolar a lógica do controller
 */
@ExtendWith(MockitoExtension.class)
class TransacaoControladorMockTest {

    @Mock
    private CatalogoTransacao catalogoMock;
    
    @Mock
    private ControladorMetaEconomica metaControladorMock;

    private ControladorTransacao controller;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        controller = new ControladorTransacao(catalogoMock, metaControladorMock);
    }

    @Test
    void deveCriarTransacaoComSucesso() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Alimentação", false, true);
        
        when(catalogoMock.adicionar(any(Transacao.class))).thenReturn(true);
        
        // Act
        boolean resultado = controller.criarTransacao(new Date(), 100.0f, categoria, 
                                                      "Almoço", null, TiposTransacao.SAIDA, conta, null);
        
        // Assert
        assertThat(resultado).isTrue();
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).adicionar(any(Transacao.class));
    }

    @Test
    void naoDeveCriarTransacaoComValorNegativo() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Alimentação", false, true);
        
        // Act
        boolean resultado = controller.criarTransacao(new Date(), -100.0f, categoria, 
                                                      "Teste", null, TiposTransacao.SAIDA, conta, null);
        
        // Assert
        assertThat(resultado).isFalse();
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.never()).adicionar(any(Transacao.class));
    }

    @Test
    void deveListarTodasTransacoes() {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Alimentação", false, true);
        
        Transacao t1 = new Transacao(new Date(), 100.0f, categoria, "Almoço", null, TiposTransacao.SAIDA, conta, null);
        Transacao t2 = new Transacao(new Date(), 50.0f, categoria, "Lanche", null, TiposTransacao.SAIDA, conta, null);
        List<Transacao> transacoesEsperadas = Arrays.asList(t1, t2);
        
        when(catalogoMock.listarTodas()).thenReturn(transacoesEsperadas);
        
        // Act
        List<Transacao> resultado = controller.listarTodas();
        
        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(t1, t2);
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).listarTodas();
    }

    @Test
    void deveListarTransacoesPorConta() {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Alimentação", false, true);
        
        Transacao t1 = new Transacao(new Date(), 100.0f, categoria, "Almoço", null, TiposTransacao.SAIDA, conta, null);
        List<Transacao> transacoesEsperadas = Arrays.asList(t1);
        
        when(catalogoMock.listarTransacoesFiltrada(conta, null, null, null)).thenReturn(transacoesEsperadas);
        
        // Act
        List<Transacao> resultado = controller.buscarTransacoesFiltradas(conta, null, null, null);
        
        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(t1);
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).listarTransacoesFiltrada(conta, null, null, null);
    }

    @Test
    void deveExcluirTransacaoComSucesso() throws IOException {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Alimentação", false, true);
        Transacao transacao = new Transacao(new Date(), 100.0f, categoria, "Almoço", null, TiposTransacao.SAIDA, conta, null);
        
        when(catalogoMock.excluir(transacao)).thenReturn(true);
        
        // Act
        boolean resultado = controller.excluirTransacao(transacao);
        
        // Assert
        assertThat(resultado).isTrue();
        org.mockito.Mockito.verify(catalogoMock, org.mockito.Mockito.times(1)).excluir(transacao);
    }
}
