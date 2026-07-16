package pokedex.ui;

import pokedex.abstracts.MenuBase;
import pokedex.repository.PokemonRepositorio;
import pokedex.service.PokemonService;

import java.util.Scanner;

/**
 * Tela inicial do jogo: mostra o letreiro "Pokédex" e o menu principal
 * (Cadastrar / Visualizar / Editar).
 */
public class TelaPrincipal extends MenuBase {

    private static final String LOGO =
            "  ██████╗  ██████╗ ██╗  ██╗███████╗██████╗ ███████╗██╗  ██╗\n" +
            "  ██╔══██╗██╔═══██╗██║ ██╔╝██╔════╝██╔══██╗██╔════╝╚██╗██╔╝\n" +
            "  ██████╔╝██║   ██║█████╔╝ █████╗  ██║  ██║█████╗   ╚███╔╝ \n" +
            "  ██╔═══╝ ██║   ██║██╔═██╗ ██╔══╝  ██║  ██║██╔══╝   ██╔██╗ \n" +
            "  ██║     ╚██████╔╝██║  ██╗███████╗██████╔╝███████╗██╔╝ ██╗\n" +
            "  ╚═╝      ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═════╝ ╚══════╝╚═╝  ╚═╝\n";

    private final PokemonService pokemonService;

    public TelaPrincipal(Scanner scanner) {
        super("Pokedex", scanner);
        PokemonRepositorio repositorio = new PokemonRepositorio();
        this.pokemonService = new PokemonService(repositorio);
    }

    @Override
    protected void exibirOpcoes() {
        System.out.println(LOGO);
        System.out.println("               Bem-vindo(a) a sua Pokedex!");
        System.out.println("=================================================");
        System.out.println(" 1. Cadastrar novo Pokemon");
        System.out.println(" 2. Visualizar Pokemon");
        System.out.println(" 3. Editar Pokemon");
        System.out.println(" 0. Sair");
        System.out.println("=================================================");
    }

    @Override
    protected boolean processarOpcao(String opcao) {
        switch (opcao) {
            case "1":
                new TelaCadastroPokemon(scanner, pokemonService).iniciar();
                return true;
                case "2":
                    new TelaConsultaPokemon(scanner, pokemonService).iniciar();
                    return true;
            case "3":
                System.out.println("\n[Editar Pokemon] Em construção — chegando em breve!\n");
                pausar();
                return true;
            case "0":
                System.out.println("\nAte a proxima, treinador(a)!");
                return false;
            default:
                System.out.println("\nOpção invalida. Tente novamente.");
                pausar();
                return true;
        }
    }

    private void pausar() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }
}
