package pokedex.ui;

import pokedex.enums.RelacaoEvolucao;
import pokedex.enums.Sexo;
import pokedex.enums.TipoPokemon;
import pokedex.exceptions.PokemonDuplicadoException;
import pokedex.exceptions.PokemonNaoEncontradoException;
import pokedex.model.Estatisticas;
import pokedex.model.Pokemon;
import pokedex.service.PokemonService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Tela responsavel por conduzir o cadastro de um novo Pokemon, campo a
 * campo. A qualquer momento o usuario pode digitar "corrigir" para voltar
 * ao campo anterior e preenche-lo novamente.
 */
public class TelaCadastroPokemon {

    private static final String COMANDO_CORRIGIR = "corrigir";

    // Códigos de cores ANSI para estilização
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    /** Ordem dos passos do formulario de cadastro. */
    private enum Passo {
        NOME, ALTURA, PESO, SEXO, TIPO_PRINCIPAL, TIPO_SECUNDARIO,
        VIDA, ATAQUE, DEFESA, ATAQUE_ESPECIAL, DEFESA_ESPECIAL, VELOCIDADE,
        RELACAO_EVOLUCAO, CONFIRMACAO
    }

    /** Pequena interface funcional usada para atribuir o valor lido ao campo correspondente. */
    private interface Atribuidor<T> {
        void atribuir(T valor);
    }

    private final Scanner scanner;
    private final PokemonService pokemonService;

    // Respostas coletadas ao longo do cadastro
    private String nome;
    private double altura;
    private double peso;
    private Sexo sexo;
    private TipoPokemon tipoPrincipal;
    private TipoPokemon tipoSecundario;
    private int vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade;
    private RelacaoEvolucao relacaoEvolucao = RelacaoEvolucao.NENHUMA;
    private Pokemon pokemonRelacionado;

    public TelaCadastroPokemon(Scanner scanner, PokemonService pokemonService) {
        this.scanner = scanner;
        this.pokemonService = pokemonService;
    }

    private void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void exibirCabecalho() {
        limparConsole();
        System.out.println(YELLOW + BOLD + "+---------------------------------------------------+" + RESET);
        System.out.println(YELLOW + BOLD + "| " + RESET + BOLD + "             CADASTRO DE NOVO POKEMON             " + RESET + YELLOW + BOLD + " |" + RESET);
        System.out.println(YELLOW + BOLD + "+---------------------------------------------------+" + RESET);
        System.out.println(CYAN + " (Digite \"" + RED + "corrigir" + CYAN + "\" para voltar ao campo anterior)\n" + RESET);
    }

    public void iniciar() {
        Passo passo = Passo.NOME;
        while (passo != null) {
            passo = executarPasso(passo);
        }
    }

