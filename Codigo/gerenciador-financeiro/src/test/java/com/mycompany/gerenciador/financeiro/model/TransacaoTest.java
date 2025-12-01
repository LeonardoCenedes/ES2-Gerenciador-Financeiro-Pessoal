package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransacaoTest {

    private Usuario usuario;
    private Conta conta;
    private Categoria categoria;
    private Date data;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        usuario = new Usuario("João", "joao@email.com", "senha123");
        conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        categoria = new Categoria("Alimentação", true, true);
        data = new Date();
    }

    @Test
    void deveCriarTransacaoComConstrutorVazio() {
        Transacao transacao = new Transacao();
        assertThat(transacao).isNotNull();
    }

    @Test
    void deveCriarTransacaoDeEntradaCompleta() {
        byte[] comprovante = "comprovante".getBytes();
        Transacao transacao = new Transacao(
            data, 500.0f, categoria, "Salário", 
            comprovante, TiposTransacao.ENTRADA, conta, null
        );
        
        assertThat(transacao.getData()).isEqualTo(data);
        assertThat(transacao.getValor()).isEqualTo(500.0f);
        assertThat(transacao.getCategoria()).isEqualTo(categoria);
        assertThat(transacao.getDescricao()).isEqualTo("Salário");
        assertThat(transacao.getComprovante()).isEqualTo(comprovante);
        assertThat(transacao.getTipo()).isEqualTo(TiposTransacao.ENTRADA);
        assertThat(transacao.getConta()).isEqualTo(conta);
    }

    @Test
    void deveCriarTransacaoDeSaidaCompleta() {
        Transacao transacao = new Transacao(
            data, 150.0f, categoria, "Supermercado", 
            null, TiposTransacao.SAIDA, conta, null
        );
        
        assertThat(transacao.getTipo()).isEqualTo(TiposTransacao.SAIDA);
        assertThat(transacao.getValor()).isEqualTo(150.0f);
    }

    @Test
    void deveAlterarDataDaTransacao() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        Date novaData = new Date();
        transacao.setData(novaData);
        
        assertThat(transacao.getData()).isEqualTo(novaData);
    }

    @Test
    void deveAlterarValorDaTransacao() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        transacao.setValor(750.0f);
        
        assertThat(transacao.getValor()).isEqualTo(750.0f);
    }

    @Test
    void deveAlterarCategoria() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        Categoria novaCategoria = new Categoria("Transporte", true, true);
        transacao.setCategoria(novaCategoria);
        
        assertThat(transacao.getCategoria()).isEqualTo(novaCategoria);
    }

    @Test
    void deveTerValorPositivo() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        assertThat(transacao.getValor()).isPositive();
    }

    @Test
    void deveTerTipoDefinido() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        assertThat(transacao.getTipo()).isNotNull();
    }

    @Test
    void deveTerContaAssociada() {
        Transacao transacao = new Transacao(data, 500.0f, categoria, "Teste", null, TiposTransacao.ENTRADA, conta, null);
        assertThat(transacao.getConta()).isNotNull();
    }
}
