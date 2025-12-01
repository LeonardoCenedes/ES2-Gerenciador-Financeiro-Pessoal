package com.mycompany.gerenciador.financeiro.view;

import java.io.IOException;

import com.mycompany.gerenciador.financeiro.controller.ControladorUsuario;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.view.components.Snackbar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Tela de Login moderna e responsiva
 * Segue o padrão MVC: View -> Controller apenas
 * Passa a instância do controller para as próximas telas
 */
public class TelaLogin {
    
    private final Stage stage;
    private ControladorUsuario controladorUsuario;
    private TextField emailField;
    private PasswordField senhaField;
    private Button loginButton;
    private Button cadastroButton;
    private StackPane rootContainer;

    public TelaLogin(Stage stage) {
        this.stage = stage;
        try {
            this.controladorUsuario = new ControladorUsuario();
        } catch (IOException e) {
            mostrarErro("Erro ao inicializar sistema", e.getMessage());
        }
    }

    public void show() {
        // Container principal com gradiente dark
        rootContainer = new StackPane();
        
        // Layout com barra de título
        javafx.scene.layout.BorderPane mainLayout = new javafx.scene.layout.BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a0a 0%, #1a0a1f 100%);");
        
        // Barra de título
        javafx.scene.layout.HBox titleBar = criarBarraTitulo();
        mainLayout.setTop(titleBar);
        
        // Card central com sombra
        VBox cardLogin = criarCardLogin();
        javafx.scene.layout.StackPane centerPane = new javafx.scene.layout.StackPane(cardLogin);
        centerPane.setStyle("-fx-background-color: transparent;");
        mainLayout.setCenter(centerPane);
        
        rootContainer.getChildren().add(mainLayout);
        
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
        
        // Focus no campo de email
        emailField.requestFocus();
    }

    private VBox criarCardLogin() {
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);
        card.setMaxHeight(600);
        card.setPadding(new Insets(50, 60, 50, 60));
        
