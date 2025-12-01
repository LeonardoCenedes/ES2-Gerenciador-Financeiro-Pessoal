package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    private ControladorConta controller;
    private ControladorTransacao transacaoController;
    private ControladorMetaEconomica metaController;
    private Usuario usuario;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        usuario = new Usuario("João", "joao@email.com", "senha123");
        metaController = new ControladorMetaEconomica();
        transacaoController = new ControladorTransacao(metaController);
        controller = new ControladorConta(transacaoController);
    }

    @Test
    void deveCriarContaComSucesso() throws IOException {
        controller.criarConta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        
        List<Conta> contas = controller.buscarContasUsuario(usuario);
        
        assertThat(contas).isNotEmpty();
        assertThat(contas).anyMatch(c -> c.getNome().equals("Conta Corrente"));
    }

    @Test
    void naoDeveCriarContaComNomeVazio() {
        assertThatThrownBy(() -> 
            controller.criarConta("", "Corrente", 1000.0f, "BRL", usuario)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void naoDeveCriarContaComSaldoNegativo() {
        assertThatThrownBy(() -> 
            controller.criarConta("Conta", "Corrente", -100.0f, "BRL", usuario)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void naoDeveCriarContaComMoedaVazia() {
        assertThatThrownBy(() -> 
            controller.criarConta("Conta", "Corrente", 1000.0f, "", usuario)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveListarContasPorUsuario() throws IOException {
        controller.criarConta("Conta 1", "Corrente", 1000.0f, "BRL", usuario);
        controller.criarConta("Conta 2", "Poupança", 2000.0f, "BRL", usuario);
        
        List<Conta> contas = controller.buscarContasUsuario(usuario);
        
        assertThat(contas).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveEditarConta() throws IOException {
        controller.criarConta("Conta Original", "Corrente", 1000.0f, "BRL", usuario);
        
        List<Conta> contas = controller.buscarContasUsuario(usuario);
        Conta conta = contas.stream()
            .filter(c -> c.getNome().equals("Conta Original"))
            .findFirst()
            .orElse(null);
        
        assertThat(conta).isNotNull();
        
        controller.editarConta(conta, "Conta Editada", "Poupança", 2000.0f);
        
        List<Conta> contasAtualizadas = controller.buscarContasUsuario(usuario);
        assertThat(contasAtualizadas).anyMatch(c -> c.getNome().equals("Conta Editada"));
    }

    @Test
    void deveExcluirConta() throws IOException {
        controller.criarConta("Conta Para Excluir", "Corrente", 1000.0f, "BRL", usuario);
        
        List<Conta> contas = controller.buscarContasUsuario(usuario);
        Conta conta = contas.stream()
            .filter(c -> c.getNome().equals("Conta Para Excluir"))
            .findFirst()
            .orElse(null);
        
        assertThat(conta).isNotNull();
        
        controller.excluirConta(conta);
        
        List<Conta> contasAposExclusao = controller.buscarContasUsuario(usuario);
        assertThat(contasAposExclusao).noneMatch(c -> c.getNome().equals("Conta Para Excluir"));
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> controller.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
