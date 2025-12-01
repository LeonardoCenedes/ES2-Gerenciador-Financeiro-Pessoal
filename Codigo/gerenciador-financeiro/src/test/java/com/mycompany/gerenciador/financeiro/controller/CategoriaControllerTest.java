package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.model.Categoria;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    private ControladorCategoria controller;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        controller = new ControladorCategoria();
    }

    @Test
    void deveCriarCategoriaComSucesso() throws IOException {
        boolean resultado = controller.criarCategoria("Alimentação", false, true);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarCategoriaComNomeVazio() throws IOException {
        boolean resultado = controller.criarCategoria("", false, true);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarCategoriaComNomeNull() throws IOException {
        boolean resultado = controller.criarCategoria(null, false, true);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveListarTodasAsCategorias() throws IOException {
        controller.criarCategoria("Alimentação", false, true);
        controller.criarCategoria("Transporte", false, true);
        
        List<Categoria> categorias = controller.buscarCategorias();
        
        assertThat(categorias).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveListarApenasCategoriasAtivas() throws IOException {
        controller.criarCategoria("Ativa", false, true);
        controller.criarCategoria("Inativa", false, false);
        
        List<Categoria> ativas = controller.buscarCategoriasAtivas();
        
        assertThat(ativas).isNotEmpty();
        assertThat(ativas).allMatch(Categoria::isStatus);
    }

    @Test
    void deveEditarCategoria() throws IOException {
        controller.criarCategoria("Original", false, true);
        
        Categoria categoria = controller.buscarCategorias().stream()
            .filter(c -> c.getNome().equals("Original"))
            .findFirst()
            .orElse(null);
        
        assertThat(categoria).isNotNull();
        
        boolean resultado = controller.editarCategoria(categoria, categoria, "Editado", false, true);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveDesativarCategoria() throws IOException {
        controller.criarCategoria("ParaDesativar", false, true);
        
        Categoria categoria = controller.buscarCategorias().stream()
            .filter(c -> c.getNome().equals("ParaDesativar"))
            .findFirst()
            .orElse(null);
        
        assertThat(categoria).isNotNull();
        
        boolean resultado = controller.desativarCategoria(categoria);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveDesativarCategoriaPadrao() throws IOException {
        Categoria categoriaPadrao = new Categoria("Padrão", true, true);
        
        boolean resultado = controller.desativarCategoria(categoriaPadrao);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
