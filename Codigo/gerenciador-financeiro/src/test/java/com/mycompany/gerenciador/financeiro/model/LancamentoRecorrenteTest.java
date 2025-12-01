package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class LancamentoRecorrenteTest {

    @Test
    void deveCriarLancamentoRecorrenteComConstrutorVazio() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente();
        assertThat(lancamento).isNotNull();
    }

    @Test
    void deveCriarLancamentoRecorrenteComTodosOsDados() {
        Date dataInicio = new Date();
        Date proximaData = new Date(System.currentTimeMillis() + 86400000); // +1 dia
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        Categoria categoria = new Categoria("Assinatura", false, true);
        Transacao transacao = new Transacao(new Date(), 100.0f, categoria, "Netflix",
                                           null, TiposTransacao.SAIDA, conta, null);
        
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura Netflix", 
            50.0f, 
            Periodicidade.MENSAL, 
            dataInicio, 
            12, 
            proximaData, 
            conta, 
            transacao
        );
        
        assertThat(lancamento.getDescricao()).isEqualTo("Assinatura Netflix");
        assertThat(lancamento.getValor()).isEqualTo(50.0f);
        assertThat(lancamento.getPeriodicidade()).isEqualTo(Periodicidade.MENSAL);
        assertThat(lancamento.getDataInicio()).isEqualTo(dataInicio);
        assertThat(lancamento.getNumeroOcorrencias()).isEqualTo(12);
        assertThat(lancamento.getProximaData()).isEqualTo(proximaData);
        assertThat(lancamento.getConta()).isEqualTo(conta);
        assertThat(lancamento.getTransacao()).isEqualTo(transacao);
    }

    @Test
    void deveAlterarDescricao() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        lancamento.setDescricao("Assinatura Premium");
        
        assertThat(lancamento.getDescricao()).isEqualTo("Assinatura Premium");
    }

    @Test
    void deveAlterarValor() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        lancamento.setValor(75.0f);
        
        assertThat(lancamento.getValor()).isEqualTo(75.0f);
    }

    @Test
    void deveAlterarPeriodicidade() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        lancamento.setPeriodicidade(Periodicidade.ANUAL);
        
        assertThat(lancamento.getPeriodicidade()).isEqualTo(Periodicidade.ANUAL);
    }

    @Test
    void deveAlterarNumeroOcorrencias() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        lancamento.setNumeroOcorrencias(24);
        
        assertThat(lancamento.getNumeroOcorrencias()).isEqualTo(24);
    }

    @Test
    void deveAlterarProximaData() {
        Date data1 = new Date();
        Date data2 = new Date(System.currentTimeMillis() + 86400000);
        
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, data1, null, null
        );
        lancamento.setProximaData(data2);
        
        assertThat(lancamento.getProximaData()).isEqualTo(data2);
    }

    @Test
    void deveValidarValorPositivo() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        assertThat(lancamento.getValor()).isPositive();
    }

    @Test
    void deveTerDescricaoDefinida() {
        LancamentoRecorrente lancamento = new LancamentoRecorrente(
            "Assinatura", 50.0f, Periodicidade.MENSAL, new Date(), 12, new Date(), null, null
        );
        assertThat(lancamento.getDescricao()).isNotNull().isNotEmpty();
    }
}
