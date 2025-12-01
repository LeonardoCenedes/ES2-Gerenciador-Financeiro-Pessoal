package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class OrcamentoTest {

    @Test
    void deveCriarOrcamentoComConstrutorVazio() {
        Orcamento orcamento = new Orcamento();
        assertThat(orcamento).isNotNull();
    }

    @Test
    void deveCriarOrcamentoComTodosOsDados() {
        Date periodo = new Date();
        Categoria categoria = new Categoria("Alimentação", false, true);
        Usuario usuario = new Usuario("João", "joao@email.com", "senha123");
        
        Orcamento orcamento = new Orcamento(periodo, 1500.0f, categoria, usuario);
        
        assertThat(orcamento.getPeriodo()).isEqualTo(periodo);
        assertThat(orcamento.getValorMaximo()).isEqualTo(1500.0f);
        assertThat(orcamento.getCategoria()).isEqualTo(categoria);
        assertThat(orcamento.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void deveAlterarPeriodo() {
        Date periodo1 = new Date();
        Date periodo2 = new Date(System.currentTimeMillis() + 86400000); // +1 dia
        
        Orcamento orcamento = new Orcamento(periodo1, 1000.0f, null, null);
        orcamento.setPeriodo(periodo2);
        
        assertThat(orcamento.getPeriodo()).isEqualTo(periodo2);
    }

    @Test
    void deveAlterarValorMaximo() {
        Orcamento orcamento = new Orcamento(new Date(), 1000.0f, null, null);
        orcamento.setValorMaximo(2000.0f);
        
        assertThat(orcamento.getValorMaximo()).isEqualTo(2000.0f);
    }

    @Test
    void deveAlterarCategoria() {
        Categoria cat1 = new Categoria("Alimentação", false, true);
        Categoria cat2 = new Categoria("Transporte", false, true);
        
        Orcamento orcamento = new Orcamento(new Date(), 1000.0f, cat1, null);
        orcamento.setCategoria(cat2);
        
        assertThat(orcamento.getCategoria()).isEqualTo(cat2);
    }

    @Test
    void deveAlterarUsuario() {
        Usuario user1 = new Usuario("João", "joao@email.com", "senha");
        Usuario user2 = new Usuario("Maria", "maria@email.com", "senha");
        
        Orcamento orcamento = new Orcamento(new Date(), 1000.0f, null, user1);
        orcamento.setUsuario(user2);
        
        assertThat(orcamento.getUsuario()).isEqualTo(user2);
    }

    @Test
    void deveValidarValorMaximoPositivo() {
        Orcamento orcamento = new Orcamento(new Date(), 1500.0f, null, null);
        assertThat(orcamento.getValorMaximo()).isPositive();
    }
}
