/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.repository.ContaRepositoryTxt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Laís Isabella
 */
public class CatalogoContas {

    private List<Conta> contas;
    private ContaRepositoryTxt repositorio;

    public CatalogoContas() throws IOException {
        this.repositorio = new ContaRepositoryTxt();
        this.contas = new ArrayList<>(repositorio.listar());
    }

    // Adiciona uma nova conta (e salva no arquivo)
    public void adicionarConta(Conta conta) throws IOException {
        contas.add(conta);
        repositorio.salvar(conta);
    }

    // Retorna a lista completa de contas
    public List<Conta> listarContas() {
        return new ArrayList<>(contas);
    }

    // Busca uma conta específica pelo ID
    public Conta buscarPorId(int id) {
        for (Conta c : contas) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    // Atualiza lista local lendo novamente o arquivo
    public void recarregar() throws IOException {
        this.contas = new ArrayList<>(repositorio.listar());
    }

    /**
     * Atualiza uma conta existente no catálogo e persiste no arquivo.
     * Implementa o caso de uso CE1.3 - Editar Conta Financeira.
     */
    public void atualizarConta(Conta contaEditada) throws IOException {
        // Localiza a conta pelo ID
        int index = -1;
        for (int i = 0; i < contas.size(); i++) {
            if (contas.get(i).getId() == contaEditada.getId()) {
                index = i;
                break;
            }
        }

        // Verifica se encontrou
        if (index == -1) {
            throw new IllegalArgumentException("Conta não encontrada para atualização.");
        }

        // Atualiza a conta na lista
        contas.set(index, contaEditada);

        // Sobrescreve o arquivo inteiro com todas as contas
        repositorio.salvarTodas(contas);
    }
    
    /**
 * Remove uma conta existente do catálogo e do arquivo.
 * Implementa o caso de uso CE1.4 - Excluir Conta Financeira.
 */
public void excluirConta(int id) throws IOException {
    // Localiza a conta pelo ID
    Conta contaParaRemover = null;
    for (Conta c : contas) {
        if (c.getId() == id) {
            contaParaRemover = c;
            break;
        }
    }
    
    // Verifica se encontrou
    if (contaParaRemover == null) {
        throw new IllegalArgumentException("Conta não encontrada para exclusão.");
    }
    
    // Remove a conta da lista
    contas.remove(contaParaRemover);
    
    // Sobrescreve o arquivo sem a conta removida
    repositorio.salvarTodas(contas);
}
}
