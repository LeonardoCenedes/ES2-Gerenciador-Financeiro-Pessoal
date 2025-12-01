package com.mycompany.gerenciador.financeiro.view.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Componente reutilizável de Snackbar/Toast para notificações
 * Aparece no canto superior direito com animação
 */
public class Snackbar {
    
    /**
     * Tipos de snackbar disponíveis
     */
    public enum Type {
        SUCCESS,
        ERROR,
        INFO,
        WARNING
    }
    
    /**
     * Mostra uma snackbar no container especificado
     * 
     * @param container O StackPane onde a snackbar será exibida
     * @param mensagem O texto da mensagem
     * @param tipo O tipo da snackbar (SUCCESS, ERROR, INFO, WARNING)
     */
    public static void show(StackPane container, String mensagem, Type tipo) {
        show(container, mensagem, tipo, 3000);
    }
    
    /**
     * Mostra uma snackbar no container especificado com duração customizada
     * 
     * @param container O StackPane onde a snackbar será exibida
     * @param mensagem O texto da mensagem
     * @param tipo O tipo da snackbar
     * @param duracao Duração em milissegundos antes de desaparecer
     */
    public static void show(StackPane container, String mensagem, Type tipo, int duracao) {
        // Criar snackbar
        HBox snackbar = new HBox(10);
        snackbar.setAlignment(Pos.CENTER_LEFT);
        snackbar.setPadding(new Insets(8, 16, 8, 16));
        snackbar.setMaxWidth(350);
        snackbar.setMaxHeight(45);
        snackbar.setPrefHeight(45);
        
        // Ícone baseado no tipo
        Label icone = new Label(getIcone(tipo));
        icone.setFont(Font.font("Segoe UI", 18));
        icone.setTextFill(Color.WHITE);
        
        // Mensagem
        Label labelMensagem = new Label(mensagem);
        labelMensagem.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        labelMensagem.setWrapText(true);
        labelMensagem.setTextFill(Color.WHITE);
        labelMensagem.setMaxWidth(280);
        
        snackbar.getChildren().addAll(icone, labelMensagem);
        
        // Estilo baseado no tipo
        snackbar.setStyle(getEstilo(tipo));
        
        // Posicionar no canto superior direito
        StackPane.setAlignment(snackbar, Pos.TOP_RIGHT);
        StackPane.setMargin(snackbar, new Insets(20, 20, 0, 0));
        
        // Animação de entrada - começa invisível e fora da tela
        snackbar.setTranslateX(400);
        snackbar.setOpacity(0);
        
        container.getChildren().add(snackbar);
        
        // Animar entrada (desliza da direita)
        Timeline timelineIn = new Timeline(
            new KeyFrame(Duration.millis(400),
                new KeyValue(snackbar.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(snackbar.opacityProperty(), 1, Interpolator.EASE_OUT)
            )
        );
        timelineIn.play();
        
        // Remover após duração especificada
        new Thread(() -> {
            try {
                Thread.sleep(duracao);
                javafx.application.Platform.runLater(() -> {
                    // Animar saída
                    Timeline timelineOut = new Timeline(
                        new KeyFrame(Duration.millis(400),
                            new KeyValue(snackbar.translateXProperty(), 400, Interpolator.EASE_IN),
                            new KeyValue(snackbar.opacityProperty(), 0, Interpolator.EASE_IN)
                        )
                    );
                    timelineOut.setOnFinished(e -> container.getChildren().remove(snackbar));
                    timelineOut.play();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private static String getIcone(Type tipo) {
        return switch (tipo) {
            case SUCCESS -> "✓";
            case ERROR -> "✕";
            case INFO -> "ℹ";
            case WARNING -> "⚠";
            default -> "•";
        };
    }
    
    private static String getEstilo(Type tipo) {
        String baseStyle = "-fx-background-radius: 10;" +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);";
        
        return switch (tipo) {
            case SUCCESS -> "-fx-background-color: linear-gradient(to right, #51cf66, #40c057);" + baseStyle;
            case ERROR -> "-fx-background-color: linear-gradient(to right, #ff6b6b, #ee5a6f);" + baseStyle;
            case INFO -> "-fx-background-color: linear-gradient(to right, #4dabf7, #339af0);" + baseStyle;
            case WARNING -> "-fx-background-color: linear-gradient(to right, #ffd43b, #fcc419);" + baseStyle;
            default -> "-fx-background-color: linear-gradient(to right, #868e96, #495057);" + baseStyle;
        };
    }
}
