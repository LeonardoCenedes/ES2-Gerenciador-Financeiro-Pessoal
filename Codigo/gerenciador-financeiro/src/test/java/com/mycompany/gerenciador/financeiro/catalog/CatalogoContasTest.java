package com.mycompany.gerenciador.financeiro.catalog;

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
class CatalogoContasTest {

    private CatalogoContas catalogo;
    private Usuario usuario;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException {
        catalogo = new CatalogoContas();
        usuario = new Usuario("João", "joao@email.com", "senha123");
    }

    @Test
    void deveAdicionarNovaConta() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        
        catalogo.salvar(conta);
        
        assertThat(catalogo.listarContas()).contains(conta);
    }

    @Test
    void deveBuscarContasPorUsuario() {
        Conta conta1 = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        Conta conta2 = new Conta("Poupança", "Poupança", 5000.0f, "BRL", usuario);
        
        Usuario outroUsuario = new Usuario("Maria", "maria@email.com", "senha456");
        Conta conta3 = new Conta("Conta Maria", "Corrente", 2000.0f, "BRL", outroUsuario);
        
        catalogo.salvar(conta1);
        catalogo.salvar(conta2);
        catalogo.salvar(conta3);
        
        List<Conta> contasDoUsuario = catalogo.buscarPorUsuario(usuario);
        
        assertThat(contasDoUsuario).hasSize(2);
        assertThat(contasDoUsuario).contains(conta1, conta2);
        assertThat(contasDoUsuario).doesNotContain(conta3);
    }

    @Test
    void deveAtualizarConta() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        catalogo.salvar(conta);
        
        conta.setNome("Nova Conta Corrente");
        conta.setSaldoInicial(2000.0f);
        
        catalogo.atualizar(conta, "Conta Corrente");
        
        List<Conta> contas = catalogo.buscarPorUsuario(usuario);
        assertThat(contas).hasSize(1);
        assertThat(contas.get(0).getNome()).isEqualTo("Nova Conta Corrente");
        assertThat(contas.get(0).getSaldoInicial()).isEqualTo(2000.0f);
    }

    @Test
    void deveExcluirConta() {
        Conta conta = new Conta("Conta Corrente", "Corrente", 1000.0f, "BRL", usuario);
        catalogo.salvar(conta);
        
        catalogo.excluir(conta);
        
        assertThat(catalogo.buscarPorUsuario(usuario)).isEmpty();
    }

    @Test
    void deveListarTodasAsContas() {
        Conta conta1 = new Conta("Conta 1", "Corrente", 1000.0f, "BRL", usuario);
        Conta conta2 = new Conta("Conta 2", "Poupança", 2000.0f, "BRL", usuario);
        
        catalogo.salvar(conta1);
        catalogo.salvar(conta2);
        
        List<Conta> contas = catalogo.listarContas();
        
        assertThat(contas).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveLancarExcecaoAoAtualizarContaInexistente() {
        Conta conta = new Conta("Conta Inexistente", "Corrente", 1000.0f, "BRL", usuario);
        
        assertThatThrownBy(() -> catalogo.atualizar(conta, "Conta Que Não Existe"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não encontrada");
    }

    @Test
    void deveChamarSalvarAoEncerrar() {
        assertThatCode(() -> catalogo.salvarAoEncerrar()).doesNotThrowAnyException();
    }
}
