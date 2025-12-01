package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para CatalogoOrcamento
 */
class CatalogoOrcamentoTest {

    private CatalogoOrcamento catalogo;
    private Usuario usuario;
    private Categoria categoria;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoOrcamento();
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
        categoria = new Categoria("Alimentação", false, true);
    }

    @Test
    void deveSalvarNovoOrcamento() {
        Date periodo = new Date();
        Orcamento orcamento = new Orcamento(periodo, 1000.0f, categoria, usuario);
        boolean resultado = catalogo.salvar(orcamento);
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarTodos()).contains(orcamento);
    }

    @Test
    void deveListarTodosOsOrcamentos() {
        Date periodo = new Date();
        Orcamento o1 = new Orcamento(periodo, 1000.0f, categoria, usuario);
        Orcamento o2 = new Orcamento(periodo, 2000.0f, categoria, usuario);
        catalogo.salvar(o1);
        catalogo.salvar(o2);
        List<Orcamento> orcamentos = catalogo.listarTodos();
        assertThat(orcamentos).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveBuscarOrcamentosPorUsuario() {
        Date periodo = new Date();
        Orcamento orcamento = new Orcamento(periodo, 1000.0f, categoria, usuario);
        catalogo.salvar(orcamento);
        List<Orcamento> orcamentosDoUsuario = catalogo.buscarPorUsuario(usuario);
        assertThat(orcamentosDoUsuario).isNotEmpty();
        assertThat(orcamentosDoUsuario).anyMatch(o -> o.getUsuario().getEmail().equals("teste@email.com"));
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemOrcamentos() {
        Usuario outroUsuario = new Usuario("Outro", "outro@email.com", "senha456");
        List<Orcamento> orcamentos = catalogo.buscarPorUsuario(outroUsuario);
        assertThat(orcamentos).isEmpty();
    }


    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
