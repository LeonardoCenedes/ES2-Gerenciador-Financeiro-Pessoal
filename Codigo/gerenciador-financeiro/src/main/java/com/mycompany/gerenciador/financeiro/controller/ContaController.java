package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoContas;
import com.mycompany.gerenciador.financeiro.model.Conta;
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
    public void cadastrarConta(String nome, String tipo, double saldoInicial, String moeda)
            throws IllegalArgumentException, IOException {

        validarDados(nome, tipo, saldoInicial, moeda);

        Conta novaConta = new Conta(nome, tipo, saldoInicial, moeda);
        catalogo.adicionarConta(novaConta);
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
    public Conta buscarContaPorId(int id) {
        return catalogo.buscarPorId(id);
    }

    private void validarDados(String nome, String tipo, double saldoInicial, String moeda)
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
    public void editarConta(int id, String nome, String tipo, double saldoInicial)
            throws IllegalArgumentException, IOException {

        // Busca a conta pelo ID
        Conta conta = catalogo.buscarPorId(id);

        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

        // Valida os novos dados
        validarDados(nome, tipo, saldoInicial, conta.getMoeda());

        // Atualiza os campos (mantém ID e moeda)
        conta.setNome(nome);
        conta.setTipo(tipo);
        conta.setSaldoInicial(saldoInicial);
        // A moeda NÃO é alterada

        // Salva a atualização
        catalogo.atualizarConta(conta);
    }

    /**
     * Exclui uma conta existente. Implementa o caso de uso CE1.4 - Excluir
     * Conta Financeira.
     */
    public void excluirConta(int id) throws IllegalArgumentException, IOException {
        catalogo.excluirConta(id);
    }
}
