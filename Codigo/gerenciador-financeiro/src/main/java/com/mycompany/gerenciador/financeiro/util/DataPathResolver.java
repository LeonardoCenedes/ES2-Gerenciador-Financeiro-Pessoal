package com.mycompany.gerenciador.financeiro.util;

import java.io.File;
import java.nio.file.Paths;

/**
 * Utilitário para resolver o caminho correto da pasta data
 * Funciona tanto em desenvolvimento quanto no executável empacotado
 * 
 * @author GitHub Copilot
 */
public class DataPathResolver {
    
    private static String dataPath = null;
    
    /**
     * Retorna o caminho absoluto para a pasta data
     * Procura a pasta data na seguinte ordem:
     * 1. Na raiz do projeto (3 níveis acima)
     * 2. No diretório atual
     * 3. Cria no diretório atual se não existir
     */
    public static String getDataPath() {
        if (dataPath != null) {
            return dataPath;
        }
        
        // Tenta encontrar a pasta data na raiz do projeto
        File currentDir = new File(System.getProperty("user.dir"));
        
        // Opção 1: Pasta data 3 níveis acima (quando rodando do App)
        File dataDirParent3 = new File(currentDir.getParentFile().getParentFile().getParentFile(), "data");
        if (dataDirParent3.exists() && dataDirParent3.isDirectory()) {
            dataPath = dataDirParent3.getAbsolutePath();
            return dataPath;
        }
        
        // Opção 2: Pasta data no diretório atual (desenvolvimento)
        File dataDirCurrent = new File(currentDir, "data");
        if (dataDirCurrent.exists() && dataDirCurrent.isDirectory()) {
            dataPath = dataDirCurrent.getAbsolutePath();
            return dataPath;
        }
        
        // Opção 3: Cria pasta data no diretório atual
        if (!dataDirCurrent.exists()) {
            dataDirCurrent.mkdir();
        }
        dataPath = dataDirCurrent.getAbsolutePath();
        return dataPath;
    }
    
    /**
     * Retorna o caminho completo para um arquivo na pasta data
     */
    public static String getFilePath(String filename) {
        return Paths.get(getDataPath(), filename).toString();
    }
}
