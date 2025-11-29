package com.mycompany.gerenciador.financeiro.repository;

import com.mycompany.gerenciador.financeiro.model.Usuario;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório para persistência de usuários em arquivo texto
 * 
 * @author Laís Isabella
 */
public class UsuarioRepositoryTxt {
    
    private static final String DIRETORIO = "data";
    private static final String ARQUIVO = "data/usuarios.txt";
    private static final String SEPARADOR = ";";

    public UsuarioRepositoryTxt() {
        criarDiretorioSeNaoExistir();
    }
    
    private void criarDiretorioSeNaoExistir() {
        File dir = new File(DIRETORIO);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Salva um novo usuário no arquivo (SEM ID)
     */
    public void salvar(Usuario usuario) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
            String linha = formatarUsuarioParaLinha(usuario);
            writer.write(linha);
            writer.newLine();
        }
    }

    private String formatarUsuarioParaLinha(Usuario usuario) {
        return usuario.getNome() + SEPARADOR
                + usuario.getEmail() + SEPARADOR
                + usuario.getSenha();
    }

    /**
     * Lista todos os usuários do arquivo
     */
    public List<Usuario> listar() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return usuarios;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Usuario usuario = parsearLinha(linha);
                    usuarios.add(usuario);
                }
            }
        }

        return usuarios;
    }

    /**
     * Converte uma linha do arquivo em um objeto Usuario
     * Formato: nome;email;senha
     */
    private Usuario parsearLinha(String linha) {
        String[] partes = linha.split(SEPARADOR);

        String nome = partes[0];
        String email = partes[1];
        String senha = partes[2];

        return new Usuario(nome, email, senha);
    }
}