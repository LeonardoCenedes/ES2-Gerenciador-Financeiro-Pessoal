package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoCategoria;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaController {
    
    private CatalogoCategoria catalogo;

    public CategoriaController() throws IOException {
        this.catalogo = new CatalogoCategoria();
    }

    public List<Categoria> buscarCategorias() throws IOException {
        catalogo.recarregar();
        return catalogo.listarTodas();
    }
    
    //precisa add esse metodo ao diagrama de classes nessa classe, preciso dela pra puxar as categorias no FE
    public List<Categoria> buscarCategoriasAtivas() throws IOException {
        catalogo.recarregar();
        List<Categoria> todasCategorias = catalogo.listarTodas();
        List<Categoria> ativas = new ArrayList<>();
        
        for (Categoria c : todasCategorias) {
            if (c.isStatus()) {
                ativas.add(c);
            }
        }
        
        return ativas;
    }
}