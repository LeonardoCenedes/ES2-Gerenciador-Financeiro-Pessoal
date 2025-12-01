package com.mycompany.gerenciador.financeiro.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.gerenciador.financeiro.catalog.CatalogoCategoria;
import com.mycompany.gerenciador.financeiro.model.Categoria;

/**
 * Testes UNITÁRIOS com MOCKS - CategoriaControlador
 * Isola o controlador testando apenas sua lógica, sem executar código real do Catalog
 * Usa injeção de dependência via construtor para passar mocks
 */
@ExtendWith(MockitoExtension.class)
class CategoriaControladorMockUnitTest {

    @Mock
    private CatalogoCategoria catalogoMock;

    private ControladorCategoria controlador;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        controlador = new ControladorCategoria(catalogoMock); // Injeção do mock
    }

    /**
     * Teste unitário mockado - Criar categoria com sucesso
     * Verifica que o controlador chama o catálogo corretamente
     */
    @Test
    void deveCriarCategoriaComSucesso_Mock() throws IOException {
        // Arrange: Mock retorna true quando salvar é chamado
        when(catalogoMock.salvar(any(Categoria.class))).thenReturn(true);
        
        // Act: Chamar método do controlador
        boolean resultado = controlador.criarCategoria("Alimentação", false, true);
        
        // Assert: Verificar resultado e que mock foi chamado
        assertThat(resultado).isTrue();
        verify(catalogoMock, times(1)).salvar(any(Categoria.class));
    }

    /**
     * Teste unitário mockado - Validação de nome vazio
     * Testa que o controlador NÃO chama o catálogo quando validação falha
     */
    @Test
    void naoDeveCriarCategoriaComNomeVazio_Mock() throws IOException {
        // Act: Tentar criar categoria com nome vazio
        boolean resultado = controlador.criarCategoria("", false, true);
        
        // Assert: Validação falha, catálogo NÃO é chamado
        assertThat(resultado).isFalse();
        verify(catalogoMock, never()).salvar(any(Categoria.class));
    }

    /**
     * Teste unitário mockado - Listar categorias
     * Mock retorna lista fixa, não busca do arquivo
     */
    @Test
    void deveListarCategorias_Mock() {
        // Arrange: Mock retorna lista predefinida
        Categoria cat1 = new Categoria("Alimentação", false, true);
        Categoria cat2 = new Categoria("Transporte", false, true);
        List<Categoria> categoriasSimuladas = Arrays.asList(cat1, cat2);
        
        when(catalogoMock.listarTodas()).thenReturn(categoriasSimuladas);
        
        // Act: Buscar categorias
        List<Categoria> resultado = controlador.buscarCategorias();
        
        // Assert: Retorna a lista mockada, não do arquivo
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(cat1, cat2);
        verify(catalogoMock, times(1)).listarTodas();
    }

    /**
     * Teste unitário mockado - Listar apenas ativas
     * Verifica que a lógica de filtragem do controlador funciona
     */
    @Test
    void deveListarApenasAtivas_Mock() {
        // Arrange: Mock retorna lista mista (ativas e inativas)
        Categoria ativa = new Categoria("Ativa", false, true);
        Categoria inativa = new Categoria("Inativa", false, false);
        List<Categoria> todasCategorias = Arrays.asList(ativa, inativa);
        
        when(catalogoMock.listarTodas()).thenReturn(todasCategorias);
        
        // Act: Buscar apenas ativas
        List<Categoria> ativas = controlador.buscarCategoriasAtivas();
        
        // Assert: Controlador filtra corretamente, retorna só ativa
        assertThat(ativas).hasSize(1);
        assertThat(ativas).contains(ativa);
        assertThat(ativas).doesNotContain(inativa);
    }

    /**
     * Teste unitário mockado - Desativar categoria padrão
     * Verifica que regra de negócio é respeitada
     */
    @Test
    void naoDeveDesativarCategoriaPadrao_Mock() throws IOException {
        // Arrange: Mock retorna false para categoria padrão
        Categoria categoriaPadrao = new Categoria("Padrão", true, true);
        when(catalogoMock.desativar(any(Categoria.class))).thenReturn(false);
        
        // Act: Tentar desativar categoria padrão
        boolean resultado = controlador.desativarCategoria(categoriaPadrao);
        
        // Assert: Operação falha, regra de negócio respeitada
        assertThat(resultado).isFalse();
        verify(catalogoMock, times(1)).desativar(categoriaPadrao);
    }

    /**
     * Teste unitário mockado - Simular erro do catálogo
     * Testa como controlador lida com exceções
     */
    @Test
    void deveTratarErroAoSalvar_Mock() throws IOException {
        // Arrange: Mock lança exceção
        when(catalogoMock.salvar(any(Categoria.class)))
            .thenThrow(new IOException("Erro ao salvar"));
        
        // Act & Assert: Controlador propaga exceção
        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            controlador.criarCategoria("Teste", false, true);
        });
        
        assertThat(exception.getMessage()).contains("Erro ao salvar");
        verify(catalogoMock, times(1)).salvar(any(Categoria.class));
    }
}
