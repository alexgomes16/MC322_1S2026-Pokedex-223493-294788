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
 * Tela responsável por conduzir o cadastro de um novo Pokemon, campo a
 * campo. A qualquer momento o usuário pode digitar "corrigir" para voltar
 * ao campo anterior e preenchê-lo novamente.
 */
public class TelaCadastroPokemon {

    private static final String COMANDO_CORRIGIR = "corrigir";

    /** Ordem dos passos do formulário de cadastro. */
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

    public void iniciar() {
        System.out.println("\n=========== CADASTRAR NOVO POKEMON ===========");
        System.out.println("(Em qualquer campo, digite \"corrigir\" para voltar ao campo anterior.)\n");

        Passo passo = Passo.NOME;
        while (passo != null) {
            passo = executarPasso(passo);
        }
    }

    /** Executa um passo do formulário e devolve o próximo passo a ser executado (ou null para encerrar). */
    private Passo executarPasso(Passo passo) {
        switch (passo) {
            case NOME:
                return lerTexto("Nome do Pokemon: ", valor -> nome = valor, Passo.NOME, Passo.ALTURA);
            case ALTURA:
                return lerDouble("Altura (em metros, ex: 0.7): ", valor -> altura = valor, Passo.NOME, Passo.PESO);
            case PESO:
                return lerDouble("Peso (em kg, ex: 6.9): ", valor -> peso = valor, Passo.ALTURA, Passo.SEXO);
            case SEXO:
                return lerSexo();
            case TIPO_PRINCIPAL:
                return lerTipoPrincipal();
            case TIPO_SECUNDARIO:
                return lerTipoSecundario();
            case VIDA:
                return lerInteiro("Vida (HP): ", valor -> vida = valor, Passo.TIPO_SECUNDARIO, Passo.ATAQUE);
            case ATAQUE:
                return lerInteiro("Ataque: ", valor -> ataque = valor, Passo.VIDA, Passo.DEFESA);
            case DEFESA:
                return lerInteiro("Defesa: ", valor -> defesa = valor, Passo.ATAQUE, Passo.ATAQUE_ESPECIAL);
            case ATAQUE_ESPECIAL:
                return lerInteiro("Ataque Especial: ", valor -> ataqueEspecial = valor, Passo.DEFESA, Passo.DEFESA_ESPECIAL);
            case DEFESA_ESPECIAL:
                return lerInteiro("Defesa Especial: ", valor -> defesaEspecial = valor, Passo.ATAQUE_ESPECIAL, Passo.VELOCIDADE);
            case VELOCIDADE:
                return lerInteiro("Velocidade: ", valor -> velocidade = valor, Passo.DEFESA_ESPECIAL, Passo.RELACAO_EVOLUCAO);
            case RELACAO_EVOLUCAO:
                return lerRelacaoEvolucao();
            case CONFIRMACAO:
                return confirmarECadastrar();
            default:
                return null;
        }
    }

    // Leitura de campos simples (texto, double, inteiro)

