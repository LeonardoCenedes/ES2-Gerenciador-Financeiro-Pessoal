package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.repository.RepositorioCategoria;

/**
 * Catálogo de categorias - Padrão In-Memory Cache
 * Mantém todas as categorias em memória e persiste apenas ao final
 */
public class CatalogoCategoria {
    
    private final List<Categoria> categorias;
    private final RepositorioCategoria repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoCategoria() throws IOException {
        this.repositorio = new RepositorioCategoria();
        this.categorias = new ArrayList<>(repositorio.carregarTodos());
        
        // Cria categorias padrão se não existirem
        if (categorias.isEmpty()) {
            criarCategoriasPadrao();
        }
    }

    /**
     * Construtor com injeção para testes - permite mockar o repository
     * Para testes unitários com mocks
     */
    public CatalogoCategoria(RepositorioCategoria repositorio, List<Categoria> categoriasIniciais) {
        this.repositorio = repositorio;
        this.categorias = new ArrayList<>(categoriasIniciais);
    }

    //criei esse metodo só pra nao ter que gastar mt tempo criado o fluxo pra fazer isso pela interface, dps removam plsss
    //ele é tipo uma seed
    private void criarCategoriasPadrao() {
        String[] nomesPadrao = {"Alimentação", "Saúde", "Lazer"};
        
        for (String nome : nomesPadrao) {
            Categoria cat = new Categoria(nome, true, true);
            categorias.add(cat);
        }
    }

    /**
     * Retorna a lista completa de categorias da memória
     */
    public List<Categoria> listarTodas() {
        return new ArrayList<>(categorias);
    }

    /**
     * Retorna apenas as categorias ativas (status = true)
     */
    public List<Categoria> listarAtivas() {
        List<Categoria> ativas = new ArrayList<>();
        for (Categoria c : categorias) {
            if (c.isStatus()) {
                ativas.add(c);
            }
        }
        return ativas;
    }

    /**
     * Busca uma categoria pelo nome
     */
    public Categoria buscarPorNome(String nome) {
        for (Categoria c : categorias) {
            if (c.getNome().equalsIgnoreCase(nome)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adiciona uma nova categoria APENAS em memória
     */
    public boolean salvar(Categoria categoria) {
        // Verifica se já existe uma categoria com esse nome
        for (Categoria c : categorias) {
            if (c.getNome().equalsIgnoreCase(categoria.getNome())) {
                return false; // Categoria já existe
            }
        }
        
        categorias.add(categoria);
        return true;
    }

    /**
     * Atualiza uma categoria existente APENAS em memória
     */
    public boolean atualizar(Categoria categoria, String nome, boolean padrao, boolean status) {
        // Localiza a categoria original pelos dados atuais
        int index = -1;
        for (int i = 0; i < categorias.size(); i++) {
            Categoria c = categorias.get(i);
            if (c.getNome().equals(categoria.getNome()) && 
                c.isPadrao() == categoria.isPadrao() && 
                c.isStatus() == categoria.isStatus()) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false; // Categoria não encontrada
        }

        // Verifica se o novo nome já existe em outra categoria
        if (!categoria.getNome().equalsIgnoreCase(nome)) {
            for (Categoria c : categorias) {
                if (c.getNome().equalsIgnoreCase(nome)) {
                    return false; // Nome já existe
                }
            }
        }

        // Cria categoria atualizada com os novos valores
        Categoria categoriaAtualizada = new Categoria(nome, padrao, status);
        categorias.set(index, categoriaAtualizada);
        return true;
    }

    /**
     * Desativa uma categoria APENAS em memória (soft delete)
     */
    public boolean desativar(Categoria categoria) {
        // Localiza a categoria
        for (Categoria c : categorias) {
            if (c.getNome().equals(categoria.getNome()) && 
                c.isPadrao() == categoria.isPadrao()) {
                c.setStatus(false);
                return true;
            }
        }
        return false; // Categoria não encontrada
    }

    /**
     * Persiste todas as categorias para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(categorias);
    }
}