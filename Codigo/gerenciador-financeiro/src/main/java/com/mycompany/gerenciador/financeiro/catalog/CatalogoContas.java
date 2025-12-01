/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.ContaRepositoryTxt;

/**
 *
 * @author Laís Isabella
 */
public class CatalogoContas {

    private final List<Conta> contas;
    private final ContaRepositoryTxt repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoContas() throws IOException {
        this.repositorio = new ContaRepositoryTxt();
        this.contas = new ArrayList<>(repositorio.carregarTodos());
    }

    /**
     * Adiciona uma nova conta APENAS em memória
     */
    public void salvar(Conta conta) {
        contas.add(conta);
    }

    /**
     * Retorna a lista completa de contas da memória
     */
    public List<Conta> listarContas() {
        return new ArrayList<>(contas);
    }

    /**
     * Busca contas por usuário na memória
     */
    public List<Conta> buscarPorUsuario(Usuario usuario) {
        List<Conta> contasDoUsuario = new ArrayList<>();

        for (Conta c : contas) {
            if (c.getUsuario() != null
                    && c.getUsuario().getEmail().equals(usuario.getEmail())) {
                contasDoUsuario.add(c);
            }
        }

        return contasDoUsuario;
    }

    /**
     * Atualiza uma conta existente APENAS em memória
     * Implementa o caso de uso CE1.3 - Editar Conta Financeira
     */
    public void atualizar(Conta contaEditada, String nomeAnterior) {
        // Localiza a conta pelo NOME ANTERIOR e email do usuário
        int index = -1;
        for (int i = 0; i < contas.size(); i++) {
            Conta c = contas.get(i);
            if (c.getNome().equals(nomeAnterior)
                    && c.getUsuario().getEmail().equals(contaEditada.getUsuario().getEmail())) {
                index = i;
                break;
            }
        }

        // Verifica se encontrou
        if (index == -1) {
            throw new IllegalArgumentException("Conta não encontrada para atualização.");
        }

        // Atualiza a conta na lista em memória
        contas.set(index, contaEditada);
    }

    /**
     * Remove uma conta existente APENAS da memória
     * Implementa o caso de uso CE1.4 - Excluir Conta Financeira
     */
    public void excluir(Conta conta) {
        // Localiza a conta
        Conta contaParaRemover = null;
        for (Conta c : contas) {
            if (c.getNome().equals(conta.getNome())
                    && c.getUsuario().getEmail().equals(conta.getUsuario().getEmail())) {
                contaParaRemover = c;
                break;
            }
        }

        // Verifica se encontrou
        if (contaParaRemover == null) {
            throw new IllegalArgumentException("Conta não encontrada para exclusão.");
        }

        // Remove a conta da lista em memória
        contas.remove(contaParaRemover);
        // Remoção apenas da memória
    }

    /**
     * Persiste todas as contas da memória para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(contas);
    }

    /**
     * CE14 - Soma o saldo de uma lista de contas
     * Calcula o saldo consolidado
     */
    public float somarSaldoDeContas(List<Conta> contas) {
        float saldoTotal = 0.0f;
        for (Conta conta : contas) {
            saldoTotal += conta.getSaldoInicial();
        }
        return saldoTotal;
    }
}
