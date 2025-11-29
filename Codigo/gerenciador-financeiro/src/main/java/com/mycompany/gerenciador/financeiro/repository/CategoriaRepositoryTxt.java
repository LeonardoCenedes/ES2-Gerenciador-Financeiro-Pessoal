/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repository;

import com.mycompany.gerenciador.financeiro.model.Categoria;
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
public class CategoriaRepositoryTxt {

    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = "data/categorias.txt";
    private static final String SEPARADOR = ";";

    public CategoriaRepositoryTxt() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Salva uma nova categoria no arquivo
     */
    public void salvar(Categoria categoria) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
            String linha = formatarCategoriaParaLinha(categoria);
            writer.write(linha);
            writer.newLine();
        }
    }

    private String formatarCategoriaParaLinha(Categoria categoria) {
        return categoria.getNome() + SEPARADOR
                + categoria.isPadrao() + SEPARADOR
                + categoria.isStatus();
    }

    /**
     * Lista todas as categorias do arquivo
     */
    public List<Categoria> listar() throws IOException {
        List<Categoria> categorias = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return categorias;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Categoria categoria = parsearLinha(linha);
                    categorias.add(categoria);
                }
            }
        }

        return categorias;
    }

    /**
     * Converte uma linha do arquivo em um objeto Categoria Formato:
     * nome;padrao;status
     */
    private Categoria parsearLinha(String linha) {
        String[] partes = linha.split(SEPARADOR);

        String nome = partes[0];
        boolean padrao = Boolean.parseBoolean(partes[1]);
        boolean status = Boolean.parseBoolean(partes[2]);

        return new Categoria(nome, padrao, status);
    }

    /**
     * Sobrescreve o arquivo com todas as categorias
     */
    public void salvarTodas(List<Categoria> categorias) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Categoria categoria : categorias) {
                String linha = formatarCategoriaParaLinha(categoria);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
