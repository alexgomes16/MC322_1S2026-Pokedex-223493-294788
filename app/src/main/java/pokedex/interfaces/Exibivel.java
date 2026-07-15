package pokedex.interfaces;

/**
 * Contrato para qualquer elemento que possa ser exibido ao usuário,
 * seja em forma resumida (listagens) ou detalhada (tela de detalhes).
 */
public interface Exibivel {
    String exibirResumo();
    String exibirDetalhado();
}
