package pokedex.enums;

/**
 * Representa o sexo de um Pokémon
 */
public enum Sexo {
    MACHO("Macho"),
    FEMEA("Femea"),
    SEM_GENERO("Sem genero definido");

    private final String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
