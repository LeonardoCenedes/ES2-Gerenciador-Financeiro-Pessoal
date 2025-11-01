/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.repositoy.ContaRepositoryTxt;
import java.io.IOException;


/**
 *
 * @author Laís Isabella
 */
public class ContaController {
    private ContaRepositoryTxt repository;

    /**
     * Construtor que inicializa o repositório
     */
    public ContaController() {
        this.repository = new ContaRepositoryTxt();
    }

    /**
     * Cadastra uma nova conta financeira.
     * Realiza validações básicas antes de persistir.
     * 
     * @param nome Nome da conta
     * @param tipo Tipo da conta (Conta Corrente, Poupança, etc)
     * @param saldoInicial Saldo inicial da conta
     * @param moeda Moeda padrão (BRL, USD, etc)
     * @throws IllegalArgumentException se alguma validação falhar
     * @throws IOException se houver erro ao salvar no arquivo
     */
    public void cadastrarConta(String nome, String tipo, double saldoInicial, String moeda) 
            throws IllegalArgumentException, IOException {
        
        // Validações
        validarDados(nome, tipo, saldoInicial, moeda);

        // Cria a nova conta
        Conta novaConta = new Conta(nome, tipo, saldoInicial, moeda);

        // Salva no repositório
        repository.salvar(novaConta);
    }

    /**
     * Valida os dados da conta antes de criar
     */
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
