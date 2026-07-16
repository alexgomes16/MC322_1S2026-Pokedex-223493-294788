package pokedex.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Representa a Pokédex do treinador
 */
public class Pokedex {

    private final List<Pokemon> pokemons = new ArrayList<>();

    public void adicionar(Pokemon pokemon) {
        pokemons.add(pokemon);
    }

    public int proximoNumero() {
        return pokemons.size() + 1;
    }

    public List<Pokemon> listarTodos() {
        return new ArrayList<>(pokemons);
    }

    public Optional<Pokemon> buscarPorNumero(int numero) {
        return pokemons.stream().filter(p -> p.getNumero() == numero).findFirst();
    }

    public List<Pokemon> buscarPorNome(String termo) {
        List<Pokemon> encontrados = new ArrayList<>();
        for (Pokemon p : pokemons) {
            if (p.correspondeATermo(termo)) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }

    public boolean existeNumero(int numero) {
        return buscarPorNumero(numero).isPresent();
    }

    public boolean estaVazia() {
        return pokemons.isEmpty();
    }

    public int tamanho() {
        return pokemons.size();
    }
}
