/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repositoy;
import com.mycompany.gerenciador.financeiro.model.Conta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Laís Isabella
 */
public class ContaRepositoryTxt {
    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = "data/contas.txt";
    private static final String SEPARADOR = ";";

    /**
     * Construtor que garante a existência do diretório data
     */
    public ContaRepositoryTxt() {
        criarDiretorioSeNaoExistir();
    }

    /**
     * Cria o diretório data se não existir
     */
    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Salva uma nova conta no arquivo.
     * Gera automaticamente o ID baseado no último ID existente.
     */
    public void salvar(Conta conta) throws IOException {
        // Gera o próximo ID
        int proximoId = gerarProximoId();
        conta.setId(proximoId);

        // Abre o arquivo em modo append
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
            String linha = formatarContaParaLinha(conta);
            writer.write(linha);
            writer.newLine();
        }
    }

    /**
     * Gera o próximo ID lendo o arquivo e pegando o maior ID + 1
     */
    private int gerarProximoId() throws IOException {
        File arquivo = new File(ARQUIVO);
        
        // Se o arquivo não existe, retorna 1
        if (!arquivo.exists()) {
            return 1;
        }

        int maiorId = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    String[] partes = linha.split(SEPARADOR);
                    int id = Integer.parseInt(partes[0]);
                    if (id > maiorId) {
                        maiorId = id;
                    }
                }
            }
        }

        return maiorId + 1;
    }

    /**
     * Formata uma conta para uma linha do arquivo
     * Formato: id;nome;tipo;saldoInicial;moeda
     */
    private String formatarContaParaLinha(Conta conta) {
        return conta.getId() + SEPARADOR +
               conta.getNome() + SEPARADOR +
               conta.getTipo() + SEPARADOR +
               conta.getSaldoInicial() + SEPARADOR +
               conta.getMoeda();
    }

    /**
     * Lista todas as contas do arquivo
     */
    public List<Conta> listar() throws IOException {
        List<Conta> contas = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        // Se o arquivo não existe, retorna lista vazia
        if (!arquivo.exists()) {
            return contas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Conta conta = parsearLinha(linha);
                    contas.add(conta);
                }
            }
        }

        return contas;
    }

    /**
     * Converte uma linha do arquivo em um objeto Conta
     */
    private Conta parsearLinha(String linha) {
        String[] partes = linha.split(SEPARADOR);
        
        int id = Integer.parseInt(partes[0]);
        String nome = partes[1];
        String tipo = partes[2];
        double saldoInicial = Double.parseDouble(partes[3]);
        String moeda = partes[4];

        return new Conta(id, nome, tipo, saldoInicial, moeda);
    }    
}
