package pokedex.interfaces;

/**
 * Contrato para qualquer elemento que possa ser localizado através
 * de uma busca textual (ex: pesquisa por nome de Pokémon)
 */
public interface Buscavel {
    boolean correspondeATermo(String termo);
}
