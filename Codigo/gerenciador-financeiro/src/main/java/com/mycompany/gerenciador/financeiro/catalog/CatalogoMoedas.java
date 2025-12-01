/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.catalog;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Moedas;

/**
 *
 * @author Laís Isabella
 */
public class CatalogoMoedas {
    
    private final List<Moedas> moedas;

    public CatalogoMoedas() {
        this.moedas = new ArrayList<>();
        inicializarMoedasPadrao();
    }

    // Inicializa com algumas moedas comuns
    private void inicializarMoedasPadrao() {
        moedas.add(new Moedas("Real", "R$"));
        moedas.add(new Moedas("Dólar", "$"));
        moedas.add(new Moedas("Euro", "€"));
        moedas.add(new Moedas("Libra", "£"));
    }

    public List<Moedas> buscarTodas() {
        return new ArrayList<>(moedas);
    }
}
