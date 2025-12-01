/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

/**
 *
 * @author Laís Isabella
 */
public class Orcamento {
    
    private Date periodo;
    private float valorMaximo;
    private Categoria categoria;
    private Usuario usuario;

    public Orcamento() {
    }

    public Orcamento(Date periodo, float valorMaximo, Categoria categoria, Usuario usuario) {
        this.periodo = periodo;
        this.valorMaximo = valorMaximo;
        this.categoria = categoria;
        this.usuario = usuario;
    }

    public Date getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Date periodo) {
        this.periodo = periodo;
    }

    public float getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(float valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Orcamento{" +
                "periodo=" + periodo +
                ", valorMaximo=" + valorMaximo +
                ", categoria=" + (categoria != null ? categoria.getNome() : "null") +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                '}';
    }
}
