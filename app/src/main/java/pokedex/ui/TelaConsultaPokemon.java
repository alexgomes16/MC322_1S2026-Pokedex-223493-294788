package pokedex.ui;

import pokedex.abstracts.MenuBase;
import pokedex.exceptions.PokemonNaoEncontradoException;
import pokedex.model.Pokemon;
import pokedex.service.PokemonService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Tela responsavel por consultar os Pokemon ja cadastrados: pesquisar por
 * nome ou visualizar a lista completa (em ordem de cadastramento), podendo
 * abrir os detalhes de qualquer um deles a partir do numero exibido na
 * lista, e voltar tanto para a lista quanto direto para o menu principal.
 */
public class TelaConsultaPokemon extends MenuBase {

    private static final String COMANDO_VOLTAR = "voltar";
    private static final String COMANDO_MENU = "menu";

    // Codigos de cores ANSI para estilização
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    private final PokemonService pokemonService;

    public TelaConsultaPokemon(Scanner scanner, PokemonService pokemonService) {
        super("Visualizar Pokemon", scanner);
        this.pokemonService = pokemonService;
    }

    private void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    protected void exibirOpcoes() {
        limparConsole();
        // Caixa de menu perfeitamente alinhada com 51 caracteres
        System.out.println(CYAN + "+-------------------------------------------------+" + RESET);
        System.out.println(CYAN + "| " + RESET + BOLD + "               VISUALIZAR POKEMON               " + RESET + CYAN + " |" + RESET);
        System.out.println(CYAN + "+-------------------------------------------------+" + RESET);
        System.out.println(CYAN + "| " + RESET + " " + GREEN + "[1]" + RESET + " Pesquisar Pokemon pelo nome                  " + CYAN + "|" + RESET);
        System.out.println(CYAN + "| " + RESET + " " + BLUE + "[2]" + RESET + " Ver lista completa de Pokemon                " + CYAN + "|" + RESET);
        System.out.println(CYAN + "| " + RESET + " " + RED + "[0]" + RESET + " Voltar ao menu principal                     " + CYAN + "|" + RESET);
        System.out.println(CYAN + "+-------------------------------------------------+" + RESET);
        System.out.print("\nDigite sua opcao: ");
    }

    @Override
    protected boolean processarOpcao(String opcao) {
        switch (opcao) {
            case "1":
                return !pesquisarPorNome();
            case "2":
                return !exibirListaCompleta();
            case "0":
                return false;
            default:
                System.out.println(RED + "\nOpcao invalida. Tente novamente." + RESET);
                pausar();
                return true;
        }
    }

    /** @return true se o usuario optou por ir direto ao menu principal a partir da consulta. */
    private boolean pesquisarPorNome() {
        if (pokemonService.getRepositorio().listarTodos().isEmpty()) {
            System.out.println(RED + "\nSua Pokedex ainda nao tem nenhum Pokemon cadastrado." + RESET);
            pausar();
            return false;
        }

        limparConsole();
        System.out.println(YELLOW + BOLD + "================ PESQUISAR POKEMON ================" + RESET);
        System.out.print("\nDigite o nome do Pokemon que deseja pesquisar: ");
        String nome = scanner.nextLine().trim();

        List<Pokemon> encontrados = pokemonService.getRepositorio().buscarPorNome(nome);
        if (encontrados.isEmpty()) {
            System.out.println(RED + "\nNenhum Pokemon chamado \"" + nome + "\" foi encontrado." + RESET);
            pausar();
            return false;
        }

        return exibirListaEAbrirDetalhes(encontrados, "Resultado da pesquisa por \"" + nome + "\"");
    }

    /** @return true se o usuario optou por ir direto ao menu principal a partir da consulta. */
    private boolean exibirListaCompleta() {
        List<Pokemon> todos = pokemonService.getRepositorio().listarTodos();
        if (todos.isEmpty()) {
            System.out.println(RED + "\nSua Pokedex ainda nao tem nenhum Pokemon cadastrado." + RESET);
            pausar();
            return false;
        }
        return exibirListaEAbrirDetalhes(todos, "Lista completa (ordem de cadastro)");
    }

    /**
     * Mostra uma lista de Pokemon (numero + nome)
     * e permite abrir os detalhes de qualquer um deles digitando o numero
     * correspondente.
     */
    private boolean exibirListaEAbrirDetalhes(List<Pokemon> lista, String titulo) {
        while (true) {
            limparConsole();
            System.out.println(YELLOW + BOLD + "=== " + titulo.toUpperCase() + " ===" + RESET + "\n");
            
            for (Pokemon pokemon : lista) {
                // Destaca os resumos com verde/ciano para ficar agradavel
                System.out.println(" " + GREEN + "-> " + RESET + pokemon.exibirResumo());
            }
            
            System.out.println(CYAN + "\n+-------------------------------------------------+" + RESET);
            System.out.println("  Digite o " + BOLD + "numero" + RESET + " do Pokemon para detalhar");
            System.out.println("  Ou digite \"" + RED + COMANDO_VOLTAR + RESET + "\" para retornar");
            System.out.println(CYAN + "+-------------------------------------------------+" + RESET);
            System.out.print("\nSua escolha: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase(COMANDO_VOLTAR)) {
                return false;
            }

            try {
                Pokemon pokemon = buscarPokemonPorNumero(entrada);
                boolean irParaMenuPrincipal = mostrarDetalhes(pokemon);
                if (irParaMenuPrincipal) {
                    return true;
                }
            } catch (PokemonNaoEncontradoException e) {
                System.out.println(RED + "\n" + e.getMessage() + RESET);
                pausar();
            }
        }
    }

    private Pokemon buscarPokemonPorNumero(String entrada) throws PokemonNaoEncontradoException {
        String somenteDigitos = entrada.replace("#", "").trim();
        int numero;
        try {
            numero = Integer.parseInt(somenteDigitos);
        } catch (NumberFormatException e) {
            throw new PokemonNaoEncontradoException("Digite um numero valido ou \"" + COMANDO_VOLTAR + "\".");
        }

        Optional<Pokemon> encontrado = pokemonService.getRepositorio().buscarPorNumero(numero);
        if (encontrado.isEmpty()) {
            throw new PokemonNaoEncontradoException("Nao existe nenhum Pokemon com o numero #" + numero + ".");
        }
        return encontrado.get();
    }

    /**
     * Mostra os detalhes completos de um Pokemon e pergunta para onde o
     * usuario deseja ir em seguida.
     */
    private boolean mostrarDetalhes(Pokemon pokemon) {
        limparConsole();
        System.out.println(YELLOW + BOLD + "================== DETALHES DO POKEMON ==================" + RESET);
        System.out.println(pokemon.exibirDetalhado());
        System.out.println(YELLOW + BOLD + "=========================================================" + RESET);

        while (true) {
            System.out.print("\nDigite \"" + GREEN + COMANDO_VOLTAR + RESET + "\" para a lista, ou \""
                    + BLUE + COMANDO_MENU + RESET + "\" para o menu principal: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_VOLTAR)) {
                return false;
            }
            if (entrada.equalsIgnoreCase(COMANDO_MENU)) {
                return true;
            }
            System.out.println(RED + "Opcao invalida." + RESET);
        }
    }

    private void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}