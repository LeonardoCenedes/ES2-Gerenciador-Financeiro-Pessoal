package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoCategoria;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControladorCategoria {
    
    private CatalogoCategoria catalogo;

    public ControladorCategoria() throws IOException {
        this.catalogo = new CatalogoCategoria();
    }

    public List<Categoria> buscarCategorias() throws IOException {
        catalogo.recarregar();
        return catalogo.listar();
    }
    
    public List<Categoria> buscarCategoriasAtivas() throws IOException {
        catalogo.recarregar();
        List<Categoria> todasCategorias = catalogo.listar();
        List<Categoria> ativas = new ArrayList<>();
        
        for (Categoria c : todasCategorias) {
            if (c.isStatus()) {
                ativas.add(c);
            }
        }
        
        return ativas;
    }
}