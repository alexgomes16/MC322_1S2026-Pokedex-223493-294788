package pokedex.interfaces;

/**
 * Contrato para qualquer elemento que possa ser exibido ao usuário,
 * seja em forma resumida ou detalhada
 */
public interface Exibivel {
    String exibirResumo();
    String exibirDetalhado();
}
