package com.mycompany.gerenciador.financeiro.view;

import com.mycompany.gerenciador.financeiro.controller.ContaController;
import com.mycompany.gerenciador.financeiro.model.Conta;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Tela para listar contas financeiras cadastradas.
 * Implementa o caso de uso CE1.2 - Visualizar Conta Financeira.
 * 
 * @author Laís Isabella
 */
public class TelaListarContas extends javax.swing.JFrame {
    
    private ContaController controller;
    private DefaultTableModel modeloTabela;

    /**
     * Creates new form TelaListarContas
     */
    public TelaListarContas() {
        initComponents();
        this.controller = new ContaController();
        configurarTela();
        configurarTabela();
        carregarContas();
    }
    
    /**
     * Configurações adicionais da tela
     */
    private void configurarTela() {
        setTitle("Visualizar Contas Financeiras");
        setLocationRelativeTo(null);
    }

    /**
     * Configura o modelo da tabela com as colunas
     */
    private void configurarTabela() {
        modeloTabela = new DefaultTableModel(
            new Object[][] {},
            new String[] { "ID", "Nome", "Tipo", "Saldo Inicial", "Moeda" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblContas.setModel(modeloTabela);
    }
    
    /**
     * Carrega as contas do arquivo e preenche a tabela
     */
    private void carregarContas() {
        try {
            modeloTabela.setRowCount(0);

            List<Conta> contas = controller.listarContas();

            for (Conta conta : contas) {
                Object[] linha = {
                    conta.getId(),
                    conta.getNome(),
                    conta.getTipo(),
                    String.format("R$ %.2f", conta.getSaldoInicial()),
                    conta.getMoeda()
                };
                modeloTabela.addRow(linha);
            }

            if (contas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma conta cadastrada ainda.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblContas = new javax.swing.JTable();
        btnAtualizar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Contas Cadastradas");

        tblContas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {}
        ));
        jScrollPane1.setViewportView(tblContas);

        btnAtualizar.setText("Atualizar Lista");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                        .addComponent(btnFechar))
                    .addComponent(btnAtualizar)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnFechar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAtualizar)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>                        

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        carregarContas();
    }                                            

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dispose();
    }                                         

    public static void main(String args[]) {
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
            new TelaListarContas().setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblContas;
    // End of variables declaration                   
}