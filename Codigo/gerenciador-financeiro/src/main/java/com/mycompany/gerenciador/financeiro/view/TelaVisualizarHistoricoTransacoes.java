/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.view;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.controller.CategoriaController;
import com.mycompany.gerenciador.financeiro.controller.TransacaoController;
import com.mycompany.gerenciador.financeiro.model.Categoria;
import com.mycompany.gerenciador.financeiro.model.Conta;
import com.mycompany.gerenciador.financeiro.model.TiposTransacao;
import com.mycompany.gerenciador.financeiro.model.Transacao;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Laís Isabella
 */
public class TelaVisualizarHistoricoTransacoes extends javax.swing.JFrame {

    private TransacaoController controller;
    private ContaController contaController;
    private DefaultTableModel modeloTabela;
    private Usuario usuarioLogado;
    private List<Conta> contasUsuario;
    private CategoriaController categoriaController;
    private List<Categoria> categoriasDisponiveis;

    /**
     * Creates new form TelaVisualizarHistoricoTransacoes
     */
    public TelaVisualizarHistoricoTransacoes(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        initComponents();
        try {
            this.controller = new TransacaoController();
            this.contaController = new ContaController();
            this.categoriaController = new CategoriaController();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao inicializar: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        configurarTela();
        configurarTabela();
        carregarContas();
        carregarCategorias();
        btnBuscar.addActionListener(evt -> buscarTransacoes());
        btnLimparFiltros.addActionListener(evt -> btnLimparFiltrosActionPerformed(evt));
        btnEditar.addActionListener(evt -> btnEditarActionPerformed(evt));
        btnExcluir.addActionListener(evt -> btnExcluirActionPerformed(evt));

// Carrega transações automaticamente ao abrir
        buscarTransacoes();
    }

    private void configurarTela() {
        setTitle("Visualizar Histórico de Transações");
        setLocationRelativeTo(null);

        // Limpa data inicialmente
        txtData.setText("");
    }

    private void configurarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Data", "Tipo", "Categoria", "Descrição", "Valor", "Conta"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTransacoes.setModel(modeloTabela);
    }

