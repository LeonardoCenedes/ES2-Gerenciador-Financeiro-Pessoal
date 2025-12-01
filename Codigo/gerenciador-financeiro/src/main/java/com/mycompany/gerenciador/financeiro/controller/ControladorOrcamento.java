/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoOrcamento;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 *
 * @author Laís Isabella
 */
/**
 * Controller de Orçamentos - Padrão GRASP
 * Recebe dependências via construtor
 */
public class ControladorOrcamento {
    
    private final CatalogoOrcamento catalogo;

    /**
     * Construtor padrão - cria Catalog que cria Repository
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     */
    public ControladorOrcamento() throws IOException {
        this.catalogo = new CatalogoOrcamento();
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar um catálogo mockado
     */
    public ControladorOrcamento(CatalogoOrcamento catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * CE07 - Criar Orçamento
     * Valida e cria um novo orçamento para uma categoria
     */
    public boolean criarOrcamento(Date periodo, float valorMaximo, Categoria categoria, Usuario usuario) throws IOException {
        // Valida os dados
        if (!validarOrcamento(periodo, valorMaximo, categoria)) {
            return false;
        }

        Orcamento novoOrcamento = new Orcamento(periodo, valorMaximo, categoria, usuario);
        return catalogo.salvar(novoOrcamento);
    }

    /**
     * CE08 - Buscar Informações de Orçamento
     * Retorna informações detalhadas sobre orçamentos e gastos por categoria
     * Recebe o TransacaoController como parâmetro para buscar transações
     */
    public Map<Categoria, List<Transacao>> buscarInfosOrcamento(Orcamento orcamento, Conta conta, ControladorTransacao controladorTransacao) {
        Map<Categoria, List<Transacao>> resultado = new HashMap<>();
        
        // Busca as transações filtradas pela conta e categoria (SAIDA = despesas)
        List<Transacao> transacoesFiltradas = controladorTransacao.buscarTransacoesFiltradas(
            conta, 
            orcamento.getPeriodo(), 
            orcamento.getCategoria(), 
            com.mycompany.gerenciador.financeiro.model.TiposTransacao.SAIDA
        );

        // Organiza as transações por categoria
        resultado.put(orcamento.getCategoria(), transacoesFiltradas);
        
        return resultado;
    }

    /**
     * Busca todos os orçamentos de um usuário
     */
    public List<Orcamento> buscarPorUsuario(Usuario usuario) {
        return catalogo.buscarPorUsuario(usuario);
    }

    /**
     * Remove um orçamento do sistema
     */
    public boolean deletarOrcamento(Orcamento orcamento) {
        if (orcamento == null) {
            return false;
        }
        return catalogo.deletar(orcamento);
    }

    /**
     * Valida os dados de um orçamento
     */
    private boolean validarOrcamento(Date periodo, float valorMaximo, Categoria categoria) {
        return periodo != null && 
               valorMaximo > 0 && 
               categoria != null && 
               categoria.getNome() != null && 
               !categoria.getNome().trim().isEmpty();
    }

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}
