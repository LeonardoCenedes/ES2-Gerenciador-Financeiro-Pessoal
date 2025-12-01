package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoUsuario;
import com.mycompany.gerenciador.financeiro.model.Usuario;

/**
 * Controlador para operações com usuários
 * Padrão GRASP - Recebe dependências via construtor
 * 
 * @author Laís Isabella
 */
public class ControladorUsuario {
    
    private final CatalogoUsuario catalogo;

    /**
     * Construtor padrão - cria Catalog que cria Repository
     * Cadeia de construção OO: Controller -> Catalog -> Repository
     */
    public ControladorUsuario() throws IOException {
        this.catalogo = new CatalogoUsuario();
    }

    /**
     * Construtor com injeção de dependência para testes
     * Permite injetar um catálogo mockado
     */
    public ControladorUsuario(CatalogoUsuario catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * Implementa: 1: criarUsuario(nome:String, email:String, senha:String) : boolean
     * Conforme o diagrama de colaboração
     */
    public boolean criarUsuario(String nome, String email, String senha) {
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

    /**
     * Salva dados ao encerrar - deve ser chamado pela View ao fechar
     */
    public void salvarAoEncerrar() throws IOException {
        catalogo.salvarAoEncerrar();
    }
}