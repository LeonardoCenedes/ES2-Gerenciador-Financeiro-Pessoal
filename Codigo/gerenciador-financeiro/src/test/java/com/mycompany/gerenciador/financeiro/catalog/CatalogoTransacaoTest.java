package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Testes de integração para CatalogoTransacao
 */
class CatalogoTransacaoTest {

    private CatalogoTransacao catalogo;
    private Usuario usuario;
    private Conta conta;
    private Categoria categoria;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() throws IOException {
        catalogo = new CatalogoTransacao();
        usuario = new Usuario("Teste", "teste@email.com", "senha123");
        conta = new Conta("Conta Teste", "Corrente", 1000.0f, "BRL", usuario);
        categoria = new Categoria("Alimentação", false, true);
    }

    @Test
    void deveAdicionarNovaTransacao() {
        Date data = new Date();
        Transacao transacao = new Transacao(data, 100.0f, categoria, "Compra", null, 
                                            TiposTransacao.SAIDA, conta, null);
        
        boolean resultado = catalogo.adicionar(transacao);
        
        assertThat(resultado).isTrue();
        assertThat(catalogo.listarTodas()).contains(transacao);
    }

    @Test
    void deveListarTodasAsTransacoes() {
        Date data = new Date();
        Transacao t1 = new Transacao(data, 100.0f, categoria, "Compra 1", null, 
                                      TiposTransacao.SAIDA, conta, null);
        Transacao t2 = new Transacao(data, 200.0f, categoria, "Compra 2", null, 
                                      TiposTransacao.ENTRADA, conta, null);
        
        catalogo.adicionar(t1);
        catalogo.adicionar(t2);
        
        List<Transacao> transacoes = catalogo.listarTodas();
        
        assertThat(transacoes).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveFiltrarTransacoesPorConta() {
        Date data = new Date();
        Transacao transacao = new Transacao(data, 100.0f, categoria, "Compra", null, 
                                            TiposTransacao.SAIDA, conta, null);
        catalogo.adicionar(transacao);
        
        List<Transacao> filtradas = catalogo.listarTransacoesFiltrada(conta, null, null, null);
        
        assertThat(filtradas).isNotEmpty();
        assertThat(filtradas).anyMatch(t -> t.getDescricao().equals("Compra"));
    }

    @Test
    void deveFiltrarTransacoesPorTipo() {
        Date data = new Date();
        Transacao entrada = new Transacao(data, 500.0f, categoria, "Salário", null, 
                                          TiposTransacao.ENTRADA, conta, null);
        Transacao saida = new Transacao(data, 100.0f, categoria, "Compra", null, 
                                        TiposTransacao.SAIDA, conta, null);
        
        catalogo.adicionar(entrada);
        catalogo.adicionar(saida);
        
        List<Transacao> entradas = catalogo.listarTransacoesFiltrada(null, null, null, TiposTransacao.ENTRADA);
        
        assertThat(entradas).anyMatch(t -> t.getTipo() == TiposTransacao.ENTRADA);
    }

    @Test
    void deveFiltrarTransacoesPorCategoria() {
        Date data = new Date();
        Categoria lazer = new Categoria("Lazer", false, true);
        Transacao t1 = new Transacao(data, 100.0f, categoria, "Comida", null, 
                                      TiposTransacao.SAIDA, conta, null);
        Transacao t2 = new Transacao(data, 50.0f, lazer, "Cinema", null, 
                                      TiposTransacao.SAIDA, conta, null);
        
        catalogo.adicionar(t1);
        catalogo.adicionar(t2);
        
        List<Transacao> filtradas = catalogo.listarTransacoesFiltrada(null, null, categoria, null);
        
        assertThat(filtradas).anyMatch(t -> t.getCategoria().getNome().equals("Alimentação"));
    }

    @Test
    void deveAtualizarTransacao() {
        Date data = new Date();
        Transacao transacao = new Transacao(data, 100.0f, categoria, "Original", null, 
                                            TiposTransacao.SAIDA, conta, null);
        catalogo.adicionar(transacao);
        
        boolean resultado = catalogo.atualizar(transacao, data, 150.0f, categoria, 
                                               "Atualizado", null, TiposTransacao.SAIDA);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveExcluirTransacao() {
        Date data = new Date();
        Transacao transacao = new Transacao(data, 100.0f, categoria, "Para excluir", null, 
                                            TiposTransacao.SAIDA, conta, null);
        catalogo.adicionar(transacao);
        
        boolean resultado = catalogo.excluir(transacao);
        
        assertThat(resultado).isTrue();
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
