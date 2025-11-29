package com.mycompany.gerenciador.financeiro.catalog;

import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.UsuarioRepositoryTxt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo de usuários do sistema
 * 
 * @author Laís Isabella
 */
public class CatalogoUsuario {
    
    private List<Usuario> usuarios;
    private UsuarioRepositoryTxt repositorio;

    public CatalogoUsuario() throws IOException {
        this.repositorio = new UsuarioRepositoryTxt();
        this.usuarios = new ArrayList<>(repositorio.listar());
    }

    /**
     * Implementa 1.1: criar(usuario:Usuario) : boolean do diagrama
     */
    public boolean criar(Usuario usuario) throws IOException {
        // Verifica se email já existe
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return false;
            }
        }
        
        usuarios.add(usuario);
        repositorio.salvar(usuario);  // ← SALVA NO ARQUIVO
        return true;
    }

    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    /**
     * Busca um usuário pelo email
     */
    public Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }
    
    /**
     * Implementa 1.1: autenticar(email:String, senha:String) : Usuario
     * Conforme o diagrama de colaboração
     */
    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Atualiza lista local lendo novamente o arquivo
     */
    public void recarregar() throws IOException {
        this.usuarios = new ArrayList<>(repositorio.listar());
    }
}