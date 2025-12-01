package com.mycompany.gerenciador.financeiro.view;

import com.mycompany.gerenciador.financeiro.controller.ControladorUsuario;
import com.mycompany.gerenciador.financeiro.view.components.Snackbar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Tela de Cadastro de novo usuário
 * Recebe o ControladorUsuario da tela anterior
 */
public class TelaCadastro {
    
    private final Stage stage;
    private final ControladorUsuario controladorUsuario;
    private final TelaLogin telaLoginAnterior;
    private TextField nomeField;
    private TextField emailField;
    private PasswordField senhaField;
    private PasswordField confirmarSenhaField;
    private Button cadastrarButton;
    private Button voltarButton;
    private StackPane rootContainer;

    public TelaCadastro(Stage stage, ControladorUsuario controladorUsuario, TelaLogin telaLoginAnterior) {
        this.stage = stage;
        this.controladorUsuario = controladorUsuario;
        this.telaLoginAnterior = telaLoginAnterior;
    }

    public void show() {
        // Container principal com gradiente dark
        rootContainer = new StackPane();
        rootContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a0a 0%, #1a0a1f 100%);");
        
        // Card central
        VBox cardCadastro = criarCardCadastro();
        
        rootContainer.getChildren().add(cardCadastro);
        
        // Atualizar Scene ou criar se não existir
        if (stage.getScene() == null) {
            Scene scene = new Scene(rootContainer, 1280, 720);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setScene(scene);
            
            // Obter dimensões da tela
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
            
            // Definir posição e tamanho para ocupar área visível (sem taskbar)
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            
            stage.show();
        } else {
            stage.getScene().setRoot(rootContainer);
        }
        
        // Focus no campo de nome
        nomeField.requestFocus();
    }

    private VBox criarCardCadastro() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);
        card.setMaxHeight(700);
        card.setPadding(new Insets(40, 60, 40, 60));
        
        card.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(138,43,226,0.4), 25, 0, 0, 10);"
        );
        
        // Título
        Label titulo = new Label("✨ Criar Nova Conta");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#9d4edd"));
        titulo.setWrapText(true);
        titulo.setMaxWidth(400);
        titulo.setAlignment(Pos.CENTER);
        
        Label subtitulo = new Label("Comece a controlar suas finanças agora");
        subtitulo.setFont(Font.font("Segoe UI", 14));
        subtitulo.setTextFill(Color.web("#aaaaaa"));
        
        Separator separador = new Separator();
        separador.setMaxWidth(300);
        
        // Campos
        VBox nomeBox = criarCampoTexto("👤 Nome Completo", nomeField = new TextField());
        nomeField.setPromptText("João da Silva");
        
        VBox emailBox = criarCampoTexto("📧 Email", emailField = new TextField());
        emailField.setPromptText("seu.email@exemplo.com");
        
        VBox senhaBox = criarCampoTexto("🔒 Senha", senhaField = new PasswordField());
        senhaField.setPromptText("Mínimo 6 caracteres");
        
        VBox confirmarSenhaBox = criarCampoTexto("🔒 Confirmar Senha", confirmarSenhaField = new PasswordField());
        confirmarSenhaField.setPromptText("Digite a senha novamente");
        
        // Botões
        cadastrarButton = criarBotaoPrimario("Criar Conta");
        cadastrarButton.setOnAction(e -> realizarCadastro());
        
        voltarButton = criarBotaoSecundario("← Voltar ao Login");
        voltarButton.setOnAction(e -> voltarLogin());
        
        // Enter nos campos
        confirmarSenhaField.setOnAction(e -> realizarCadastro());
        senhaField.setOnAction(e -> confirmarSenhaField.requestFocus());
        emailField.setOnAction(e -> senhaField.requestFocus());
        nomeField.setOnAction(e -> emailField.requestFocus());
        
        card.getChildren().addAll(
            titulo,
            subtitulo,
            separador,
            nomeBox,
            emailBox,
            senhaBox,
            confirmarSenhaBox,
            cadastrarButton,
            voltarButton
        );
        
        return card;
    }

    private VBox criarCampoTexto(String label, TextField campo) {
        VBox box = new VBox(8);
        
        Label labelField = new Label(label);
        labelField.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        labelField.setTextFill(Color.web("#ffffff"));
        
        campo.setPrefHeight(45);
        campo.setFont(Font.font("Segoe UI", 14));
        campo.setStyle(
            "-fx-background-color: #0a0a0a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 0 15 0 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #666666;"
        );
        
        campo.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                campo.setStyle(
                    "-fx-background-color: #0f0f0f;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #7b2cbf;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 0 15 0 15;" +
                    "-fx-text-fill: white;" +
                    "-fx-prompt-text-fill: #666666;"
                );
            } else {
                campo.setStyle(
                    "-fx-background-color: #0a0a0a;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #3a3a3a;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-padding: 0 15 0 15;" +
                    "-fx-text-fill: white;" +
                    "-fx-prompt-text-fill: #666666;"
                );
            }
        });
        
        box.getChildren().addAll(labelField, campo);
        return box;
    }

    private Button criarBotaoPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setPrefWidth(350);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle(
            "-fx-background-color: #7b2cbf;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #9d4edd;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: #7b2cbf;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        return btn;
    }

    private Button criarBotaoSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setPrefWidth(350);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9d4edd;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #7b2cbf33;" +
                "-fx-text-fill: #c77dff;" +
                "-fx-border-color: #9d4edd;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #9d4edd;" +
                "-fx-border-color: #7b2cbf;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        return btn;
    }

    private void realizarCadastro() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = senhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();
        
        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            mostrarMensagem("Por favor, preencha todos os campos", "erro");
            return;
        }
        
        if (nome.length() < 3) {
            mostrarMensagem("Nome deve ter pelo menos 3 caracteres", "erro");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            mostrarMensagem("Email inválido", "erro");
            return;
        }
        
        if (senha.length() < 6) {
            mostrarMensagem("Senha deve ter pelo menos 6 caracteres", "erro");
            return;
        }
        
        if (!senha.equals(confirmarSenha)) {
            mostrarMensagem("As senhas não coincidem", "erro");
            return;
        }
        
        // Desabilitar botão
        cadastrarButton.setDisable(true);
        cadastrarButton.setText("Criando conta...");
        
        // Tentar criar usuário
        boolean sucesso = controladorUsuario.criarUsuario(nome, email, senha);
        
        if (sucesso) {
            // IMPORTANTE: Salvar imediatamente após criar o usuário
            try {
                System.out.println("Salvando novo usuário criado...");
                controladorUsuario.salvarAoEncerrar();
                System.out.println("Usuário salvo com sucesso!");
            } catch (Exception ex) {
                System.err.println("ERRO ao salvar usuário: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            mostrarMensagem("Conta criada com sucesso! Redirecionando...", "sucesso");
            
            // Aguardar 2 segundos e voltar ao login
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::voltarLogin);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            mostrarMensagem("Email já cadastrado no sistema", "erro");
            cadastrarButton.setDisable(false);
            cadastrarButton.setText("Criar Conta");
            emailField.clear();
            emailField.requestFocus();
        }
    }

    private void voltarLogin() {
        telaLoginAnterior.show();
    }

    private void mostrarMensagem(String texto, String tipo) {
        Snackbar.Type snackbarType = tipo.equals("erro") ? Snackbar.Type.ERROR : Snackbar.Type.SUCCESS;
        Snackbar.show(rootContainer, texto, snackbarType);
    }
}
