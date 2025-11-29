package com.mycompany.gerenciador.financeiro.catalog;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.repository.CategoriaRepositoryTxt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoCategoria {
    
    private List<Categoria> categorias;
    private CategoriaRepositoryTxt repositorio;

    public CatalogoCategoria() throws IOException {
        this.repositorio = new CategoriaRepositoryTxt();
        this.categorias = new ArrayList<>(repositorio.listar());
        
        // Cria categorias padrão se não existirem
        if (categorias.isEmpty()) {
            criarCategoriasPadrao();
        }
    }

    //criei esse metodo só pra nao ter que gastar mt tempo criado o fluxo pra fazer isso pela interface, dps removam plsss
    //ele é tipo uma seed
    private void criarCategoriasPadrao() throws IOException {
        String[] nomesPadrao = {"Alimentação", "Saúde", "Lazer"};
        
        for (String nome : nomesPadrao) {
            Categoria cat = new Categoria(nome, true, true);
            categorias.add(cat);
            repositorio.salvar(cat);
        }
    }

    public List<Categoria> listarTodas() {
        return new ArrayList<>(categorias);
    }

    public void recarregar() throws IOException {
        this.categorias = new ArrayList<>(repositorio.listar());
    }
}