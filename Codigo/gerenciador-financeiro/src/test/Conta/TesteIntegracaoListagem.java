/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.test.Conta;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.repository.ContaRepositoryTxt;
import java.util.List;

/**
 * Classe para testar a integração da funcionalidade de listagem
 */
public class TesteIntegracaoListagem {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTE DE INTEGRAÇÃO - LISTAGEM ===\n");
        
        // Teste 1: Preparar dados - cadastrar contas para teste
        prepararDadosTeste();
        
        // Teste 2: Listar todas as contas
        testarListarTodasContas();
        
        // Teste 3: Verificar dados das contas listadas
        testarVerificarDadosContas();
        
        // Teste 4: Testar listagem com arquivo vazio (simulado)
        testarListagemArquivoVazio();
        
        System.out.println("\n=== TESTES FINALIZADOS ===");
    }
    
    private static void prepararDadosTeste() {
        System.out.println("TESTE 1: Preparar Dados para Teste de Listagem");
        try {
            ContaController controller = new ContaController();
            
            controller.criarConta("Conta Teste 1", "Conta Corrente", 1000.0, "BRL");
            controller.criarConta("Conta Teste 2", "Poupança", 2000.0, "BRL");
            controller.criarConta("Conta Teste 3", "Cartão de Crédito", 0.0, "BRL");
            
            System.out.println("OK -- 3 contas cadastradas para teste");
            
        } catch (Exception e) {
            System.out.println("X -- ERRO ao preparar dados: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarListarTodasContas() {
        System.out.println("TESTE 2: Listar Todas as Contas via Controller");
        try {
            ContaController controller = new ContaController();
            List<Conta> contas = controller.listarContas();
            
            if (contas != null) {
                System.out.println("OK -- Lista retornada com sucesso");
                System.out.println("OK -- Total de contas: " + contas.size());
                
                if (contas.size() >= 3) {
                    System.out.println("OK -- Todas as contas de teste foram listadas");
                } else {
                    System.out.println("X -- AVISO: Esperava pelo menos 3 contas");
                }
            } else {
                System.out.println("X -- ERRO: Lista retornada é null");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarVerificarDadosContas() {
        System.out.println("TESTE 3: Verificar Dados das Contas Listadas");
        try {
            ContaController controller = new ContaController();
            List<Conta> contas = controller.listarContas();
            
            System.out.println("---------------------------------------------------");
            System.out.println("Contas listadas:");
            System.out.println("---------------------------------------------------");
            
            boolean todosOsDadosCorretos = true;
            
            for (Conta conta : contas) {
                // Verifica se todos os campos estão preenchidos
                boolean dadosValidos = conta.getId() > 0 
                    && conta.getNome() != null && !conta.getNome().isEmpty()
                    && conta.getTipo() != null && !conta.getTipo().isEmpty()
                    && conta.getMoeda() != null && !conta.getMoeda().isEmpty();
                
                String status = dadosValidos ? "OK" : "X";
                
                System.out.printf("[%s] ID: %d | Nome: %s | Tipo: %s | Saldo: R$ %.2f | Moeda: %s\n",
                    status,
                    conta.getId(),
                    conta.getNome(),
                    conta.getTipo(),
                    conta.getSaldoInicial(),
                    conta.getMoeda()
                );
                
                if (!dadosValidos) {
                    todosOsDadosCorretos = false;
                }
            }
            
            System.out.println("---------------------------------------------------");
            
            if (todosOsDadosCorretos) {
                System.out.println("OK -- Todos os dados estão corretos");
            } else {
                System.out.println("X -- Alguns dados estão incorretos ou vazios");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testarListagemArquivoVazio() {
        System.out.println("TESTE 4: Comportamento com Arquivo Inexistente/Vazio");
        try {
            // Simula cenário onde não há problema se arquivo não existir
            ContaRepositoryTxt repository = new ContaRepositoryTxt();
            List<Conta> contas = repository.listar();
            
            if (contas != null) {
                System.out.println("OK -- Lista retornada (não é null)");
                
                if (contas.isEmpty()) {
                    System.out.println("OK -- Lista vazia retornada corretamente");
                } else {
                    System.out.println("OK -- Lista com " + contas.size() + " contas");
                }
            } else {
                System.out.println("X -- ERRO: Lista não deveria ser null");
            }
            
        } catch (Exception e) {
            System.out.println("X -- ERRO: " + e.getMessage());
        }
        System.out.println();
    }
}
