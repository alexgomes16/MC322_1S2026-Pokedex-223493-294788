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
import java.util.Optional;
import java.util.Scanner;

/**
 * Tela responsavel por editar as informacoes de um Pokemon ja cadastrado.
 * Se o usuario apenas pressionar ENTER em um campo, o valor atual e mantido.
 */
public class TelaEdicaoPokemon {

    private static final String COMANDO_CORRIGIR = "corrigir";

    // Codigos de cores ANSI para estilizacao
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    private enum Passo {
        NOME, ALTURA, PESO, SEXO, TIPO_PRINCIPAL, TIPO_SECUNDARIO,
        VIDA, ATAQUE, DEFESA, ATAQUE_ESPECIAL, DEFESA_ESPECIAL, VELOCIDADE,
        RELACAO_EVOLUCAO, CONFIRMACAO
    }

    private interface Atribuidor<T> {
        void atribuir(T valor);
    }

    private final Scanner scanner;
    private final PokemonService pokemonService;
    private Pokemon pokemonAlvo;

    // Novos valores temporarios
    private String nome;
    private double altura;
    private double peso;
    private Sexo sexo;
    private TipoPokemon tipoPrincipal;
    private TipoPokemon tipoSecundario;
    private int vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade;
    private RelacaoEvolucao relacaoEvolucao;
    private Pokemon pokemonRelacionado;

    public TelaEdicaoPokemon(Scanner scanner, PokemonService pokemonService) {
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
        System.out.println(YELLOW + BOLD + "| " + RESET + BOLD + "             EDITAR DADOS DE POKEMON              " + RESET + YELLOW + BOLD + " |" + RESET);
        System.out.println(YELLOW + BOLD + "+---------------------------------------------------+" + RESET);
        System.out.println(CYAN + " (Pressione " + GREEN + "ENTER" + CYAN + " para manter o valor atual)" + RESET);
        System.out.println(CYAN + " (Digite \"" + RED + "corrigir" + CYAN + "\" para voltar ao campo anterior)\n" + RESET);
    }

    public void iniciar() {
        limparConsole();
        System.out.println(YELLOW + BOLD + "================== EDITAR POKEMON ==================" + RESET);
        System.out.print("\nDigite o nome ou numero (#) do Pokemon que deseja editar: ");
        String busca = scanner.nextLine().trim();

        try {
            pokemonAlvo = identificarPokemon(busca);
            carregarDadosOriginais();
        } catch (PokemonNaoEncontradoException e) {
            System.out.println(RED + "\n" + e.getMessage() + RESET);
            pausarInformativo();
            return;
        }

        Passo passo = Passo.NOME;
        while (passo != null) {
            passo = executarPasso(passo);
        }
    }

    private void carregarDadosOriginais() {
        this.nome = pokemonAlvo.getNome();
        this.altura = pokemonAlvo.getAltura();
        this.peso = pokemonAlvo.getPeso();
        this.sexo = pokemonAlvo.getSexo();
        this.tipoPrincipal = pokemonAlvo.getTipoPrincipal();
        this.tipoSecundario = pokemonAlvo.getTipoSecundario();
        
        Estatisticas stats = pokemonAlvo.getEstatisticas();
        this.vida = stats.getVida();
        this.ataque = stats.getAtaque();
        this.defesa = stats.getDefesa();
        this.ataqueEspecial = stats.getAtaqueEspecial();
        this.defesaEspecial = stats.getDefesaEspecial();
        this.velocidade = stats.getVelocidade();
        this.relacaoEvolucao = pokemonAlvo.getRelacaoEvolucao();
        this.pokemonRelacionado = pokemonAlvo.getRelacionado();
    }

    private Pokemon identificarPokemon(String busca) throws PokemonNaoEncontradoException {
        if (busca.startsWith("#")) {
            try {
                int num = Integer.parseInt(busca.replace("#", "").trim());
                Optional<Pokemon> p = pokemonService.getRepositorio().buscarPorNumero(num);
                if (p.isPresent()) return p.get();
            } catch (NumberFormatException ignored) {}
        }

        List<Pokemon> encontrados = pokemonService.getRepositorio().buscarPorNome(busca);
        if (!encontrados.isEmpty()) {
            return encontrados.get(0);
        }
        
        try {
            int num = Integer.parseInt(busca);
            Optional<Pokemon> p = pokemonService.getRepositorio().buscarPorNumero(num);
            if (p.isPresent()) return p.get();
        } catch (NumberFormatException ignored) {}

        throw new PokemonNaoEncontradoException("Nenhum Pokemon encontrado com a busca: \"" + busca + "\".");
    }

