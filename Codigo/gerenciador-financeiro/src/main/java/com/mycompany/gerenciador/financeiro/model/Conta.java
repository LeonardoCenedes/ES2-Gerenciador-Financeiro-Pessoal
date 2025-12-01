/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.model;

/**
 *
 * @author Laís Isabella
 */
public class Conta {
     private String nome;
    private String tipo;
    private float saldoInicial;
    private String moeda;
    private Usuario usuario;

    public Conta() {
    }

    public Conta(String nome, String tipo, float saldoInicial, String moeda, Usuario usuario) {
        this.nome = nome;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.moeda = moeda;
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(float saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public String getMoeda() {
        return moeda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", saldoInicial=" + saldoInicial +
                ", moeda='" + moeda + '\'' +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                '}';
    }
}