    private void carregarCategorias() {
        try {
            categoriasDisponiveis = categoriaController.buscarCategoriasAtivas();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Todas as categorias");
            for (Categoria categoria : categoriasDisponiveis) {
                model.addElement(categoria.getNome());
            }
            cbFiltrarCategoria.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage());
        }
    }

    private void carregarContas() {
        try {
            contasUsuario = contaController.buscarContasUsuario(usuarioLogado);

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Todas as contas");
            for (Conta conta : contasUsuario) {
                model.addElement(conta.getNome());
            }
            cbFiltrarConta.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contas: " + e.getMessage());
        }
    }

    private void buscarTransacoes() {
        try {
            modeloTabela.setRowCount(0);

            if (controller == null) {
                JOptionPane.showMessageDialog(this, "Controlador não inicializado.");
                return;
            }

            Conta conta = null;
            Date data = null;
            Categoria categoria = null;
            TiposTransacao tipo = null;

            // Filtro por conta
            int indiceConta = cbFiltrarConta.getSelectedIndex();
            if (indiceConta > 0) {
                conta = contasUsuario.get(indiceConta - 1);
            }

            // Filtro por data
            String textoData = txtData.getText().trim();
            if (!textoData.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    data = sdf.parse(textoData);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/MM/yyyy");
                    return;
                }
            }

            // ✅ NOVO: Filtro por categoria do ComboBox
            int indiceCategoria = cbFiltrarCategoria.getSelectedIndex();
            if (indiceCategoria > 0) {
                categoria = categoriasDisponiveis.get(indiceCategoria - 1);
            }

            // Filtro por tipo
            int indiceTipo = cbFiltrarTipo.getSelectedIndex();
            if (indiceTipo == 1) {
                tipo = TiposTransacao.ENTRADA;
            } else if (indiceTipo == 2) {
                tipo = TiposTransacao.SAIDA;
            }

            // ✅ Passa usuarioLogado
            List<Transacao> transacoes = controller.buscarTransacoesFiltradas(
                    usuarioLogado, conta, data, categoria, tipo
            );

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (Transacao t : transacoes) {
                Object[] linha = {
                    sdf.format(t.getData()),
                    t.getTipo(),
                    t.getCategoria().getNome(),
                    t.getDescricao(),
                    String.format("R$ %.2f", t.getValor()),
                    t.getConta().getNome()
                };
                modeloTabela.addRow(linha);
            }

            lblTotal.setText("Total de transações: " + transacoes.size());

            if (transacoes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma transação encontrada com os filtros aplicados.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar transações: " + e.getMessage());
        }
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = tblTransacoes.getSelectedRow();

        if (linha >= 0) {
            try {
                // ✅ Passa usuarioLogado
                List<Transacao> todasTransacoes = controller.buscarTransacoesFiltradas(
                        usuarioLogado, null, null, null, null
                );

                if (linha < todasTransacoes.size()) {
                    Transacao transacaoSelecionada = todasTransacoes.get(linha);
                    new TelaEditarTransacao(transacaoSelecionada).setVisible(true);
                    buscarTransacoes();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma transação para editar.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransacoes = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cbFiltrarConta = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbFiltrarTipo = new javax.swing.JComboBox<>();
        btnLimparFiltros = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        cbFiltrarCategoria = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblTransacoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblTransacoes);

        jLabel1.setText("Contas");

        cbFiltrarConta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Data");

        jLabel3.setText("Categoria");

        jLabel4.setText("Tipo");

        cbFiltrarTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ENTRADA", "SAÍDA" }));

        btnLimparFiltros.setText("Limpar Filtros");

        btnBuscar.setBackground(new java.awt.Color(204, 204, 255));
        btnBuscar.setForeground(new java.awt.Color(51, 51, 51));
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel5.setText("Vizualizar o historico de transações");

        lblTotal.setText("0");

        btnEditar.setText("Editar uma transação");

        btnExcluir.setText("Excluir transação");

        cbFiltrarCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimparFiltros))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnExcluir)
                                .addGap(28, 28, 28)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnEditar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(75, 75, 75)
                                    .addComponent(jLabel2)
                                    .addGap(69, 69, 69)
                                    .addComponent(jLabel3)
                                    .addGap(61, 61, 61)
                                    .addComponent(jLabel4))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cbFiltrarConta, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(cbFiltrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbFiltrarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(40, 40, 40)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(btnLimparFiltros))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbFiltrarConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbFiltrarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbFiltrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(btnExcluir))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {
    int linha = tblTransacoes.getSelectedRow();

    if (linha >= 0) {
        try {
            String dataStr = (String) tblTransacoes.getValueAt(linha, 0);
            Object tipoObj = tblTransacoes.getValueAt(linha, 1);
            String tipoStr = tipoObj.toString(); // Converte para String
            String categoriaStr = (String) tblTransacoes.getValueAt(linha, 2);
            String descricaoStr = (String) tblTransacoes.getValueAt(linha, 3);
            String valorStr = (String) tblTransacoes.getValueAt(linha, 4);
            String contaStr = (String) tblTransacoes.getValueAt(linha, 5);

            int opcao = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente excluir esta transação?\n\n"
                    + "Data: " + dataStr + "\n"
                    + "Tipo: " + tipoStr + "\n"
                    + "Categoria: " + categoriaStr + "\n"
                    + "Valor: " + valorStr,
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (opcao == JOptionPane.YES_OPTION) {
                List<Transacao> todasTransacoes = controller.buscarTransacoesFiltradas(
                        usuarioLogado, null, null, null, null
                );

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Transacao transacaoParaExcluir = null;

                for (Transacao t : todasTransacoes) {
                    if (sdf.format(t.getData()).equals(dataStr)
                            && t.getTipo().toString().equals(tipoStr)
                            && t.getCategoria().getNome().equals(categoriaStr)
                            && t.getDescricao().equals(descricaoStr)
                            && t.getConta().getNome().equals(contaStr)) {
                        transacaoParaExcluir = t;
                        break;
                    }
                }

                if (transacaoParaExcluir != null) {
                    boolean sucesso = controller.excluirTransacao(transacaoParaExcluir);

                    if (sucesso) {
                        JOptionPane.showMessageDialog(this, "Transação excluída com sucesso!");
                        buscarTransacoes();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Transação não encontrada.");
                }
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir transação: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Selecione uma transação para excluir.");
    }
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaVisualizarHistoricoTransacoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaVisualizarHistoricoTransacoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaVisualizarHistoricoTransacoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaVisualizarHistoricoTransacoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Usa look and feel padrão
        }

        java.awt.EventQueue.invokeLater(() -> {
            Usuario usuarioTeste = new Usuario("Teste", "teste@email.com", "123456");
            new TelaVisualizarHistoricoTransacoes(usuarioTeste).setVisible(true);
        });
    }

    private void btnLimparFiltrosActionPerformed(java.awt.event.ActionEvent evt) {
        cbFiltrarConta.setSelectedIndex(0);
        txtData.setText("");
        cbFiltrarCategoria.setSelectedIndex(0); // ✅ ComboBox
        cbFiltrarTipo.setSelectedIndex(0);
        buscarTransacoes();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnLimparFiltros;
    private javax.swing.JComboBox<String> cbFiltrarCategoria;
    private javax.swing.JComboBox<String> cbFiltrarConta;
    private javax.swing.JComboBox<String> cbFiltrarTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblTransacoes;
    private javax.swing.JTextField txtData;
    // End of variables declaration//GEN-END:variables
}
