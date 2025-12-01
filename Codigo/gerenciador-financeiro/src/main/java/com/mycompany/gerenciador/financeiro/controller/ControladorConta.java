package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.List;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoContas;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Controller de Contas - Padrão GRASP
 * Recebe dependências via construtor
 */
public class ControladorConta {

    private final CatalogoContas catalogoContas;
    private final ControladorTransacao controladorTransacao;

    /**
     * Construtor que recebe ControladorTransacao para colaboração
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     * GRASP: Controller só enxerga seu Catalog, colabora via outros Controllers
     */
    public ControladorConta(ControladorTransacao controladorTransacao) throws IOException {
        this.catalogoContas = new CatalogoContas();
        this.controladorTransacao = controladorTransacao;
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar catálogo e controlador mockados
     */
    public ControladorConta(CatalogoContas catalogoContas, ControladorTransacao controladorTransacao) {
        this.catalogoContas = catalogoContas;
        this.controladorTransacao = controladorTransacao;
    }

    /**
     * Cadastra uma nova conta financeira. Implementa o caso de uso CE1.1.
     */
    public void criarConta(String nome, String tipo, float saldoInicial, String moeda, Usuario usuario)
            throws IllegalArgumentException, IOException {

        validarConta(nome, tipo, saldoInicial, moeda);

        Conta novaConta = new Conta(nome, tipo, saldoInicial, moeda, usuario);
        catalogoContas.salvar(novaConta);
    }

    /**
     * Lista todas as contas cadastradas. Implementa o caso de uso CE1.2 -
     * Visualizar Conta Financeira.
     *
     * @return Lista de todas as contas
     * @throws IOException se houver erro ao ler o arquivo
     */
    public List<Conta> listarContas() {
        return catalogoContas.listarContas();
    }

    /**
     * Busca uma conta específica pelo ID (opcional, pode ser usado em edições
     * futuras).
     */
    public List<Conta> buscarContasUsuario(Usuario usuario) throws IOException {
        return catalogoContas.buscarPorUsuario(usuario);
    }

    private void validarConta(String nome, String tipo, float saldoInicial, String moeda)
            throws IllegalArgumentException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da conta não pode ser vazio.");
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo da conta deve ser selecionado.");
        }

        if (saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }

        if (moeda == null || moeda.trim().isEmpty()) {
            throw new IllegalArgumentException("A moeda deve ser selecionada.");
        }
    }

    /**
     * Edita uma conta existente. Implementa o caso de uso CE1.3 - Editar Conta
     * Financeira.
     */
    public void editarConta(Conta contaOriginal, String nome, String tipo, float saldoInicial)
        throws IllegalArgumentException, IOException {

    validarConta(nome, tipo, saldoInicial, contaOriginal.getMoeda());

    // ✅ GUARDAR o nome original ANTES de mudar
    String nomeAnterior = contaOriginal.getNome();
    
    // Atualiza os dados
    contaOriginal.setNome(nome);
    contaOriginal.setTipo(tipo);
    contaOriginal.setSaldoInicial(saldoInicial);

    // ✅ Passa o nome anterior para o catálogo localizar
    catalogoContas.atualizar(contaOriginal, nomeAnterior);
}

    /**
     * Exclui uma conta existente. Implementa o caso de uso CE1.4 - Excluir
     * Conta Financeira.
     */
    public void excluirConta(Conta conta) {
        catalogoContas.excluir(conta);
    }

    /**
     * CE14 - Visualizar Painel de Saldo Consolidado
     * Retorna o saldo consolidado de todas as contas de um usuário
     */
    public float buscarSaldoConsolidado(Usuario usuario) throws IOException {
        List<Conta> contasDoUsuario = catalogoContas.buscarPorUsuario(usuario);
        return catalogoContas.somarSaldoDeContas(contasDoUsuario);
    }

    /**
     * CE16 - Gerar Relatório de Fluxo de Caixa
     * Retorna um mapa com data e saldo (entradas - saídas) por data
     */
    public java.util.Map<String, Float> gerarRelatorioFluxoCaixa(Conta conta) throws IOException {
        // 1.1: buscarPorUsuario - busca as contas do usuário (mas já temos a conta específica)
        List<Conta> contas = new java.util.ArrayList<>();
        if (conta != null) {
            contas.add(conta);
        } else {
            // Se conta for null, pega todas as contas em memória
            contas = catalogoContas.listarContas();
        }

        // 1.2: agruparPorData - colabora via TransacaoController (GRASP)
        
        List<com.mycompany.gerenciador.financeiro.model.Transacao> todasTransacoes = new java.util.ArrayList<>();
        
        // Busca transações de cada conta via ControladorTransacao
        for (Conta c : contas) {
            List<com.mycompany.gerenciador.financeiro.model.Transacao> transacoesConta = 
                controladorTransacao.buscarTransacoesFiltradas(c, null, null, null);
            todasTransacoes.addAll(transacoesConta);
        }

        return controladorTransacao.agruparPorData(todasTransacoes);
    }

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogoContas.salvarAoEncerrar();
    }
}
