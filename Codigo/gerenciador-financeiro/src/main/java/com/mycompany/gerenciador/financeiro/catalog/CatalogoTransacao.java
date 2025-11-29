package com.mycompany.gerenciador.financeiro.catalog;

import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.TransacaoRepositoryTxt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CatalogoTransacao {

    private List<Transacao> transacoes;
    private TransacaoRepositoryTxt repositorio;

    public CatalogoTransacao() throws IOException {
        this.repositorio = new TransacaoRepositoryTxt();
        this.transacoes = new ArrayList<>(repositorio.listar());
    }

    public boolean adicionar(Transacao transacao) throws IOException {
        transacoes.add(transacao);
        repositorio.salvar(transacao);
        return true;
    }

    public List<Transacao> listarTodas() {
        return new ArrayList<>(transacoes);
    }

    public void recarregar() throws IOException {
        this.transacoes = new ArrayList<>(repositorio.listar());
    }

    public List<Transacao> listarTransacoesFiltrada(Usuario usuario, Conta conta,
            Date data, Categoria categoria,
            TiposTransacao tipo) {
        List<Transacao> resultado = new ArrayList<>();

        for (Transacao t : transacoes) {
            boolean match = true;

            if (usuario != null && t.getUsuario() != null) {
                if (!t.getUsuario().getEmail().equals(usuario.getEmail())) {
                    match = false;
                }
            }

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

    public boolean atualizar(Transacao atual, Date data, float valor,
            Categoria categoria, String descricao,
            String comprovante, TiposTransacao tipo)
            throws IOException {

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

        // Atualiza a lista na posição
        transacoes.set(index, transacaoAtualizada);

        // Sobrescreve o arquivo com todas as transações
        repositorio.salvarTodas(transacoes);

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

    public void excluir(Transacao transacao) throws IOException {
        boolean removido = transacoes.remove(transacao);

        if (!removido) {
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

            if (transacaoParaRemover == null) {
                throw new IllegalArgumentException("Transação não encontrada para exclusão.");
            }

            transacoes.remove(transacaoParaRemover);
        }

        // Sobrescreve o arquivo sem a transação removida
        repositorio.salvarTodas(transacoes);
    }
}
