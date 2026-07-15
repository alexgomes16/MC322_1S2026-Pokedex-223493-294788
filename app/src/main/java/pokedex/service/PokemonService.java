package pokedex.service;

import pokedex.exceptions.PokemonDuplicadoException;
import pokedex.model.Pokemon;
import pokedex.repository.PokemonRepositorio;

import java.io.IOException;

/**
 * Camada de serviço: orquestra as regras de negócio do cadastro de Pokémon,
 * como o cálculo automático de vantagens/desvantagens de tipo, a atribuição
 * do número de catalogação e a persistência em arquivo.
 */
public class PokemonService {

    private final PokemonRepositorio repositorio;
    private final CalculadoraTipo calculadoraTipo;

    public PokemonService(PokemonRepositorio repositorio) {
        this.repositorio = repositorio;
        this.calculadoraTipo = new CalculadoraTipo();
    }

    public PokemonRepositorio getRepositorio() {
        return repositorio;
    }

    /**
     * Finaliza o cadastro de um Pokémon: calcula vantagens/desvantagens de
     * tipo, atribui o número de catalogação e persiste em arquivo.
     */
    public Pokemon cadastrarPokemon(Pokemon pokemon) throws PokemonDuplicadoException, IOException {
        pokemon.setVantagens(calculadoraTipo.calcularVantagens(pokemon.getTipoPrincipal(), pokemon.getTipoSecundario()));
        pokemon.setDesvantagens(calculadoraTipo.calcularDesvantagens(pokemon.getTipoPrincipal(), pokemon.getTipoSecundario()));
        pokemon.setNumero(repositorio.proximoNumero());

        repositorio.adicionar(pokemon);
        repositorio.salvarEmArquivo();
        return pokemon;
    }
}
