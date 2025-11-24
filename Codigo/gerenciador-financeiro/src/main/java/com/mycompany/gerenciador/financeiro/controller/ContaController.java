package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoContas;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import java.io.IOException;
import java.util.List;

public class ContaController {

    private CatalogoContas catalogo;

    public ContaController() throws IOException {
        this.catalogo = new CatalogoContas();
    }

    /**
     * Cadastra uma nova conta financeira. Implementa o caso de uso CE1.1.
     */
    public void criarConta(String nome, String tipo, double saldoInicial, String moeda, Usuario usuario)
            throws IllegalArgumentException, IOException {

        validarConta(nome, tipo, saldoInicial, moeda);

        Conta novaConta = new Conta(nome, tipo, saldoInicial, moeda, usuario);
        catalogo.salvar(novaConta);
    }

    /**
     * Lista todas as contas cadastradas. Implementa o caso de uso CE1.2 -
     * Visualizar Conta Financeira.
     *
     * @return Lista de todas as contas
     * @throws IOException se houver erro ao ler o arquivo
     */
    public List<Conta> listarContas() throws IOException {
        catalogo.recarregar(); // Atualiza com o conteúdo mais recente do arquivo
        return catalogo.listarContas();
    }

    /**
     * Busca uma conta específica pelo ID (opcional, pode ser usado em edições
     * futuras).
     */
    public List<Conta> buscarContasUsuario(Usuario usuario) throws IOException {
        catalogo.recarregar();
        return catalogo.buscarPorUsuario(usuario);
    }

    private void validarConta(String nome, String tipo, double saldoInicial, String moeda)
            throws IllegalArgumentException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da conta não pode ser vazio.");
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo da conta deve ser selecionado.");
        }

        if (saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }

        if (moeda == null || moeda.trim().isEmpty()) {
            throw new IllegalArgumentException("A moeda deve ser selecionada.");
        }
    }

    /**
     * Edita uma conta existente. Implementa o caso de uso CE1.3 - Editar Conta
     * Financeira.
     */
    public void editarConta(Conta contaOriginal, String nome, String tipo, double saldoInicial)
            throws IllegalArgumentException, IOException {

        validarConta(nome, tipo, saldoInicial, contaOriginal.getMoeda());

        contaOriginal.setNome(nome);
        contaOriginal.setTipo(tipo);
        contaOriginal.setSaldoInicial(saldoInicial);

        catalogo.atualizar(contaOriginal);
    }

    /**
     * Exclui uma conta existente. Implementa o caso de uso CE1.4 - Excluir
     * Conta Financeira.
     */
    public void excluirConta(Conta conta) throws IOException {
        catalogo.excluir(conta);
    }
}
