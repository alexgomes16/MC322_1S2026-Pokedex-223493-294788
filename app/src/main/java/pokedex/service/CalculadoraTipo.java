package pokedex.service;

import pokedex.enums.TipoPokemon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static pokedex.enums.TipoPokemon.*;

/**
 * Responsável por calcular automaticamente as vantagens e desvantagens
 * de um Pokémon com base no(s) seu(s) tipo(s), seguindo a tabela de
 * efetividade de tipos definida para o Pokémon
 */
public class CalculadoraTipo {

    private static final Map<TipoPokemon, List<TipoPokemon>> VANTAGENS = new EnumMap<>(TipoPokemon.class);
    private static final Map<TipoPokemon, List<TipoPokemon>> DESVANTAGENS = new EnumMap<>(TipoPokemon.class);

    static {
        VANTAGENS.put(ACO, List.of(FADA, GELO, PEDRA));
        DESVANTAGENS.put(ACO, List.of(FOGO, LUTADOR, TERRA));

        VANTAGENS.put(AGUA, List.of(FOGO, TERRA, PEDRA));
        DESVANTAGENS.put(AGUA, List.of(ELETRICO, PLANTA));

        VANTAGENS.put(DRAGAO, List.of(DRAGAO));
        DESVANTAGENS.put(DRAGAO, List.of(DRAGAO, FADA, GELO));

        VANTAGENS.put(ELETRICO, List.of(AGUA, VOADOR));
        DESVANTAGENS.put(ELETRICO, List.of(TERRA));

        VANTAGENS.put(FADA, List.of(DRAGAO, LUTADOR, SOMBRIO));
        DESVANTAGENS.put(FADA, List.of(ACO, VENENOSO));

        VANTAGENS.put(FANTASMA, List.of(FANTASMA, PSIQUICO));
        DESVANTAGENS.put(FANTASMA, List.of(FANTASMA, SOMBRIO));

        VANTAGENS.put(FOGO, List.of(ACO, GELO, INSETO, PLANTA));
        DESVANTAGENS.put(FOGO, List.of(AGUA, PEDRA, TERRA));

        VANTAGENS.put(GELO, List.of(DRAGAO, PLANTA, TERRA, VOADOR));
        DESVANTAGENS.put(GELO, List.of(ACO, FOGO, LUTADOR, PEDRA));

        VANTAGENS.put(INSETO, List.of(PLANTA, PSIQUICO, SOMBRIO));
        DESVANTAGENS.put(INSETO, List.of(FOGO, VOADOR, PEDRA));

        VANTAGENS.put(LUTADOR, List.of(ACO, GELO, NORMAL, PEDRA, SOMBRIO));
        DESVANTAGENS.put(LUTADOR, List.of(FADA, PSIQUICO, VOADOR));

        VANTAGENS.put(NORMAL, List.of());
        DESVANTAGENS.put(NORMAL, List.of(LUTADOR));

        VANTAGENS.put(PEDRA, List.of(FOGO, GELO, INSETO, VOADOR));
        DESVANTAGENS.put(PEDRA, List.of(ACO, AGUA, LUTADOR, PLANTA, TERRA));

        VANTAGENS.put(PLANTA, List.of(AGUA, PEDRA, TERRA));
        DESVANTAGENS.put(PLANTA, List.of(GELO, FOGO, INSETO, VENENOSO, VOADOR));

        VANTAGENS.put(PSIQUICO, List.of(LUTADOR, VENENOSO));
        DESVANTAGENS.put(PSIQUICO, List.of(INSETO, FANTASMA, SOMBRIO));

        VANTAGENS.put(SOMBRIO, List.of(FANTASMA, PSIQUICO));
        DESVANTAGENS.put(SOMBRIO, List.of(FADA, INSETO, LUTADOR));

        VANTAGENS.put(TERRA, List.of(ACO, ELETRICO, FOGO, PEDRA, VENENOSO));
        DESVANTAGENS.put(TERRA, List.of(AGUA, GELO, PLANTA));

        VANTAGENS.put(VENENOSO, List.of(FADA, PLANTA));
        DESVANTAGENS.put(VENENOSO, List.of(PSIQUICO, TERRA));

        VANTAGENS.put(VOADOR, List.of(INSETO, LUTADOR, PLANTA));
        DESVANTAGENS.put(VOADOR, List.of(ELETRICO, GELO, PEDRA));
    }

    /** 
     * Vantagens combinadas do tipo principal (+ secundário, se houver), sem repetir tipos
     */
    public List<TipoPokemon> calcularVantagens(TipoPokemon principal, TipoPokemon secundario) {
        return combinarSemDuplicar(principal, secundario, VANTAGENS);
    }

    /** 
     * Desvantagens combinadas do tipo principal (+ secundário, se houver), sem repetir tipos
     */
    public List<TipoPokemon> calcularDesvantagens(TipoPokemon principal, TipoPokemon secundario) {
        return combinarSemDuplicar(principal, secundario, DESVANTAGENS);
    }

    private List<TipoPokemon> combinarSemDuplicar(TipoPokemon principal, TipoPokemon secundario,
                                                    Map<TipoPokemon, List<TipoPokemon>> tabela) {
        LinkedHashSet<TipoPokemon> resultado = new LinkedHashSet<>(tabela.getOrDefault(principal, List.of()));
        if (secundario != null) {
            resultado.addAll(tabela.getOrDefault(secundario, List.of()));
        }
        return new ArrayList<>(resultado);
    }
}
