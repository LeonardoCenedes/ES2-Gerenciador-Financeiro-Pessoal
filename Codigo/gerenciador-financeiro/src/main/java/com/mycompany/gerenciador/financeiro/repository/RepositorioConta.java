/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de contas em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 */
public class RepositorioConta implements Repositorio<Conta> {

    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = DataPathResolver.getFilePath("contas.txt");
    private static final String SEPARADOR = ";";

    public RepositorioConta() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String formatarContaParaLinha(Conta conta) {
        return conta.getNome() + SEPARADOR
                + conta.getTipo() + SEPARADOR
                + conta.getSaldoInicial() + SEPARADOR
                + conta.getMoeda() + SEPARADOR
                + conta.getUsuario().getEmail();
    }

    /**
     * Carrega todas as contas do arquivo para memória
     */
    @Override
    public List<Conta> carregarTodos() throws IOException {
        List<Conta> contas = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return contas;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(ARQUIVO), StandardCharsets.UTF_8))) {
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

        String nome = partes[0];
        String tipo = partes[1];
        float saldoInicial = Float.parseFloat(partes[2]);
        String moeda = partes[3];
        String emailUsuario = partes[4];

        // Cria um objeto Usuario básico apenas com o email
        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        return new Conta(nome, tipo, saldoInicial, moeda, usuario);
    }

    /**
     * Salva todas as contas da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<Conta> contas) throws IOException {
        // Sobrescreve o arquivo (false = não append)
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ARQUIVO, false), StandardCharsets.UTF_8))) {
            for (Conta conta : contas) {
                String linha = formatarContaParaLinha(conta);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
