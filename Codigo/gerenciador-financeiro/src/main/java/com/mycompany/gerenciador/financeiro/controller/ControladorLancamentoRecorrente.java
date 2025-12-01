/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoLancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.LancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.model.Periodicidade;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Controlador para gerenciar lançamentos recorrentes
 * CE11 - Criar Lançamento Recorrente
 * CE12 - Buscar Lançamentos por Conta
 * CE13 - Cancelar Lançamento Recorrente
 * CE22 - Verificar e Gerar Lançamentos do Dia
 * @author Laís Isabella
 */
public class ControladorLancamentoRecorrente {
    
    private final CatalogoLancamentoRecorrente catalogo;
    private final ControladorTransacao controladorTransacao;

    /**
     * Construtor que recebe ControladorTransacao para colaboração
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     * GRASP: Controller só enxerga seu Catalog, colabora via outros Controllers
     */
    public ControladorLancamentoRecorrente(ControladorTransacao controladorTransacao) throws IOException {
        this.catalogo = new CatalogoLancamentoRecorrente();
        this.controladorTransacao = controladorTransacao;
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar catálogo e controlador mockados
     */
    public ControladorLancamentoRecorrente(CatalogoLancamentoRecorrente catalogo, ControladorTransacao controladorTransacao) {
        this.catalogo = catalogo;
        this.controladorTransacao = controladorTransacao;
    }

    /**
     * CE11 - Criar Lançamento Recorrente
     */
    public boolean criarLancamento(String descricao, float valor, Periodicidade periodicidade,
                                   Date dataInicio, int numeroOcorrencias, Date proximaData,
                                   Conta conta, Transacao transacao) throws IOException {
        // Valida os dados
        if (!validarLancamento(descricao, valor, periodicidade, dataInicio, numeroOcorrencias, proximaData, conta, transacao)) {
            return false;
        }

        LancamentoRecorrente novoLancamento = new LancamentoRecorrente(
            descricao, valor, periodicidade, dataInicio, numeroOcorrencias, 
            proximaData, conta, transacao
        );

        return catalogo.add(novoLancamento);
    }

    /**
     * CE12 - Buscar Lançamentos Recorrentes por Conta
     */
    public List<LancamentoRecorrente> buscarPorConta(Conta conta) {
        return catalogo.buscarPorConta(conta);
    }

    /**
     * CE13 - Cancelar Lançamento Recorrente
     */
    public boolean cancelar(LancamentoRecorrente lancamento) throws IOException {
        return catalogo.cancelar(lancamento);
    }

    /**
     * CE22 - Verificar e Gerar Lançamentos do Dia (CronJob)
     * Verifica se há lançamentos recorrentes para executar hoje
     */
    public void verificarLancamentosDoDia() throws IOException {
        List<LancamentoRecorrente> lancamentosHoje = catalogo.buscarLancamentosHoje();

        for (LancamentoRecorrente lancamento : lancamentosHoje) {
            // Cria a transação correspondente usando a transação do lançamento como template
            Transacao template = lancamento.getTransacao();
            if (template != null) {
                boolean criado = controladorTransacao.criarTransacao(
                    new Date(),
                    template.getValor(),
                    template.getCategoria(),
                    template.getDescricao(),
                    template.getComprovante(),
                    template.getTipo(),
                    template.getConta(),
                    template.getMetaEconomica()
                );

                if (criado) {
                    atualizarProximaData(lancamento);
                }
            }
        }
    }

    /**
     * Atualiza a próxima data de execução do lançamento recorrente
     * baseado na periodicidade
     */
    private void atualizarProximaData(LancamentoRecorrente lancamento) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lancamento.getProximaData());

        switch (lancamento.getPeriodicidade()) {
            case DIARIA -> calendar.add(Calendar.DAY_OF_MONTH, 1);
            case SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1);
            case QUINZENAL -> calendar.add(Calendar.DAY_OF_MONTH, 15);
            case MENSAL -> calendar.add(Calendar.MONTH, 1);
            case ANUAL -> calendar.add(Calendar.YEAR, 1);
        }

        lancamento.setProximaData(calendar.getTime());
        catalogo.atualizarProximaData(lancamento);
    }

    /**
     * Valida os dados de um lançamento recorrente
     */
    private boolean validarLancamento(String descricao, float valor, Periodicidade periodicidade,
                                     Date dataInicio, int numeroOcorrencias, Date proximaData,
                                     Conta conta, Transacao transacao) {
        return descricao != null && 
               !descricao.trim().isEmpty() &&
               valor > 0 &&
               periodicidade != null &&
               dataInicio != null &&
               numeroOcorrencias > 0 &&
               proximaData != null &&
               conta != null &&
               transacao != null;
    }

    /**
     * Associa contas e categorias aos lançamentos recorrentes após carregamento
     * Deve ser chamado pela View após inicialização com os controladores necessários
     */
    public void resolverReferencias(ControladorConta controladorConta,
                                    ControladorCategoria controladorCategoria,
                                    Usuario usuario) throws IOException {
        List<Object[]> dadosComMetadados = catalogo.carregarComMetadados();
        
        // Limpar lançamentos atuais e recarregar com referências resolvidas
        List<LancamentoRecorrente> lancamentosAtuais = catalogo.buscarTodos();
        lancamentosAtuais.clear();
        
        for (Object[] dados : dadosComMetadados) {
            LancamentoRecorrente lancamento = (LancamentoRecorrente) dados[0];
            String contaNome = (String) dados[1];
            String categoriaNome = (String) dados[2];
            
            // Resolver conta
            List<Conta> contas = controladorConta.buscarContasUsuario(usuario);
            for (Conta c : contas) {
                if (c.getNome().equals(contaNome)) {
                    lancamento.setConta(c);
                    break;
                }
            }
            
            // Resolver categoria e criar transação template
            if (!categoriaNome.isEmpty() && lancamento.getConta() != null) {
                List<Categoria> categorias = controladorCategoria.buscarCategorias();
                Categoria categoria = null;
                
                for (Categoria cat : categorias) {
                    if (cat.getNome().equals(categoriaNome)) {
                        categoria = cat;
                        break;
                    }
                }
                
                // Criar uma transação template com a categoria
                if (categoria != null) {
                    Transacao transacaoTemplate = new Transacao(
                        lancamento.getDataInicio(),
                        lancamento.getValor(),
                        categoria,
                        lancamento.getDescricao(),
                        null,
                        TiposTransacao.SAIDA,
                        lancamento.getConta(),
                        null
                    );
                    lancamento.setTransacao(transacaoTemplate);
                }
            }
            
            lancamentosAtuais.add(lancamento);
        }
    }
    
    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}
