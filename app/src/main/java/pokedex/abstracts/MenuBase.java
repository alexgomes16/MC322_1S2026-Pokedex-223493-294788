package pokedex.abstracts;

import java.util.Scanner;

/**
 * Base para qualquer tela do sistema que funcione como um menu de opções
 * (Tela Principal, e futuramente Consulta/Edição). Concentra o laço de
 * leitura de opção e delega a exibição e o processamento para as subclasses.
 */
public abstract class MenuBase {

    protected final Scanner scanner;
    protected final String titulo;

    protected MenuBase(String titulo, Scanner scanner) {
        this.titulo = titulo;
        this.scanner = scanner;
    }

    /** Imprime o cabeçalho/logo (quando houver) e as opções do menu. */
    protected abstract void exibirOpcoes();

    /**
     * Processa a opção escolhida pelo usuário.
     *
     * @return false se o menu deve ser encerrado, true para continuar exibindo.
     */
    protected abstract boolean processarOpcao(String opcao);

    /** Laço principal do menu, comum a todas as telas baseadas em menu. */
    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            exibirOpcoes();
            System.out.print("\n> ");
            String opcao = scanner.nextLine().trim();
            continuar = processarOpcao(opcao);
        }
    }
}
