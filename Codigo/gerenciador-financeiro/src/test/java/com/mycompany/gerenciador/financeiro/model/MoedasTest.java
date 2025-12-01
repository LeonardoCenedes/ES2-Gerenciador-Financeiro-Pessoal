package com.mycompany.gerenciador.financeiro.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MoedasTest {

    @Test
    void deveCriarMoedaComConstrutorVazio() {
        Moedas moeda = new Moedas();
        assertThat(moeda).isNotNull();
    }

    @Test
    void deveCriarMoedaComTodosOsDados() {
        Moedas moeda = new Moedas("Real Brasileiro", "R$");
        
        assertThat(moeda.getNome()).isEqualTo("Real Brasileiro");
        assertThat(moeda.getSimbolo()).isEqualTo("R$");
    }

    @Test
    void deveAlterarNome() {
        Moedas moeda = new Moedas("Real", "R$");
        moeda.setNome("Dólar");
        
        assertThat(moeda.getNome()).isEqualTo("Dólar");
    }

    @Test
    void deveAlterarSimbolo() {
        Moedas moeda = new Moedas("Real", "R$");
        moeda.setSimbolo("BRL");
        
        assertThat(moeda.getSimbolo()).isEqualTo("BRL");
    }

    @Test
    void deveValidarNomeDefinido() {
        Moedas moeda = new Moedas("Real", "R$");
        assertThat(moeda.getNome()).isNotNull().isNotEmpty();
    }

    @Test
    void deveValidarSimboloDefinido() {
        Moedas moeda = new Moedas("Real", "R$");
        assertThat(moeda.getSimbolo()).isNotNull().isNotEmpty();
    }

    @Test
    void deveRetornarStringFormatada() {
        Moedas moeda = new Moedas("Dólar", "$");
        assertThat(moeda.toString()).contains("Dólar").contains("$");
    }

    @Test
    void deveInstanciarMoedaSemParametros() {
        Moedas moeda = new Moedas();
        assertThat(moeda).isNotNull();
    }

    @Test
    void deveCompararMoedasPorNome() {
        Moedas moeda1 = new Moedas("Real", "R$");
        Moedas moeda2 = new Moedas("Real", "R$");
        assertThat(moeda1.getNome()).isEqualTo(moeda2.getNome());
    }

    @Test
    void deveCompararMoedasPorSimbolo() {
        Moedas moeda1 = new Moedas("Dólar", "$");
        Moedas moeda2 = new Moedas("Euro", "€");
        assertThat(moeda1.getSimbolo()).isNotEqualTo(moeda2.getSimbolo());
    }
}
