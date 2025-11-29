package com.mycompany.gerenciador.financeiro;

import com.mycompany.gerenciador.financeiro.view.TelaLogin;

/**
 * Classe principal do sistema Gerenciador Financeiro Pessoal.
 * 
 * @author Laís Isabella
 */
public class GerenciadorFinanceiro {
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}