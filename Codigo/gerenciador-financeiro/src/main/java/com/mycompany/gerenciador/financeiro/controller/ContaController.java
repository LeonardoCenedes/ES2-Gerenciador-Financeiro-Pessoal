package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.repository.ContaRepositoryTxt;
import java.io.IOException;
import java.util.List;

public class ContaController {
    
    private ContaRepositoryTxt repository;

    public ContaController() {
        this.repository = new ContaRepositoryTxt();
    }

    /**
     * Cadastra uma nova conta financeira.
     * Implementa o caso de uso CE1.1.
     */
    public void cadastrarConta(String nome, String tipo, double saldoInicial, String moeda) 
            throws IllegalArgumentException, IOException {
        
        validarDados(nome, tipo, saldoInicial, moeda);

        Conta novaConta = new Conta(nome, tipo, saldoInicial, moeda);

        repository.salvar(novaConta);
    }

    /**
     * Lista todas as contas cadastradas.
     * Implementa o caso de uso CE1.2 - Visualizar Conta Financeira.
     * 
     * @return Lista de todas as contas
     * @throws IOException se houver erro ao ler o arquivo
     */
    public List<Conta> listarContas() throws IOException {
        return repository.listar();
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
}