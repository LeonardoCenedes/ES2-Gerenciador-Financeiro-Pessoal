/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.model;

import java.util.Date;

/**
 * Representa um lançamento recorrente (transação que se repete periodicamente)
 * CE11, CE12, CE13, CE22
 * @author Laís Isabella
 */
public class LancamentoRecorrente {
    private String descricao;
    private float valor;
    private Periodicidade periodicidade;
    private Date dataInicio;
    private int numeroOcorrencias;
    private Date proximaData;
    private Conta conta;
    private Transacao transacao;

    public LancamentoRecorrente() {
    }

    public LancamentoRecorrente(String descricao, float valor, Periodicidade periodicidade,
                                Date dataInicio, int numeroOcorrencias, Date proximaData,
                                Conta conta, Transacao transacao) {
        this.descricao = descricao;
        this.valor = valor;
        this.periodicidade = periodicidade;
        this.dataInicio = dataInicio;
        this.numeroOcorrencias = numeroOcorrencias;
        this.proximaData = proximaData;
        this.conta = conta;
        this.transacao = transacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public int getNumeroOcorrencias() {
        return numeroOcorrencias;
    }

    public void setNumeroOcorrencias(int numeroOcorrencias) {
        this.numeroOcorrencias = numeroOcorrencias;
    }

    public Date getProximaData() {
        return proximaData;
    }

    public void setProximaData(Date proximaData) {
        this.proximaData = proximaData;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Transacao getTransacao() {
        return transacao;
    }

    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
    }

    @Override
    public String toString() {
        return "LancamentoRecorrente{" +
                "descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", periodicidade=" + periodicidade +
                ", dataInicio=" + dataInicio +
                ", numeroOcorrencias=" + numeroOcorrencias +
                ", proximaData=" + proximaData +
                ", conta=" + conta +
                '}';
    }
}
