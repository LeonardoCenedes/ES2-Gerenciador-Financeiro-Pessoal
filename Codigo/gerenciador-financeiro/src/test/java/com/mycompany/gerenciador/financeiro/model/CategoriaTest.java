package com.mycompany.gerenciador.financeiro.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void deveCriarCategoriaComConstrutorVazio() {
        Categoria categoria = new Categoria();
        assertThat(categoria).isNotNull();
    }

    @Test
    void deveCriarCategoriaComTodosOsDados() {
        Categoria categoria = new Categoria("Alimentação", true, true);
        
        assertThat(categoria.getNome()).isEqualTo("Alimentação");
        assertThat(categoria.isPadrao()).isTrue();
        assertThat(categoria.isStatus()).isTrue();
    }

    @Test
    void deveAlterarNomeDaCategoria() {
        Categoria categoria = new Categoria("Alimentação", true, true);
        categoria.setNome("Transporte");
        
        assertThat(categoria.getNome()).isEqualTo("Transporte");
    }

    @Test
    void deveAlterarStatusPadrao() {
        Categoria categoria = new Categoria("Alimentação", true, true);
        categoria.setPadrao(false);
        
        assertThat(categoria.isPadrao()).isFalse();
    }

    @Test
    void deveAlterarStatus() {
        Categoria categoria = new Categoria("Alimentação", true, true);
        categoria.setStatus(false);
        
        assertThat(categoria.isStatus()).isFalse();
    }

    @Test
    void deveTerNomeDefinido() {
        Categoria categoria = new Categoria("Alimentação", true, true);
        assertThat(categoria.getNome()).isNotNull().isNotEmpty();
    }

    @Test
    void categoriaDesativadaDeveRetornarFalse() {
        Categoria categoria = new Categoria("Alimentação", true, false);
        assertThat(categoria.isStatus()).isFalse();
    }
}