        // Estilo do card - dark com sombra
        card.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(138,43,226,0.4), 25, 0, 0, 10);"
        );
        
        // Logo/Título
        Label titulo = new Label("💰 Gerenciador Financeiro");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#9d4edd"));
        titulo.setWrapText(true);
        titulo.setMaxWidth(400);
        titulo.setAlignment(Pos.CENTER);
        titulo.setStyle("-fx-padding: 0 0 10 0;");
        
        Label subtitulo = new Label("Controle suas finanças com inteligência");
        subtitulo.setFont(Font.font("Segoe UI", 14));
        subtitulo.setTextFill(Color.web("#aaaaaa"));
        
        // Separador
        Separator separador = new Separator();
        separador.setMaxWidth(300);
        separador.setStyle("-fx-padding: 10 0 10 0;");
        
        // Campo de Email
        VBox emailBox = criarCampoTexto("📧 Email", emailField = new TextField());
        emailField.setPromptText("seu.email@exemplo.com");
        
        // Campo de Senha
        VBox senhaBox = criarCampoTexto("🔒 Senha", senhaField = new PasswordField());
        senhaField.setPromptText("Digite sua senha");
        
        // Botão de Login
        loginButton = criarBotaoPrimario("Entrar");
        loginButton.setOnAction(e -> realizarLogin());
        
        // Linha com "ou"
        HBox ouBox = criarLinhaOu();
        
        // Botão de Cadastro
        cadastroButton = criarBotaoSecundario("Criar Conta");
        cadastroButton.setOnAction(e -> abrirTelaCadastro());
        
        // Adicionar enter no campo senha
        senhaField.setOnAction(e -> realizarLogin());
        emailField.setOnAction(e -> senhaField.requestFocus());
        
        // Adicionar tudo ao card
        card.getChildren().addAll(
            titulo,
            subtitulo,
            separador,
            emailBox,
            senhaBox,
            loginButton,
            ouBox,
            cadastroButton
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
        
        // Efeito de focus
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
        
        // Efeito hover
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #9d4edd;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
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
        
        // Efeito hover
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

    private HBox criarLinhaOu() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(350);
        
        Separator linha1 = new Separator();
        linha1.setPrefWidth(150);
        
        Label ou = new Label("ou");
        ou.setFont(Font.font("Segoe UI", 12));
        ou.setTextFill(Color.web("#666666"));
        
        Separator linha2 = new Separator();
        linha2.setPrefWidth(150);
        
        box.getChildren().addAll(linha1, ou, linha2);
        return box;
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String senha = senhaField.getText();
        
        // Validar campos
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarMensagem("Por favor, preencha todos os campos", "erro");
            return;
        }
        
        // Desabilitar botão durante processamento
        loginButton.setDisable(true);
        loginButton.setText("Entrando...");
        
        // Tentar autenticar
        Usuario usuario = controladorUsuario.autenticar(email, senha);
        
        if (usuario != null) {
            mostrarMensagem("Login realizado com sucesso! Bem-vindo, " + usuario.getNome(), "sucesso");
            
            // Aguardar 1 segundo e abrir tela principal
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> abrirTelaPrincipal(usuario));
                } catch (InterruptedException e) {
                }
            }).start();
        } else {
            mostrarMensagem("Email ou senha incorretos", "erro");
            loginButton.setDisable(false);
            loginButton.setText("Entrar");
            senhaField.clear();
            senhaField.requestFocus();
        }
    }

    private void abrirTelaCadastro() {
        TelaCadastro telaCadastro = new TelaCadastro(stage, controladorUsuario, this);
        telaCadastro.show();
    }

    private void abrirTelaPrincipal(Usuario usuario) {
        TelaHome telaHome = new TelaHome(stage, usuario);
        telaHome.show();
    }

    private void mostrarMensagem(String texto, String tipo) {
        Snackbar.Type snackbarType = tipo.equals("erro") ? Snackbar.Type.ERROR : Snackbar.Type.SUCCESS;
        Snackbar.show(rootContainer, texto, snackbarType);
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private javafx.scene.layout.HBox criarBarraTitulo() {
        javafx.scene.layout.HBox titleBar = new javafx.scene.layout.HBox();
        titleBar.setPrefHeight(35);
        titleBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        titleBar.setPadding(new javafx.geometry.Insets(0, 0, 0, 15));
        titleBar.setStyle("-fx-background-color: #000000;");
        
        javafx.scene.control.Label title = new javafx.scene.control.Label("💰 Gerenciador Financeiro");
        title.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.NORMAL, 13));
        title.setTextFill(javafx.scene.paint.Color.web("#cccccc"));
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        String btnStyle = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 14; -fx-pref-width: 45; -fx-pref-height: 35; -fx-cursor: hand;";
        
        javafx.scene.control.Button minimizeBtn = new javafx.scene.control.Button("−");
        minimizeBtn.setStyle(btnStyle);
        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setStyle(btnStyle + "-fx-background-color: #333333;"));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setStyle(btnStyle));
        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        
        javafx.scene.control.Button maximizeBtn = new javafx.scene.control.Button("□");
        maximizeBtn.setStyle(btnStyle);
        maximizeBtn.setOnMouseEntered(e -> maximizeBtn.setStyle(btnStyle + "-fx-background-color: #333333;"));
        maximizeBtn.setOnMouseExited(e -> maximizeBtn.setStyle(btnStyle));
        maximizeBtn.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });
        
        javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("✕");
        closeBtn.setStyle(btnStyle);
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(btnStyle + "-fx-background-color: #e81123; -fx-text-fill: white;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(btnStyle));
        closeBtn.setOnAction(e -> stage.close());
        
        // Arrastar janela
        final double[] offset = {0, 0};
        titleBar.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
        
        titleBar.getChildren().addAll(title, spacer, minimizeBtn, maximizeBtn, closeBtn);
        return titleBar;
    }
}