    /** Executa um passo do formulario e devolve o proximo passo a ser executado (ou null para encerrar). */
    private Passo executarPasso(Passo passo) {
        exibirCabecalho();
        switch (passo) {
            case NOME:
                return lerTexto("Nome do Pokemon: ", valor -> nome = valor, Passo.NOME, Passo.ALTURA);
            case ALTURA:
                exibirDadosAtuais();
                return lerDouble("Altura (em metros, ex: 0.7): ", valor -> altura = valor, Passo.NOME, Passo.PESO);
            case PESO:
                exibirDadosAtuais();
                return lerDouble("Peso (em kg, ex: 6.9): ", valor -> peso = valor, Passo.ALTURA, Passo.SEXO);
            case SEXO:
                exibirDadosAtuais();
                return lerSexo();
            case TIPO_PRINCIPAL:
                exibirDadosAtuais();
                return lerTipoPrincipal();
            case TIPO_SECUNDARIO:
                exibirDadosAtuais();
                return lerTipoSecundario();
            case VIDA:
                exibirDadosAtuais();
                return lerInteiro("Vida (HP): ", valor -> vida = valor, Passo.TIPO_SECUNDARIO, Passo.ATAQUE);
            case ATAQUE:
                exibirDadosAtuais();
                return lerInteiro("Ataque: ", valor -> ataque = valor, Passo.VIDA, Passo.DEFESA);
            case DEFESA:
                exibirDadosAtuais();
                return lerInteiro("Defesa: ", valor -> defesa = valor, Passo.ATAQUE, Passo.ATAQUE_ESPECIAL);
            case ATAQUE_ESPECIAL:
                exibirDadosAtuais();
                return lerInteiro("Ataque Especial: ", valor -> ataqueEspecial = valor, Passo.DEFESA, Passo.DEFESA_ESPECIAL);
            case DEFESA_ESPECIAL:
                exibirDadosAtuais();
                return lerInteiro("Defesa Especial: ", valor -> defesaEspecial = valor, Passo.ATAQUE_ESPECIAL, Passo.VELOCIDADE);
            case VELOCIDADE:
                exibirDadosAtuais();
                return lerInteiro("Velocidade: ", valor -> velocidade = valor, Passo.DEFESA_ESPECIAL, Passo.RELACAO_EVOLUCAO);
            case RELACAO_EVOLUCAO:
                exibirDadosAtuais();
                return lerRelacaoEvolucao();
            case CONFIRMACAO:
                return confirmarECadastrar();
            default:
                return null;
        }
    }

    /** Mostra de forma organizada o que o usuario ja preencheu ate o momento */
    private void exibirDadosAtuais() {
        System.out.println(CYAN + "Progresso atual:" + RESET);
        if (nome != null) System.out.println("  -> Nome: " + GREEN + nome + RESET);
        if (altura > 0) System.out.println("  -> Altura: " + GREEN + altura + "m" + RESET);
        if (peso > 0) System.out.println("  -> Peso: " + GREEN + peso + "kg" + RESET);
        if (sexo != null) System.out.println("  -> Sexo: " + GREEN + sexo + RESET);
        if (tipoPrincipal != null) System.out.println("  -> Tipo Principal: " + GREEN + tipoPrincipal.getNomeExibicao() + RESET);
        if (tipoSecundario != null) System.out.println("  -> Tipo Secundario: " + GREEN + tipoSecundario.getNomeExibicao() + RESET);
        System.out.println(CYAN + "-----------------------------------------------------" + RESET);
    }

