package com.mycompany.gerenciador.financeiro.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContaTest {

    private Usuario usuario;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        usuario = new Usuario("João", "joao@email.com", "senha123");
    }

    @Test
    void deveCriarContaComConstrutorVazio() {
        Conta conta = new Conta();
        assertThat(conta).isNotNull();
    }

    @Test
    void deveCriarContaComTodosOsDados() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        
        assertThat(conta.getNome()).isEqualTo("Conta Corrente");
        assertThat(conta.getTipo()).isEqualTo("Corrente");
        assertThat(conta.getSaldoInicial()).isEqualTo(1000.0f);
        assertThat(conta.getMoeda()).isEqualTo("BRL");
        assertThat(conta.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void deveAlterarNomeDaConta() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        conta.setNome("Conta Poupança");
        
        assertThat(conta.getNome()).isEqualTo("Conta Poupança");
    }

    @Test
    void deveAlterarTipoDaConta() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        conta.setTipo("Poupança");
        
        assertThat(conta.getTipo()).isEqualTo("Poupança");
    }

    @Test
    void deveAlterarSaldoInicial() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        conta.setSaldoInicial(2000.0f);
        
        assertThat(conta.getSaldoInicial()).isEqualTo(2000.0f);
    }

    @Test
    void deveAssociarUsuario() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        Usuario novoUsuario = new Usuario("Maria", "maria@email.com", "senha456");
        conta.setUsuario(novoUsuario);
        
        assertThat(conta.getUsuario()).isEqualTo(novoUsuario);
    }

    @Test
    void deveTerSaldoPositivo() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        assertThat(conta.getSaldoInicial()).isPositive();
    }

    @Test
    void deveTerMoedaDefinida() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        assertThat(conta.getMoeda()).isNotNull().isNotEmpty();
    }
}
