/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.MetaEconomicaRepositoryTxt;

/**
 * Catálogo de metas econômicas - Padrão In-Memory Cache
 * Mantém todas as metas em memória e persiste apenas ao final
 * @author Laís Isabella
 */
public class CatalogoMetaEconomica {
    
    private final List<MetaEconomica> metas;
    private final MetaEconomicaRepositoryTxt repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoMetaEconomica() throws IOException {
        this.repositorio = new MetaEconomicaRepositoryTxt();
        this.metas = new ArrayList<>(repositorio.carregarTodos());
    }

    /**
     * Adiciona uma nova meta econômica APENAS em memória
     */
    public boolean salvar(MetaEconomica meta) {
        metas.add(meta);
        return true;
    }

    /**
     * Retorna a lista completa de metas da memória
     */
    public List<MetaEconomica> listarTodas() {
        return new ArrayList<>(metas);
    }

    /**
     * Busca metas de um usuário específico na memória
     */
    public List<MetaEconomica> buscarPorUsuario(Usuario usuario) {
        List<MetaEconomica> metasDoUsuario = new ArrayList<>();
        
        for (MetaEconomica m : metas) {
            if (m.getUsuario() != null && 
                m.getUsuario().getEmail().equals(usuario.getEmail())) {
                metasDoUsuario.add(m);
            }
        }
        
        return metasDoUsuario;
    }

    /**
     * Busca uma meta específica pelo nome e usuário na memória
     */
    public MetaEconomica buscarPorNome(String nome, Usuario usuario) {
        for (MetaEconomica m : metas) {
            if (m.getNome().equals(nome) && 
                m.getUsuario() != null && 
                m.getUsuario().getEmail().equals(usuario.getEmail())) {
                return m;
            }
        }
        return null;
    }

    /**
     * Atualiza uma meta existente APENAS em memória
     */
    public boolean atualizar(MetaEconomica meta) {
        // Localiza a meta
        int index = -1;
        for (int i = 0; i < metas.size(); i++) {
            MetaEconomica m = metas.get(i);
            if (m.getNome().equals(meta.getNome()) && 
                m.getUsuario().getEmail().equals(meta.getUsuario().getEmail())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false; // Meta não encontrada
        }

        // Atualiza a meta
        metas.set(index, meta);
        return true;
    }

    /**
     * Remove uma meta econômica APENAS da memória
     */
    public boolean deletar(MetaEconomica meta) {
        return metas.removeIf(m -> 
            m.getNome().equals(meta.getNome()) && 
            m.getUsuario() != null && 
            meta.getUsuario() != null &&
            m.getUsuario().getEmail().equals(meta.getUsuario().getEmail())
        );
    }

    /**
     * Persiste todas as metas econômicas para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(metas);
    }
}
