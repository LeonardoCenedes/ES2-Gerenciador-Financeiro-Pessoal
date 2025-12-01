package com.mycompany.gerenciador.financeiro.catalog;

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
 * Testes de integração para CatalogoMetaEconomica
 */
class CatalogoMetaEconomicaTest {

    private CatalogoMetaEconomica catalogo;
    private Usuario usuario;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoMetaEconomica();
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
    }

    @Test
    void deveSalvarNovaMetaEconomica() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // +1 ano
        MetaEconomica meta = new MetaEconomica("Carro Novo", 50000.0f, prazo, 0.0f, usuario);
        
        boolean resultado = catalogo.salvar(meta);
        
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarTodas()).contains(meta);
    }

    @Test
    void deveListarTodasAsMetas() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        MetaEconomica m1 = new MetaEconomica("Carro", 50000.0f, prazo, 0.0f, usuario);
        MetaEconomica m2 = new MetaEconomica("Casa", 200000.0f, prazo, 0.0f, usuario);
        
        catalogo.salvar(m1);
        catalogo.salvar(m2);
        
        List<MetaEconomica> metas = catalogo.listarTodas();
        
        assertThat(metas).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveBuscarMetasPorUsuario() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        MetaEconomica meta = new MetaEconomica("Viagem", 10000.0f, prazo, 0.0f, usuario);
        
        catalogo.salvar(meta);
        
        List<MetaEconomica> metasDoUsuario = catalogo.buscarPorUsuario(usuario);
        
        assertThat(metasDoUsuario).isNotEmpty();
        assertThat(metasDoUsuario).anyMatch(m -> m.getUsuario().getEmail().equals("teste@email.com"));
    }

    @Test
    void deveBuscarMetaPorNome() {
        Date prazo = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        MetaEconomica meta = new MetaEconomica("Notebook", 5000.0f, prazo, 0.0f, usuario);
        
        catalogo.salvar(meta);
        
        MetaEconomica encontrada = catalogo.buscarPorNome("Notebook", usuario);
        
        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getNome()).isEqualTo("Notebook");
    }

    @Test
    void deveRetornarNullQuandoMetaNaoExiste() {
        MetaEconomica meta = catalogo.buscarPorNome("Inexistente", usuario);
        
        assertThat(meta).isNull();
    }


    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
