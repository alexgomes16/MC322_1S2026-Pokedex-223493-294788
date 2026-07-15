package pokedex.exceptions;

/**
 * Lançada quando uma busca por um Pokémon (por nome ou número) não
 * encontra nenhum resultado na Pokédex.
 */
public class PokemonNaoEncontradoException extends Exception {
    public PokemonNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
