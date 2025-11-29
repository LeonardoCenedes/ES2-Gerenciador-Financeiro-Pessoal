package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoTransacao;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransacaoController {

    private CatalogoTransacao catalogo;

    public TransacaoController() throws IOException {
        this.catalogo = new CatalogoTransacao();
    }

    /**
     * Implementa mensagem 1 do diagrama de criar transação
     */
    public boolean criarTransacao(Date data, float valor, Categoria categoria,
            String descricao, String comprovante,
            TiposTransacao tipo, Conta conta)
            throws IllegalArgumentException, IOException {

        if (!validarTransacao(data, valor, categoria, descricao, comprovante, tipo)) {
            return false;
        }

        Transacao novaTransacao = new Transacao(
                data,
                valor,
                categoria,
                descricao,
                comprovante,
                tipo,
                conta,
                conta.getUsuario()
        );

        boolean resultado = catalogo.adicionar(novaTransacao);

        return resultado;
    }

    private boolean validarTransacao(Date data, float valor, Categoria categoria,
            String descricao, String comprovante,
            TiposTransacao tipo)
            throws IllegalArgumentException {

        if (data == null) {
            throw new IllegalArgumentException("A data da transação não pode ser nula.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }

        if (categoria == null) {
            throw new IllegalArgumentException("A categoria deve ser selecionada.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da transação deve ser selecionado.");
        }

        return true;
    }

    /**
     * Implementa mensagem 1 do diagrama de visualizar histórico: 1:
     * buscarTransacoesFiltradas(conta:Conta, data:Date, categoria:Categoria,
     * tipo:TiposTransacao) : List<Transacao>
     *
     * FLUXO: 1. Usuário chama buscarTransacoesFiltradas 1.1. Sistema chama
     * listarTransacoesFiltrada no CatalogoTransacao Retorna: "Transações do
     * usuário filtradas buscadas"
     */
    public List<Transacao> buscarTransacoesFiltradas(Usuario usuario, Conta conta, 
                                                 Date data, Categoria categoria, 
                                                 TiposTransacao tipo) 
        throws IOException {
    
    // Recarrega as transações do arquivo
    catalogo.recarregar();
    
    // ✅ Agora passa usuario como primeiro parâmetro
    List<Transacao> resultado = catalogo.listarTransacoesFiltrada(usuario, conta, data, categoria, tipo);
    
    return resultado;
}

    public boolean excluirTransacao(Transacao transacao) throws IOException {
        try {
            catalogo.excluir(transacao);
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public Map<String, Float> requererRelatorioDespesas(Usuario usuario, Conta conta,
            Categoria categoria,
            TiposTransacao tipo)
            throws IOException {
        List<Transacao> transacoes = buscarTransacoesFiltradas(usuario, conta, null, categoria, tipo);
        return agruparDespesasPorCategoria(transacoes);
    }

    public Map<String, Float> agruparDespesasPorCategoria(List<Transacao> transacoes) {
        Map<String, Float> agrupamento = new HashMap<>();

        for (Transacao t : transacoes) {
            String nomeCategoria = t.getCategoria().getNome();
            float valorAtual = agrupamento.getOrDefault(nomeCategoria, 0.0f);
            agrupamento.put(nomeCategoria, valorAtual + t.getValor());
        }

        return agrupamento;
    }

    public boolean editarTransacao(Transacao atualizada, Date data, float valor,
            Categoria categoria, String descricao,
            String comprovante, TiposTransacao tipo)
            throws IllegalArgumentException, IOException {

        // Valida os novos dados
        if (!validarTransacao(data, valor, categoria, descricao, comprovante, tipo)) {
            return false;
        }

        // 1.1: atualizarTransacao - conforme diagrama de colaboração
        boolean resultado = catalogo.atualizarTransacao(atualizada, data, valor,
                categoria, descricao,
                comprovante, tipo);

        // Retorno: "Transação editada com sucesso"
        return resultado;
    }
}
