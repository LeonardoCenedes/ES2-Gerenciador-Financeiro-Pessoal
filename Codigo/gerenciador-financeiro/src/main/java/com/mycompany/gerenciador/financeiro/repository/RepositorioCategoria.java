/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de categorias em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * 
 * @author Laís Isabella
 */
public class RepositorioCategoria implements Repositorio<Categoria> {

    private static final String DIRETORIO = DataPathResolver.getDataPath();
    private static final String ARQUIVO = DataPathResolver.getFilePath("categorias.txt");
    private static final String SEPARADOR = ";";

    public RepositorioCategoria() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String formatarCategoriaParaLinha(Categoria categoria) {
        return categoria.getNome() + SEPARADOR
                + categoria.isPadrao() + SEPARADOR
                + categoria.isStatus();
    }

    /**
     * Carrega todas as categorias do arquivo para memória
     */
    @Override
    public List<Categoria> carregarTodos() throws IOException {
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
     * Salva todas as categorias da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<Categoria> categorias) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Categoria categoria : categorias) {
                String linha = formatarCategoriaParaLinha(categoria);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
