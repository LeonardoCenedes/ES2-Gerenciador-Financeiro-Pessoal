/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.test.Conta;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.model.Conta;
import java.util.List;

/**
 * Classe para testar a integração da funcionalidade de edição de contas
 */
public class TesteIntegracaoEdicao {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTE DE INTEGRAÇÃO - EDIÇÃO ===\n");
        
        // Teste 1: Preparar dados - cadastrar conta para editar
        prepararDadosTeste();
        
        // Teste 2: Editar conta com sucesso
        testarEditarConta();
        
        // Teste 3: Verificar se edição foi persistida
        testarVerificarEdicao();
        
        // Teste 4: Verificar que moeda não foi alterada
        testarMoedaNaoAlterada();
        
        // Teste 5: Validações na edição
        testarValidacoesEdicao();
        
        // Teste 6: Tentar editar conta inexistente
        testarEditarContaInexistente();
        
        System.out.println("\n=== TESTES FINALIZADOS ===");
    }
    
    private static void prepararDadosTeste() {
        System.out.println("TESTE 1: Preparar Dados para Teste de Edição");
        try {
            ContaController controller = new ContaController();
            
            // Cadastra conta inicial para ser editada
            controller.cadastrarConta("Conta Original", "Conta Corrente", 1000.0, "BRL");
            System.out.println("OK -- Conta cadastrada para teste");
            
        } catch (Exception e) {
            System.out.println("X -- ERRO ao preparar dados: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarEditarConta() {
        System.out.println("TESTE 2: Editar Conta");
        try {
            ContaController controller = new ContaController();
            
            // Busca a conta para editar
            List<Conta> contas = controller.listarContas();
            if (contas.isEmpty()) {
                System.out.println("X -- ERRO: Nenhuma conta encontrada para editar");
                return;
            }
            
            Conta conta = contas.get(contas.size() - 1); // Pega a última conta
            int idConta = conta.getId();
            
            // Edita a conta
            controller.editarConta(idConta, "Conta Editada", "Poupança", 2500.0);
            System.out.println("OK -- Conta editada com sucesso");
            System.out.println("    ID: " + idConta);
            System.out.println("    Novo nome: Conta Editada");
            System.out.println("    Novo tipo: Poupança");
            System.out.println("    Novo saldo: R$ 2500.00");
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarVerificarEdicao() {
        System.out.println("TESTE 3: Verificar se Edição foi Persistida");
        try {
            ContaController controller = new ContaController();
            List<Conta> contas = controller.listarContas();
            
            if (contas.isEmpty()) {
                System.out.println("X -- ERRO: Nenhuma conta encontrada");
                return;
            }
            
            Conta conta = contas.get(contas.size() - 1);
            
            System.out.println("---------------------------------------------------");
            System.out.println("Dados da conta após edição:");
            System.out.printf("ID: %d | Nome: %s | Tipo: %s | Saldo: R$ %.2f | Moeda: %s\n",
                conta.getId(),
                conta.getNome(),
                conta.getTipo(),
                conta.getSaldoInicial(),
                conta.getMoeda()
            );
            System.out.println("---------------------------------------------------");
            
            // Verifica se os dados foram alterados corretamente
            boolean nomeCorreto = "Conta Editada".equals(conta.getNome());
            boolean tipoCorreto = "Poupança".equals(conta.getTipo());
            boolean saldoCorreto = Math.abs(conta.getSaldoInicial() - 2500.0) < 0.01;
            
            if (nomeCorreto && tipoCorreto && saldoCorreto) {
                System.out.println("OK -- Todos os dados foram persistidos corretamente");
            } else {
                if (!nomeCorreto) System.out.println("X -- ERRO: Nome não foi alterado corretamente");
                if (!tipoCorreto) System.out.println("X -- ERRO: Tipo não foi alterado corretamente");
                if (!saldoCorreto) System.out.println("X -- ERRO: Saldo não foi alterado corretamente");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarMoedaNaoAlterada() {
        System.out.println("TESTE 4: Verificar que Moeda NÃO foi Alterada");
        try {
            ContaController controller = new ContaController();
            List<Conta> contas = controller.listarContas();
            
            if (contas.isEmpty()) {
                System.out.println("X -- ERRO: Nenhuma conta encontrada");
                return;
            }
            
            Conta conta = contas.get(contas.size() - 1);
            
            if ("BRL".equals(conta.getMoeda())) {
                System.out.println("OK -- Moeda permaneceu inalterada (BRL)");
                System.out.println("    Conforme RF001.4: moeda não pode ser alterada após criação");
            } else {
                System.out.println("X -- ERRO: Moeda foi alterada indevidamente: " + conta.getMoeda());
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarValidacoesEdicao() {
        System.out.println("TESTE 5: Validações na Edição");
        
        try {
            ContaController controller = new ContaController();
            List<Conta> contas = controller.listarContas();
            
            if (contas.isEmpty()) {
                System.out.println("X -- ERRO: Nenhuma conta encontrada para testar validações");
                return;
            }
            
            int idConta = contas.get(0).getId();
            
            // Teste 5.1: Nome vazio
            try {
                controller.editarConta(idConta, "", "Poupança", 1000.0);
                System.out.println("X -- ERRO: Deveria ter rejeitado nome vazio");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Nome vazio rejeitado: " + e.getMessage());
            }
            
            // Teste 5.2: Saldo negativo
            try {
                controller.editarConta(idConta, "Conta Teste", "Poupança", -500.0);
                System.out.println("X -- ERRO: Deveria ter rejeitado saldo negativo");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Saldo negativo rejeitado: " + e.getMessage());
            }
            
            // Teste 5.3: Tipo vazio
            try {
                controller.editarConta(idConta, "Conta Teste", "", 1000.0);
                System.out.println("X -- ERRO: Deveria ter rejeitado tipo vazio");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Tipo vazio rejeitado: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO ao inicializar testes: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testarEditarContaInexistente() {
        System.out.println("TESTE 6: Tentar Editar Conta Inexistente");
        try {
            ContaController controller = new ContaController();
            
            // Tenta editar uma conta com ID inexistente (9999)
            controller.editarConta(9999, "Conta Fantasma", "Poupança", 1000.0);
            System.out.println("X -- ERRO: Deveria ter lançado exceção para ID inexistente");
            
        } catch (IllegalArgumentException e) {
            System.out.println("OK -- Conta inexistente rejeitada: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("X -- ERRO inesperado: " + e.getMessage());
        }
        
        System.out.println();
    }
}
