package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoCategoria;
import com.mycompany.gerenciador.financeiro.model.Categoria;

/**
 * Controller de Categorias - Padrão GRASP
 * Recebe dependências via construtor
 */
public class ControladorCategoria {
    
    private final CatalogoCategoria catalogo;

    /**
     * Construtor padrão - cria Catalog que cria Repository
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     */
    public ControladorCategoria() throws IOException {
        this.catalogo = new CatalogoCategoria();
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar um catálogo mockado
     */
    public ControladorCategoria(CatalogoCategoria catalogo) {
        this.catalogo = catalogo;
    }

    public List<Categoria> buscarCategorias() {
        return catalogo.listarTodas();
    }
    
    // Retorna apenas as categorias ativas (status = true)
    public List<Categoria> buscarCategoriasAtivas() {
        List<Categoria> todasCategorias = catalogo.listarTodas();
        List<Categoria> ativas = new ArrayList<>();
        
        for (Categoria c : todasCategorias) {
            if (c.isStatus()) {
                ativas.add(c);
            }
        }
        
        return ativas;
    }

    /**
     * CE06.1 - Criar Categoria
     * Valida e cria uma nova categoria no sistema
     */
    public boolean criarCategoria(String nome, boolean padrao, boolean status) throws IOException {
        // Valida os dados de entrada
        if (!validarCategoria(nome)) {
            return false;
        }

        Categoria novaCategoria = new Categoria(nome, padrao, status);
        return catalogo.salvar(novaCategoria);
    }

    /**
     * CE06.2 - Editar Categoria
     * Valida e atualiza uma categoria existente
     */
    public boolean editarCategoria(Categoria categoriaAtual, Categoria categoriaEditada, String nome, boolean padrao, boolean status) throws IOException {
        // Valida os novos dados
        if (!validarCategoria(nome)) {
            return false;
        }

        return catalogo.atualizar(categoriaAtual, nome, padrao, status);
    }

    /**
     * CE06.3 - Desativar Categoria
     * Realiza soft delete da categoria
     */
    public boolean desativarCategoria(Categoria categoria) throws IOException {
        return catalogo.desativar(categoria);
    }

    /**
     * Valida os dados de uma categoria
     * Verifica se o nome não está vazio
     */
    private boolean validarCategoria(String nome) {
        // Verifica se o nome é válido
        return nome != null && !nome.trim().isEmpty();
    }

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}