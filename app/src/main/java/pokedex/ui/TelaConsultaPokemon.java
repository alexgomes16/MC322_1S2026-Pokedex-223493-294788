package pokedex.ui;

import pokedex.abstracts.MenuBase;
import pokedex.exceptions.PokemonNaoEncontradoException;
import pokedex.model.Pokemon;
import pokedex.service.PokemonService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Tela responsável por consultar os Pokémon já cadastrados: pesquisar por
 * nome ou visualizar a lista completa (em ordem de cadastramento), podendo
 * abrir os detalhes de qualquer um deles a partir do número exibido na
 * lista, e voltar tanto para a lista quanto direto para o menu principal.
 */
public class TelaConsultaPokemon extends MenuBase {

    private static final String COMANDO_VOLTAR = "voltar";
    private static final String COMANDO_MENU = "menu";

    private final PokemonService pokemonService;

    public TelaConsultaPokemon(Scanner scanner, PokemonService pokemonService) {
        super("Visualizar Pokemon", scanner);
        this.pokemonService = pokemonService;
    }

    @Override
    protected void exibirOpcoes() {
        System.out.println("\n=========== VISUALIZAR POKEMON ===========");
        System.out.println(" 1. Pesquisar Pokemon pelo nome");
        System.out.println(" 2. Ver lista completa de Pokemon");
        System.out.println(" 0. Voltar ao menu principal");
        System.out.println("============================================");
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
                System.out.println("\nOpcao invalida. Tente novamente.");
                pausar();
                return true;
        }
    }

    /** @return true se o usuário optou por ir direto ao menu principal a partir da consulta. */
    private boolean pesquisarPorNome() {
        if (pokemonService.getRepositorio().listarTodos().isEmpty()) {
            System.out.println("\nSua Pokedex ainda nao tem nenhum Pokemon cadastrado.");
            pausar();
            return false;
        }

        System.out.print("\nDigite o nome do Pokemon que deseja pesquisar: ");
        String nome = scanner.nextLine().trim();

        List<Pokemon> encontrados = pokemonService.getRepositorio().buscarPorNome(nome);
        if (encontrados.isEmpty()) {
            System.out.println("\nNenhum Pokemon chamado \"" + nome + "\" foi encontrado.");
            pausar();
            return false;
        }

        return exibirListaEAbrirDetalhes(encontrados, "Resultado da pesquisa por \"" + nome + "\"");
    }

    /** @return true se o usuário optou por ir direto ao menu principal a partir da consulta. */
    private boolean exibirListaCompleta() {
        List<Pokemon> todos = pokemonService.getRepositorio().listarTodos();
        if (todos.isEmpty()) {
            System.out.println("\nSua Pokedex ainda nao tem nenhum Pokemon cadastrado.");
            pausar();
            return false;
        }
        return exibirListaEAbrirDetalhes(todos, "Lista completa de Pokemon (ordem de cadastro)");
    }

    /**
     * Mostra uma lista de Pokémon (número + nome)
     * e permite abrir os detalhes de qualquer um deles digitando o número
     * correspondente.
     *
     * @return true se o usuário optou por ir direto ao menu principal a
     *         partir da tela de detalhes; false se voltou apenas desta lista
     *         (retornando ao submenu de consulta).
     */
    private boolean exibirListaEAbrirDetalhes(List<Pokemon> lista, String titulo) {
        while (true) {
            System.out.println("\n--- " + titulo + " ---");
            for (Pokemon pokemon : lista) {
                System.out.println(pokemon.exibirResumo());
            }
            System.out.print("\nDigite o numero do Pokemon para ver detalhes, ou \""
                    + COMANDO_VOLTAR + "\" para retornar: ");
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
                // caso contrário, o laço continua e a lista é exibida novamente
            } catch (PokemonNaoEncontradoException e) {
                System.out.println("\n" + e.getMessage());
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
     * Mostra os detalhes completos de um Pokémon e pergunta para onde o
     * usuário deseja ir em seguida.
     *
     * @return true se o usuário escolheu ir direto ao menu principal.
     */
    private boolean mostrarDetalhes(Pokemon pokemon) {
        System.out.println("\n=========== DETALHES DO POKEMON ===========");
        System.out.println(pokemon.exibirDetalhado());
        System.out.println("=============================================");

        while (true) {
            System.out.print("Digite \"" + COMANDO_VOLTAR + "\" para voltar à lista, ou \""
                    + COMANDO_MENU + "\" para voltar ao menu principal: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_VOLTAR)) {
                return false;
            }
            if (entrada.equalsIgnoreCase(COMANDO_MENU)) {
                return true;
            }
            System.out.println("Opção invalida.");
        }
    }

    private void pausar() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }
}
