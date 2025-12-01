package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para ControladorMetaEconomica
 */
class ControladorMetaEconomicaTest {

    private ControladorMetaEconomica controller;
    private Usuario usuario;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        controller = new ControladorMetaEconomica();
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
    }

    @Test
    void deveCriarMetaEconomicaComSucesso() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // +1 ano
        
        boolean resultado = controller.criarMeta("Carro Novo", 50000.0f, prazo, usuario);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void naoDeveCriarMetaComNomeVazio() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        boolean resultado = controller.criarMeta("", 50000.0f, prazo, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarMetaComNomeNulo() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        boolean resultado = controller.criarMeta(null, 50000.0f, prazo, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarMetaComValorZero() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        boolean resultado = controller.criarMeta("Meta", 0.0f, prazo, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarMetaComValorNegativo() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        boolean resultado = controller.criarMeta("Meta", -100.0f, prazo, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarMetaComDataLimiteNula() {
        boolean resultado = controller.criarMeta("Meta", 10000.0f, null, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void naoDeveCriarMetaDuplicada() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        controller.criarMeta("Viagem", 10000.0f, prazo, usuario);
        boolean resultado = controller.criarMeta("Viagem", 15000.0f, prazo, usuario);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveBuscarMetasPorUsuario() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        controller.criarMeta("Notebook", 5000.0f, prazo, usuario);
        
        List<MetaEconomica> metas = controller.buscarPorUsuario(usuario);
        
        assertThat(metas).isNotEmpty();
        assertThat(metas).anyMatch(m -> m.getNome().equals("Notebook"));
    }

    @Test
    void deveContribuirParaMeta() throws IOException {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        controller.criarMeta("Investimento", 10000.0f, prazo, usuario);
        
        List<MetaEconomica> metas = controller.buscarPorUsuario(usuario);
        MetaEconomica meta = metas.stream()
            .filter(m -> m.getNome().equals("Investimento"))
            .findFirst()
            .orElse(null);
        
        assertThat(meta).isNotNull();
        
        boolean resultado = controller.contribuirParaMeta(meta, 500.0f);
        
        assertThat(resultado).isTrue();
        assertThat(meta.getValorEconomizadoAtual()).isEqualTo(500.0f);
    }

    @Test
    void naoDeveContribuirParaMetaNula() throws IOException {
        boolean resultado = controller.contribuirParaMeta(null, 500.0f);
        
        assertThat(resultado).isFalse();
    }

    @Test
    void deveExcluirMeta() throws IOException {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
