package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.repository.TransacaoRepositoryTxt;

/**
 * Catálogo de transações
 * Padrão In-Memory Cache: mantém dados em memória, repository apenas para I/O
 */
public class CatalogoTransacao {

    private final List<Transacao> transacoes;
    private final TransacaoRepositoryTxt repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoTransacao() throws IOException {
        this.repositorio = new TransacaoRepositoryTxt();
        this.transacoes = new ArrayList<>(repositorio.carregarTodos());
    }

    /**
     * Adiciona uma nova transação APENAS em memória
     */
    public boolean adicionar(Transacao transacao) {
        transacoes.add(transacao);
        return true;
    }

    /**
     * Lista todas as transações da memória
     */
    public List<Transacao> listarTodas() {
        return new ArrayList<>(transacoes);
    }

    public List<Transacao> listarTransacoesFiltrada(Conta conta,
            Date data, Categoria categoria,
            TiposTransacao tipo) {
        List<Transacao> resultado = new ArrayList<>();

        for (Transacao t : transacoes) {
            boolean match = true;

            // Filtro por conta (opcional - se for "Todas as contas", conta = null)
            if (conta != null && t.getConta() != null) {
                if (!t.getConta().getNome().equals(conta.getNome())) {
                    match = false;
                }
            }

            // Filtro por data (opcional)
            if (data != null && t.getData() != null) {
                if (!isMesmaData(t.getData(), data)) {
                    match = false;
                }
            }

            // Filtro por categoria (opcional)
            if (categoria != null && t.getCategoria() != null) {
                if (!t.getCategoria().getNome().equalsIgnoreCase(categoria.getNome())) {
                    match = false;
                }
            }

            // Filtro por tipo (opcional)
            if (tipo != null && t.getTipo() != null) {
                if (t.getTipo() != tipo) {
                    match = false;
                }
            }

            if (match) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    /**
     * Atualiza uma transação APENAS em memória
     */
    public boolean atualizar(Transacao atual, Date data, float valor,
            Categoria categoria, String descricao,
            byte[] comprovante, TiposTransacao tipo) {

        // Localiza a transação na lista
        int index = -1;
        for (int i = 0; i < transacoes.size(); i++) {
            Transacao t = transacoes.get(i);
            if (t.getData().equals(atual.getData())
                    && t.getValor() == atual.getValor()
                    && t.getCategoria().getNome().equals(atual.getCategoria().getNome())
                    && t.getConta().getNome().equals(atual.getConta().getNome())) {
                index = i;
                break;
            }
        }

        // Verifica se encontrou
        if (index == -1) {
            throw new IllegalArgumentException("Transação não encontrada para atualização.");
        }

        // Atualiza os dados da transação
        Transacao transacaoAtualizada = transacoes.get(index);
        transacaoAtualizada.setData(data);
        transacaoAtualizada.setValor(valor);
        transacaoAtualizada.setCategoria(categoria);
        transacaoAtualizada.setDescricao(descricao);
        transacaoAtualizada.setComprovante(comprovante);
        transacaoAtualizada.setTipo(tipo);

        // Atualiza a lista na posição em memória
        transacoes.set(index, transacaoAtualizada);

        return true;
    }

    /**
     * Verifica se duas datas são do mesmo dia
     */
    private boolean isMesmaData(Date data1, Date data2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(data1);
        cal2.setTime(data2);

        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR)
                && cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH)
                && cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH);
    }

    /**
     * Exclui uma transação APENAS da memória
     * @return true se removeu, false se não encontrou
     */
    public boolean excluir(Transacao transacao) {
        boolean removido = transacoes.remove(transacao);
        if (removido) {
            return true;
        }
        // Se não removeu diretamente, busca por igualdade de campos
        Transacao transacaoParaRemover = null;
        for (Transacao t : transacoes) {
            if (t.getData().equals(transacao.getData())
                    && t.getValor() == transacao.getValor()
                    && t.getCategoria().getNome().equals(transacao.getCategoria().getNome())
                    && t.getConta().getNome().equals(transacao.getConta().getNome())) {
                transacaoParaRemover = t;
                break;
            }
        }
        if (transacaoParaRemover != null) {
            transacoes.remove(transacaoParaRemover);
            return true;
        }
        // Não encontrou para remover
        return false;
    }

    /**
     * Persiste todas as transações da memória para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(transacoes);
    }

    /**
     * CE16 - Agrupa transações por data
     * Retorna um Map ordenado cronologicamente com a data como String e o saldo do dia
     */
    public java.util.Map<String, Float> agruparPorData(List<Transacao> transacoes) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        
        // Primeiro agrupa em um TreeMap com Date como chave para ordenar cronologicamente
        java.util.Map<Date, Float> agrupamentoTemp = new java.util.TreeMap<>();

        for (Transacao t : transacoes) {
            Date data = t.getData();
            float valorAtual = agrupamentoTemp.getOrDefault(data, 0.0f);
            
            // Se for ENTRADA, soma; se for SAIDA, subtrai
            if (t.getTipo() == TiposTransacao.ENTRADA) {
                agrupamentoTemp.put(data, valorAtual + t.getValor());
            } else {
                agrupamentoTemp.put(data, valorAtual - t.getValor());
            }
        }

        // Converte para LinkedHashMap mantendo a ordem cronológica
        java.util.Map<String, Float> agrupamento = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<Date, Float> entry : agrupamentoTemp.entrySet()) {
            agrupamento.put(sdf.format(entry.getKey()), entry.getValue());
        }

        return agrupamento;
    }
}
