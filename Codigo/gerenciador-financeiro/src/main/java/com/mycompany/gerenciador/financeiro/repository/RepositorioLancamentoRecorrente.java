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

import com.mycompany.gerenciador.financeiro.model.LancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.model.Periodicidade;
import com.mycompany.gerenciador.financeiro.util.DataPathResolver;

/**
 * Repositório para persistência de lançamentos recorrentes em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * Formato: descricao;valor;periodicidade;dataInicio;numeroOcorrencias;proximaData;contaNome;categoriaNome
 * @author Laís Isabella
 */
public class RepositorioLancamentoRecorrente implements Repositorio<LancamentoRecorrente> {
    
    private static final String FILE_PATH = DataPathResolver.getFilePath("lancamentos_recorrentes.txt");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public RepositorioLancamentoRecorrente() {
        criarDiretorioSeNaoExistir();
    }

    private void criarDiretorioSeNaoExistir() {
        File file = new File(FILE_PATH);
        File diretorio = file.getParentFile();
        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    /**
     * Carrega todos os lançamentos recorrentes do arquivo para memória
     */
    @Override
    public List<LancamentoRecorrente> carregarTodos() throws IOException {
        List<LancamentoRecorrente> lancamentos = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return lancamentos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                LancamentoRecorrente lancamento = parsearLinha(linha);
                if (lancamento != null) {
                    lancamentos.add(lancamento);
                }
            }
        }

        return lancamentos;
    }

    private LancamentoRecorrente parsearLinha(String linha) {
        try {
            String[] partes = linha.split(";", -1); // -1 para incluir campos vazios
            if (partes.length < 7) return null;

            String descricao = partes[0];
            float valor = Float.parseFloat(partes[1]);
            Periodicidade periodicidade = Periodicidade.valueOf(partes[2]);
            Date dataInicio = DATE_FORMAT.parse(partes[3]);
            int numeroOcorrencias = Integer.parseInt(partes[4]);
            Date proximaData = DATE_FORMAT.parse(partes[5]);
            // partes[6] é o nome da conta, será resolvido pelo controlador
            // partes[7] é o nome da categoria, será resolvido pelo controlador

            // Conta e transação com categoria serão setadas pelo controlador após carregar
            return new LancamentoRecorrente(descricao, valor, periodicidade, dataInicio,
                                           numeroOcorrencias, proximaData, null, null);

        } catch (ParseException | IllegalArgumentException e) {
            System.err.println("Erro ao parsear linha: " + linha);
            return null;
        }
    }
    
    /**
     * Carrega dados brutos de lançamentos incluindo nomes de conta e categoria
     * Retorna array com: [LancamentoRecorrente, contaNome, categoriaNome]
     */
    public List<Object[]> carregarComMetadados() throws IOException {
        List<Object[]> resultado = new java.util.ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return resultado;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";", -1);
                if (partes.length < 7) continue;

                LancamentoRecorrente lancamento = parsearLinha(linha);
                if (lancamento != null) {
                    String contaNome = partes[6];
                    String categoriaNome = partes.length > 7 ? partes[7] : "";
                    resultado.add(new Object[]{lancamento, contaNome, categoriaNome});
                }
            }
        }

        return resultado;
    }

    /**
     * Salva todos os lançamentos recorrentes da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<LancamentoRecorrente> lancamentos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (LancamentoRecorrente lancamento : lancamentos) {
                String linha = formatarLancamento(lancamento);
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    private String formatarLancamento(LancamentoRecorrente lancamento) {
        String categoriaNome = "";
        if (lancamento.getTransacao() != null && lancamento.getTransacao().getCategoria() != null) {
            categoriaNome = lancamento.getTransacao().getCategoria().getNome();
        }
        
        return lancamento.getDescricao() + ";" +
               lancamento.getValor() + ";" +
               lancamento.getPeriodicidade().name() + ";" +
               DATE_FORMAT.format(lancamento.getDataInicio()) + ";" +
               lancamento.getNumeroOcorrencias() + ";" +
               DATE_FORMAT.format(lancamento.getProximaData()) + ";" +
               (lancamento.getConta() != null ? lancamento.getConta().getNome() : "") + ";" +
               categoriaNome;
    }

}