   private Passo lerTexto(String rotulo, Atribuidor<String> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println(RED + "\nEste campo e obrigatorio e nao pode ficar em branco.\n" + RESET);
                pausarInformativo();
                return passoAnterior; // Retorna com segurança para o passo atual
            }
            atribuidor.atribuir(entrada);
            return proximoPasso;
        }
    }

    private Passo lerDouble(String rotulo, Atribuidor<Double> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println(RED + "\nEste campo e obrigatorio e nao pode ficar em branco.\n" + RESET);
                pausarInformativo();
                return passoAnterior; // Retorna com segurança para o passo atual
            }
            try {
                double valor = Double.parseDouble(entrada.replace(",", "."));
                if (valor <= 0) {
                    System.out.println(RED + "\nO valor deve ser maior que zero.\n" + RESET);
                    pausarInformativo();
                    return passoAnterior;
                }
                atribuidor.atribuir(valor);
                return proximoPasso;
            } catch (NumberFormatException e) {
                System.out.println(RED + "\nValor invalido. Digite um numero (ex: 1.7).\n" + RESET);
                pausarInformativo();
                return passoAnterior;
            }
        }
    }

    private Passo lerInteiro(String rotulo, Atribuidor<Integer> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println(RED + "\nEste campo e obrigatorio e nao pode ficar em branco.\n" + RESET);
                pausarInformativo();
                return passoAnterior; // Retorna com segurança para o passo atual
            }
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < 0) {
                    System.out.println(RED + "\nO valor nao pode ser negativo.\n" + RESET);
                    pausarInformativo();
                    return passoAnterior;
                }
                atribuidor.atribuir(valor);
                return proximoPasso;
            } catch (NumberFormatException e) {
                System.out.println(RED + "\nValor invalido. Digite um numero inteiro.\n" + RESET);
                pausarInformativo();
                return passoAnterior;
            }
        }
    }
    private Passo lerSexo() {
        while (true) {
            System.out.println(BOLD + "Escolha o Sexo:" + RESET);
            System.out.println("  " + GREEN + "[1]" + RESET + " Macho");
            System.out.println("  " + GREEN + "[2]" + RESET + " Femea");
            System.out.println("  " + GREEN + "[3]" + RESET + " Sem genero definido");
            System.out.print("\nEscolha uma opcao: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.PESO;
            }
            switch (entrada) {
                case "1":
                    sexo = Sexo.MACHO;
                    return Passo.TIPO_PRINCIPAL;
                case "2":
                    sexo = Sexo.FEMEA;
                    return Passo.TIPO_PRINCIPAL;
                case "3":
                    sexo = Sexo.SEM_GENERO;
                    return Passo.TIPO_PRINCIPAL;
                default:
                    System.out.println(RED + "\nOpcao invalida. Escolha 1, 2 ou 3.\n" + RESET);
                    pausarInformativo();
                    return Passo.SEXO;
            }
        }
    }

    private Passo lerTipoPrincipal() {
        while (true) {
            exibirListaDeTipos();
            System.out.print(BOLD + "Tipo principal (numero): " + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.SEXO;
            }
            TipoPokemon tipo = converterParaTipo(entrada);
            if (tipo == null) {
                System.out.println(RED + "\nOpcao invalida.\n" + RESET);
                pausarInformativo();
                return Passo.TIPO_PRINCIPAL;
            }
            tipoPrincipal = tipo;
            return Passo.TIPO_SECUNDARIO;
        }
    }

    private Passo lerTipoSecundario() {
        while (true) {
            exibirListaDeTipos();
            System.out.print(BOLD + "Tipo secundario (numero, ou ENTER para nenhum): " + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.TIPO_PRINCIPAL;
            }
            if (entrada.isEmpty()) {
                tipoSecundario = null;
                return Passo.VIDA;
            }
            TipoPokemon tipo = converterParaTipo(entrada);
            if (tipo == null) {
                System.out.println(RED + "\nOpcao invalida.\n" + RESET);
                pausarInformativo();
                return Passo.TIPO_SECUNDARIO;
            }
            if (tipo == tipoPrincipal) {
                System.out.println(RED + "\nO tipo secundario deve ser diferente do tipo principal.\n" + RESET);
                pausarInformativo();
                return Passo.TIPO_SECUNDARIO;
            }
            tipoSecundario = tipo;
            return Passo.VIDA;
        }
    }

    private void exibirListaDeTipos() {
        TipoPokemon[] tipos = TipoPokemon.values();
        System.out.println(CYAN + "\n+---------------------------------------------------+" + RESET);
        System.out.println(CYAN + "|                 TABELA DE TIPOS                   |" + RESET);
        System.out.println(CYAN + "+---------------------------------------------------+" + RESET);
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("  " + GREEN + "[%2d]" + RESET + " %-12s", i + 1, tipos[i].getNomeExibicao());
            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }
        System.out.println(CYAN + "\n+---------------------------------------------------+\n" + RESET);
    }

    private TipoPokemon converterParaTipo(String entrada) {
        try {
            int indice = Integer.parseInt(entrada);
            TipoPokemon[] tipos = TipoPokemon.values();
            if (indice >= 1 && indice <= tipos.length) {
                return tipos[indice - 1];
            }
        } catch (NumberFormatException ignorado) {}
        return null;
    }

    private Passo lerRelacaoEvolucao() {
        while (true) {
            System.out.println(BOLD + "Este Pokemon e evolucao ou desevolucao de outro ja cadastrado?" + RESET);
            System.out.println("  " + GREEN + "[1]" + RESET + " E evolucao de outro Pokemon");
            System.out.println("  " + GREEN + "[2]" + RESET + " E desevolucao (forma anterior) de outro Pokemon");
            System.out.println("  " + GREEN + "[3]" + RESET + " Nenhuma relacao");
            System.out.print("\nEscolha uma opcao: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.VELOCIDADE;
            }
            if (entrada.equals("3")) {
                relacaoEvolucao = RelacaoEvolucao.NENHUMA;
                pokemonRelacionado = null;
                return Passo.CONFIRMACAO;
            }
            if (!entrada.equals("1") && !entrada.equals("2")) {
                System.out.println(RED + "\nOpcao invalida. Escolha 1, 2 ou 3.\n" + RESET);
                pausarInformativo();
                return Passo.RELACAO_EVOLUCAO;
            }
            RelacaoEvolucao relacaoEscolhida = entrada.equals("1")
                    ? RelacaoEvolucao.EVOLUCAO
                    : RelacaoEvolucao.DESEVOLUCAO;

            System.out.print(BOLD + "\nNome do Pokemon ja cadastrado: " + RESET);
            String nomeBusca = scanner.nextLine().trim();
            if (nomeBusca.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.RELACAO_EVOLUCAO;
            }
            try {
                pokemonRelacionado = buscarPokemonPorNome(nomeBusca);
                relacaoEvolucao = relacaoEscolhida;
                return Passo.CONFIRMACAO;
            } catch (PokemonNaoEncontradoException e) {
                System.out.println(RED + "\n" + e.getMessage() + "\n" + RESET);
                pausarInformativo();
                return Passo.RELACAO_EVOLUCAO;
            }
        }
    }

    private Pokemon buscarPokemonPorNome(String nome) throws PokemonNaoEncontradoException {
        List<Pokemon> encontrados = pokemonService.getRepositorio().buscarPorNome(nome);
        if (encontrados.isEmpty()) {
            throw new PokemonNaoEncontradoException(
                    "Nenhum Pokemon chamado \"" + nome + "\" foi encontrado na sua Pokedex. "
                            + "Certifique-se de que ele ja foi cadastrado antes.");
        }
        return encontrados.get(0);
    }

    private Passo confirmarECadastrar() {
        limparConsole();
        Estatisticas estatisticas = new Estatisticas(vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade);
        Pokemon novoPokemon = new Pokemon(nome, altura, peso, sexo, tipoPrincipal, tipoSecundario, estatisticas);
        novoPokemon.setRelacaoEvolucao(relacaoEvolucao);
        novoPokemon.setRelacionado(pokemonRelacionado);

        System.out.println(YELLOW + BOLD + "================== RESUMO DO CADASTRO ==================" + RESET);
        System.out.println(novoPokemon.exibirDetalhado());
        System.out.println(YELLOW + BOLD + "========================================================" + RESET);
        System.out.print("\nSalvar este Pokemon na Pokedex? (s = salvar / corrigir = revisar / n = cancelar): ");
        String entrada = scanner.nextLine().trim();

        if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
            return Passo.RELACAO_EVOLUCAO;
        }
        if (!entrada.equalsIgnoreCase("s")) {
            System.out.println(RED + "\nCadastro cancelado. Nenhum Pokemon foi salvo.\n" + RESET);
            pausarInformativo();
            return null;
        }

        try {
            pokemonService.cadastrarPokemon(novoPokemon);
            System.out.println(GREEN + BOLD + "\n" + nome + " foi cadastrado com sucesso na Pokedex! (#"
                    + String.format("%03d", novoPokemon.getNumero()) + ")\n" + RESET);
        } catch (PokemonDuplicadoException e) {
            System.out.println(RED + "\nNao foi possivel cadastrar: " + e.getMessage() + "\n" + RESET);
        } catch (IOException e) {
            System.out.println(RED + "\nO Pokemon foi registrado, mas houve um erro ao salvar em arquivo: "
                    + e.getMessage() + "\n" + RESET);
        }
        pausarInformativo();
        return null;
    }

    private void pausarInformativo() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }
}