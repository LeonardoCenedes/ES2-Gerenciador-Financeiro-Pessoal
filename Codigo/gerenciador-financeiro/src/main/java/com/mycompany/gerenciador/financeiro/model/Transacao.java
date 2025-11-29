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
public class Transacao {
    private Date data;
    private float valor;
    private Categoria categoria;
    private String descricao;
    private String comprovante;
    private TiposTransacao tipo;
    private Conta Conta;
    private Usuario usuario;

    public Transacao() {
    }

    public Transacao(Date data, float valor, Categoria categoria, String descricao,
                     String comprovante, TiposTransacao tipo, Conta Conta, Usuario usuario) {
        this.data = data;
        this.valor = valor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.comprovante = comprovante;
        this.tipo = tipo;
        this.Conta = Conta;
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComprovante() {
        return comprovante;
    }

    public void setComprovante(String comprovante) {
        this.comprovante = comprovante;
    }

    public TiposTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TiposTransacao tipo) {
        this.tipo = tipo;
    }

    public Conta getConta() {
        return Conta;
    }

    public void setConta(Conta Conta) {
        this.Conta = Conta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "data=" + data +
                ", valor=" + valor +
                ", categoria=" + (categoria != null ? categoria.getNome() : "null") +
                ", descricao='" + descricao + '\'' +
                ", tipo=" + tipo +
                ", Conta=" + (Conta != null ? Conta.getNome() : "null") +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                '}';
    } 
}
