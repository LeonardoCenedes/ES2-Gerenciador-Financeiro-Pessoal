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

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de orçamentos em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * 
 * @author Laís Isabella
 */
public class RepositorioOrcamento implements Repositorio<Orcamento> {
    
    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = DataPathResolver.getFilePath("orcamentos.txt");
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public RepositorioOrcamento() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String formatarOrcamentoParaLinha(Orcamento orcamento) {
        return DATE_FORMAT.format(orcamento.getPeriodo()) + SEPARADOR
                + orcamento.getValorMaximo() + SEPARADOR
                + orcamento.getCategoria().getNome() + SEPARADOR
                + orcamento.getUsuario().getEmail();
    }

    /**
     * Carrega todos os orçamentos do arquivo para memória
     */
    @Override
    public List<Orcamento> carregarTodos() throws IOException {
        List<Orcamento> orcamentos = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return orcamentos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        Orcamento orcamento = parsearLinha(linha);
                        orcamentos.add(orcamento);
                    } catch (ParseException e) {
                        // Ignora linhas com erro de formatação
                        System.err.println("Erro ao parsear linha: " + linha);
                    }
                }
            }
        }

        return orcamentos;
    }

    // Converte uma linha do arquivo em Orcamento
    // Formato: periodo;valorMaximo;categoriaNome;usuarioEmail
    private Orcamento parsearLinha(String linha) throws ParseException {
        String[] partes = linha.split(SEPARADOR);

        Date periodo = DATE_FORMAT.parse(partes[0]);
        float valorMaximo = Float.parseFloat(partes[1]);
        
        // Cria objetos Categoria e Usuario apenas com informações básicas
        Categoria categoria = new Categoria();
        categoria.setNome(partes[2]);
        
        Usuario usuario = new Usuario();
        usuario.setEmail(partes[3]);

        return new Orcamento(periodo, valorMaximo, categoria, usuario);
    }

    /**
     * Salva todos os orçamentos da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<Orcamento> orcamentos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Orcamento orcamento : orcamentos) {
                String linha = formatarOrcamentoParaLinha(orcamento);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
