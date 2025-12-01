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
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de transações em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * 
 * @author Laís Isabella
 */
public class RepositorioTransacao implements Repositorio<Transacao> {
    private static final String DIRETORIO = DataPathResolver.getDataPath();
    private static final String ARQUIVO = DataPathResolver.getFilePath("transacoes.txt");
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public RepositorioTransacao() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private String formatarTransacaoParaLinha(Transacao transacao) {
        // Converte bytes do comprovante para Base64
        String comprovanteBase64 = "";
        if (transacao.getComprovante() != null && transacao.getComprovante().length > 0) {
            comprovanteBase64 = java.util.Base64.getEncoder().encodeToString(transacao.getComprovante());
        }
        
        return DATE_FORMAT.format(transacao.getData()) + SEPARADOR
                + transacao.getValor() + SEPARADOR
                + transacao.getCategoria().getNome() + SEPARADOR
                + (transacao.getDescricao() != null ? transacao.getDescricao() : "") + SEPARADOR
                + comprovanteBase64 + SEPARADOR
                + transacao.getTipo().name() + SEPARADOR
                + transacao.getConta().getNome() + SEPARADOR
                + (transacao.getMetaEconomica() != null ? transacao.getMetaEconomica().getNome() : "");
    }

    /**
     * Carrega todas as transações do arquivo para memória
     */
    @Override
    public List<Transacao> carregarTodos() throws IOException {
        List<Transacao> transacoes = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return transacoes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        Transacao transacao = parsearLinha(linha);
                        transacoes.add(transacao);
                    } catch (ParseException e) {
                        System.err.println("Erro ao parsear linha: " + linha);
                    }
                }
            }
        }

        return transacoes;
    }

    private Transacao parsearLinha(String linha) throws ParseException {
        String[] partes = linha.split(SEPARADOR);

        Date data = DATE_FORMAT.parse(partes[0]);
        float valor = Float.parseFloat(partes[1]);
        String nomeCategoria = partes[2];
        String descricao = partes[3];
        String comprovanteBase64 = partes[4];
        TiposTransacao tipo = TiposTransacao.valueOf(partes[5]);
        String nomeConta = partes[6];
        String nomeMetaEconomica = partes.length > 7 ? partes[7] : "";

        // Converte Base64 de volta para bytes
        byte[] comprovante = null;
        if (comprovanteBase64 != null && !comprovanteBase64.trim().isEmpty()) {
            try {
                comprovante = java.util.Base64.getDecoder().decode(comprovanteBase64);
            } catch (IllegalArgumentException e) {
                // Se não for Base64 válido, deixa null
                comprovante = null;
            }
        }

        Categoria categoria = new Categoria();
        categoria.setNome(nomeCategoria);

        Conta conta = new Conta();
        conta.setNome(nomeConta);

        // MetaEconomica é opcional
        MetaEconomica metaEconomica = null;
        if (nomeMetaEconomica != null && !nomeMetaEconomica.trim().isEmpty()) {
            metaEconomica = new MetaEconomica();
            metaEconomica.setNome(nomeMetaEconomica);
        }

        return new Transacao(data, valor, categoria, descricao, 
                           comprovante, tipo, conta, metaEconomica);
    }

    /**
     * Salva todas as transações da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<Transacao> transacoes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Transacao transacao : transacoes) {
                String linha = formatarTransacaoParaLinha(transacao);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
