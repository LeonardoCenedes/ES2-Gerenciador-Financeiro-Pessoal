package com.mycompany.gerenciador.financeiro.catalog;

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
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para CatalogoLancamentoRecorrente
 */
class CatalogoLancamentoRecorrenteTest {

    private CatalogoLancamentoRecorrente catalogo;
    private Usuario usuario;
    private Conta conta;
    @SuppressWarnings("unused")
    private Categoria categoria;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoLancamentoRecorrente();
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
        conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        categoria = new Categoria("Assinatura", false, true);
    }

    @Test
    void deveAdicionarNovoLancamentoRecorrente() {
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date dataInicio = new Date();
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Netflix", 100.0f, com.mycompany.gerenciador.financeiro.model.Periodicidade.MENSAL,
            dataInicio, 12, proximaData, conta, null
        );
        
        boolean resultado = catalogo.add(lancamento);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveBuscarLancamentosPorConta() {
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date dataInicio = new Date();
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Spotify", 50.0f, com.mycompany.gerenciador.financeiro.model.Periodicidade.MENSAL,
            dataInicio, 12, proximaData, conta, null
        );
        
        catalogo.add(lancamento);
        
        List<LancamentoRecorrente> lancamentos = catalogo.buscarPorConta(conta);
        
        assertThat(lancamentos).isNotEmpty();
    }

    @Test
    void deveRetornarListaVaziaQuandoContaNaoTemLancamentos() {
        Conta outraConta = new Conta("Outra Conta", "Poupança", 500.0f, "BRL", usuario);
        
        List<LancamentoRecorrente> lancamentos = catalogo.buscarPorConta(outraConta);
        
        assertThat(lancamentos).isEmpty();
    }

    @Test
    void deveCancelarLancamentoRecorrente() {
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date dataInicio = new Date();
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Cancelar", 100.0f, com.mycompany.gerenciador.financeiro.model.Periodicidade.MENSAL,
            dataInicio, 12, proximaData, conta, null
        );
        
        catalogo.add(lancamento);
        boolean resultado = catalogo.cancelar(lancamento);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveAtualizarProximaDataDoLancamento() {
        Date proximaData = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date dataInicio = new Date();
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Atualizar", 100.0f, com.mycompany.gerenciador.financeiro.model.Periodicidade.MENSAL,
            dataInicio, 12, proximaData, conta, null
        );
        
        catalogo.add(lancamento);
        boolean resultado = catalogo.atualizarProximaData(lancamento);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
