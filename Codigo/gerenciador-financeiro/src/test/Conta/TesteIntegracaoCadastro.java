package com.mycompany.gerenciador.financeiro.test.Conta;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.repository.ContaRepositoryTxt;
import java.util.List;

/**
 * Classe para testar a integração completa do sistema
 */
public class TesteIntegracaoCadastro {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTE DE INTEGRAÇÃO ===\n");
        
        // Teste 1: Cadastrar conta via Controller
        testarCadastrarConta();
        
        // Teste 2: Listar contas do arquivo
        testarListarContas();
        
        // Teste 3: Validações
        testarValidacoes();
        
        System.out.println("\n=== TESTES FINALIZADOS ===");
    }
    
    private static void testarCadastrarConta() {
        System.out.println("TESTE 1: Cadastrar Conta");
        try {
            ContaController controller = new ContaController();
            
            // Cadastrar primeira conta
            controller.criarConta("Conta Corrente Itaú", "Conta Corrente", 5000.0, "BRL");
            System.out.println(" Conta 1 cadastrada com sucesso");
            
            // Cadastrar segunda conta
            controller.criarConta("Poupança Caixa", "Poupança", 1500.0, "BRL");
            System.out.println("OK -- Conta 2 cadastrada com sucesso");
            
            // Cadastrar terceira conta
            controller.criarConta("Cartão Nubank", "Cartão de Crédito", 0.0, "BRL");
            System.out.println("OK -- Conta 3 cadastrada com sucesso");
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarListarContas() {
        System.out.println("TESTE 2: Listar Contas do Arquivo");
        try {
            ContaRepositoryTxt repository = new ContaRepositoryTxt();
            List<Conta> contas = repository.listar();
            
            System.out.println("Total de contas cadastradas: " + contas.size());
            System.out.println("\nContas no arquivo:");
            System.out.println("---------------------------------------------------");
            
            for (Conta conta : contas) {
                System.out.printf("ID: %d | Nome: %s | Tipo: %s | Saldo: R$ %.2f | Moeda: %s\n",
                    conta.getId(),
                    conta.getNome(),
                    conta.getTipo(),
                    conta.getSaldoInicial(),
                    conta.getMoeda()
                );
            }
            System.out.println("---------------------------------------------------");
            
        } catch (Exception e) {
            System.out.println("✗ ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarValidacoes() {
        System.out.println("TESTE 3: Validações");

        try {
            ContaController controller = new ContaController();

            // Teste 3.1: Nome vazio
            try {
                controller.criarConta("", "Conta Corrente", 1000.0, "BRL");
                System.out.println("X -- ERRO: Deveria ter rejeitado nome vazio");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Nome vazio rejeitado corretamente: " + e.getMessage());
            }

            // Teste 3.2: Saldo negativo
            try {
                controller.criarConta("Conta Teste", "Poupança", -500.0, "BRL");
                System.out.println("X -- ERRO: Deveria ter rejeitado saldo negativo");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Saldo negativo rejeitado corretamente: " + e.getMessage());
            }

            // Teste 3.3: Tipo vazio
            try {
                controller.criarConta("Conta Teste", "", 1000.0, "BRL");
                System.out.println("X -- ERRO: Deveria ter rejeitado tipo vazio");
            } catch (IllegalArgumentException e) {
                System.out.println("OK -- Tipo vazio rejeitado corretamente: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("✗ ERRO ao inicializar controller: " + e.getMessage());
        }

        System.out.println();
    }

}