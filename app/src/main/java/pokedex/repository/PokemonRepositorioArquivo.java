package pokedex.repository;

import pokedex.abstracts.RepositorioArquivoBase;
import pokedex.enums.RelacaoEvolucao;
import pokedex.enums.Sexo;
import pokedex.enums.TipoPokemon;
import pokedex.model.Estatisticas;
import pokedex.model.Pokemon;

/**
 * Implementação concreta de {@link RepositorioArquivoBase} responsável por
 * ler e gravar Pokémon em um arquivo de texto (uma linha por Pokémon,
 * campos separados por "|").
 */
public class PokemonRepositorioArquivo extends RepositorioArquivoBase<Pokemon> {

    public PokemonRepositorioArquivo(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    protected Pokemon deserializar(String linha) {
        String[] partes = linha.split("\\|", -1);

        int numero = Integer.parseInt(partes[0]);
        String nome = partes[1];
        double altura = Double.parseDouble(partes[2]);
        double peso = Double.parseDouble(partes[3]);
        Sexo sexo = Sexo.valueOf(partes[4]);
        TipoPokemon tipoPrincipal = TipoPokemon.valueOf(partes[5]);
        TipoPokemon tipoSecundario = partes[6].isEmpty() ? null : TipoPokemon.valueOf(partes[6]);
        Estatisticas estatisticas = Estatisticas.deserializar(partes[7]);
        RelacaoEvolucao relacaoEvolucao = RelacaoEvolucao.valueOf(partes[8]);

        Pokemon pokemon = new Pokemon(nome, altura, peso, sexo, tipoPrincipal, tipoSecundario, estatisticas);
        pokemon.setNumero(numero);
        pokemon.setRelacaoEvolucao(relacaoEvolucao);

        if (partes.length > 9 && !partes[9].isEmpty()) {
            // A referência real ao objeto Pokemon relacionado é resolvida depois,
            // pelo PokemonRepositorio, quando todos já estiverem carregados.
            pokemon.setNumeroRelacionado(Integer.parseInt(partes[9]));
        }

        return pokemon;
    }
}
