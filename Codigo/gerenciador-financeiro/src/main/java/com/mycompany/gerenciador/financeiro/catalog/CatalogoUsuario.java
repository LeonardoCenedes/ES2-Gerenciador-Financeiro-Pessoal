package com.mycompany.gerenciador.financeiro.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciador.financeiro.model.Usuario;
import com.mycompany.gerenciador.financeiro.repository.UsuarioRepositoryTxt;

/**
 * Catálogo de usuários do sistema
 * Padrão In-Memory Cache: mantém dados em memória, repository apenas para I/O
 * 
 * @author Laís Isabella
 */
public class CatalogoUsuario {
    
    private final List<Usuario> usuarios;
    private final UsuarioRepositoryTxt repositorio;

    /**
     * Construtor padrão - cria Repository e carrega dados
     * Cadeia de construção OO: Catalog cria Repository
     */
    public CatalogoUsuario() throws IOException {
        this.repositorio = new UsuarioRepositoryTxt();
        this.usuarios = new ArrayList<>(repositorio.carregarTodos());
    }

    /**
     * Construtor com injeção para testes - permite mockar o repository
     * Para testes unitários com mocks
     */
    public CatalogoUsuario(UsuarioRepositoryTxt repositorio, List<Usuario> usuariosIniciais) {
        this.repositorio = repositorio;
        this.usuarios = new ArrayList<>(usuariosIniciais);
    }

    /**
     * Implementa 1.1: criar(usuario:Usuario) : boolean do diagrama
     * Opera APENAS em memória, não salva no arquivo
     */
    public boolean criar(Usuario usuario) {
        // Verifica se email já existe
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return false;
            }
        }
        
        usuarios.add(usuario);
        return true;
    }

    /**
     * Lista todos os usuários da memória
     */
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    /**
     * Busca um usuário pelo email na memória
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
     * Persiste todos os usuários da memória para o arquivo
     * Deve ser chamado ao encerrar a aplicação (quando View fechar)
     */
    public void salvarAoEncerrar() throws IOException {
        repositorio.salvarTodos(usuarios);
    }
}