package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoTransacao;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.util.PDFUtil;

/**
 * Controller de Transações - Padrão GRASP
 * Recebe dependências via construtor
 */
public class ControladorTransacao {

    private final CatalogoTransacao catalogo;
    private final ControladorMetaEconomica controladorMeta;

    /**
     * Construtor que recebe ControladorMetaEconomica para colaboração
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     * GRASP: Controller só enxerga seu Catalog, colabora via outros Controllers
     */
    public ControladorTransacao(ControladorMetaEconomica controladorMeta) throws IOException {
        this.catalogo = new CatalogoTransacao();
        this.controladorMeta = controladorMeta;
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar catálogo e controlador mockados
     */
    public ControladorTransacao(CatalogoTransacao catalogo, ControladorMetaEconomica controladorMeta) {
        this.catalogo = catalogo;
        this.controladorMeta = controladorMeta;
    }

    /**
     * Implementa mensagem 1 do diagrama de criar transação
     */
    public boolean criarTransacao(Date data, float valor, Categoria categoria,
            String descricao, byte[] comprovante,
            TiposTransacao tipo, Conta conta, MetaEconomica metaEconomica)
            throws IllegalArgumentException, IOException {

        if (!validarTransacao(data, valor, categoria, tipo)) {
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
                metaEconomica
        );

        boolean resultado = catalogo.adicionar(novaTransacao);

        // Se a transação tem uma meta econômica associada, contribui para ela
        if (resultado && metaEconomica != null) {
            controladorMeta.contribuirParaMeta(metaEconomica, valor);
        }

        return resultado;
    }

    private boolean validarTransacao(Date data, float valor, Categoria categoria, TiposTransacao tipo)
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
    
    public List<Transacao> buscarTransacoesFiltradas(Conta conta,
            Date data, Categoria categoria,
            TiposTransacao tipo) {

        // Busca transações da memória
        List<Transacao> resultado = catalogo.listarTransacoesFiltrada(conta, data, categoria, tipo);

        return resultado;
    }

    /**
     * Lista todas as transações
     */
    public List<Transacao> listarTodas() {
        return catalogo.listarTodas();
    }

    /**
     * Busca transações por conta específica
     */
    public List<Transacao> buscarPorConta(Conta conta) {
        return buscarTransacoesFiltradas(conta, null, null, null);
    }

    public boolean excluirTransacao(Transacao transacao) throws IOException {
        try {
            catalogo.excluir(transacao);
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Gera relatório de despesas por categoria para uma conta
     * Retorna Map com nome da categoria como chave e soma dos valores como valor
     */
    public Map<String, Float> requererRelatorioDespesas(Conta conta,
            Categoria categoria,
            TiposTransacao tipo)
            throws IOException {
        // 1.1: listarTransacoesFiltrada - busca transações filtradas
        List<Transacao> transacoes = buscarTransacoesFiltradas(conta, null, categoria, tipo);
        // 1.2: agruparDespesasPorCategoria - agrupa e retorna dados agregados
        return agruparDespesasPorCategoria(transacoes);
    }

    /**
     * Agrupa despesas por categoria - soma valores de transações agrupadas por nome da categoria
     * Retorna Map<String, Float> onde a chave é o nome da categoria e o valor é a soma
     */
    public Map<String, Float> agruparDespesasPorCategoria(List<Transacao> transacoes) {
        Map<String, Float> agrupamento = new HashMap<>();

        for (Transacao t : transacoes) {
            Categoria cat = t.getCategoria();
            String nomeCategoria = cat != null ? cat.getNome() : "Sem categoria";
            float valorAtual = agrupamento.getOrDefault(nomeCategoria, 0.0f);
            agrupamento.put(nomeCategoria, valorAtual + t.getValor());
        }

        return agrupamento;
    }

    public boolean editarTransacao(Transacao atualizada, Date data, float valor,
            Categoria categoria, String descricao,
            byte[] comprovante, TiposTransacao tipo)
            throws IllegalArgumentException, IOException {

        // Valida os novos dados
        if (!validarTransacao(data, valor, categoria, tipo)) {
            return false;
        }

        // 1.1: atualizarTransacao - conforme diagrama de colaboração
        boolean resultado = catalogo.atualizar(atualizada, data, valor,
                categoria, descricao,
                comprovante, tipo);

        // Retorno: "Transação editada com sucesso"
        return resultado;
    }

    /**
     * CE17 - Gerar Extrato
     * Busca transações filtradas e gera um PDF com o extrato
     */
    public boolean gerarExtrato(Conta conta, Date data, Categoria categoria, 
                               TiposTransacao tipo, String caminho) throws IOException {
        // 1.1: buscarTransacoesFiltradas - busca as transações com os filtros
        List<Transacao> transacoes = buscarTransacoesFiltradas(conta, data, categoria, tipo);
        
        // 1.1.1: listarTransacoesFiltrada - já é chamado dentro de buscarTransacoesFiltradas
        
        // 1.2: gerarPDF - chama o PDFUtil para gerar o arquivo PDF
        PDFUtil pdfUtil = new PDFUtil();
        
        return pdfUtil.gerarPDF(transacoes, caminho);
    }

    /**
     * Agrupa transações por data (usado no relatório de fluxo de caixa)
     * Delega para o Catalog
     */
    public Map<String, Float> agruparPorData(List<Transacao> transacoes) {
        return catalogo.agruparPorData(transacoes);
    }

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}
