/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.LancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.repository.LancamentoRecorrenteRepositoryTxt;

/**
 * Catálogo de lançamentos recorrentes - Padrão In-Memory Cache
 * Mantém todos os lançamentos recorrentes em memória e persiste apenas ao final
 * @author Laís Isabella
 */
public class CatalogoLancamentoRecorrente {
    
    private final List<LancamentoRecorrente> lancamentos;
    private final LancamentoRecorrenteRepositoryTxt repository;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoLancamentoRecorrente() throws IOException {
        this.repository = new LancamentoRecorrenteRepositoryTxt();
        this.lancamentos = repository.carregarTodos();
    }

    /**
     * Adiciona um novo lançamento recorrente APENAS em memória
     */
    public boolean add(LancamentoRecorrente lancamento) {
        lancamentos.add(lancamento);
        return true;
    }

    /**
     * Busca lançamentos recorrentes de uma conta específica na memória
     */
    public List<LancamentoRecorrente> buscarPorConta(Conta conta) {
        return lancamentos.stream()
                .filter(l -> l.getConta() != null && l.getConta().equals(conta))
                .collect(Collectors.toList());
    }

    /**
     * Cancela um lançamento recorrente APENAS em memória
     */
    public boolean cancelar(LancamentoRecorrente lancamento) {
        return lancamentos.remove(lancamento);
    }

    /**
     * Atualiza a próxima data de um lançamento recorrente APENAS em memória
     */
    public boolean atualizarProximaData(LancamentoRecorrente lancamento) {
        // O lançamento já está na lista, apenas retorna sucesso
        return lancamentos.contains(lancamento);
    }

    /**
     * CE22 - Busca lançamentos que devem ser executados hoje
     */
    public List<LancamentoRecorrente> buscarLancamentosHoje() {
        Date hoje = new Date();
        return lancamentos.stream()
                .filter(l -> {
                    Date proximaData = l.getProximaData();
                    return proximaData != null && isMesmoDia(proximaData, hoje);
                })
                .collect(Collectors.toList());
    }

    private boolean isMesmoDia(Date data1, Date data2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(data1);
        cal2.setTime(data2);
        
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
               cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH);
    }

    /**
     * Persiste todos os lançamentos recorrentes para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repository.salvarTodos(lancamentos);
    }
    
    /**
     * Retorna todos os lançamentos em memória
     */
    public List<LancamentoRecorrente> buscarTodos() {
        return lancamentos;
    }
    
    /**
     * Carrega lançamentos com metadados de conta e categoria
     */
    public List<Object[]> carregarComMetadados() throws IOException {
        return repository.carregarComMetadados();
    }
}
