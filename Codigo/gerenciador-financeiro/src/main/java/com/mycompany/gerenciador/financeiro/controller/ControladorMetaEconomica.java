/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoMetaEconomica;
import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 *
 * @author Laís Isabella
 */
/**
 * Controlador de Metas Econômicas - Padrão GRASP
 * Recebe dependências via construtor
 */
public class ControladorMetaEconomica {
    
    private final CatalogoMetaEconomica catalogo;

    /**
     * Construtor padrão - cria Catalog que cria Repository
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     */
    public ControladorMetaEconomica() throws IOException {
        this.catalogo = new CatalogoMetaEconomica();
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar um catálogo mockado
     */
    public ControladorMetaEconomica(CatalogoMetaEconomica catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * CE09 - Criar Meta Econômica
     * Valida e cria uma nova meta econômica no sistema
     */
    public boolean criarMeta(String nome, float valor, Date dataLimite, Usuario usuario) {
        // Valida os dados
        if (!validarMeta(nome, valor, dataLimite)) {
            return false;
        }

        // Verifica se já existe uma meta com esse nome para o usuário
        MetaEconomica metaExistente = catalogo.buscarPorNome(nome, usuario);
        if (metaExistente != null) {
            return false; // Meta já existe
        }

        // Cria a meta com valorEconomizadoAtual = 0
        MetaEconomica novaMeta = new MetaEconomica(nome, valor, dataLimite, 0.0f, usuario);
        return catalogo.salvar(novaMeta);
    }

    /**
     * CE10 - Buscar/Visualizar Metas Econômicas por Usuário
     * Retorna todas as metas de um usuário específico
     */
    public List<MetaEconomica> buscarPorUsuario(Usuario usuario) {
        return catalogo.buscarPorUsuario(usuario);
    }

    /**
     * Contribui com um valor para a meta econômica
     * Chamado automaticamente quando uma transação com meta é criada
     */
    public boolean contribuirParaMeta(MetaEconomica meta, float valorContribuicao) throws IOException {
        if (meta == null) {
            return false;
        }

        // Busca a meta no catálogo para garantir que estamos atualizando a referência correta
        MetaEconomica metaNoCatalogo = catalogo.buscarPorNome(meta.getNome(), meta.getUsuario());
        if (metaNoCatalogo == null) {
            return false;
        }

        // Adiciona o valor da transação ao valorEconomizadoAtual
        float novoValor = metaNoCatalogo.getValorEconomizadoAtual() + valorContribuicao;
        metaNoCatalogo.setValorEconomizadoAtual(novoValor);

        // Atualiza também a meta passada como parâmetro para manter sincronizado
        meta.setValorEconomizadoAtual(novoValor);

        return catalogo.atualizar(metaNoCatalogo);
    }

    /**
     * Remove uma meta econômica do sistema
     */
    public boolean deletarMeta(MetaEconomica meta) {
        if (meta == null) {
            return false;
        }
        return catalogo.deletar(meta);
    }

    /**
     * Valida os dados de uma meta econômica
     */
    private boolean validarMeta(String nome, float valor, Date dataLimite) {
        return nome != null && 
               !nome.trim().isEmpty() && 
               valor > 0 && 
               dataLimite != null;
    }

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}
