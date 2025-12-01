package com.mycompany.gerenciador.financeiro.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Repositório para persistência de usuários em arquivo texto
 * Padrão In-Memory Cache: carrega tudo na inicialização, salva tudo ao encerrar
 * 
 * @author Laís Isabella
 */
public class UsuarioRepositoryTxt implements Repository<Usuario> {
    
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
     * Carrega todos os usuários do arquivo para memória
     */
    @Override
    public List<Usuario> carregarTodos() throws IOException {
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
     * Salva todos os usuários da memória para o arquivo
     * Sobrescreve o arquivo completamente
     */
    @Override
    public void salvarTodos(List<Usuario> usuarios) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Usuario usuario : usuarios) {
                String linha = formatarUsuarioParaLinha(usuario);
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    private String formatarUsuarioParaLinha(Usuario usuario) {
        return usuario.getNome() + SEPARADOR
                + usuario.getEmail() + SEPARADOR
                + usuario.getSenha();
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