package com.mycompany.gerenciador.financeiro.controller;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoUsuario;
import com.mycompany.gerenciador.financeiro.model.Usuario;
import java.io.IOException;

/**
 * Controlador para operações com usuários
 * 
 * @author Laís Isabella
 */
public class UsuarioController {
    
    private CatalogoUsuario catalogo;

    public UsuarioController() throws IOException {
        this.catalogo = new CatalogoUsuario();
    }

    /**
     * Implementa: 1: criarUsuario(nome:String, email:String, senha:String) : boolean
     * Conforme o diagrama de colaboração
     */
    public boolean criarUsuario(String nome, String email, String senha) throws IOException {
        // Cria o objeto Usuario
        Usuario novoUsuario = new Usuario(nome, email, senha);
        
        // 1.1: criar(usuario:Usuario) : boolean
        // Delega ao CatalogoUsuario
        return catalogo.criar(novoUsuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return catalogo.buscarPorEmail(email);
    }
    
    /**
     * Implementa: 1: autenticar(email:String, senha:String) : Usuario
     * Conforme o diagrama de colaboração
     */
    public Usuario autenticar(String email, String senha) {
        // 1.1: autenticar(email:String, senha:String) : Usuario
        return catalogo.autenticar(email, senha);
    }
}