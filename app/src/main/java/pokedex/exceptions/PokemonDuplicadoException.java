package pokedex.exceptions;

/**
 * Lançada quando se tenta cadastrar um Pokémon com um número de
 * catalogação que já existe na Pokédex.
 */
public class PokemonDuplicadoException extends Exception {
    public PokemonDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
