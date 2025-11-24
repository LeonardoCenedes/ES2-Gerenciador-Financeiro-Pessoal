/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.test.Conta;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.model.Conta;
import java.util.List;

/**
 * Classe para testar a integração da funcionalidade de exclusão de contas
 */
public class TesteIntegracaoExclusao {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTE DE INTEGRAÇÃO - EXCLUSÃO ===\n");
        
        // Teste 1: Preparar dados - cadastrar contas para excluir
        prepararDadosTeste();
        
        // Teste 2: Excluir conta com sucesso
        testarExcluirConta();
        
        // Teste 3: Verificar se exclusão foi persistida
        testarVerificarExclusao();
        
        // Teste 4: Tentar excluir conta inexistente
        testarExcluirContaInexistente();
        
        // Teste 5: Verificar integridade após múltiplas exclusões
        testarMultiplasExclusoes();
        
        System.out.println("\n=== TESTES FINALIZADOS ===");
    }
    
    private static void prepararDadosTeste() {
        System.out.println("TESTE 1: Preparar Dados para Teste de Exclusão");
        try {
            ContaController controller = new ContaController();
            
            // Cadastra contas para teste
            controller.criarConta("Conta Excluir 1", "Conta Corrente", 1000.0, "BRL");
            controller.criarConta("Conta Excluir 2", "Poupança", 2000.0, "BRL");
            controller.criarConta("Conta Excluir 3", "Cartão de Crédito", 0.0, "BRL");
            
            System.out.println("OK -- 3 contas cadastradas para teste");
            
        } catch (Exception e) {
            System.out.println("X -- ERRO ao preparar dados: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarExcluirConta() {
        System.out.println("TESTE 2: Excluir Conta");
        try {
            ContaController controller = new ContaController();
            
            // Lista as contas antes da exclusão
            List<Conta> contasAntes = controller.listarContas();
            int quantidadeAntes = contasAntes.size();
            
            if (contasAntes.isEmpty()) {
                System.out.println("X -- ERRO: Nenhuma conta encontrada para excluir");
                return;
            }
            
            // Pega a última conta para excluir
            Conta contaParaExcluir = contasAntes.get(contasAntes.size() - 1);
            int idExcluir = contaParaExcluir.getId();
            String nomeExcluir = contaParaExcluir.getNome();
            
            System.out.println("Conta a ser excluída:");
            System.out.printf("    ID: %d | Nome: %s | Tipo: %s\n",
                idExcluir, nomeExcluir, contaParaExcluir.getTipo());
            
            // Exclui a conta
            controller.excluirConta(idExcluir);
            System.out.println("OK -- Conta excluída com sucesso");
            
            // Verifica se quantidade diminuiu
            List<Conta> contasDepois = controller.listarContas();
            int quantidadeDepois = contasDepois.size();
            
            if (quantidadeDepois == quantidadeAntes - 1) {
                System.out.println("OK -- Quantidade de contas diminuiu corretamente (-1)");
                System.out.println("    Antes: " + quantidadeAntes + " | Depois: " + quantidadeDepois);
            } else {
                System.out.println("X -- ERRO: Quantidade incorreta");
                System.out.println("    Esperado: " + (quantidadeAntes - 1) + " | Obtido: " + quantidadeDepois);
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarVerificarExclusao() {
        System.out.println("TESTE 3: Verificar se Exclusão foi Persistida");
        try {
            ContaController controller = new ContaController();
            
            // Lista todas as contas
            List<Conta> contas = controller.listarContas();
            
            System.out.println("---------------------------------------------------");
            System.out.println("Contas restantes após exclusão:");
            System.out.println("---------------------------------------------------");
            
            for (Conta conta : contas) {
                System.out.printf("ID: %d | Nome: %s | Tipo: %s | Saldo: R$ %.2f\n",
                    conta.getId(),
                    conta.getNome(),
                    conta.getTipo(),
                    conta.getSaldoInicial()
                );
            }
            System.out.println("---------------------------------------------------");
            
            // Verifica se a conta "Conta Excluir 3" não está mais na lista
            boolean contaExcluidaEncontrada = false;
            for (Conta c : contas) {
                if ("Conta Excluir 3".equals(c.getNome())) {
                    contaExcluidaEncontrada = true;
                    break;
                }
            }
            
            if (!contaExcluidaEncontrada) {
                System.out.println("OK -- Conta excluída não está mais na lista");
            } else {
                System.out.println("X -- ERRO: Conta excluída ainda aparece na lista");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarExcluirContaInexistente() {
        System.out.println("TESTE 4: Tentar Excluir Conta Inexistente");
        try {
            ContaController controller = new ContaController();
            
            // Tenta excluir uma conta com ID inexistente (9999)
            controller.excluirConta(9999);
            System.out.println("X -- ERRO: Deveria ter lançado exceção para ID inexistente");
            
        } catch (IllegalArgumentException e) {
            System.out.println("OK -- Conta inexistente rejeitada: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("X -- ERRO inesperado: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testarMultiplasExclusoes() {
        System.out.println("TESTE 5: Verificar Integridade após Múltiplas Exclusões");
        try {
            ContaController controller = new ContaController();
            
            // Conta inicial
            List<Conta> contas = controller.listarContas();
            int quantidadeInicial = contas.size();
            System.out.println("Quantidade inicial de contas: " + quantidadeInicial);
            
            if (quantidadeInicial < 2) {
                System.out.println("X -- AVISO: Poucas contas para testar múltiplas exclusões");
                return;
            }
            
            // Exclui 2 contas
            int id1 = contas.get(contas.size() - 1).getId();
            int id2 = contas.get(contas.size() - 2).getId();
            
            controller.excluirConta(id1);
            System.out.println("OK -- Primeira conta excluída (ID: " + id1 + ")");
            
            controller.excluirConta(id2);
            System.out.println("OK -- Segunda conta excluída (ID: " + id2 + ")");
            
            // Verifica quantidade final
            contas = controller.listarContas();
            int quantidadeFinal = contas.size();
            
            if (quantidadeFinal == quantidadeInicial - 2) {
                System.out.println("OK -- Quantidade correta após exclusões múltiplas");
                System.out.println("    Inicial: " + quantidadeInicial + " | Final: " + quantidadeFinal);
            } else {
                System.out.println("X -- ERRO: Quantidade incorreta");
                System.out.println("    Esperado: " + (quantidadeInicial - 2) + " | Obtido: " + quantidadeFinal);
            }
            
            // Verifica integridade dos dados restantes
            boolean todosValidos = true;
            for (Conta c : contas) {
                if (c.getId() <= 0 || c.getNome() == null || c.getNome().isEmpty()) {
                    todosValidos = false;
                    break;
                }
            }
            
            if (todosValidos) {
                System.out.println("OK -- Integridade dos dados mantida");
            } else {
                System.out.println("X -- ERRO: Dados corrompidos após exclusões");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
}