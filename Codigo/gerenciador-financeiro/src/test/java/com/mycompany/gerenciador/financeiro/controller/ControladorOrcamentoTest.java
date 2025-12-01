package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para ControladorOrcamento
 */
class ControladorOrcamentoTest {

    private ControladorOrcamento controller;
    private ControladorTransacao transacaoController;
    private ControladorMetaEconomica metaController;
    private Usuario usuario;
    private Categoria categoria;
    private Conta conta;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        metaController = new ControladorMetaEconomica();
        transacaoController = new ControladorTransacao(metaController);
        controller = new ControladorOrcamento();
        
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
        categoria = new Categoria("Alimentação", false, true);
        conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
    }

    @Test
    void deveCriarOrcamentoComSucesso() throws IOException {
        Date periodo = new Date();
        
        boolean resultado = controller.criarOrcamento(periodo, 1000.0f, categoria, usuario);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarOrcamentoComPeriodoNulo() throws IOException {
        boolean resultado = controller.criarOrcamento(null, 1000.0f, categoria, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarOrcamentoComValorZero() throws IOException {
        Date periodo = new Date();
        
        boolean resultado = controller.criarOrcamento(periodo, 0.0f, categoria, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarOrcamentoComValorNegativo() throws IOException {
        Date periodo = new Date();
        
        boolean resultado = controller.criarOrcamento(periodo, -100.0f, categoria, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarOrcamentoComCategoriaNula() throws IOException {
        Date periodo = new Date();
        
        boolean resultado = controller.criarOrcamento(periodo, 1000.0f, null, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveBuscarOrcamentosPorUsuario() throws IOException {
        Date periodo = new Date();
        controller.criarOrcamento(periodo, 1000.0f, categoria, usuario);
        
        List<Orcamento> orcamentos = controller.buscarPorUsuario(usuario);
        
        assertThat(orcamentos).isNotEmpty();
    }

    @Test
    void deveBuscarInformacoesDeOrcamento() throws IOException {
        Date periodo = new Date();
        Orcamento orcamento = new Orcamento(periodo, 1000.0f, categoria, usuario);
        
        Map<Categoria, List<Transacao>> infos = controller.buscarInfosOrcamento(orcamento, conta, transacaoController);
        
        assertThat(infos).isNotNull();
        assertThat(infos).containsKey(categoria);
    }

    @Test
    void deveExcluirOrcamento() throws IOException {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
