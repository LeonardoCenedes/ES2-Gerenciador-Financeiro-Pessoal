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
public class MetaEconomica {
    
    private String nome;
    private float valor;
    private Date dataLimite;
    private float valorEconomizadoAtual;
    private Usuario usuario;

    public MetaEconomica() {
    }

    public MetaEconomica(String nome, float valor, Date dataLimite, float valorEconomizadoAtual, Usuario usuario) {
        this.nome = nome;
        this.valor = valor;
        this.dataLimite = dataLimite;
        this.valorEconomizadoAtual = valorEconomizadoAtual;
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }

    public float getValorEconomizadoAtual() {
        return valorEconomizadoAtual;
    }

    public void setValorEconomizadoAtual(float valorEconomizadoAtual) {
        this.valorEconomizadoAtual = valorEconomizadoAtual;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "MetaEconomica{" +
                "nome='" + nome + '\'' +
                ", valor=" + valor +
                ", dataLimite=" + dataLimite +
                ", valorEconomizadoAtual=" + valorEconomizadoAtual +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                '}';
    }
}