    private Passo executarPasso(Passo passo) {
        exibirCabecalho();
        switch (passo) {
            case NOME:
                return lerTexto("Nome (" + pokemonAlvo.getNome() + "): ", valor -> nome = valor, Passo.NOME, Passo.ALTURA);
            case ALTURA:
                return lerDouble("Altura (" + pokemonAlvo.getAltura() + "m): ", valor -> altura = valor, Passo.NOME, Passo.PESO, pokemonAlvo.getAltura());
            case PESO:
                return lerDouble("Peso (" + pokemonAlvo.getPeso() + "kg): ", valor -> peso = valor, Passo.ALTURA, Passo.SEXO, pokemonAlvo.getPeso());
            case SEXO:
                return lerSexo();
            case TIPO_PRINCIPAL:
                return lerTipoPrincipal();
            case TIPO_SECUNDARIO:
                return lerTipoSecundario();
            case VIDA:
                return lerInteiro("Vida/HP (" + pokemonAlvo.getEstatisticas().getVida() + "): ", valor -> vida = valor, Passo.TIPO_SECUNDARIO, Passo.ATAQUE, pokemonAlvo.getEstatisticas().getVida());
            case ATAQUE:
                return lerInteiro("Ataque (" + pokemonAlvo.getEstatisticas().getAtaque() + "): ", valor -> ataque = valor, Passo.VIDA, Passo.DEFESA, pokemonAlvo.getEstatisticas().getAtaque());
            case DEFESA:
                return lerInteiro("Defesa (" + pokemonAlvo.getEstatisticas().getDefesa() + "): ", valor -> defesa = valor, Passo.ATAQUE, Passo.ATAQUE_ESPECIAL, pokemonAlvo.getEstatisticas().getDefesa());
            case ATAQUE_ESPECIAL:
                return lerInteiro("Ataque Especial (" + pokemonAlvo.getEstatisticas().getAtaqueEspecial() + "): ", valor -> ataqueEspecial = valor, Passo.DEFESA, Passo.DEFESA_ESPECIAL, pokemonAlvo.getEstatisticas().getAtaqueEspecial());
            case DEFESA_ESPECIAL:
                return lerInteiro("Defesa Especial (" + pokemonAlvo.getEstatisticas().getDefesaEspecial() + "): ", valor -> defesaEspecial = valor, Passo.ATAQUE_ESPECIAL, Passo.VELOCIDADE, pokemonAlvo.getEstatisticas().getDefesaEspecial());
            case VELOCIDADE:
                return lerInteiro("Velocidade (" + pokemonAlvo.getEstatisticas().getVelocidade() + "): ", valor -> velocidade = valor, Passo.DEFESA_ESPECIAL, Passo.RELACAO_EVOLUCAO, pokemonAlvo.getEstatisticas().getVelocidade());
            case RELACAO_EVOLUCAO:
                return lerRelacaoEvolucao();
            case CONFIRMACAO:
                return confirmarESalvar();
            default:
                return null;
        }
    }

