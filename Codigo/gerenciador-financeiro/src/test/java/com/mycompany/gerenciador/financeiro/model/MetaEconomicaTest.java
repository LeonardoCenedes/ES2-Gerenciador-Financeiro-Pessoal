package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MetaEconomicaTest {

    @Test
    void deveCriarMetaEconomicaComConstrutorVazio() {
        MetaEconomica meta = new MetaEconomica();
        assertThat(meta).isNotNull();
    }

    @Test
    void deveCriarMetaEconomicaComTodosOsDados() {
        Date dataLimite = new Date(System.currentTimeMillis() + 86400000L * 30); // +30 dias
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, dataLimite, 0.0f, usuario);
        
        assertThat(meta.getNome()).isEqualTo("Viagem");
        assertThat(meta.getValor()).isEqualTo(5000.0f);
        assertThat(meta.getDataLimite()).isNotNull();
        assertThat(meta.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void deveAlterarNome() {
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, new Date(), 0.0f, null);
        meta.setNome("Carro Novo");
        
        assertThat(meta.getNome()).isEqualTo("Carro Novo");
    }

    @Test
    void deveAlterarValor() {
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, new Date(), 0.0f, null);
        meta.setValor(10000.0f);
        
        assertThat(meta.getValor()).isEqualTo(10000.0f);
    }

    @Test
    void deveAlterarDataLimite() {
        Date data1 = new Date();
        Date data2 = new Date(System.currentTimeMillis() + 86400000);
        
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, data1, 0.0f, null);
        meta.setDataLimite(data2);
        
        assertThat(meta.getDataLimite()).isEqualTo(data2);
    }

    @Test
    void deveAlterarUsuario() {
        Usuario user1 = new Usuario("João", "joao@email.com", "senha");
        Usuario user2 = new Usuario("Maria", "maria@email.com", "senha");
        
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, new Date(), 0.0f, user1);
        meta.setUsuario(user2);
        
        assertThat(meta.getUsuario()).isEqualTo(user2);
    }

    @Test
    void deveValidarValorPositivo() {
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, new Date(), 0.0f, null);
        assertThat(meta.getValor()).isPositive();
    }

    @Test
    void deveValidarNomeDefinido() {
        MetaEconomica meta = new MetaEconomica("Viagem", 5000.0f, new Date(), 0.0f, null);
        assertThat(meta.getNome()).isNotNull().isNotEmpty();
    }
}
