package com.mycompany.gerenciador.financeiro.catalog;

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
class CatalogoCategoriaTest {

    private CatalogoCategoria catalogo;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoCategoria();
    }

    @Test
    void deveAdicionarNovaCategoria() {
        Categoria categoria = new Categoria("Alimentação", false, true);
        
        boolean resultado = catalogo.salvar(categoria);
        
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarTodas()).contains(categoria);
    }

    @Test
    void deveListarTodasAsCategorias() {
        Categoria categoria1 = new Categoria("Alimentação", false, true);
        Categoria categoria2 = new Categoria("Transporte", false, true);
        
        catalogo.salvar(categoria1);
        catalogo.salvar(categoria2);
        
        List<Categoria> categorias = catalogo.listarTodas();
        
        assertThat(categorias).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveListarApenasCategoriasAtivas() {
        Categoria ativa = new Categoria("Alimentação", false, true);
        Categoria inativa = new Categoria("Transporte", false, false);
        
        catalogo.salvar(ativa);
        catalogo.salvar(inativa);
        
        List<Categoria> ativas = catalogo.listarAtivas();
        
        assertThat(ativas).contains(ativa);
        assertThat(ativas).doesNotContain(inativa);
    }

    @Test
    void deveBuscarCategoriaPorNome() {
        Categoria categoria = new Categoria("Alimentação", false, true);
        catalogo.salvar(categoria);
        
        Categoria encontrada = catalogo.buscarPorNome("Alimentação");
        
        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getNome()).isEqualTo("Alimentação");
    }

    @Test
    void deveRetornarNullQuandoCategoriaNaoExiste() {
        Categoria encontrada = catalogo.buscarPorNome("NaoExiste");
        
        assertThat(encontrada).isNull();
    }

    @Test
    void deveAtualizarCategoria() {
        Categoria categoria = new Categoria("Alimentação", false, true);
        catalogo.salvar(categoria);
        
        boolean resultado = catalogo.atualizar(categoria, "Comida", false, true);
        
        assertThat(resultado).isTrue();
        Categoria atualizada = catalogo.buscarPorNome("Comida");
        assertThat(atualizada).isNotNull();
    }

    @Test
    void deveDesativarCategoria() {
        Categoria categoria = new Categoria("Alimentação", false, true);
        catalogo.salvar(categoria);
        
        boolean resultado = catalogo.desativar(categoria);
        
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarAtivas()).doesNotContain(categoria);
    }

    @Test
    void naoDeveDesativarCategoriaPadrao() {
        Categoria categoriaPadrao = new Categoria("Padrão", true, true);
        catalogo.salvar(categoriaPadrao);
        
        boolean resultado = catalogo.desativar(categoriaPadrao);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
