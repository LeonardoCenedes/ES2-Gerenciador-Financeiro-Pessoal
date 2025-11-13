/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repository;

import com.mycompany.gerenciador.financeiro.model.Conta;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContaRepositoryTxt {

    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = "data/contas.txt";
    private static final String SEPARADOR = ";";

    public ContaRepositoryTxt() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void salvar(Conta conta) throws IOException {
        int proximoId = gerarProximoId();
        conta.setId(proximoId);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
            String linha = formatarContaParaLinha(conta);
            writer.write(linha);
            writer.newLine();
        }
    }

    private int gerarProximoId() throws IOException {
        File arquivo = new File(ARQUIVO);

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

    private String formatarContaParaLinha(Conta conta) {
        return conta.getId() + SEPARADOR
                + conta.getNome() + SEPARADOR
                + conta.getTipo() + SEPARADOR
                + conta.getSaldoInicial() + SEPARADOR
                + conta.getMoeda();
    }

    /**
     * Lista todas as contas do arquivo. Implementação do RF001.2 - Visualizar
     * Conta Financeira.
     *
     * @return Lista de contas cadastradas (vazia se arquivo não existir)
     * @throws IOException se houver erro na leitura do arquivo
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
     * Converte uma linha do arquivo em um objeto Conta. Formato esperado:
     * id;nome;tipo;saldoInicial;moeda
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

    /**
     * Sobrescreve completamente o arquivo com a lista de contas fornecida.
     * Usado para atualizar contas existentes.
     *
     * @param contas Lista completa de contas a ser salva
     * @throws IOException se houver erro ao escrever no arquivo
     */
    public void salvarTodas(List<Conta> contas) throws IOException {
        // Sobrescreve o arquivo (false = não append)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Conta conta : contas) {
                String linha = formatarContaParaLinha(conta);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