    private Passo lerTexto(String rotulo, Atribuidor<String> atribuidor, Passo passoAnterior, Passo proximoPasso) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                return proximoPasso;
            }
            atribuidor.atribuir(entrada);
            return proximoPasso;
        }
    }

    private Passo lerDouble(String rotulo, Atribuidor<Double> atribuidor, Passo passoAnterior, Passo proximoPasso, double valorPadrao) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                atribuidor.atribuir(valorPadrao);
                return proximoPasso;
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

    private Passo lerInteiro(String rotulo, Atribuidor<Integer> atribuidor, Passo passoAnterior, Passo proximoPasso, int valorPadrao) {
        while (true) {
            System.out.print(BOLD + rotulo + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return passoAnterior;
            }
            if (entrada.isEmpty()) {
                atribuidor.atribuir(valorPadrao);
                return proximoPasso;
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
            System.out.println("Sexo atual: " + GREEN + sexo + RESET);
            System.out.println(BOLD + "Escolha o novo Sexo (ou ENTER para manter):" + RESET);
            System.out.println("  " + GREEN + "[1]" + RESET + " Macho");
            System.out.println("  " + GREEN + "[2]" + RESET + " Femea");
            System.out.println("  " + GREEN + "[3]" + RESET + " Sem genero definido");
            System.out.print("\nEscolha uma opcao: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.PESO;
            }
            if (entrada.isEmpty()) {
                return Passo.TIPO_PRINCIPAL;
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
            System.out.println("Tipo principal atual: " + GREEN + tipoPrincipal.getNomeExibicao() + RESET);
            exibirListaDeTipos();
            System.out.print(BOLD + "Novo Tipo principal (numero, ou ENTER para manter): " + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.SEXO;
            }
            if (entrada.isEmpty()) {
                return Passo.TIPO_SECUNDARIO;
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
            String atual = tipoSecundario != null ? tipoSecundario.getNomeExibicao() : "Nenhum";
            System.out.println("Tipo secundario atual: " + GREEN + atual + RESET);
            exibirListaDeTipos();
            System.out.print(BOLD + "Novo Tipo secundario (numero, ENTER para manter, ou digite 'nenhum' para remover): " + RESET);
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.TIPO_PRINCIPAL;
            }
            if (entrada.isEmpty()) {
                return Passo.VIDA;
            }
            if (entrada.equalsIgnoreCase("nenhum")) {
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
            String relacaoAtual = "Nenhuma";
            if (relacaoEvolucao == RelacaoEvolucao.EVOLUCAO) relacaoAtual = "E evolucao de " + (pokemonRelacionado != null ? pokemonRelacionado.getNome() : "?");
            if (relacaoEvolucao == RelacaoEvolucao.DESEVOLUCAO) relacaoAtual = "E desevolucao de " + (pokemonRelacionado != null ? pokemonRelacionado.getNome() : "?");

            System.out.println("Relacao atual: " + GREEN + relacaoAtual + RESET);
            System.out.println(BOLD + "Escolha a nova Relacao (ou ENTER para manter):" + RESET);
            System.out.println("  " + GREEN + "[1]" + RESET + " E evolucao de outro Pokemon");
            System.out.println("  " + GREEN + "[2]" + RESET + " E desevolucao (forma anterior) de outro Pokemon");
            System.out.println("  " + GREEN + "[3]" + RESET + " Nenhuma relacao");
            System.out.print("\nEscolha uma opcao: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
                return Passo.VELOCIDADE;
            }
            if (entrada.isEmpty()) {
                return Passo.CONFIRMACAO;
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
                    "Nenhum Pokemon chamado \"" + nome + "\" foi encontrado na sua Pokedex.");
        }
        return encontrados.get(0);
    }

   private Passo confirmarESalvar() {
        limparConsole();
        
        Estatisticas novasStats = new Estatisticas(vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade);
        
        // Criamos uma cópia apenas para exibição das mudanças na tela de confirmação
        Pokemon pokemonAlterado = new Pokemon(nome, altura, peso, sexo, tipoPrincipal, tipoSecundario, novasStats);
        pokemonAlterado.setNumero(pokemonAlvo.getNumero()); 
        pokemonAlterado.setRelacaoEvolucao(relacaoEvolucao);
        pokemonAlterado.setRelacionado(pokemonRelacionado);

        System.out.println(YELLOW + BOLD + "================== REVISAO DAS ALTERACOES ==================" + RESET);
        System.out.println(pokemonAlterado.exibirDetalhado());
        System.out.println(YELLOW + BOLD + "============================================================" + RESET);
        System.out.print("\nSalvar as alteracoes na Pokedex? (s = salvar / corrigir = revisar / n = cancelar): ");
        String entrada = scanner.nextLine().trim();

        if (entrada.equalsIgnoreCase(COMANDO_CORRIGIR)) {
            return Passo.RELACAO_EVOLUCAO;
        }
        if (!entrada.equalsIgnoreCase("s")) {
            System.out.println(RED + "\nEdicao cancelada. Nenhuma alteracao foi salva.\n" + RESET);
            pausarInformativo();
            return null;
        }

        try {
            // 1. Atualiza diretamente o objeto original que já está na lista da Pokédex
            pokemonAlvo.setNome(nome);
            pokemonAlvo.setAltura(altura);
            pokemonAlvo.setPeso(peso);
            pokemonAlvo.setSexo(sexo);
            pokemonAlvo.setTipoPrincipal(tipoPrincipal);
            pokemonAlvo.setTipoSecundario(tipoSecundario);
            pokemonAlvo.setEstatisticas(novasStats);
            pokemonAlvo.setRelacaoEvolucao(relacaoEvolucao);
            pokemonAlvo.setRelacionado(pokemonRelacionado);

            // 2. Salva o estado atualizado do repositório de volta no arquivo de dados!
            pokemonService.getRepositorio().salvarEmArquivo();
            
            System.out.println(GREEN + BOLD + "\n" + nome + " foi atualizado com sucesso na Pokedex! (#"
                    + String.format("%03d", pokemonAlvo.getNumero()) + ")\n" + RESET);
        } catch (IOException e) {
            System.out.println(RED + "\nErro ao salvar dados atualizados no arquivo: " + e.getMessage() + "\n" + RESET);
        }
        pausarInformativo();
        return null;
    }
    
    private void pausarInformativo() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }
}