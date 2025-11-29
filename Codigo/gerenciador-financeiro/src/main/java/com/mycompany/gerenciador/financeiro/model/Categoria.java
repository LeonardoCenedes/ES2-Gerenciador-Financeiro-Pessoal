/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.model;

/**
 *
 * @author Laís Isabella
 */
public class Categoria {
        private String nome;
    private boolean padrao;
    private boolean status;

    public Categoria() {
    }

    public Categoria(String nome, boolean padrao, boolean status) {
        this.nome = nome;
        this.padrao = padrao;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isPadrao() {
        return padrao;
    }

    public void setPadrao(boolean padrao) {
        this.padrao = padrao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "nome='" + nome + '\'' +
                ", padrao=" + padrao +
                ", status=" + status +
                '}';
    }
}
