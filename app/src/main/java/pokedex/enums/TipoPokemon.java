package pokedex.enums;

/**
 * Os 18 tipos de Pokémon utilizados no projeto, com o nome de exibição
 * em português. A ordem segue a tabela de vantagens/desvantagens do grupo.
 */
public enum TipoPokemon {
    ACO("Aco"),
    AGUA("Agua"),
    DRAGAO("Dragao"),
    ELETRICO("Eletrico"),
    FADA("Fada"),
    FANTASMA("Fantasma"),
    FOGO("Fogo"),
    GELO("Gelo"),
    INSETO("Inseto"),
    LUTADOR("Lutador"),
    NORMAL("Normal"),
    PEDRA("Pedra"),
    PLANTA("Planta"),
    PSIQUICO("Psiquico"),
    SOMBRIO("Sombrio"),
    TERRA("Terra"),
    VENENOSO("Venenoso"),
    VOADOR("Voador");

    private final String nomeExibicao;

    TipoPokemon(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @Override
    public String toString() {
        return nomeExibicao;
    }
}