    private Passo lerTexto(String rotulo, Atribuidor<String> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(rotulo);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println("Este campo e obrigatorio e nao pode ficar em branco.\n");
                continue;
            }
            atribuidor.atribuir(entrada);
            return proximoPasso;
        }
    }

    private Passo lerDouble(String rotulo, Atribuidor<Double> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(rotulo);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println("Este campo e obrigatorio e nao pode ficar em branco.\n");
                continue;
            }
            try {
                double valor = Double.parseDouble(entrada.replace(",", "."));
                if (valor <= 0) {
                    System.out.println("O valor deve ser maior que zero.\n");
                    continue;
                }
                atribuidor.atribuir(valor);
                return proximoPasso;
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Digite um número (ex: 1.7).\n");
            }
        }
    }

    private Passo lerInteiro(String rotulo, Atribuidor<Integer> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(rotulo);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                System.out.println("Este campo e obrigatorio e nao pode ficar em branco.\n");
                continue;
            }
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < 0) {
                    System.out.println("O valor nao pode ser negativo.\n");
                    continue;
                }
                atribuidor.atribuir(valor);
                return proximoPasso;
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Digite um numero inteiro.\n");
            }
        }
    }

    // Passos com escolha entre opções

    private Passo lerSexo() {
        while (true) {
            System.out.println("Sexo:");
            System.out.println("  1. Macho");
            System.out.println("  2. Femea");
            System.out.println("  3. Sem genero definido");
            System.out.print("Escolha uma opção: ");
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
                    System.out.println("Opção invalida. Escolha 1, 2 ou 3.\n");
            }
        }
    }

    private Passo lerTipoPrincipal() {
        while (true) {
            exibirListaDeTipos();
            System.out.print("Tipo principal (numero): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.SEXO;
            }
            TipoPokemon tipo = converterParaTipo(entrada);
            if (tipo == null) {
                System.out.println("Opcao invalida.\n");
                continue;
            }
            tipoPrincipal = tipo;
            return Passo.TIPO_SECUNDARIO;
        }
    }

    private Passo lerTipoSecundario() {
        while (true) {
            exibirListaDeTipos();
            System.out.print("Tipo secundario (numero, ou ENTER para nenhum — este campo e opcional): ");
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
                System.out.println("Opção invalida.\n");
                continue;
            }
            if (tipo == tipoPrincipal) {
                System.out.println("O tipo secundario deve ser diferente do tipo principal.\n");
                continue;
            }
            tipoSecundario = tipo;
            return Passo.VIDA;
        }
    }

    private void exibirListaDeTipos() {
        TipoPokemon[] tipos = TipoPokemon.values();
        System.out.println();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("  %2d. %-12s", i + 1, tipos[i].getNomeExibicao());
            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n");
    }

    private TipoPokemon converterParaTipo(String entrada) {
        try {
            int indice = Integer.parseInt(entrada);
            TipoPokemon[] tipos = TipoPokemon.values();
            if (indice >= 1 && indice <= tipos.length) {
                return tipos[indice - 1];
            }
        } catch (NumberFormatException ignorado) {
            // trata como opção inválida abaixo
        }
        return null;
    }

    private Passo lerRelacaoEvolucao() {
        while (true) {
            System.out.println("Este Pokemon e evolucao ou desevolucao de outro ja cadastrado?");
            System.out.println("  1. E evolucao de outro Pokemon");
            System.out.println("  2. E desevolução (forma anterior) de outro Pokemon");
            System.out.println("  3. Nenhuma relacao");
            System.out.print("Escolha uma opcao: ");
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
                System.out.println("Opção invalida. Escolha 1, 2 ou 3.\n");
                continue;
            }
            RelacaoEvolucao relacaoEscolhida = entrada.equals("1")
                    ? RelacaoEvolucao.EVOLUCAO
                    : RelacaoEvolucao.DESEVOLUCAO;

            System.out.print("Nome do Pokemon ja cadastrado (ou \"corrigir\" para voltar): ");
            String nomeBusca = scanner.nextLine().trim();
            if (nomeBusca.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                continue; // volta a perguntar a relação (evolução/desevolução/nenhuma)
            }
            try {
                pokemonRelacionado = buscarPokemonPorNome(nomeBusca);
                relacaoEvolucao = relacaoEscolhida;
                return Passo.CONFIRMACAO;
            } catch (PokemonNaoEncontradoException e) {
                System.out.println(e.getMessage() + "\n");
                // continua no mesmo passo, permitindo tentar de novo
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

    // Confirmação final

    private Passo confirmarECadastrar() {
        Estatisticas estatisticas = new Estatisticas(vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade);
        Pokemon novoPokemon = new Pokemon(nome, altura, peso, sexo, tipoPrincipal, tipoSecundario, estatisticas);
        novoPokemon.setRelacaoEvolucao(relacaoEvolucao);
        novoPokemon.setRelacionado(pokemonRelacionado);

        System.out.println("=========== RESUMO DO CADASTRO ===========");
        System.out.println(novoPokemon.exibirDetalhado());
        System.out.println("===========================================");
        System.out.print("Salvar este Pokemon na Pokedex? (s = salvar / corrigir = revisar / n = cancelar): ");
        String entrada = scanner.nextLine().trim();

        if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
            return Passo.RELACAO_EVOLUCAO;
        }
        if (!entrada.equalsIgnoreCase("s")) {
            System.out.println("\nCadastro cancelado. Nenhum Pokemon foi salvo.\n");
            return null;
        }

        try {
            pokemonService.cadastrarPokemon(novoPokemon);
            System.out.println("\n" + nome + " foi cadastrado com sucesso na Pokedex! (#"
                    + String.format("%03d", novoPokemon.getNumero()) + ")\n");
        } catch (PokemonDuplicadoException e) {
            System.out.println("\nNao foi possível cadastrar: " + e.getMessage() + "\n");
        } catch (IOException e) {
            System.out.println("\nO Pokemon foi registrado, mas houve um erro ao salvar em arquivo: "
                    + e.getMessage() + "\n");
        }
        return null;
    }
}
