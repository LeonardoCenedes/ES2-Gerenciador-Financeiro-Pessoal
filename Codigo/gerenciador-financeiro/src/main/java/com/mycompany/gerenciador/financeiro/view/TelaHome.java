package com.mycompany.gerenciador.financeiro.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mycompany.gerenciador.financeiro.controller.ControladorCategoria;
import com.mycompany.gerenciador.financeiro.controller.ControladorConta;
import com.mycompany.gerenciador.financeiro.controller.ControladorLancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.controller.ControladorMetaEconomica;
import com.mycompany.gerenciador.financeiro.controller.ControladorOrcamento;
import com.mycompany.gerenciador.financeiro.controller.ControladorTransacao;
import com.mycompany.gerenciador.financeiro.controller.ControladorUsuario;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.LancamentoRecorrente;
import com.mycompany.gerenciador.financeiro.model.MetaEconomica;
import com.mycompany.gerenciador.financeiro.model.Orcamento;
import com.mycompany.gerenciador.financeiro.model.Periodicidade;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.util.PDFUtil;
import com.mycompany.gerenciador.financeiro.view.components.Snackbar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Tela Home com sidebar e navegação
 * Permite acesso a todas as funcionalidades do sistema
 */
public class TelaHome {
    
    private final Stage stage;
    private final Usuario usuarioLogado;
    private ControladorUsuario controladorUsuario;
    private ControladorCategoria controladorCategoria;
    private ControladorConta controladorConta;
    private ControladorTransacao controladorTransacao;
    private ControladorOrcamento controladorOrcamento;
    private ControladorMetaEconomica controladorMeta;
    private ControladorLancamentoRecorrente controladorLancamento;
    private StackPane rootContainer;
    private BorderPane mainLayout;
    private VBox contentArea;
    private String currentView = "dashboard";
    private List<Conta> contasUsuario;
    private Conta contaSelecionada = null;
    private Transacao transacaoSelecionada = null;
    private LancamentoRecorrente lancamentoSelecionado = null;

