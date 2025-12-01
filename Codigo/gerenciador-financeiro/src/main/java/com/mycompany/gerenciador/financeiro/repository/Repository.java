package com.mycompany.gerenciador.financeiro.repository;

import java.io.IOException;
import java.util.List;

/**
 * Interface base para repositories com padrão in-memory cache
 * Repositories carregam tudo na inicialização e salvam tudo ao encerrar
 * 
 * @param <T> Tipo da entidade
 * @author Leonardo Cenedes
 */
public interface Repository<T> {
    
    /**
     * Carrega todos os registros do arquivo para memória
     * @return Lista com todos os objetos carregados
     * @throws IOException se houver erro na leitura
     */
    List<T> carregarTodos() throws IOException;
    
    /**
     * Salva todos os registros da memória para o arquivo
     * Sobrescreve o arquivo completamente
     * @param entidades Lista de objetos a serem salvos
     * @throws IOException se houver erro na escrita
     */
    void salvarTodos(List<T> entidades) throws IOException;
}
