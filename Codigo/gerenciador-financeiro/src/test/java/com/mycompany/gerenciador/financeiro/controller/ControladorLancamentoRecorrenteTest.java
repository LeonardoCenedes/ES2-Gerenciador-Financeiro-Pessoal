package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.LancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.model.Periodicidade;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para ControladorLancamentoRecorrente
 */
class ControladorLancamentoRecorrenteTest {

    private ControladorLancamentoRecorrente controller;
    private ControladorTransacao transacaoController;
    private ControladorMetaEconomica metaController;
    private Usuario usuario;
    private Conta conta;
    private Categoria categoria;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() throws IOException {
        metaController = new ControladorMetaEconomica();
        transacaoController = new ControladorTransacao(metaController);
        controller = new ControladorLancamentoRecorrente(transacaoController);
        
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
        conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        categoria = new Categoria("Assinatura", false, true);
    }

    @Test
    void deveCriarLancamentoRecorrenteComSucesso() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 100.0f, categoria, "Netflix", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        boolean resultado = controller.criarLancamento("Netflix Mensal", 100.0f, Periodicidade.MENSAL,
                                                       dataInicio, 12, proximaData, conta, transacao);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarLancamentoComDescricaoVazia() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 100.0f, categoria, "Teste", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        boolean resultado = controller.criarLancamento("", 100.0f, Periodicidade.MENSAL,
                                                       dataInicio, 12, proximaData, conta, transacao);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarLancamentoComValorZero() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 100.0f, categoria, "Teste", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        boolean resultado = controller.criarLancamento("Teste", 0.0f, Periodicidade.MENSAL,
                                                       dataInicio, 12, proximaData, conta, transacao);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarLancamentoComValorNegativo() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 100.0f, categoria, "Teste", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        boolean resultado = controller.criarLancamento("Teste", -50.0f, Periodicidade.MENSAL,
                                                       dataInicio, 12, proximaData, conta, transacao);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveBuscarLancamentosPorConta() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 50.0f, categoria, "Spotify", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        controller.criarLancamento("Spotify", 50.0f, Periodicidade.MENSAL,
                                  dataInicio, 12, proximaData, conta, transacao);
        
        List<LancamentoRecorrente> lancamentos = controller.buscarPorConta(conta);
        
        assertThat(lancamentos).isNotEmpty();
    }

    @Test
    void deveCancelarLancamentoRecorrente() throws IOException {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Transacao transacao = new Transacao(dataInicio, 100.0f, categoria, "Cancelar", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        controller.criarLancamento("Para Cancelar", 100.0f, Periodicidade.MENSAL,
                                  dataInicio, 12, proximaData, conta, transacao);
        
        List<LancamentoRecorrente> lancamentos = controller.buscarPorConta(conta);
        assertThat(lancamentos).isNotEmpty();
        
        LancamentoRecorrente lancamento = lancamentos.stream()
            .filter(l -> l.getDescricao().equals("Para Cancelar"))
            .findFirst()
            .orElse(null);
        
        assertThat(lancamento).isNotNull();
        
        boolean resultado = controller.cancelar(lancamento);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveVerificarLancamentosDoDia() {
        assertThatCode(() -> controller.verificarLancamentosDoDia()).doesNotThrowAnyException();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
