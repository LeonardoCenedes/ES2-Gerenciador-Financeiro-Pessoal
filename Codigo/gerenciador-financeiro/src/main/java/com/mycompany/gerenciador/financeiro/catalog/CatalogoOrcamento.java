/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.OrcamentoRepositoryTxt;

/**
 * Catálogo de orçamentos - Padrão In-Memory Cache
 * Mantém todos os orçamentos em memória e persiste apenas ao final
 * @author Laís Isabella
 */
public class CatalogoOrcamento {
    
    private final List<Orcamento> orcamentos;
    private final OrcamentoRepositoryTxt repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoOrcamento() throws IOException {
        this.repositorio = new OrcamentoRepositoryTxt();
        this.orcamentos = new ArrayList<>(repositorio.carregarTodos());
    }

    /**
     * Adiciona um novo orçamento APENAS em memória
     */
    public boolean salvar(Orcamento orcamento) {
        orcamentos.add(orcamento);
        return true;
    }

    /**
     * Retorna a lista completa de orçamentos da memória
     */
    public List<Orcamento> listarTodos() {
        return new ArrayList<>(orcamentos);
    }

    /**
     * Busca orçamentos de um usuário específico na memória
     */
    public List<Orcamento> buscarPorUsuario(Usuario usuario) {
        List<Orcamento> orcamentosDoUsuario = new ArrayList<>();
        
        for (Orcamento o : orcamentos) {
            if (o.getUsuario() != null && 
                o.getUsuario().getEmail().equals(usuario.getEmail())) {
                orcamentosDoUsuario.add(o);
            }
        }
        
        return orcamentosDoUsuario;
    }

    /**
     * Remove um orçamento APENAS da memória
     */
    public boolean deletar(Orcamento orcamento) {
        return orcamentos.removeIf(o -> 
            o.getPeriodo().equals(orcamento.getPeriodo()) && 
            o.getCategoria() != null && orcamento.getCategoria() != null &&
            o.getCategoria().getNome().equals(orcamento.getCategoria().getNome()) &&
            o.getValorMaximo() == orcamento.getValorMaximo() &&
            o.getUsuario() != null && orcamento.getUsuario() != null &&
            o.getUsuario().getEmail().equals(orcamento.getUsuario().getEmail())
        );
    }

    /**
     * Persiste todos os orçamentos para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(orcamentos);
    }
}
