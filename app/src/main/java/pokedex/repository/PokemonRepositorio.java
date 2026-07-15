package pokedex.repository;

import pokedex.exceptions.PokemonDuplicadoException;
import pokedex.model.Pokedex;
import pokedex.model.Pokemon;
import pokedex.service.CalculadoraTipo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Camada de acesso a dados do Pokémon. Mantém a {@link Pokedex} em memória
 * (composição) e delega a leitura/gravação em arquivo para o
 * {@link PokemonRepositorioArquivo} (também composição).
 */
public class PokemonRepositorio {

    private static final String CAMINHO_PADRAO = "dados/pokemons.txt";

    private final Pokedex pokedex = new Pokedex();
    private final PokemonRepositorioArquivo repositorioArquivo;
    private final CalculadoraTipo calculadoraTipo = new CalculadoraTipo();

    public PokemonRepositorio() {
        this(CAMINHO_PADRAO);
    }

    public PokemonRepositorio(String caminhoArquivo) {
        this.repositorioArquivo = new PokemonRepositorioArquivo(caminhoArquivo);
        carregarDoArquivo();
    }

    private void carregarDoArquivo() {
        try {
            List<Pokemon> carregados = repositorioArquivo.carregarTodos();
            for (Pokemon p : carregados) {
                // Recalcula vantagens/desvantagens (não são persistidas, são derivadas do tipo)
                p.setVantagens(calculadoraTipo.calcularVantagens(p.getTipoPrincipal(), p.getTipoSecundario()));
                p.setDesvantagens(calculadoraTipo.calcularDesvantagens(p.getTipoPrincipal(), p.getTipoSecundario()));
                pokedex.adicionar(p);
            }
            // Religa as associações de evolução/desevolução entre os objetos já carregados
            for (Pokemon p : pokedex.listarTodos()) {
                if (p.getNumeroRelacionado() != -1) {
                    pokedex.buscarPorNumero(p.getNumeroRelacionado()).ifPresent(p::setRelacionado);
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: nao foi possivel carregar o arquivo de Pokemon (" + e.getMessage()
                    + "). Iniciando com uma Pokedex vazia.");
        }
    }

    public int proximoNumero() {
        return pokedex.proximoNumero();
    }

    public void adicionar(Pokemon pokemon) throws PokemonDuplicadoException {
        if (pokedex.existeNumero(pokemon.getNumero())) {
            throw new PokemonDuplicadoException(
                    "Ja existe um Pokemon cadastrado com o numero #" + pokemon.getNumero() + ".");
        }
        pokedex.adicionar(pokemon);
    }

    public void salvarEmArquivo() throws IOException {
        repositorioArquivo.salvarTodos(pokedex.listarTodos());
    }

    public Optional<Pokemon> buscarPorNumero(int numero) {
        return pokedex.buscarPorNumero(numero);
    }

    public List<Pokemon> buscarPorNome(String termo) {
        return pokedex.buscarPorNome(termo);
    }

    public List<Pokemon> listarTodos() {
        return pokedex.listarTodos();
    }

    public boolean estaVazia() {
        return pokedex.estaVazia();
    }
}
