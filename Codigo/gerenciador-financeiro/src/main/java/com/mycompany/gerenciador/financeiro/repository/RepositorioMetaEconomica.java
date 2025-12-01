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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de metas econômicas em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * 
 * @author Laís Isabella
 */
public class RepositorioMetaEconomica implements Repositorio<MetaEconomica> {
    
    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = DataPathResolver.getFilePath("metas_economicas.txt");
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public RepositorioMetaEconomica() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String formatarMetaParaLinha(MetaEconomica meta) {
        return meta.getNome() + SEPARADOR
                + meta.getValor() + SEPARADOR
                + DATE_FORMAT.format(meta.getDataLimite()) + SEPARADOR
                + meta.getValorEconomizadoAtual() + SEPARADOR
                + meta.getUsuario().getEmail();
    }

    /**
     * Carrega todas as metas econômicas do arquivo para memória
     */
    @Override
    public List<MetaEconomica> carregarTodos() throws IOException {
        List<MetaEconomica> metas = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return metas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        MetaEconomica meta = parsearLinha(linha);
                        metas.add(meta);
                    } catch (ParseException e) {
                        System.err.println("Erro ao parsear linha: " + linha);
                    }
                }
            }
        }

        return metas;
    }

    // Converte uma linha do arquivo em MetaEconomica
    // Formato: nome;valor;dataLimite;valorEconomizadoAtual;usuarioEmail
    private MetaEconomica parsearLinha(String linha) throws ParseException {
        String[] partes = linha.split(SEPARADOR);

        String nome = partes[0];
        float valor = Float.parseFloat(partes[1]);
        Date dataLimite = DATE_FORMAT.parse(partes[2]);
        float valorEconomizadoAtual = Float.parseFloat(partes[3]);
        
        Usuario usuario = new Usuario();
        usuario.setEmail(partes[4]);

        return new MetaEconomica(nome, valor, dataLimite, valorEconomizadoAtual, usuario);
    }

    /**
     * Salva todas as metas econômicas da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<MetaEconomica> metas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (MetaEconomica meta : metas) {
                String linha = formatarMetaParaLinha(meta);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
