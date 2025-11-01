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
    private int id;
    private String nome;
    private String tipo;
    private double saldoInicial;
    private String moeda;

    /**
     * Construtor vazio
     */
    public Conta() {
    }

    /**
     * Construtor completo (sem id, pois será gerado pelo repositório)
     */
    public Conta(String nome, String tipo, double saldoInicial, String moeda) {
        this.nome = nome;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.moeda = moeda;
    }

    /**
     * Construtor completo com id (usado ao carregar do arquivo)
     */
    public Conta(int id, String nome, String tipo, double saldoInicial, String moeda) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.moeda = moeda;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public String getMoeda() {
        return moeda;
    }

    // Setter de moeda é privado conforme RF001.4 (moeda não pode ser alterada)
    private void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", saldoInicial=" + saldoInicial +
                ", moeda='" + moeda + '\'' +
                '}';
    }
}
