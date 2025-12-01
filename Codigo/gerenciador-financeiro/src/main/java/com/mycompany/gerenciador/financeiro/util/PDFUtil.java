/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gerenciador.financeiro.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.itextpdf.html2pdf.HtmlConverter;
import com.mycompany.gerenciador.financeiro.model.Transacao;

/**
 * Utilitário para geração de PDFs
 * @author Laís Isabella
 */
public class PDFUtil {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Gera um PDF com extrato de transações
     * @param transacoes Lista de transações para o extrato
     * @param caminho Caminho onde salvar o PDF
     * @return true se gerado com sucesso
     */
    public boolean gerarPDF(List<Transacao> transacoes, String caminho) {
        try {
            String html = gerarHTMLExtrato(transacoes);
            HtmlConverter.convertToPdf(html, new FileOutputStream(caminho));
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gera o HTML do extrato com estilo
     */
    private String gerarHTMLExtrato(List<Transacao> transacoes) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'/>");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { font-family: 'System', 'Segoe UI', Arial, sans-serif; margin: 0; padding: 40px; background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%); color: #ffffff; min-height: 100vh; }");
        html.append("h1 { color: #c77dff; text-align: center; border-bottom: 3px solid #7b2cbf; padding-bottom: 15px; margin-bottom: 10px; }");
        html.append(".subtitle { text-align: center; color: #9d4edd; margin-bottom: 30px; font-size: 14px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #1a1a1a; border-radius: 10px; overflow: hidden; }");
        html.append("th { background: linear-gradient(135deg, #7b2cbf 0%, #9d4edd 100%); color: white; padding: 15px; text-align: left; font-weight: bold; }");
        html.append("td { padding: 12px 15px; border-bottom: 1px solid #3a3a3a; color: #e0e0e0; }");
        html.append("tr:nth-child(even) { background-color: #0a0a0a; }");
        html.append("tr:hover { background-color: #2a1a3f; }");
        html.append(".entrada { color: #10b981; font-weight: bold; }");
        html.append(".saida { color: #ff006e; font-weight: bold; }");
        html.append(".total { font-size: 18px; font-weight: bold; margin-top: 30px; padding: 20px; background-color: #1a1a1a; border-radius: 10px; border: 2px solid #7b2cbf; }");
        html.append(".total p { margin: 10px 0; }");
        html.append(".rodape { margin-top: 50px; text-align: center; color: #9d4edd; font-size: 12px; padding: 20px; border-top: 2px solid #7b2cbf; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        html.append("<h1>📊 Extrato de Transações</h1>");
        html.append("<p class='subtitle'>Gerado em: ")
            .append(DATE_FORMAT.format(new java.util.Date()))
            .append("</p>");
        
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Data</th>");
        html.append("<th>Descrição</th>");
        html.append("<th>Categoria</th>");
        html.append("<th>Tipo</th>");
        html.append("<th>Valor</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        
        float totalEntradas = 0.0f;
        float totalSaidas = 0.0f;
        
        for (Transacao t : transacoes) {
            html.append("<tr>");
            html.append("<td>").append(DATE_FORMAT.format(t.getData())).append("</td>");
            html.append("<td>").append(t.getDescricao() != null ? t.getDescricao() : "-").append("</td>");
            html.append("<td>").append(t.getCategoria().getNome()).append("</td>");
            html.append("<td>").append(t.getTipo().name()).append("</td>");
            
            String valorClass = t.getTipo().name().equals("ENTRADA") ? "entrada" : "saida";
            String sinal = t.getTipo().name().equals("ENTRADA") ? "+" : "-";
            html.append("<td class='").append(valorClass).append("'>")
                .append(sinal).append(" R$ ")
                .append(String.format("%.2f", t.getValor()))
                .append("</td>");
            
            if (t.getTipo().name().equals("ENTRADA")) {
                totalEntradas += t.getValor();
            } else {
                totalSaidas += t.getValor();
            }
            
            html.append("</tr>");
        }
        
        html.append("</tbody>");
        html.append("</table>");
        
        html.append("<div class='total'>");
        html.append("<p>Total de Entradas: <span class='entrada'>R$ ")
            .append(String.format("%.2f", totalEntradas)).append("</span></p>");
        html.append("<p>Total de Saídas: <span class='saida'>R$ ")
            .append(String.format("%.2f", totalSaidas)).append("</span></p>");
        html.append("<p>Saldo: <span style='color: ")
            .append(totalEntradas >= totalSaidas ? "#27ae60" : "#e74c3c")
            .append("'>R$ ")
            .append(String.format("%.2f", totalEntradas - totalSaidas))
            .append("</span></p>");
        html.append("</div>");
        
        html.append("<div class='rodape'>");
        html.append("<p>Gerenciador Financeiro Pessoal</p>");
        html.append("<p>Este documento é um extrato gerado automaticamente.</p>");
        html.append("</div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * CE18 - Importa um PDF e retorna como bytes
     * @param caminho Caminho do arquivo PDF a ser importado
     * @return bytes do PDF
     */
    public byte[] importarPDF(String caminho) throws IOException {
        java.io.File file = new java.io.File(caminho);
        
        if (!file.exists()) {
            throw new IOException("Arquivo não encontrado: " + caminho);
        }
        
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return bytes;
        }
    }
}
