package com.mycompany.gerenciador.financeiro.view;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Aplicação principal do Gerenciador Financeiro
 * Inicializa a interface gráfica com look and feel moderno
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Configurar FlatLaf para look and feel moderno
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (javax.swing.UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao configurar look and feel: " + e.getMessage());
        }

        // Criar e exibir tela de login
        TelaLogin telaLogin = new TelaLogin(primaryStage);
        telaLogin.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
