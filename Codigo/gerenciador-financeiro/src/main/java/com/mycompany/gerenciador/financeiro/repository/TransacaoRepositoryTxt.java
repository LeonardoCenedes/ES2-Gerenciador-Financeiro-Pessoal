/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.repository;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;
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

/**
 *
 * @author Laís Isabella
 */
public class TransacaoRepositoryTxt {
    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = "data/transacoes.txt";
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public TransacaoRepositoryTxt() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void salvar(Transacao transacao) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
            String linha = formatarTransacaoParaLinha(transacao);
            writer.write(linha);
            writer.newLine();
        }
    }

    private String formatarTransacaoParaLinha(Transacao transacao) {
        return DATE_FORMAT.format(transacao.getData()) + SEPARADOR
                + transacao.getValor() + SEPARADOR
                + transacao.getCategoria().getNome() + SEPARADOR
                + (transacao.getDescricao() != null ? transacao.getDescricao() : "") + SEPARADOR
                + (transacao.getComprovante() != null ? transacao.getComprovante() : "") + SEPARADOR
                + transacao.getTipo().name() + SEPARADOR
                + transacao.getUsuario().getEmail() + SEPARADOR
                + transacao.getConta().getNome();
    }

    public List<Transacao> listar() throws IOException {
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
        String comprovante = partes[4];
        TiposTransacao tipo = TiposTransacao.valueOf(partes[5]);
        String emailUsuario = partes[6];
        String nomeConta = partes[7];

        Categoria categoria = new Categoria();
        categoria.setNome(nomeCategoria);

        Usuario usuario = new Usuario();
        usuario.setEmail(emailUsuario);

        Conta conta = new Conta();
        conta.setNome(nomeConta);

        return new Transacao(data, valor, categoria, descricao, 
                           comprovante, tipo, conta, usuario);
    }

    public void salvarTodas(List<Transacao> transacoes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Transacao transacao : transacoes) {
                String linha = formatarTransacaoParaLinha(transacao);
                writer.write(linha);
                writer.newLine();
            }
        }
    }
}