    public TelaHome(Stage stage, Usuario usuarioLogado) {
        this.stage = stage;
        this.usuarioLogado = usuarioLogado;
        try {
            this.controladorUsuario = new ControladorUsuario();
            this.controladorCategoria = new ControladorCategoria();
            this.controladorOrcamento = new ControladorOrcamento();
            
            // Inicialização com dependências corretas
            this.controladorMeta = new ControladorMetaEconomica();
            this.controladorTransacao = new ControladorTransacao(controladorMeta);
            this.controladorLancamento = new ControladorLancamentoRecorrente(controladorTransacao);
            this.controladorConta = new ControladorConta(controladorTransacao);
            this.contasUsuario = controladorConta.buscarContasUsuario(usuarioLogado);
            
            // Resolver referências de contas e categorias dos lançamentos recorrentes
            try {
                controladorLancamento.resolverReferencias(controladorConta, controladorCategoria, usuarioLogado);
                System.out.println("Referências de lançamentos recorrentes resolvidas!");
            } catch (Exception ex) {
                System.err.println("Erro ao resolver referências dos lançamentos: " + ex.getMessage());
            }
            
            // Verificar e gerar lançamentos recorrentes do dia
            try {
                controladorLancamento.verificarLancamentosDoDia();
                controladorLancamento.salvarAoEncerrar();
                System.out.println("Lançamentos recorrentes verificados com sucesso!");
            } catch (Exception ex) {
                System.err.println("Erro ao verificar lançamentos do dia: " + ex.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Erro ao inicializar controladores: " + e.getMessage());
        }
        
        // Adicionar shutdown hook para garantir que salve ao fechar
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Salvando dados ao encerrar aplicação...");
                controladorUsuario.salvarAoEncerrar();
                controladorConta.salvarAoEncerrar();
                controladorTransacao.salvarAoEncerrar();
                controladorLancamento.salvarAoEncerrar();
                System.out.println("Dados salvos com sucesso!");
            } catch (IOException e) {
                System.err.println("ERRO ao salvar dados no shutdown: " + e.getMessage());
                e.printStackTrace();
            }
        }));
    }

    public void show() {
        rootContainer = new StackPane();
        mainLayout = new BorderPane();
        
        // Barra de título customizada simples
        HBox titleBar = criarBarraTitulo();
        mainLayout.setTop(titleBar);
        
        // Sidebar à esquerda
        VBox sidebar = criarSidebar();
        mainLayout.setLeft(sidebar);
        
        // Área de conteúdo central
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-background-color: #1e1e1e;");
        
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: #1e1e1e;");
        
        scrollPane.setContent(contentArea);
        mainLayout.setCenter(scrollPane);
        
        // Estilo do layout principal
        mainLayout.setStyle("-fx-background-color: #1e1e1e;");
        
        rootContainer.getChildren().add(mainLayout);
        
        // Mostra dashboard inicial
        mostrarDashboard();
        
        if (stage.getScene() == null) {
            Scene scene = new Scene(rootContainer, 1280, 720);
            stage.initStyle(StageStyle.UNDECORATED);
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
        
        // Salvar dados ao fechar a janela (sempre adiciona, independente do if)
        stage.setOnCloseRequest(event -> {
            try {
                System.out.println("Salvando dados ao fechar janela...");
                controladorUsuario.salvarAoEncerrar();
                controladorConta.salvarAoEncerrar();
                controladorTransacao.salvarAoEncerrar();
                System.out.println("Dados salvos ao fechar janela!");
            } catch (IOException e) {
                System.err.println("ERRO ao salvar dados ao fechar: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private HBox criarBarraTitulo() {
        HBox titleBar = new HBox();
        titleBar.setPrefHeight(35);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(0, 0, 0, 15));
        titleBar.setStyle("-fx-background-color: #000000;");
        
        Label title = new Label("💰 Gerenciador Financeiro");
        title.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        title.setTextFill(Color.web("#cccccc"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        String btnStyle = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 14; -fx-pref-width: 45; -fx-pref-height: 35; -fx-cursor: hand;";
        
        Button minimizeBtn = new Button("−");
        minimizeBtn.setStyle(btnStyle);
        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setStyle(btnStyle + "-fx-background-color: #333333;"));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setStyle(btnStyle));
        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        
        Button maximizeBtn = new Button("□");
        maximizeBtn.setStyle(btnStyle);
        maximizeBtn.setOnMouseEntered(e -> maximizeBtn.setStyle(btnStyle + "-fx-background-color: #333333;"));
        maximizeBtn.setOnMouseExited(e -> maximizeBtn.setStyle(btnStyle));
        maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        
        Button closeBtn = new Button("✕");
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
    
    private String formatarMoeda(float valor, String moeda) {
        String simbolo;
        switch (moeda.toUpperCase()) {
            case "BRL":
            case "REAL":
                simbolo = "R$";
                break;
            case "USD":
            case "DOLAR":
                simbolo = "$";
                break;
            case "EUR":
            case "EURO":
                simbolo = "€";
                break;
            default:
                simbolo = moeda;
        }
        return String.format("%s %.2f", simbolo, valor);
    }

    private VBox criarSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.setMaxWidth(280);
        sidebar.setStyle(
            "-fx-background-color: #252526;" +
            "-fx-border-color: #3e3e42;" +
            "-fx-border-width: 0 1 0 0;"
        );
        
        // Header com nome do usuário
        VBox header = criarSidebarHeader();
        
        // Menu de navegação
        VBox menu = criarMenu();
        
        // Botão de logout no rodapé
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button logoutBtn = criarMenuButton("🚪 Sair", false, false);
        logoutBtn.setOnAction(e -> logout());
        logoutBtn.setStyle(
            logoutBtn.getStyle() +
            "-fx-border-color: #ff006e;" +
            "-fx-border-width: 1 0 0 0;"
        );
        
        sidebar.getChildren().addAll(header, menu, spacer, logoutBtn);
        
        return sidebar;
    }

    private VBox criarSidebarHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(25, 20, 25, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: #2d2d30;" +
            "-fx-border-color: #3e3e42;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        Label titulo = new Label("💰 Gerenciador");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.web("#e0e0e0"));
        
        Label subtitulo = new Label("Financeiro Pessoal");
        subtitulo.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitulo.setTextFill(Color.web("#9d9d9d"));
        
        Label nomeUsuario = new Label("👤 " + usuarioLogado.getNome());
        nomeUsuario.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        nomeUsuario.setTextFill(Color.web("#808080"));
        nomeUsuario.setPadding(new Insets(8, 0, 0, 0));
        
        header.getChildren().addAll(titulo, subtitulo, nomeUsuario);
        
        return header;
    }

    private VBox criarMenu() {
        VBox menu = new VBox(0);
        menu.setPadding(new Insets(20, 0, 20, 0));
        
        Button dashboardBtn = criarMenuButton("📊 Dashboard", currentView.equals("dashboard"), false);
        dashboardBtn.setOnAction(e -> {
            currentView = "dashboard";
            mostrarDashboard();
            atualizarMenu(menu);
        });
        
        Button contasBtn = criarMenuButton("💳 Contas", currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos"), false);
        contasBtn.setOnAction(e -> {
            if (currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos")) {
                // Já está expandido, colapsa
                currentView = "dashboard";
                mostrarDashboard();
            } else {
                // Expande e mostra contas
                currentView = "contas";
                contaSelecionada = null;
                mostrarContas();
            }
            atualizarMenu(menu);
        });
        
        menu.getChildren().addAll(dashboardBtn, contasBtn);
        
        // Submenu de Contas (só aparece quando Contas está ativo)
        if (currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos")) {
            Button transacoesBtn = criarMenuButton("   💸 Transações", currentView.equals("transacoes"), true);
            transacoesBtn.setOnAction(e -> {
                currentView = "transacoes";
                mostrarTransacoes();
                atualizarMenu(menu);
            });
            
            Button recorrentesBtn = criarMenuButton("   🔄 Lançamentos Recorrentes", currentView.equals("recorrentes"), true);
            recorrentesBtn.setOnAction(e -> {
                currentView = "recorrentes";
                mostrarRecorrentes();
                atualizarMenu(menu);
            });
            
            Button orcamentosBtn = criarMenuButton("   📋 Orçamentos", currentView.equals("orcamentos"), true);
            orcamentosBtn.setOnAction(e -> {
                currentView = "orcamentos";
                mostrarOrcamentos();
                atualizarMenu(menu);
            });
            
            menu.getChildren().addAll(transacoesBtn, recorrentesBtn, orcamentosBtn);
        }
        
        Button categoriasBtn = criarMenuButton("🏷️  Categorias", currentView.equals("categorias"), false);
        categoriasBtn.setOnAction(e -> {
            currentView = "categorias";
            mostrarCategorias();
            atualizarMenu(menu);
        });
        
        Button metasBtn = criarMenuButton("🎯 Metas", currentView.equals("metas"), false);
        metasBtn.setOnAction(e -> {
            currentView = "metas";
            mostrarMetas();
            atualizarMenu(menu);
        });
        
        menu.getChildren().addAll(
            categoriasBtn,
            metasBtn
        );
        
        return menu;
    }

    private Button criarMenuButton(String texto, boolean ativo, boolean isSubmenu) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, isSubmenu ? 40 : 20));
        btn.setFont(Font.font("System", FontWeight.NORMAL, isSubmenu ? 13 : 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        
        String backgroundColor = isSubmenu ? "#0a0a0a" : "transparent";
        
        if (ativo) {
            btn.setStyle(
                "-fx-background-color: " + (isSubmenu ? "#5a1a7f" : "#7b2cbf") + ";" +
                "-fx-text-fill: white;" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-border-color: #9d4edd;" +
                "-fx-border-width: 0 0 0 4;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                "-fx-text-fill: " + (isSubmenu ? "#9d4edd" : "#c77dff") + ";" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;"
            );
            
            btn.setOnMouseEntered(e -> {
                btn.setStyle(
                    "-fx-background-color: #7b2cbf33;" +
                    "-fx-text-fill: white;" +
                    "-fx-border-width: 0;" +
                    "-fx-background-radius: 0;"
                );
            });
            
            btn.setOnMouseExited(e -> {
                btn.setStyle(
                    "-fx-background-color: " + backgroundColor + ";" +
                    "-fx-text-fill: " + (isSubmenu ? "#9d4edd" : "#c77dff") + ";" +
                    "-fx-border-width: 0;" +
                    "-fx-background-radius: 0;"
                );
            });
        }
        
        return btn;
    }

    private void atualizarMenu(VBox menu) {
        menu.getChildren().clear();
        
        Button dashboardBtn = criarMenuButton("📊 Dashboard", currentView.equals("dashboard"), false);
        dashboardBtn.setOnAction(e -> {
            currentView = "dashboard";
            mostrarDashboard();
            atualizarMenu(menu);
        });
        
        Button contasBtn = criarMenuButton("💳 Contas", currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos"), false);
        contasBtn.setOnAction(e -> {
            if (currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos")) {
                currentView = "dashboard";
                mostrarDashboard();
            } else {
                currentView = "contas";
                contaSelecionada = null;
                mostrarContas();
            }
            atualizarMenu(menu);
        });
        
        menu.getChildren().addAll(dashboardBtn, contasBtn);
        
        if (currentView.equals("contas") || currentView.equals("transacoes") || currentView.equals("recorrentes") || currentView.equals("orcamentos")) {
            Button transacoesBtn = criarMenuButton("   💸 Transações", currentView.equals("transacoes"), true);
            transacoesBtn.setOnAction(e -> {
                currentView = "transacoes";
                mostrarTransacoes();
                atualizarMenu(menu);
            });
            
            Button recorrentesBtn = criarMenuButton("   🔄 Lançamentos Recorrentes", currentView.equals("recorrentes"), true);
            recorrentesBtn.setOnAction(e -> {
                currentView = "recorrentes";
                mostrarRecorrentes();
                atualizarMenu(menu);
            });
            
            Button orcamentosBtn = criarMenuButton("   📋 Orçamentos", currentView.equals("orcamentos"), true);
            orcamentosBtn.setOnAction(e -> {
                currentView = "orcamentos";
                mostrarOrcamentos();
                atualizarMenu(menu);
            });
            
            menu.getChildren().addAll(transacoesBtn, recorrentesBtn, orcamentosBtn);
        }
        
        Button categoriasBtn = criarMenuButton("🏷️  Categorias", currentView.equals("categorias"), false);
        categoriasBtn.setOnAction(e -> {
            currentView = "categorias";
            mostrarCategorias();
            atualizarMenu(menu);
        });
        
        Button metasBtn = criarMenuButton("🎯 Metas", currentView.equals("metas"), false);
        metasBtn.setOnAction(e -> {
            currentView = "metas";
            mostrarMetas();
            atualizarMenu(menu);
        });
        
        menu.getChildren().addAll(categoriasBtn, metasBtn);
    }

    private void mostrarDashboard() {
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Dashboard");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Visão geral das suas finanças");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        // ComboBox para selecionar conta - usando objetos diretamente
        ComboBox<Conta> contaCombo = new ComboBox<>();
        contaCombo.setPromptText("Selecione uma conta");
        
        // Item especial "Todas as contas" representado por null
        contaCombo.getItems().add(null);
        
        try {
            List<Conta> contas = controladorConta.buscarContasUsuario(usuarioLogado);
            contaCombo.getItems().addAll(contas);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar contas: " + ex.getMessage());
        }
        
        // StringConverter para exibir o nome da conta
        contaCombo.setConverter(new StringConverter<Conta>() {
            @Override
            public String toString(Conta conta) {
                return conta == null ? "📊 Todas as contas" : conta.getNome();
            }
            
            @Override
            public Conta fromString(String string) {
                return null; // Não precisamos converter de volta
            }
        });
        
        contaCombo.setValue(null); // Todas as contas por padrão
        contaCombo.setPrefWidth(300);
        contaCombo.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8 12;"
        );
        
        // Cards de resumo (serão atualizados)
        HBox cardsBox = new HBox(20);
        VBox saldoCard = criarCardResumo("💰 Saldo Total", "R$ 0,00", "#10b981");
        VBox receitasCard = criarCardResumo("📈 Receitas", "R$ 0,00", "#7b2cbf");
        VBox despesasCard = criarCardResumo("📉 Despesas", "R$ 0,00", "#ff006e");
        cardsBox.getChildren().addAll(saldoCard, receitasCard, despesasCard);
        
        // Atualizar cards quando mudar a seleção - agora recebe o objeto Conta diretamente
        contaCombo.setOnAction(e -> {
            Conta contaSelecionada = contaCombo.getValue();
            atualizarDashboardCards(contaSelecionada, saldoCard, receitasCard, despesasCard);
        });
        
        contentArea.getChildren().addAll(titulo, subtitulo, contaCombo, cardsBox);
        
        // Carregar dados iniciais
        atualizarDashboardCards(null, saldoCard, receitasCard, despesasCard);
    }
    
    private void atualizarDashboardCards(Conta conta, VBox saldoCard, VBox receitasCard, VBox despesasCard) {
        try {
            float saldoTotal = 0;
            float totalReceitas = 0;
            float totalDespesas = 0;
            
            if (conta == null) {
                // Calcular saldo inicial de todas as contas + transações
                List<Conta> contas = controladorConta.buscarContasUsuario(usuarioLogado);
                for (Conta c : contas) {
                    saldoTotal += c.getSaldoInicial();
                    
                    List<Transacao> transacoes = controladorTransacao.buscarPorConta(c);
                    for (Transacao t : transacoes) {
                        if (t.getTipo().equals(TiposTransacao.ENTRADA)) {
                            totalReceitas += t.getValor();
                            saldoTotal += t.getValor();
                        } else if (t.getTipo().equals(TiposTransacao.SAIDA)) {
                            totalDespesas += t.getValor();
                            saldoTotal -= t.getValor();
                        }
                    }
                }
            } else {
                // Conta específica - já temos o objeto diretamente!
                saldoTotal = conta.getSaldoInicial();
                
                // Buscar transações da conta específica
                List<Transacao> transacoes = controladorTransacao.buscarPorConta(conta);
                for (Transacao t : transacoes) {
                    if (t.getTipo().equals(TiposTransacao.ENTRADA)) {
                        totalReceitas += t.getValor();
                        saldoTotal += t.getValor();
                    } else if (t.getTipo().equals(TiposTransacao.SAIDA)) {
                        totalDespesas += t.getValor();
                        saldoTotal -= t.getValor();
                    }
                }
            }
            
            // Atualizar os cards
            atualizarCardValor(saldoCard, String.format("R$ %.2f", saldoTotal));
            atualizarCardValor(receitasCard, String.format("R$ %.2f", totalReceitas));
            atualizarCardValor(despesasCard, String.format("R$ %.2f", totalDespesas));
            
        } catch (Exception ex) {
            System.err.println("Erro ao atualizar dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void atualizarCardValor(VBox card, String novoValor) {
        // O card tem 2 filhos: título (Label) e valor (Label)
        if (card.getChildren().size() >= 2) {
            Label valorLabel = (Label) card.getChildren().get(1);
            valorLabel.setText(novoValor);
        }
    }

    private VBox criarCardResumo(String titulo, String valor, String cor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(120);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + cor + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        Label tituloLabel = new Label(titulo);
        tituloLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        tituloLabel.setTextFill(Color.web("#c77dff"));
        
        Label valorLabel = new Label(valor);
        valorLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valorLabel.setTextFill(Color.web(cor));
        
        card.getChildren().addAll(tituloLabel, valorLabel);
        
        return card;
    }

    private void mostrarContas() {
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Contas");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Gerencie suas contas bancárias e carteiras");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        Button adicionarBtn = criarBotaoPrimario("+ Nova Conta");
        adicionarBtn.setOnAction(e -> mostrarDialogNovaConta());
        
        contentArea.getChildren().addAll(titulo, subtitulo, adicionarBtn);
        
        // Recarregar contas
        try {
            contasUsuario = controladorConta.buscarContasUsuario(usuarioLogado);
        } catch (IOException e) {
            Snackbar.show(rootContainer, "Erro ao carregar contas: " + e.getMessage(), Snackbar.Type.ERROR);
            return;
        }
        
        if (contasUsuario.isEmpty()) {
            Label vazio = new Label("Nenhuma conta cadastrada");
            vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
            vazio.setTextFill(Color.web("#9d4edd"));
            vazio.setPadding(new Insets(40));
            vazio.setStyle(
                "-fx-background-color: #1a1a1a;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #7b2cbf;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
            );
            vazio.setMaxWidth(Double.MAX_VALUE);
            vazio.setAlignment(Pos.CENTER);
            contentArea.getChildren().add(vazio);
        } else {
            // Mostrar cards das contas
            VBox listaContas = new VBox(15);
            for (Conta conta : contasUsuario) {
                listaContas.getChildren().add(criarCardConta(conta));
            }
            contentArea.getChildren().add(listaContas);
        }
    }
    
    private VBox criarCardConta(Conta conta) {
        boolean selecionada = contaSelecionada != null && contaSelecionada.getNome().equals(conta.getNome());
        
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        String borderColor = selecionada ? "#9d4edd" : "#7b2cbf";
        String borderWidth = selecionada ? "3" : "2";
        String backgroundColor = selecionada ? "#2a1a3f" : "#1a1a1a";
        
        card.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: " + borderWidth + ";" +
            "-fx-border-radius: 10;" +
            (selecionada ? "-fx-effect: dropshadow(gaussian, rgba(157, 78, 221, 0.6), 15, 0, 0, 0);" : "")
        );
        
        // Clique para selecionar conta
        card.setOnMouseClicked(e -> {
            contaSelecionada = conta;
            mostrarContas();
        });
        
        // Header com nome e tipo
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconeTipo = new Label(obterIconeTipo(conta.getTipo()));
        iconeTipo.setFont(Font.font("System", 24));
        
        VBox infoBox = new VBox(5);
        Label nome = new Label(conta.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 18));
        nome.setTextFill(Color.WHITE);
        
        Label tipo = new Label(conta.getTipo() + " • " + conta.getMoeda());
        tipo.setFont(Font.font("System", FontWeight.NORMAL, 14));
        tipo.setTextFill(Color.web("#c77dff"));
        
        infoBox.getChildren().addAll(nome, tipo);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Buscar saldo atual do backend usando relatório de fluxo de caixa
        float saldoAtual = conta.getSaldoInicial();
        try {
            Map<String, Float> fluxo = controladorConta.gerarRelatorioFluxoCaixa(conta);
            float totalMovimentacao = 0;
            for (Float valor : fluxo.values()) {
                totalMovimentacao += valor;
            }
            saldoAtual += totalMovimentacao;
        } catch (Exception ex) {
            System.err.println("Erro ao buscar saldo: " + ex.getMessage());
        }
        
        // Exibir saldo inicial e saldo atual
        VBox saldoBox = new VBox(2);
        saldoBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label saldoAtualLabel = new Label(String.format("R$ %.2f", saldoAtual));
        saldoAtualLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        saldoAtualLabel.setTextFill(saldoAtual >= 0 ? Color.web("#10b981") : Color.web("#ff006e"));
        
        Label saldoInicialLabel = new Label(String.format("Inicial: R$ %.2f", conta.getSaldoInicial()));
        saldoInicialLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        saldoInicialLabel.setTextFill(Color.web("#9d4edd"));
        
        saldoBox.getChildren().addAll(saldoAtualLabel, saldoInicialLabel);
        
        header.getChildren().addAll(iconeTipo, infoBox, spacer, saldoBox);
        
        // Botões de ação
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        
        if (selecionada) {
            Button transacoesBtn = criarBotaoPrimario("💸 Transações");
            transacoesBtn.setOnAction(e -> {
                e.consume();
                mostrarTransacoes();
            });
            
            Button orcamentosBtn = criarBotaoPrimario("📋 Orçamentos");
            orcamentosBtn.setOnAction(e -> {
                e.consume();
                mostrarOrcamentos();
            });
            
            acoes.getChildren().addAll(transacoesBtn, orcamentosBtn);
        }
        
        Button editarBtn = criarBotaoSecundario("Editar");
        editarBtn.setOnAction(e -> {
            e.consume();
            mostrarDialogEditarConta(conta);
        });
        
        Button excluirBtn = criarBotaoSecundario("Excluir");
        excluirBtn.setOnAction(e -> {
            e.consume();
            excluirConta(conta);
        });
        excluirBtn.setStyle(
            excluirBtn.getStyle() +
            "-fx-border-color: #ff006e;" +
            "-fx-text-fill: #ff006e;"
        );
        
        acoes.getChildren().addAll(editarBtn, excluirBtn);
        
        card.getChildren().addAll(header, acoes);
        
        return card;
    }
    
    private String obterIconeTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "corrente":
                return "🏦";
            case "poupança":
            case "poupanca":
                return "💰";
            case "investimento":
                return "📈";
            case "carteira":
                return "👛";
            default:
                return "💳";
        }
    }
    
    private void mostrarDialogNovaConta() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(123, 44, 191, 0.6), 25, 0, 0, 0);"
        );
        
        // Header com título e botão fechar
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Nova Conta");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        TextField nomeField = criarTextField("Nome da conta");
        
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("Corrente", "Poupança", "Investimento", "Carteira");
        tipoCombo.setPromptText("Tipo de conta");
        tipoCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBox(tipoCombo);
        
        TextField saldoField = criarTextField("Saldo inicial");
        
        ComboBox<String> moedaCombo = new ComboBox<>();
        moedaCombo.getItems().addAll("BRL", "USD", "EUR");
        moedaCombo.setValue("BRL");
        moedaCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBox(moedaCombo);
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Conta");
        salvarBtn.setOnAction(e -> {
            try {
                String nome = nomeField.getText().trim();
                String tipo = tipoCombo.getValue();
                String saldoStr = saldoField.getText().trim();
                String moeda = moedaCombo.getValue();
                
                if (nome.isEmpty() || tipo == null || saldoStr.isEmpty()) {
                    Snackbar.show(rootContainer, "Preencha todos os campos", Snackbar.Type.WARNING);
                    return;
                }
                
                float saldo = Float.parseFloat(saldoStr);
                
                controladorConta.criarConta(nome, tipo, saldo, moeda, usuarioLogado);
                controladorConta.salvarAoEncerrar();
                
                Snackbar.show(rootContainer, "Conta criada com sucesso!", Snackbar.Type.SUCCESS);
                dialogStage.close();
                mostrarContas();
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Saldo inválido", Snackbar.Type.ERROR);
            } catch (IllegalArgumentException ex) {
                Snackbar.show(rootContainer, ex.getMessage(), Snackbar.Type.ERROR);
            } catch (IOException ex) {
                Snackbar.show(rootContainer, "Erro ao salvar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        
        dialog.getChildren().addAll(headerBox, nomeField, tipoCombo, saldoField, moedaCombo, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 450, 450);
        dialogScene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }
    
    private void mostrarDialogEditarConta(Conta conta) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(123, 44, 191, 0.6), 25, 0, 0, 0);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Editar Conta");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        TextField nomeField = criarTextField("Nome da conta");
        nomeField.setText(conta.getNome());
        
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("Corrente", "Poupança", "Investimento", "Carteira");
        tipoCombo.setValue(conta.getTipo());
        tipoCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBox(tipoCombo);
        
        TextField saldoField = criarTextField("Saldo inicial");
        saldoField.setText(String.valueOf(conta.getSaldoInicial()));
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Salvar");
        salvarBtn.setOnAction(e -> {
            try {
                String nome = nomeField.getText().trim();
                String tipo = tipoCombo.getValue();
                String saldoStr = saldoField.getText().trim();
                
                if (nome.isEmpty() || tipo == null || saldoStr.isEmpty()) {
                    Snackbar.show(rootContainer, "Preencha todos os campos", Snackbar.Type.WARNING);
                    return;
                }
                
                float saldo = Float.parseFloat(saldoStr);
                
                controladorConta.editarConta(conta, nome, tipo, saldo);
                controladorConta.salvarAoEncerrar();
                
                Snackbar.show(rootContainer, "Conta atualizada com sucesso!", Snackbar.Type.SUCCESS);
                dialogStage.close();
                mostrarContas();
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Saldo inválido", Snackbar.Type.ERROR);
            } catch (IllegalArgumentException ex) {
                Snackbar.show(rootContainer, ex.getMessage(), Snackbar.Type.ERROR);
            } catch (IOException ex) {
                Snackbar.show(rootContainer, "Erro ao salvar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        
        dialog.getChildren().addAll(headerBox, nomeField, tipoCombo, saldoField, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 450, 420);
        dialogScene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }
    
    private void excluirConta(Conta conta) {
        try {
            controladorConta.excluirConta(conta);
            controladorConta.salvarAoEncerrar();
            Snackbar.show(rootContainer, "Conta excluída com sucesso!", Snackbar.Type.SUCCESS);
            mostrarContas();
        } catch (Exception e) {
            Snackbar.show(rootContainer, "Erro ao excluir: " + e.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private TextField criarTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: #0a0a0a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 0 15 0 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #666666;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
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
                field.setStyle(
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
        
        return field;
    }
    
    private Button criarBotaoFecharDialog(Stage stage) {
        Button closeBtn = new Button("✕");
        closeBtn.setFont(Font.font("System", FontWeight.BOLD, 16));
        closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9d4edd;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5;" +
            "-fx-min-width: 30;" +
            "-fx-min-height: 30;"
        );
        closeBtn.setOnAction(e -> stage.close());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ff006e;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5;" +
            "-fx-min-width: 30;" +
            "-fx-min-height: 30;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9d4edd;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5;" +
            "-fx-min-width: 30;" +
            "-fx-min-height: 30;"
        ));
        return closeBtn;
    }
    
    // Métodos genéricos para estilizar ComboBox com qualquer tipo
    private void estilizarComboBox(ComboBox<String> combo) {
        estilizarComboBoxGenerico(combo);
    }
    
    private <T> void estilizarComboBoxGenerico(ComboBox<T> combo) {
        String estiloBase = 
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #666666;";
        
        String estiloFocado = 
            "-fx-background-color: #252525;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 2;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #666666;";
        
        combo.setStyle(estiloBase);
        
        combo.focusedProperty().addListener((obs, oldVal, newVal) -> {
            combo.setStyle(newVal ? estiloFocado : estiloBase);
        });
        
        // Estilizar o popup (lista dropdown) usando inline stylesheet
        combo.setOnShowing(e -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    if (combo.getSkin() != null) {
                        javafx.scene.Node popupContent = ((javafx.scene.control.skin.ComboBoxListViewSkin<?>) combo.getSkin()).getPopupContent();
                        if (popupContent != null) {
                            popupContent.setStyle(
                                "-fx-background-color: #1a1a1a;" +
                                "-fx-border-color: #7b2cbf;" +
                                "-fx-border-width: 2;" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-radius: 8;" +
                                "-fx-padding: 5;" +
                                "-fx-effect: dropshadow(gaussian, rgba(123, 44, 191, 0.4), 10, 0, 0, 0);"
                            );
                            
                            // Aplicar CSS para as células via lookup
                            javafx.scene.Node listView = popupContent.lookup(".list-view");
                            if (listView != null) {
                                listView.setStyle(
                                    "-fx-background-color: #1a1a1a;" +
                                    "-fx-background-insets: 0;" +
                                    "-fx-padding: 5;"
                                );
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silenciosamente ignora erros de estilização
                }
            });
        });
    }
    
    private Button criarBotaoSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setPadding(new Insets(10, 20, 10, 20));
        btn.setFont(Font.font("System", FontWeight.NORMAL, 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9d4edd;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #7b2cbf33;" +
            "-fx-text-fill: #c77dff;" +
            "-fx-border-color: #9d4edd;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #9d4edd;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;"
        ));
        
        return btn;
    }

    private DatePicker criarCampoData(String promptText) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText(promptText);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        
        // Configurar formato de data brasileiro
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }
            
            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
        
        // Definir data atual como padrão
        datePicker.setValue(LocalDate.now());
        
        // Aplicar CSS customizado
        try {
            datePicker.getStylesheets().add(getClass().getResource("/styles/datepicker-dark.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Não foi possível carregar CSS do DatePicker: " + ex.getMessage());
        }
        
        return datePicker;
    }
    
    private Date parseDataPicker(DatePicker datePicker) throws Exception {
        LocalDate localDate = datePicker.getValue();
        if (localDate == null) {
            throw new Exception("Data não selecionada");
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void mostrarCategorias() {
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Categorias");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Organize suas transações por categoria");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        Button adicionarBtn = criarBotaoPrimario("+ Nova Categoria");
        adicionarBtn.setOnAction(e -> mostrarDialogNovaCategoria());
        
        contentArea.getChildren().addAll(titulo, subtitulo, adicionarBtn);
        
        // Carregar categorias
        try {
            List<Categoria> categorias = controladorCategoria.buscarCategorias();
            
            if (categorias.isEmpty()) {
                Label vazio = new Label("Nenhuma categoria cadastrada");
                vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
                vazio.setTextFill(Color.web("#9d4edd"));
                vazio.setPadding(new Insets(40));
                vazio.setStyle(
                    "-fx-background-color: #1a1a1a;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #7b2cbf;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 10;"
                );
                vazio.setMaxWidth(Double.MAX_VALUE);
                vazio.setAlignment(Pos.CENTER);
                contentArea.getChildren().add(vazio);
            } else {
                // Separar categorias padrão e personalizadas
                Label tituloPadrao = new Label("📌 Categorias Padrão");
                tituloPadrao.setFont(Font.font("System", FontWeight.BOLD, 18));
                tituloPadrao.setTextFill(Color.web("#9d4edd"));
                tituloPadrao.setPadding(new Insets(20, 0, 10, 0));
                
                VBox listaCategoriaPadrao = new VBox(10);
                
                Label tituloPersonalizadas = new Label("✏️  Categorias Personalizadas");
                tituloPersonalizadas.setFont(Font.font("System", FontWeight.BOLD, 18));
                tituloPersonalizadas.setTextFill(Color.web("#9d4edd"));
                tituloPersonalizadas.setPadding(new Insets(20, 0, 10, 0));
                
                VBox listaCategoriaPersonalizada = new VBox(10);
                
                for (Categoria categoria : categorias) {
                    if (categoria.isPadrao()) {
                        listaCategoriaPadrao.getChildren().add(criarCardCategoria(categoria));
                    } else {
                        listaCategoriaPersonalizada.getChildren().add(criarCardCategoria(categoria));
                    }
                }
                
                if (!listaCategoriaPadrao.getChildren().isEmpty()) {
                    contentArea.getChildren().addAll(tituloPadrao, listaCategoriaPadrao);
                }
                
                if (!listaCategoriaPersonalizada.getChildren().isEmpty()) {
                    contentArea.getChildren().addAll(tituloPersonalizadas, listaCategoriaPersonalizada);
                } else {
                    Label nenhumaPersonalizada = new Label("Você ainda não criou categorias personalizadas");
                    nenhumaPersonalizada.setFont(Font.font("System", FontWeight.NORMAL, 14));
                    nenhumaPersonalizada.setTextFill(Color.web("#666666"));
                    nenhumaPersonalizada.setPadding(new Insets(10, 0, 0, 0));
                    contentArea.getChildren().addAll(tituloPersonalizadas, nenhumaPersonalizada);
                }
            }
        } catch (Exception ex) {
            Snackbar.show(rootContainer, "Erro ao carregar categorias: " + ex.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private VBox criarCardCategoria(Categoria categoria) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15, 20, 15, 20));
        
        String backgroundColor = categoria.isPadrao() ? "#0a0a0a" : "#1a1a1a";
        String borderColor = categoria.isStatus() ? "#7b2cbf" : "#3a3a3a";
        
        card.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 10;"
        );
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icone = new Label("🏷️");
        icone.setFont(Font.font("System", 20));
        
        Label nome = new Label(categoria.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 16));
        nome.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox tags = new HBox(8);
        tags.setAlignment(Pos.CENTER_RIGHT);
        
        if (categoria.isPadrao()) {
            Label tagPadrao = new Label("Padrão");
            tagPadrao.setFont(Font.font("System", FontWeight.BOLD, 11));
            tagPadrao.setTextFill(Color.web("#666666"));
            tagPadrao.setPadding(new Insets(3, 8, 3, 8));
            tagPadrao.setStyle(
                "-fx-background-color: #2a2a2a;" +
                "-fx-background-radius: 4;"
            );
            tags.getChildren().add(tagPadrao);
        }
        
        Label tagStatus = new Label(categoria.isStatus() ? "Ativa" : "Inativa");
        tagStatus.setFont(Font.font("System", FontWeight.BOLD, 11));
        tagStatus.setTextFill(categoria.isStatus() ? Color.web("#10b981") : Color.web("#666666"));
        tagStatus.setPadding(new Insets(3, 8, 3, 8));
        tagStatus.setStyle(
            "-fx-background-color: " + (categoria.isStatus() ? "#10b98133" : "#2a2a2a") + ";" +
            "-fx-background-radius: 4;"
        );
        tags.getChildren().add(tagStatus);
        
        header.getChildren().addAll(icone, nome, spacer, tags);
        
        // Botões de ação (só para categorias personalizadas)
        if (!categoria.isPadrao()) {
            HBox acoes = new HBox(10);
            acoes.setAlignment(Pos.CENTER_RIGHT);
            
            Button toggleBtn = criarBotaoSecundario(categoria.isStatus() ? "Desativar" : "Ativar");
            toggleBtn.setOnAction(e -> {
                categoria.setStatus(!categoria.isStatus());
                try {
                    controladorCategoria.salvarAoEncerrar();
                    mostrarCategorias();
                    Snackbar.show(rootContainer, "Status atualizado com sucesso!", Snackbar.Type.SUCCESS);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Erro ao atualizar: " + ex.getMessage(), Snackbar.Type.ERROR);
                }
            });
            
            acoes.getChildren().add(toggleBtn);
            card.getChildren().addAll(header, acoes);
        } else {
            card.getChildren().add(header);
        }
        
        return card;
    }
    
    private void mostrarDialogNovaCategoria() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(stage);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 10);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Nova Categoria");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        TextField nomeField = criarTextField("Nome da Categoria");
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Categoria");
        salvarBtn.setOnAction(e -> {
            String nome = nomeField.getText().trim();
            
            if (nome.isEmpty()) {
                Snackbar.show(rootContainer, "Digite um nome para a categoria", Snackbar.Type.WARNING);
                return;
            }
            
            try {
                // Verificar se já existe
                List<Categoria> categorias = controladorCategoria.buscarCategorias();
                for (Categoria cat : categorias) {
                    if (cat.getNome().equalsIgnoreCase(nome)) {
                        Snackbar.show(rootContainer, "Categoria já existe", Snackbar.Type.WARNING);
                        return;
                    }
                }
                
                // Criar nova categoria personalizada (não padrão)
                controladorCategoria.criarCategoria(nome, false, true);
                controladorCategoria.salvarAoEncerrar();
                
                Snackbar.show(rootContainer, "Categoria criada com sucesso!", Snackbar.Type.SUCCESS);
                dialogStage.close();
                mostrarCategorias();
                
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao criar categoria: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        dialog.getChildren().addAll(headerBox, nomeField, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 400, 220);
        dialogScene.setFill(Color.TRANSPARENT);
        
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void mostrarMetas() {
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Metas Econômicas");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Defina e acompanhe suas metas de economia");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        Button adicionarBtn = criarBotaoPrimario("+ Nova Meta");
        adicionarBtn.setOnAction(e -> mostrarDialogNovaMeta());
        
        contentArea.getChildren().addAll(titulo, subtitulo, adicionarBtn);
        
        List<MetaEconomica> metas = controladorMeta.buscarPorUsuario(usuarioLogado);
        
        if (metas.isEmpty()) {
            Label vazio = new Label("Nenhuma meta cadastrada");
            vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
            vazio.setTextFill(Color.web("#9d4edd"));
            vazio.setPadding(new Insets(40));
            vazio.setStyle(
                "-fx-background-color: #1a1a1a;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #7b2cbf;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
            );
            vazio.setMaxWidth(Double.MAX_VALUE);
            vazio.setAlignment(Pos.CENTER);
            contentArea.getChildren().add(vazio);
        } else {
            VBox listaMetas = new VBox(15);
            for (MetaEconomica meta : metas) {
                listaMetas.getChildren().add(criarCardMeta(meta));
            }
            contentArea.getChildren().add(listaMetas);
        }
    }
    
    private VBox criarCardMeta(MetaEconomica meta) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 10;"
        );
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icone = new Label("🎯");
        icone.setFont(Font.font("System", 24));
        
        VBox info = new VBox(5);
        Label nome = new Label(meta.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 18));
        nome.setTextFill(Color.WHITE);
        
        Label dataLimite = new Label("Prazo: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(meta.getDataLimite()));
        dataLimite.setFont(Font.font("System", FontWeight.NORMAL, 14));
        dataLimite.setTextFill(Color.web("#c77dff"));
        
        info.getChildren().addAll(nome, dataLimite);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox valores = new VBox(3);
        valores.setAlignment(Pos.CENTER_RIGHT);
        Label valorAtual = new Label(String.format("R$ %.2f", meta.getValorEconomizadoAtual()));
        valorAtual.setFont(Font.font("System", FontWeight.BOLD, 18));
        valorAtual.setTextFill(Color.web("#10b981"));
        
        Label valorMeta = new Label(String.format("de R$ %.2f", meta.getValor()));
        valorMeta.setFont(Font.font("System", FontWeight.NORMAL, 14));
        valorMeta.setTextFill(Color.web("#9d4edd"));
        
        valores.getChildren().addAll(valorAtual, valorMeta);
        
        header.getChildren().addAll(icone, info, spacer, valores);
        
        // Barra de progresso
        float progresso = (meta.getValorEconomizadoAtual() / meta.getValor()) * 100;
        if (progresso > 100) progresso = 100;
        
        VBox progressoContainer = new VBox(5);
        Label progressoLabel = new Label(String.format("%.1f%% concluído", progresso));
        progressoLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        progressoLabel.setTextFill(Color.web("#c77dff"));
        
        HBox barraBg = new HBox();
        barraBg.setStyle(
            "-fx-background-color: #0a0a0a;" +
            "-fx-background-radius: 5;"
        );
        barraBg.setMinHeight(8);
        barraBg.setMaxHeight(8);
        
        HBox barraProgress = new HBox();
        barraProgress.setStyle(
            "-fx-background-color: linear-gradient(to right, #7b2cbf, #9d4edd);" +
            "-fx-background-radius: 5;"
        );
        barraProgress.setMinHeight(8);
        barraProgress.setMaxHeight(8);
        barraProgress.prefWidthProperty().bind(barraBg.widthProperty().multiply(progresso / 100.0));
        
        StackPane barraStack = new StackPane();
        barraStack.getChildren().addAll(barraBg, barraProgress);
        StackPane.setAlignment(barraProgress, Pos.CENTER_LEFT);
        
        progressoContainer.getChildren().addAll(progressoLabel, barraStack);
        
        // Botão excluir
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        
        Button excluirBtn = new Button("🗑️");
        excluirBtn.setFont(Font.font("System", 16));
        excluirBtn.setCursor(javafx.scene.Cursor.HAND);
        excluirBtn.setPadding(new Insets(8, 12, 8, 12));
        excluirBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ff006e;" +
            "-fx-border-width: 0;"
        );
        excluirBtn.setOnMouseEntered(ev -> {
            excluirBtn.setStyle(
                "-fx-background-color: #ff006e22;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-background-radius: 8;" +
                "-fx-border-width: 0;"
            );
        });
        excluirBtn.setOnMouseExited(ev -> {
            excluirBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-border-width: 0;"
            );
        });
        
        excluirBtn.setOnAction(e -> {
            try {
                boolean sucesso = controladorMeta.deletarMeta(meta);
                
                if (sucesso) {
                    controladorMeta.salvarAoEncerrar();
                    mostrarMetas();
                    Snackbar.show(rootContainer, "Meta excluída com sucesso!", Snackbar.Type.SUCCESS);
                } else {
                    Snackbar.show(rootContainer, "Erro ao excluir meta", Snackbar.Type.ERROR);
                }
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao excluir: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        acoes.getChildren().add(excluirBtn);
        card.getChildren().addAll(header, progressoContainer, acoes);
        
        return card;
    }
    
    private void mostrarDialogNovaMeta() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(stage);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 10);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Nova Meta Econômica");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        TextField nomeField = criarTextField("Nome da Meta");
        TextField valorField = criarTextField("Valor Objetivo");
        DatePicker dataLimitePicker = criarCampoData("Data Limite");
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Meta");
        salvarBtn.setOnAction(e -> {
            String nome = nomeField.getText().trim();
            String valorStr = valorField.getText().trim();
            
            if (nome.isEmpty() || valorStr.isEmpty() || dataLimitePicker.getValue() == null) {
                Snackbar.show(rootContainer, "Preencha todos os campos", Snackbar.Type.WARNING);
                return;
            }
            
            try {
                float valor = Float.parseFloat(valorStr);
                Date dataLimite;
                try {
                    dataLimite = parseDataPicker(dataLimitePicker);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Data inválida! Selecione uma data", Snackbar.Type.ERROR);
                    return;
                }
                
                boolean sucesso = controladorMeta.criarMeta(nome, valor, dataLimite, usuarioLogado);
                
                if (sucesso) {
                    controladorMeta.salvarAoEncerrar();
                    Snackbar.show(rootContainer, "Meta criada com sucesso!", Snackbar.Type.SUCCESS);
                    dialogStage.close();
                    mostrarMetas();
                } else {
                    Snackbar.show(rootContainer, "Já existe uma meta com esse nome", Snackbar.Type.WARNING);
                }
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Valor inválido", Snackbar.Type.ERROR);
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao criar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        dialog.getChildren().addAll(headerBox, nomeField, valorField, dataLimitePicker, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 400, 400);
        dialogScene.setFill(Color.TRANSPARENT);
        
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    
    private void mostrarOrcamentos() {
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Orçamentos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Controle seus gastos por categoria");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        // Verificar se há conta selecionada
        if (contaSelecionada == null) {
            Snackbar.show(rootContainer, "Selecione uma conta primeiro", Snackbar.Type.WARNING);
            currentView = "contas";
            mostrarContas();
            return;
        }
        
        contentArea.getChildren().addAll(titulo, subtitulo);
        
        Button adicionarBtn = criarBotaoPrimario("+ Novo Orçamento");
        adicionarBtn.setOnAction(e -> mostrarDialogNovoOrcamento());
        
        contentArea.getChildren().add(adicionarBtn);
        
        try {
            List<Orcamento> orcamentos = controladorOrcamento.buscarPorUsuario(usuarioLogado);
            
            if (orcamentos.isEmpty()) {
                Label vazio = new Label("Nenhum orçamento cadastrado");
                vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
                vazio.setTextFill(Color.web("#9d4edd"));
                vazio.setPadding(new Insets(40));
                vazio.setStyle(
                    "-fx-background-color: #1a1a1a;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #7b2cbf;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 10;"
                );
                vazio.setMaxWidth(Double.MAX_VALUE);
                vazio.setAlignment(Pos.CENTER);
                contentArea.getChildren().add(vazio);
            } else {
                VBox listaOrcamentos = new VBox(15);
                for (Orcamento orc : orcamentos) {
                    listaOrcamentos.getChildren().add(criarCardOrcamento(orc));
                }
                contentArea.getChildren().add(listaOrcamentos);
            }
        } catch (Exception ex) {
            Snackbar.show(rootContainer, "Erro ao carregar orçamentos: " + ex.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private VBox criarCardOrcamento(Orcamento orcamento) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 10;"
        );
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icone = new Label("📋");
        icone.setFont(Font.font("System", 24));
        
        VBox info = new VBox(5);
        Label categoria = new Label(orcamento.getCategoria() != null ? orcamento.getCategoria().getNome() : "Sem categoria");
        categoria.setFont(Font.font("System", FontWeight.BOLD, 18));
        categoria.setTextFill(Color.WHITE);
        
        Label periodo = new Label("Período: " + new java.text.SimpleDateFormat("MM/yyyy").format(orcamento.getPeriodo()));
        periodo.setFont(Font.font("System", FontWeight.NORMAL, 14));
        periodo.setTextFill(Color.web("#c77dff"));
        
        info.getChildren().addAll(categoria, periodo);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Buscar gastos usando o backend
        float gastoTotal = 0f;
        if (contaSelecionada != null) {
            try {
                Map<Categoria, List<Transacao>> infosOrcamento = controladorOrcamento.buscarInfosOrcamento(
                    orcamento, contaSelecionada, controladorTransacao
                );
                
                List<Transacao> transacoes = infosOrcamento.get(orcamento.getCategoria());
                if (transacoes != null) {
                    for (Transacao t : transacoes) {
                        gastoTotal += t.getValor();
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao calcular gastos: " + e.getMessage());
            }
        }
        
        VBox valores = new VBox(3);
        valores.setAlignment(Pos.CENTER_RIGHT);
        
        Label valorGasto = new Label(String.format("R$ %.2f", gastoTotal));
        valorGasto.setFont(Font.font("System", FontWeight.BOLD, 18));
        float percentual = (gastoTotal / orcamento.getValorMaximo()) * 100;
        Color corGasto = percentual > 100 ? Color.web("#ff006e") : 
                        percentual > 80 ? Color.web("#ffa500") : Color.web("#10b981");
        valorGasto.setTextFill(corGasto);
        
        Label valorMax = new Label(String.format("de R$ %.2f", orcamento.getValorMaximo()));
        valorMax.setFont(Font.font("System", FontWeight.NORMAL, 14));
        valorMax.setTextFill(Color.web("#9d4edd"));
        
        valores.getChildren().addAll(valorGasto, valorMax);
        
        header.getChildren().addAll(icone, info, spacer, valores);
        
        // Barra de progresso
        VBox progressoContainer = new VBox(5);
        if (percentual > 100) percentual = 100;
        
        Label progressoLabel = new Label(String.format("%.1f%% do orçamento utilizado", (gastoTotal / orcamento.getValorMaximo()) * 100));
        progressoLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        progressoLabel.setTextFill(Color.web("#c77dff"));
        
        HBox barraBg = new HBox();
        barraBg.setStyle(
            "-fx-background-color: #0a0a0a;" +
            "-fx-background-radius: 5;"
        );
        barraBg.setMinHeight(8);
        barraBg.setMaxHeight(8);
        
        HBox barraProgress = new HBox();
        String corBarra = (gastoTotal / orcamento.getValorMaximo()) > 1.0 ? "#ff006e" :
                         (gastoTotal / orcamento.getValorMaximo()) > 0.8 ? "#ffa500" : "#10b981";
        barraProgress.setStyle(
            "-fx-background-color: " + corBarra + ";" +
            "-fx-background-radius: 5;"
        );
        barraProgress.setMinHeight(8);
        barraProgress.setMaxHeight(8);
        barraProgress.prefWidthProperty().bind(barraBg.widthProperty().multiply(percentual / 100.0));
        
        StackPane barraStack = new StackPane();
        barraStack.getChildren().addAll(barraBg, barraProgress);
        StackPane.setAlignment(barraProgress, Pos.CENTER_LEFT);
        
        progressoContainer.getChildren().addAll(progressoLabel, barraStack);
        
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        
        Button excluirBtn = new Button("🗑️");
        excluirBtn.setFont(Font.font("System", 16));
        excluirBtn.setCursor(javafx.scene.Cursor.HAND);
        excluirBtn.setPadding(new Insets(8, 12, 8, 12));
        excluirBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ff006e;" +
            "-fx-border-width: 0;"
        );
        excluirBtn.setOnMouseEntered(ev -> {
            excluirBtn.setStyle(
                "-fx-background-color: #ff006e22;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-background-radius: 8;" +
                "-fx-border-width: 0;"
            );
        });
        excluirBtn.setOnMouseExited(ev -> {
            excluirBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-border-width: 0;"
            );
        });
        
        excluirBtn.setOnAction(e -> {
            try {
                boolean sucesso = controladorOrcamento.deletarOrcamento(orcamento);
                
                if (sucesso) {
                    controladorOrcamento.salvarAoEncerrar();
                    mostrarOrcamentos();
                    Snackbar.show(rootContainer, "Orçamento excluído com sucesso!", Snackbar.Type.SUCCESS);
                } else {
                    Snackbar.show(rootContainer, "Erro ao excluir orçamento", Snackbar.Type.ERROR);
                }
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao excluir: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        acoes.getChildren().add(excluirBtn);
        card.getChildren().addAll(header, progressoContainer, acoes);
        
        return card;
    }
    
    private void mostrarDialogNovoOrcamento() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(stage);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 10);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Novo Orçamento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        ComboBox<Categoria> categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Categoria");
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);
        categoriaCombo.getStyleClass().add("combo-box");
        estilizarComboBoxGenerico(categoriaCombo);
        
        // Configurar conversor para exibir o nome da categoria
        categoriaCombo.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getNome() : "";
            }
            
            @Override
            public Categoria fromString(String string) {
                return categoriaCombo.getItems().stream()
                    .filter(cat -> cat.getNome().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        
        try {
            List<Categoria> categorias = controladorCategoria.buscarCategoriasAtivas();
            categoriaCombo.getItems().addAll(categorias);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar categorias: " + ex.getMessage());
        }
        
        TextField valorField = criarTextField("Valor Máximo");
        DatePicker periodoPicker = criarCampoData("Período (Mês/Ano)");
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Orçamento");
        salvarBtn.setOnAction(e -> {
            Categoria categoriaSel = categoriaCombo.getValue();
            String valorStr = valorField.getText().trim();
            
            if (categoriaSel == null || valorStr.isEmpty() || periodoPicker.getValue() == null) {
                Snackbar.show(rootContainer, "Preencha todos os campos", Snackbar.Type.WARNING);
                return;
            }
            
            try {
                float valor = Float.parseFloat(valorStr);
                Date periodo;
                try {
                    periodo = parseDataPicker(periodoPicker);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Data inválida! Use o formato dd/MM/yyyy", Snackbar.Type.ERROR);
                    return;
                }
                
                controladorOrcamento.criarOrcamento(periodo, valor, categoriaSel, usuarioLogado);
                controladorOrcamento.salvarAoEncerrar();
                
                Snackbar.show(rootContainer, "Orçamento criado com sucesso!", Snackbar.Type.SUCCESS);
                dialogStage.close();
                mostrarOrcamentos();
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Valor inválido", Snackbar.Type.ERROR);
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao criar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        dialog.getChildren().addAll(headerBox, categoriaCombo, valorField, periodoPicker, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 400, 420);
        dialogScene.setFill(Color.TRANSPARENT);
        
        try {
            String css = getClass().getResource("/styles/combobox-dark.css").toExternalForm();
            dialogScene.getStylesheets().add(css);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar CSS: " + ex.getMessage());
        }
        
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    
    private void mostrarRecorrentes() {
        if (contaSelecionada == null) {
            Snackbar.show(rootContainer, "Selecione uma conta primeiro", Snackbar.Type.WARNING);
            currentView = "contas";
            mostrarContas();
            return;
        }
        
        contentArea.getChildren().clear();
        
        Label titulo = new Label("Lançamentos Recorrentes");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Conta: " + contaSelecionada.getNome());
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        contentArea.getChildren().addAll(titulo, subtitulo);
        
        Button adicionarBtn = criarBotaoPrimario("+ Novo Lançamento");
        adicionarBtn.setOnAction(e -> mostrarDialogNovoLancamento());
        contentArea.getChildren().add(adicionarBtn);
        
        try {
            List<LancamentoRecorrente> lancamentos = controladorLancamento.buscarPorConta(contaSelecionada);
            
            if (lancamentos.isEmpty()) {
                Label vazio = new Label("Nenhum lançamento recorrente cadastrado");
                vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
                vazio.setTextFill(Color.web("#9d4edd"));
                vazio.setPadding(new Insets(40));
                vazio.setStyle(
                    "-fx-background-color: #1a1a1a;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #7b2cbf;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 10;"
                );
                vazio.setMaxWidth(Double.MAX_VALUE);
                vazio.setAlignment(Pos.CENTER);
                contentArea.getChildren().add(vazio);
            } else {
                VBox listaLancamentos = new VBox(15);
                for (LancamentoRecorrente lanc : lancamentos) {
                    listaLancamentos.getChildren().add(criarCardLancamento(lanc));
                }
                contentArea.getChildren().add(listaLancamentos);
            }
        } catch (Exception ex) {
            Snackbar.show(rootContainer, "Erro ao carregar lançamentos: " + ex.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private VBox criarCardLancamento(LancamentoRecorrente lancamento) {
        boolean selecionado = lancamentoSelecionado != null && 
                             lancamentoSelecionado.equals(lancamento);
        
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        String borderColor = selecionado ? "#9d4edd" : "#7b2cbf";
        String borderWidth = selecionado ? "2" : "1.5";
        String backgroundColor = selecionado ? "#2a1a3f" : "#1a1a1a";
        
        card.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: " + borderWidth + ";" +
            "-fx-border-radius: 10;" +
            (selecionado ? "-fx-effect: dropshadow(gaussian, rgba(157, 78, 221, 0.4), 10, 0, 0, 0);" : "")
        );
        
        // Primeiro clique seleciona, segundo clique mostra detalhes
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                lancamentoSelecionado = lancamento;
                mostrarRecorrentes();
            } else if (event.getClickCount() == 2) {
                mostrarDetalhesLancamento(lancamento);
            }
        });
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icone = new Label("🔄");
        icone.setFont(Font.font("System", 24));
        
        VBox info = new VBox(5);
        Label descricao = new Label(lancamento.getDescricao());
        descricao.setFont(Font.font("System", FontWeight.BOLD, 18));
        descricao.setTextFill(Color.WHITE);
        
        String periodicidadeStr = lancamento.getPeriodicidade().toString();
        Label detalhes = new Label("Periodicidade: " + periodicidadeStr + " • Próxima: " + 
            new SimpleDateFormat("dd/MM/yyyy").format(lancamento.getProximaData()));
        detalhes.setFont(Font.font("System", FontWeight.NORMAL, 14));
        detalhes.setTextFill(Color.web("#c77dff"));
        
        info.getChildren().addAll(descricao, detalhes);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox valores = new VBox(3);
        valores.setAlignment(Pos.CENTER_RIGHT);
        
        Label valorLabel = new Label(String.format("R$ %.2f", lancamento.getValor()));
        valorLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        valorLabel.setTextFill(Color.web("#9d4edd"));
        
        Label ocorrencias = new Label(lancamento.getNumeroOcorrencias() + " ocorrências");
        ocorrencias.setFont(Font.font("System", FontWeight.NORMAL, 14));
        ocorrencias.setTextFill(Color.web("#c77dff"));
        
        valores.getChildren().addAll(valorLabel, ocorrencias);
        
        header.getChildren().addAll(icone, info, spacer, valores);
        
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = new Button("🗑️");
        cancelarBtn.setFont(Font.font("System", 16));
        cancelarBtn.setCursor(javafx.scene.Cursor.HAND);
        cancelarBtn.setPadding(new Insets(8, 12, 8, 12));
        cancelarBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ff006e;" +
            "-fx-border-width: 0;"
        );
        cancelarBtn.setOnMouseEntered(ev -> {
            cancelarBtn.setStyle(
                "-fx-background-color: #ff006e22;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-background-radius: 8;" +
                "-fx-border-width: 0;"
            );
        });
        cancelarBtn.setOnMouseExited(ev -> {
            cancelarBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ff006e;" +
                "-fx-border-width: 0;"
            );
        });
        
        cancelarBtn.setOnAction(e -> {
            try {
                boolean sucesso = controladorLancamento.cancelar(lancamento);
                
                if (sucesso) {
                    controladorLancamento.salvarAoEncerrar();
                    mostrarRecorrentes();
                    Snackbar.show(rootContainer, "Lançamento cancelado com sucesso!", Snackbar.Type.SUCCESS);
                } else {
                    Snackbar.show(rootContainer, "Erro ao cancelar lançamento", Snackbar.Type.ERROR);
                }
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao cancelar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        acoes.getChildren().add(cancelarBtn);
        card.getChildren().addAll(header, acoes);
        
        return card;
    }
    
    private void mostrarDialogNovoLancamento() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(stage);
        
        // Container principal do dialog
        VBox dialogContainer = new VBox();
        dialogContainer.setPadding(new Insets(30));
        dialogContainer.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(123, 44, 191, 0.6), 25, 0, 0, 0);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox titulos = new VBox(5);
        
        Label titulo = new Label("Novo Lançamento Recorrente");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Label subtitulo = new Label("Configure a transação que será gerada automaticamente");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitulo.setTextFill(Color.web("#c77dff"));
        
        titulos.getChildren().addAll(titulo, subtitulo);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulos, spacer, closeBtn);
        
        // Separador após título
        Region espacador = new Region();
        espacador.setPrefHeight(10);
        
        // ScrollPane para o conteúdo
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);
        scrollPane.setMaxHeight(450);
        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox conteudo = new VBox(20);
        conteudo.setPadding(new Insets(10, 15, 10, 5));
        
        // Campos da transação
        TextField descricaoField = criarTextField("Descrição da Transação");
        TextField valorField = criarTextField("Valor");
        DatePicker dataInicioField = criarCampoData("Data de Início");
        
        ComboBox<TiposTransacao> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll(TiposTransacao.ENTRADA, TiposTransacao.SAIDA);
        tipoCombo.setPromptText("Tipo");
        tipoCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBoxGenerico(tipoCombo);
        
        // Configurar conversor para exibir o nome formatado
        tipoCombo.setConverter(new StringConverter<TiposTransacao>() {
            @Override
            public String toString(TiposTransacao tipo) {
                if (tipo == null) return "";
                return tipo == TiposTransacao.ENTRADA ? "Entrada" : "Saída";
            }
            
            @Override
            public TiposTransacao fromString(String string) {
                return "Entrada".equals(string) ? TiposTransacao.ENTRADA : TiposTransacao.SAIDA;
            }
        });
        
        ComboBox<Categoria> categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Categoria");
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBoxGenerico(categoriaCombo);
        
        // Configurar conversor para exibir o nome
        categoriaCombo.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getNome() : "";
            }
            
            @Override
            public Categoria fromString(String string) {
                return categoriaCombo.getItems().stream()
                    .filter(cat -> cat.getNome().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        
        try {
            List<Categoria> categorias = controladorCategoria.buscarCategoriasAtivas();
            categoriaCombo.getItems().addAll(categorias);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar categorias: " + ex.getMessage());
        }
        
        ComboBox<MetaEconomica> metaCombo = new ComboBox<>();
        metaCombo.setPromptText("Meta (opcional)");
        metaCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBoxGenerico(metaCombo);
        
        // Configurar conversor para exibir o nome
        metaCombo.setConverter(new StringConverter<MetaEconomica>() {
            @Override
            public String toString(MetaEconomica meta) {
                return meta != null ? meta.getNome() : "";
            }
            
            @Override
            public MetaEconomica fromString(String string) {
                return metaCombo.getItems().stream()
                    .filter(m -> m.getNome().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        
        List<MetaEconomica> metas = controladorMeta.buscarPorUsuario(usuarioLogado);
        metaCombo.getItems().clear();
        for (MetaEconomica meta : metas) {
            if (meta.getValorEconomizadoAtual() < meta.getValor()) {
                metaCombo.getItems().add(meta);
            }
        }
        
        // Comprovante PDF
        final File[] arquivoComprovante = {null};
        Label comprovanteLabel = new Label("📎 Nenhum comprovante anexado");
        comprovanteLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        comprovanteLabel.setTextFill(Color.web("#9d4edd"));
        
        Button anexarBtn = criarBotaoSecundario("📄 Anexar Comprovante");
        anexarBtn.setOnAction(ev -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar Comprovante");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf")
            );
            File arquivo = fileChooser.showOpenDialog(dialogStage);
            if (arquivo != null) {
                arquivoComprovante[0] = arquivo;
                comprovanteLabel.setText("📎 " + arquivo.getName());
                comprovanteLabel.setTextFill(Color.web("#10b981"));
            }
        });
        
        VBox comprovanteBox = new VBox(5);
        comprovanteBox.getChildren().addAll(anexarBtn, comprovanteLabel);
        
        // Separador visual
        javafx.scene.shape.Line separador = new javafx.scene.shape.Line();
        separador.setEndX(390);
        separador.setStroke(Color.web("#3a3a3a"));
        separador.setStrokeWidth(1);
        
        Label tituloRecorrencia = new Label("Configurações de Recorrência");
        tituloRecorrencia.setFont(Font.font("System", FontWeight.BOLD, 16));
        tituloRecorrencia.setTextFill(Color.web("#c77dff"));
        
        ComboBox<Periodicidade> periodicidadeCombo = new ComboBox<>();
        periodicidadeCombo.getItems().addAll(Periodicidade.values());
        periodicidadeCombo.setPromptText("Periodicidade");
        periodicidadeCombo.setMaxWidth(Double.MAX_VALUE);
        estilizarComboBoxGenerico(periodicidadeCombo);
        
        // Configurar conversor para exibir o nome formatado
        periodicidadeCombo.setConverter(new StringConverter<Periodicidade>() {
            @Override
            public String toString(Periodicidade periodicidade) {
                return periodicidade != null ? periodicidade.toString() : "";
            }
            
            @Override
            public Periodicidade fromString(String string) {
                try {
                    return Periodicidade.valueOf(string);
                } catch (Exception e) {
                    return null;
                }
            }
        });
        
        TextField ocorrenciasField = criarTextField("Número de Ocorrências");
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Lançamento");
        salvarBtn.setOnAction(e -> {
            try {
                String descricao = descricaoField.getText().trim();
                String valorStr = valorField.getText().trim();
                String ocorrenciasStr = ocorrenciasField.getText().trim();
                Periodicidade periodicidadeSel = periodicidadeCombo.getValue();
                TiposTransacao tipoSel = tipoCombo.getValue();
                Categoria categoriaSel = categoriaCombo.getValue();
                
                if (descricao.isEmpty() || valorStr.isEmpty() || ocorrenciasStr.isEmpty() || 
                    periodicidadeSel == null || tipoSel == null || categoriaSel == null) {
                    Snackbar.show(rootContainer, "Preencha todos os campos", Snackbar.Type.WARNING);
                    return;
                }
                
                float valor = Float.parseFloat(valorStr);
                int ocorrencias = Integer.parseInt(ocorrenciasStr);
                
                Date dataInicioLanc;
                try {
                    dataInicioLanc = parseDataPicker(dataInicioField);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Data inválida! Selecione uma data", Snackbar.Type.ERROR);
                    return;
                }
                
                // Obter meta se selecionada
                MetaEconomica metaSel = metaCombo.getValue();
                
                // Ler comprovante PDF se anexado
                byte[] comprovanteBytes = null;
                if (arquivoComprovante[0] != null) {
                    try {
                        PDFUtil pdfUtil = new PDFUtil();
                        comprovanteBytes = pdfUtil.importarPDF(arquivoComprovante[0].getAbsolutePath());
                    } catch (Exception ex) {
                        System.err.println("Erro ao importar comprovante: " + ex.getMessage());
                    }
                }
                
                // Criar a transação template primeiro usando o controlador
                boolean transacaoCriada = controladorTransacao.criarTransacao(
                    dataInicioLanc, valor, categoriaSel, descricao, comprovanteBytes, tipoSel, contaSelecionada, metaSel
                );
                
                if (!transacaoCriada) {
                    Snackbar.show(rootContainer, "Erro ao criar transação template", Snackbar.Type.ERROR);
                    return;
                }
                
                controladorTransacao.salvarAoEncerrar();
                
                // Buscar a transação recém-criada para usar como template
                List<Transacao> transacoes = controladorTransacao.buscarPorConta(contaSelecionada);
                Transacao templateTransacao = null;
                for (Transacao t : transacoes) {
                    if (t.getDescricao().equals(descricao) && t.getValor() == valor && 
                        t.getData().equals(dataInicioLanc)) {
                        templateTransacao = t;
                        break;
                    }
                }
                
                if (templateTransacao == null) {
                    Snackbar.show(rootContainer, "Erro ao localizar transação criada", Snackbar.Type.ERROR);
                    return;
                }
                
                // Agora criar o lançamento recorrente com a transação template
                boolean sucesso = controladorLancamento.criarLancamento(
                    descricao, valor, periodicidadeSel, dataInicioLanc, ocorrencias, dataInicioLanc, contaSelecionada, templateTransacao
                );
                
                if (sucesso) {
                    controladorLancamento.salvarAoEncerrar();
                    Snackbar.show(rootContainer, "Lançamento recorrente criado com sucesso!", Snackbar.Type.SUCCESS);
                    dialogStage.close();
                    mostrarRecorrentes();
                } else {
                    Snackbar.show(rootContainer, "Erro ao criar lançamento recorrente", Snackbar.Type.ERROR);
                }
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Valores inválidos", Snackbar.Type.ERROR);
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao salvar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        
        // Adicionar campos ao conteúdo do scroll
        conteudo.getChildren().addAll(descricaoField, valorField, dataInicioField, 
            tipoCombo, categoriaCombo, metaCombo, comprovanteBox, 
            separador, tituloRecorrencia, periodicidadeCombo, ocorrenciasField, botoesBox);
        
        scrollPane.setContent(conteudo);
        
        // Adicionar título e scroll ao container principal
        dialogContainer.getChildren().addAll(headerBox, espacador, scrollPane);
        
        Scene dialogScene = new Scene(dialogContainer, 480, 650);
        dialogScene.setFill(Color.TRANSPARENT);
        
        // Aplicar estilo do scrollbar
        try {
            dialogScene.getStylesheets().add(getClass().getResource("/styles/scrollbar-dark.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Não foi possível carregar CSS do scrollbar: " + ex.getMessage());
        }
        
        try {
            String css = getClass().getResource("/styles/combobox-dark.css").toExternalForm();
            dialogScene.getStylesheets().add(css);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar CSS: " + ex.getMessage());
        }
        
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    
    private void mostrarTransacoes() {
        if (contaSelecionada == null) {
            Snackbar.show(rootContainer, "Selecione uma conta primeiro", Snackbar.Type.WARNING);
            currentView = "contas";
            mostrarContas();
            return;
        }
        
        contentArea.getChildren().clear();
        
        // Header com informações da conta
        HBox headerConta = new HBox(15);
        headerConta.setAlignment(Pos.CENTER_LEFT);
        headerConta.setPadding(new Insets(20));
        headerConta.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        Label iconeConta = new Label(obterIconeTipo(contaSelecionada.getTipo()));
        iconeConta.setFont(Font.font("System", 32));
        
        VBox infoContaBox = new VBox(5);
        Label nomeConta = new Label(contaSelecionada.getNome());
        nomeConta.setFont(Font.font("System", FontWeight.BOLD, 24));
        nomeConta.setTextFill(Color.WHITE);
        
        Label tipoConta = new Label(contaSelecionada.getTipo() + " • " + contaSelecionada.getMoeda());
        tipoConta.setFont(Font.font("System", FontWeight.NORMAL, 14));
        tipoConta.setTextFill(Color.web("#c77dff"));
        
        infoContaBox.getChildren().addAll(nomeConta, tipoConta);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label saldoConta = new Label(String.format("R$ %.2f", contaSelecionada.getSaldoInicial()));
        saldoConta.setFont(Font.font("System", FontWeight.BOLD, 28));
        saldoConta.setTextFill(contaSelecionada.getSaldoInicial() >= 0 ? Color.web("#10b981") : Color.web("#ff006e"));
        
        Button voltarBtn = criarBotaoSecundario("← Voltar");
        voltarBtn.setOnAction(e -> {
            contaSelecionada = null;
            mostrarContas();
        });
        
        headerConta.getChildren().addAll(iconeConta, infoContaBox, spacer, saldoConta, voltarBtn);
        
        // Título e botão de nova transação
        HBox tituloBox = new HBox(20);
        tituloBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Transações");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Button gerarPDFBtn = criarBotaoSecundario("📄 Gerar Extrato PDF");
        gerarPDFBtn.setOnAction(e -> gerarExtratoPDF());
        
        Button novaTransacaoBtn = criarBotaoPrimario("+ Nova Transação");
        novaTransacaoBtn.setOnAction(e -> mostrarDialogNovaTransacao());
        
        tituloBox.getChildren().addAll(titulo, spacer2, gerarPDFBtn, novaTransacaoBtn);
        
        contentArea.getChildren().addAll(headerConta, tituloBox);
        
        // Carregar transações
        try {
            List<Transacao> transacoes = controladorTransacao.buscarPorConta(contaSelecionada);
            
            if (transacoes.isEmpty()) {
                Label vazio = new Label("Nenhuma transação registrada");
                vazio.setFont(Font.font("System", FontWeight.NORMAL, 14));
                vazio.setTextFill(Color.web("#9d4edd"));
                vazio.setPadding(new Insets(40));
                vazio.setStyle(
                    "-fx-background-color: #1a1a1a;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #7b2cbf;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 10;"
                );
                vazio.setMaxWidth(Double.MAX_VALUE);
                vazio.setAlignment(Pos.CENTER);
                contentArea.getChildren().add(vazio);
            } else {
                VBox listaTransacoes = new VBox(10);
                for (Transacao transacao : transacoes) {
                    listaTransacoes.getChildren().add(criarCardTransacao(transacao));
                }
                contentArea.getChildren().add(listaTransacoes);
            }
        } catch (Exception e) {
            Snackbar.show(rootContainer, "Erro ao carregar transações: " + e.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private VBox criarCardTransacao(Transacao transacao) {
        boolean selecionada = transacaoSelecionada != null && 
                             transacaoSelecionada.getData().equals(transacao.getData()) &&
                             transacaoSelecionada.getDescricao().equals(transacao.getDescricao()) &&
                             transacaoSelecionada.getValor() == transacao.getValor();
        
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        String borderColor = selecionada ? "#9d4edd" : "#3a3a3a";
        String borderWidth = selecionada ? "2" : "1";
        String backgroundColor = selecionada ? "#2a1a3f" : "#1a1a1a";
        
        card.setStyle(
            "-fx-background-color: " + backgroundColor + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: " + borderWidth + ";" +
            "-fx-border-radius: 8;" +
            (selecionada ? "-fx-effect: dropshadow(gaussian, rgba(157, 78, 221, 0.4), 10, 0, 0, 0);" : "")
        );
        
        // Primeiro clique seleciona, segundo clique mostra detalhes
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                transacaoSelecionada = transacao;
                mostrarTransacoes();
            } else if (event.getClickCount() == 2) {
                mostrarDetalhesTransacao(transacao);
            }
        });
        
        HBox mainRow = new HBox(15);
        mainRow.setAlignment(Pos.CENTER_LEFT);
        
        // Ícone do tipo
        String icone = transacao.getTipo() == TiposTransacao.ENTRADA ? "📈" : "📉";
        Label iconeLabel = new Label(icone);
        iconeLabel.setFont(Font.font("System", 20));
        
        // Informações
        VBox infoBox = new VBox(5);
        Label descricao = new Label(transacao.getDescricao() != null ? transacao.getDescricao() : "Sem descrição");
        descricao.setFont(Font.font("System", FontWeight.BOLD, 16));
        descricao.setTextFill(Color.WHITE);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataStr = sdf.format(transacao.getData());
        String categoriaStr = transacao.getCategoria() != null ? transacao.getCategoria().getNome() : "Sem categoria";
        
        Label detalhes = new Label(dataStr + " • " + categoriaStr);
        detalhes.setFont(Font.font("System", FontWeight.NORMAL, 13));
        detalhes.setTextFill(Color.web("#c77dff"));
        
        infoBox.getChildren().addAll(descricao, detalhes);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Valor
        String sinal = transacao.getTipo() == TiposTransacao.ENTRADA ? "+" : "-";
        Label valor = new Label(sinal + " R$ " + String.format("%.2f", transacao.getValor()));
        valor.setFont(Font.font("System", FontWeight.BOLD, 18));
        Color corValor = transacao.getTipo() == TiposTransacao.ENTRADA ? Color.web("#10b981") : Color.web("#ff006e");
        valor.setTextFill(corValor);
        
        // Botão excluir
        Button excluirBtn = new Button("🗑️");
        excluirBtn.setFont(Font.font("System", 14));
        excluirBtn.setCursor(javafx.scene.Cursor.HAND);
        excluirBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ff006e;" +
            "-fx-border-width: 0;"
        );
        excluirBtn.setOnAction(e -> excluirTransacao(transacao));
        
        mainRow.getChildren().addAll(iconeLabel, infoBox, spacer, valor, excluirBtn);
        card.getChildren().add(mainRow);
        
        return card;
    }
    
    private void mostrarDetalhesTransacao(Transacao transacao) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox dialog = new VBox(25);
        dialog.setPadding(new Insets(35));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 10);"
        );
        
        // Header com ícone e título
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        String icone = transacao.getTipo() == TiposTransacao.ENTRADA ? "📈" : "📉";
        Label iconeLabel = new Label(icone);
        iconeLabel.setFont(Font.font("System", 32));
        
        Label titulo = new Label("Detalhes da Transação");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        header.getChildren().addAll(iconeLabel, titulo);
        
        // Separador
        javafx.scene.shape.Line separador = new javafx.scene.shape.Line();
        separador.setEndX(380);
        separador.setStroke(Color.web("#3a3a3a"));
        separador.setStrokeWidth(1);
        
        VBox detalhes = new VBox(18);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
        
        // Data
        HBox dataBox = criarLinhaDetalhe("Data", sdf.format(transacao.getData()));
        
        // Tipo
        String tipoStr = transacao.getTipo() == TiposTransacao.ENTRADA ? "Entrada" : "Saída";
        Color corTipo = transacao.getTipo() == TiposTransacao.ENTRADA ? Color.web("#10b981") : Color.web("#ff006e");
        HBox tipoBox = criarLinhaDetalhe("Tipo", tipoStr);
        ((Label) tipoBox.getChildren().get(1)).setTextFill(corTipo);
        
        // Valor
        String sinal = transacao.getTipo() == TiposTransacao.ENTRADA ? "+ " : "- ";
        String valorStr = sinal + String.format("R$ %.2f", transacao.getValor());
        Color corValor = transacao.getTipo() == TiposTransacao.ENTRADA ? Color.web("#10b981") : Color.web("#ff006e");
        HBox valorBox = criarLinhaDetalhe("Valor", valorStr);
        Label lblValor = (Label) valorBox.getChildren().get(1);
        lblValor.setTextFill(corValor);
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Descrição
        HBox descBox = criarLinhaDetalhe("Descrição", 
            transacao.getDescricao() != null && !transacao.getDescricao().isEmpty() ? 
            transacao.getDescricao() : "Sem descrição");
        
        // Categoria
        HBox catBox = criarLinhaDetalhe("Categoria", 
            transacao.getCategoria() != null ? transacao.getCategoria().getNome() : "Sem categoria");
        
        detalhes.getChildren().addAll(dataBox, tipoBox, valorBox, descBox, catBox);
        
        // Meta (se houver)
        if (transacao.getMetaEconomica() != null) {
            HBox metaBox = criarLinhaDetalhe("Meta", transacao.getMetaEconomica().getNome());
            ((Label) metaBox.getChildren().get(1)).setTextFill(Color.web("#9d4edd"));
            detalhes.getChildren().add(metaBox);
        }
        
        // Conta
        HBox contaBox = criarLinhaDetalhe("Conta", transacao.getConta().getNome());
        detalhes.getChildren().add(contaBox);
        
        // Comprovante PDF
        if (transacao.getComprovante() != null && transacao.getComprovante().length > 0) {
            HBox pdfBox = new HBox(10);
            pdfBox.setAlignment(Pos.CENTER_LEFT);
            pdfBox.setPadding(new Insets(8, 15, 8, 15));
            pdfBox.setStyle(
                "-fx-background-color: #0a0a0a;" +
                "-fx-background-radius: 8;"
            );
            
            Label lblPDF = new Label("📎 Comprovante PDF");
            lblPDF.setFont(Font.font("System", FontWeight.BOLD, 13));
            lblPDF.setTextFill(Color.web("#10b981"));
            
            Button abrirPDFBtn = new Button("Abrir");
            abrirPDFBtn.setStyle(
                "-fx-background-color: #7b2cbf;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 5 15 5 15;" +
                "-fx-cursor: hand;"
            );
            abrirPDFBtn.setOnAction(ev -> {
                try {
                    // Salvar temporariamente e abrir
                    File tempFile = File.createTempFile("comprovante_", ".pdf");
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                        fos.write(transacao.getComprovante());
                    }
                    java.awt.Desktop.getDesktop().open(tempFile);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Erro ao abrir PDF: " + ex.getMessage(), Snackbar.Type.ERROR);
                }
            });
            
            pdfBox.getChildren().addAll(lblPDF, abrirPDFBtn);
            detalhes.getChildren().add(pdfBox);
        }
        
        Button voltarBtn = criarBotaoPrimario("Fechar");
        voltarBtn.setPrefWidth(150);
        voltarBtn.setOnAction(e -> dialogStage.close());
        
        HBox btnBox = new HBox(voltarBtn);
        btnBox.setAlignment(Pos.CENTER);
        
        dialog.getChildren().addAll(header, separador, detalhes, btnBox);
        
        Scene dialogScene = new Scene(dialog, 500, 550);
        dialogScene.setFill(Color.TRANSPARENT);
        
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }
    
    private void mostrarDetalhesLancamento(LancamentoRecorrente lancamento) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox dialog = new VBox(25);
        dialog.setPadding(new Insets(35));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 10);"
        );
        
        // Header com ícone e título
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconeLabel = new Label("🔄");
        iconeLabel.setFont(Font.font("System", 32));
        iconeLabel.setStyle("-fx-text-fill: #9d4edd;");
        
        Label titulo = new Label("Detalhes do Lançamento Recorrente");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.WHITE);
        
        header.getChildren().addAll(iconeLabel, titulo);
        
        // Separador
        javafx.scene.shape.Line separador = new javafx.scene.shape.Line();
        separador.setEndX(480);
        separador.setStroke(Color.web("#3a3a3a"));
        separador.setStrokeWidth(1);
        
        VBox detalhes = new VBox(18);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        // Descrição
        HBox descBox = criarLinhaDetalhe("Descrição", lancamento.getDescricao());
        
        // Valor
        String valorStr = String.format("R$ %.2f", lancamento.getValor());
        HBox valorBox = criarLinhaDetalhe("Valor", valorStr);
        Label lblValor = (Label) valorBox.getChildren().get(1);
        lblValor.setTextFill(Color.web("#9d4edd"));
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Periodicidade
        HBox periodoBox = criarLinhaDetalhe("Periodicidade", lancamento.getPeriodicidade().toString());
        
        // Próxima data
        HBox proximaBox = criarLinhaDetalhe("Próxima Data", sdf.format(lancamento.getProximaData()));
        
        // Número de ocorrências
        HBox ocorrenciasBox = criarLinhaDetalhe("Ocorrências", String.valueOf(lancamento.getNumeroOcorrencias()));
        
        detalhes.getChildren().addAll(descBox, valorBox, periodoBox, proximaBox, ocorrenciasBox);
        
        // Categoria (se houver transação associada)
        if (lancamento.getTransacao() != null && lancamento.getTransacao().getCategoria() != null) {
            HBox catBox = criarLinhaDetalhe("Categoria", lancamento.getTransacao().getCategoria().getNome());
            detalhes.getChildren().add(catBox);
        }
        
        // Conta
        HBox contaBox = criarLinhaDetalhe("Conta", lancamento.getConta().getNome());
        detalhes.getChildren().add(contaBox);
        
        Button voltarBtn = criarBotaoPrimario("Fechar");
        voltarBtn.setPrefWidth(150);
        voltarBtn.setOnAction(e -> dialogStage.close());
        
        HBox btnBox = new HBox(voltarBtn);
        btnBox.setAlignment(Pos.CENTER);
        
        dialog.getChildren().addAll(header, separador, detalhes, btnBox);
        
        Scene dialogScene = new Scene(dialog, 550, 600);
        dialogScene.setFill(Color.TRANSPARENT);
        
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }
    
    private HBox criarLinhaDetalhe(String label, String valor) {
        HBox linha = new HBox(15);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setPadding(new Insets(8, 15, 8, 15));
        linha.setStyle(
            "-fx-background-color: #0a0a0a;" +
            "-fx-background-radius: 8;"
        );
        
        Label lblLabel = new Label(label);
        lblLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblLabel.setTextFill(Color.web("#c77dff"));
        lblLabel.setMinWidth(100);
        
        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("System", FontWeight.NORMAL, 15));
        lblValor.setTextFill(Color.WHITE);
        lblValor.setWrapText(true);
        
        linha.getChildren().addAll(lblLabel, lblValor);
        return linha;
    }
    
    private void mostrarDialogNovaTransacao() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        
        VBox dialog = new VBox(20);
        dialog.setPadding(new Insets(30));
        dialog.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #7b2cbf;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(123, 44, 191, 0.6), 25, 0, 0, 0);"
        );
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Nova Transação");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = criarBotaoFecharDialog(dialogStage);
        
        headerBox.getChildren().addAll(titulo, spacer, closeBtn);
        
        TextField descricaoField = criarTextField("Descrição");
        TextField valorField = criarTextField("Valor");
        DatePicker dataPicker = criarCampoData("Data da Transação");
        
        // Botão para anexar comprovante
        final File[] arquivoComprovante = {null};
        Label comprovanteLabel = new Label("📎 Nenhum arquivo anexado");
        comprovanteLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        comprovanteLabel.setTextFill(Color.web("#9d4edd"));
        
        Button anexarBtn = criarBotaoSecundario("📄 Anexar Comprovante");
        anexarBtn.setOnAction(ev -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar Comprovante");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf")
            );
            File arquivo = fileChooser.showOpenDialog(dialogStage);
            if (arquivo != null) {
                arquivoComprovante[0] = arquivo;
                comprovanteLabel.setText("📎 " + arquivo.getName());
                comprovanteLabel.setTextFill(Color.web("#10b981"));
            }
        });
        
        VBox comprovanteBox = new VBox(5);
        comprovanteBox.getChildren().addAll(anexarBtn, comprovanteLabel);
        
        ComboBox<TiposTransacao> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll(TiposTransacao.ENTRADA, TiposTransacao.SAIDA);
        tipoCombo.setPromptText("Tipo");
        tipoCombo.setMaxWidth(Double.MAX_VALUE);
        tipoCombo.getStyleClass().add("combo-box");
        estilizarComboBoxGenerico(tipoCombo);
        
        // Configurar conversor para exibir o nome formatado
        tipoCombo.setConverter(new StringConverter<TiposTransacao>() {
            @Override
            public String toString(TiposTransacao tipo) {
                if (tipo == null) return "";
                return tipo == TiposTransacao.ENTRADA ? "Entrada" : "Saída";
            }
            
            @Override
            public TiposTransacao fromString(String string) {
                return "Entrada".equals(string) ? TiposTransacao.ENTRADA : TiposTransacao.SAIDA;
            }
        });
        
        ComboBox<Categoria> categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Categoria (opcional)");
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);
        categoriaCombo.getStyleClass().add("combo-box");
        estilizarComboBoxGenerico(categoriaCombo);
        
        // Configurar conversor para exibir o nome
        categoriaCombo.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getNome() : "";
            }
            
            @Override
            public Categoria fromString(String string) {
                return categoriaCombo.getItems().stream()
                    .filter(cat -> cat.getNome().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        
        // Carregar categorias existentes
        try {
            List<Categoria> categorias = controladorCategoria.buscarCategoriasAtivas();
            categoriaCombo.getItems().addAll(categorias);
        } catch (Exception ex) {
            System.err.println("Erro ao carregar categorias: " + ex.getMessage());
        }
        
        ComboBox<MetaEconomica> metaCombo = new ComboBox<>();
        metaCombo.setPromptText("Meta (opcional)");
        metaCombo.setMaxWidth(Double.MAX_VALUE);
        metaCombo.getStyleClass().add("combo-box");
        estilizarComboBoxGenerico(metaCombo);
        
        // Configurar conversor para exibir o nome
        metaCombo.setConverter(new StringConverter<MetaEconomica>() {
            @Override
            public String toString(MetaEconomica meta) {
                return meta != null ? meta.getNome() : "";
            }
            
            @Override
            public MetaEconomica fromString(String string) {
                return metaCombo.getItems().stream()
                    .filter(m -> m.getNome().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        
        // Carregar metas do usuário (somente ativas)
        List<MetaEconomica> metas = controladorMeta.buscarPorUsuario(usuarioLogado);
        metaCombo.getItems().clear(); // Limpar antes de adicionar
        for (MetaEconomica meta : metas) {
            // Só adicionar se a meta ainda não foi concluída
            if (meta.getValorEconomizadoAtual() < meta.getValor()) {
                metaCombo.getItems().add(meta);
            }
        }
        
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelarBtn = criarBotaoSecundario("Cancelar");
        cancelarBtn.setOnAction(e -> dialogStage.close());
        
        Button salvarBtn = criarBotaoPrimario("Criar Transação");
        salvarBtn.setOnAction(e -> {
            try {
                String descricao = descricaoField.getText().trim();
                String valorStr = valorField.getText().trim();
                TiposTransacao tipoTrans = tipoCombo.getValue();
                Categoria categoriaTrans = categoriaCombo.getValue();
                
                if (descricao.isEmpty() || valorStr.isEmpty() || tipoTrans == null) {
                    Snackbar.show(rootContainer, "Preencha os campos obrigatórios", Snackbar.Type.WARNING);
                    return;
                }
                
                float valor = Float.parseFloat(valorStr);
                
                // Obter meta se selecionada
                MetaEconomica metaTrans = metaCombo.getValue();
                
                // Ler arquivo de comprovante se foi anexado
                byte[] comprovanteBytes = null;
                if (arquivoComprovante[0] != null) {
                    try {
                        PDFUtil pdfUtil = new PDFUtil();
                        comprovanteBytes = pdfUtil.importarPDF(arquivoComprovante[0].getAbsolutePath());
                    } catch (Exception ex) {
                        System.err.println("Erro ao importar comprovante: " + ex.getMessage());
                    }
                }
                
                // Converter String para Date
                Date dataTransacaoNova;
                try {
                    dataTransacaoNova = parseDataPicker(dataPicker);
                } catch (Exception ex) {
                    Snackbar.show(rootContainer, "Data inválida! Selecione uma data", Snackbar.Type.ERROR);
                    return;
                }
                
                controladorTransacao.criarTransacao(
                    dataTransacaoNova,
                    valor,
                    categoriaTrans,
                    descricao,
                    comprovanteBytes,
                    tipoTrans,
                    contaSelecionada,
                    metaTrans
                );
                controladorTransacao.salvarAoEncerrar();
                
                // O backend já contribui para a meta automaticamente se ela existir
                
                Snackbar.show(rootContainer, "Transação criada com sucesso!", Snackbar.Type.SUCCESS);
                dialogStage.close();
                mostrarTransacoes();
                
            } catch (NumberFormatException ex) {
                Snackbar.show(rootContainer, "Valor inválido", Snackbar.Type.ERROR);
            } catch (Exception ex) {
                Snackbar.show(rootContainer, "Erro ao salvar: " + ex.getMessage(), Snackbar.Type.ERROR);
            }
        });
        
        botoesBox.getChildren().addAll(cancelarBtn, salvarBtn);
        
        dialog.getChildren().addAll(headerBox, descricaoField, valorField, dataPicker, tipoCombo, categoriaCombo, metaCombo, comprovanteBox, botoesBox);
        
        Scene dialogScene = new Scene(dialog, 450, 650);
        dialogScene.setFill(Color.TRANSPARENT);
        
        // Aplicar CSS para estilizar os ComboBox
        try {
            String css = getClass().getResource("/styles/combobox-dark.css").toExternalForm();
            dialogScene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Erro ao carregar CSS: " + e.getMessage());
        }
        
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }
    
    private void excluirTransacao(Transacao transacao) {
        try {
            controladorTransacao.excluirTransacao(transacao);
            controladorTransacao.salvarAoEncerrar();
            Snackbar.show(rootContainer, "Transação excluída com sucesso!", Snackbar.Type.SUCCESS);
            mostrarTransacoes();
        } catch (Exception e) {
            Snackbar.show(rootContainer, "Erro ao excluir: " + e.getMessage(), Snackbar.Type.ERROR);
        }
    }
    
    private void gerarExtratoPDF() {
        if (contaSelecionada == null) {
            Snackbar.show(rootContainer, "Selecione uma conta primeiro", Snackbar.Type.WARNING);
            return;
        }
        
        try {
            // FileChooser para salvar o PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Extrato PDF");
            fileChooser.setInitialFileName("extrato_" + contaSelecionada.getNome() + "_" + 
                new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf")
            );
            
            File arquivo = fileChooser.showSaveDialog(stage);
            
            if (arquivo != null) {
                // Chama o backend que faz toda a lógica
                boolean sucesso = controladorTransacao.gerarExtrato(
                    contaSelecionada, null, null, null, arquivo.getAbsolutePath()
                );
                
                if (sucesso) {
                    Snackbar.show(rootContainer, "Extrato PDF gerado com sucesso!", Snackbar.Type.SUCCESS);
                } else {
                    Snackbar.show(rootContainer, "Erro ao gerar PDF", Snackbar.Type.ERROR);
                }
            }
        } catch (Exception e) {
            Snackbar.show(rootContainer, "Erro ao gerar extrato: " + e.getMessage(), Snackbar.Type.ERROR);
        }
    }

    private Button criarBotaoPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setPadding(new Insets(12, 24, 12, 24));
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: #7b2cbf;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 0;"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #9d4edd;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 0;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #7b2cbf;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 0;"
        ));
        
        return btn;
    }

    private void logout() {
        try {
            // Salvar todos os dados antes de fazer logout
            controladorUsuario.salvarAoEncerrar();
            controladorConta.salvarAoEncerrar();
            controladorTransacao.salvarAoEncerrar();
            
            // Voltar para tela de login (nova instância com dados recarregados)
            TelaLogin telaLogin = new TelaLogin(stage);
            telaLogin.show();
            Snackbar.show(rootContainer, "Logout realizado com sucesso!", Snackbar.Type.SUCCESS);
        } catch (IOException e) {
            Snackbar.show(rootContainer, "Erro ao fazer logout: " + e.getMessage(), Snackbar.Type.ERROR);
        }
    }
}
