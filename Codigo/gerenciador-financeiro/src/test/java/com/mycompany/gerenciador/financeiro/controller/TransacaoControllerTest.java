package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    private ControladorTransacao controller;
    private ControladorMetaEconomica metaController;
    private Usuario usuario;
    private Conta conta;
    private Categoria categoria;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        usuario = new Usuario("João", "joao@email.com", "senha123");
        conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        categoria = new Categoria("Alimentação", false, true);
        metaController = new ControladorMetaEconomica();
        controller = new ControladorTransacao(metaController);
    }

    @Test
    void deveCriarTransacaoDeEntradaComSucesso() throws IOException {
        Date data = new Date();
        byte[] comprovante = "comprovante".getBytes();
        
        boolean resultado = controller.criarTransacao(
            data, 500.0f, categoria, "Salário", 
            comprovante, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveCriarTransacaoDeSaidaComSucesso() throws IOException {
        Date data = new Date();
        
        boolean resultado = controller.criarTransacao(
            data, 150.0f, categoria, "Supermercado", 
            null, TiposTransacao.SAIDA, conta, null
        );
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarTransacaoComValorZero() throws IOException {
        Date data = new Date();
        
        boolean resultado = controller.criarTransacao(
            data, 0.0f, categoria, "Teste", 
            null, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarTransacaoComValorNegativo() throws IOException {
        Date data = new Date();
        
        boolean resultado = controller.criarTransacao(
            data, -100.0f, categoria, "Teste", 
            null, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarTransacaoComDataNull() throws IOException {
        boolean resultado = controller.criarTransacao(
            null, 500.0f, categoria, "Teste", 
            null, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarTransacaoComCategoriaNull() throws IOException {
        Date data = new Date();
        
        boolean resultado = controller.criarTransacao(
            data, 500.0f, null, "Teste", 
            null, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveListarTodasAsTransacoes() throws IOException {
        Date data = new Date();
        
        controller.criarTransacao(data, 500.0f, categoria, "Transação 1", null, TiposTransacao.ENTRADA, conta, null);
        controller.criarTransacao(data, 200.0f, categoria, "Transação 2", null, TiposTransacao.SAIDA, conta, null);
        
        List<Transacao> transacoes = controller.listarTodas();
        
        assertThat(transacoes).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveBuscarTransacoesPorConta() throws IOException {
        Date data = new Date();
        
        controller.criarTransacao(data, 500.0f, categoria, "Transação", null, TiposTransacao.ENTRADA, conta, null);
        
        List<Transacao> transacoes = controller.buscarPorConta(conta);
        
        assertThat(transacoes).isNotEmpty();
    }

    @Test
    void deveEditarTransacao() throws IOException {
        Date data = new Date();
        controller.criarTransacao(data, 500.0f, categoria, "Original", null, TiposTransacao.ENTRADA, conta, null);
        
        List<Transacao> transacoes = controller.listarTodas();
        Transacao transacao = transacoes.stream()
            .filter(t -> t.getDescricao().equals("Original"))
            .findFirst()
            .orElse(null);
        
        assertThat(transacao).isNotNull();
        
        Date novaData = new Date();
        boolean resultado = controller.editarTransacao(
            transacao, novaData, 750.0f, categoria, 
            "Editado", null, TiposTransacao.ENTRADA
        );
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveExcluirTransacao() throws IOException {
        Date data = new Date();
        controller.criarTransacao(data, 500.0f, categoria, "Para Excluir", null, TiposTransacao.ENTRADA, conta, null);
        
        List<Transacao> transacoes = controller.listarTodas();
        Transacao transacao = transacoes.stream()
            .filter(t -> t.getDescricao().equals("Para Excluir"))
            .findFirst()
            .orElse(null);
        
        assertThat(transacao).isNotNull();
        
        boolean resultado = controller.excluirTransacao(transacao);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
