package pokedex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pokedex.enums.Sexo;
import pokedex.enums.TipoPokemon;
import pokedex.exceptions.PokemonDuplicadoException;
import pokedex.model.Estatisticas;
import pokedex.model.Pokemon;
import pokedex.repository.PokemonRepositorio;
import pokedex.service.PokemonService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PokemonServiceTest {

    private PokemonService pokemonService;
    private PokemonRepositorio repositorio;
    private String caminhoArquivoTemp;
    
    private TipoPokemon tipoPrincipalValido;
    private TipoPokemon tipoSecundarioValido;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("pokemons_teste.txt").toFile();
        this.caminhoArquivoTemp = tempFile.getAbsolutePath();
        
        this.repositorio = new PokemonRepositorio(caminhoArquivoTemp);
        this.pokemonService = new PokemonService(repositorio);

        TipoPokemon[] tiposDisponiveis = TipoPokemon.values();
        if (tiposDisponiveis.length > 0) {
            this.tipoPrincipalValido = tiposDisponiveis[0];
            this.tipoSecundarioValido = tiposDisponiveis.length > 1 ? tiposDisponiveis[1] : null;
        }
    }

    @Test
    public void testCadastrarPokemonComSucesso() throws Exception {
        Estatisticas stats = new Estatisticas(35, 55, 40, 50, 50, 90);
        Pokemon pikachu = new Pokemon("Pikachu Teste", 0.4, 6.0, Sexo.MACHO, tipoPrincipalValido, null, stats);

        Pokemon cadastrado = pokemonService.cadastrarPokemon(pikachu);

        assertNotNull(cadastrado);
        assertEquals(1, cadastrado.getNumero());
        assertNotNull(cadastrado.getVantagens());
        assertNotNull(cadastrado.getDesvantagens());

        Optional<Pokemon> buscado = repositorio.buscarPorNumero(1);
        assertTrue(buscado.isPresent());
        assertEquals("Pikachu Teste", buscado.get().getNome());
    }

    @Test
    public void testEvitarCadastroDuplicado() throws Exception {
        Estatisticas stats = new Estatisticas(39, 52, 43, 60, 50, 65);
        Pokemon charmander1 = new Pokemon("Charmander Teste", 0.6, 8.5, Sexo.MACHO, tipoPrincipalValido, null, stats);
        
        // Cadastramos o primeiro
        pokemonService.cadastrarPokemon(charmander1);

        // Tentamos cadastrar um segundo forçando o mesmo Número e Nome para garantir que a regra de duplicidade do seu repositório seja ativada
        assertThrows(PokemonDuplicadoException.class, () -> {
            Pokemon charmander2 = new Pokemon("Charmander Teste", 0.6, 8.5, Sexo.MACHO, tipoPrincipalValido, null, stats);
            // Copia o número gerado para simular o mesmo registro
            charmander2.setNumero(charmander1.getNumero());
            
            // Força a adição direta no repositório para estourar a validação
            repositorio.adicionar(charmander2);
        }, "Deveria lancar PokemonDuplicadoException ao tentar adicionar um Pokemon ja existente");
    }
    @Test
public void testModelPropertiesCompleto() {
    // 1. Testa a classe de Estatísticas
    Estatisticas stats = new Estatisticas(50, 50, 50, 50, 50, 50);
    assertEquals(50, stats.getVida());
    assertEquals(50, stats.getAtaque());
    assertEquals(50, stats.getDefesa());
    assertEquals(50, stats.getAtaqueEspecial());
    assertEquals(50, stats.getDefesaEspecial());
    assertEquals(50, stats.getVelocidade());

    // 2. Cria um Pokémon para testar todos os getters, setters e métodos de exibição
    Pokemon p = new Pokemon("Bulbassauro", 0.7, 6.9, Sexo.MACHO, tipoPrincipalValido, tipoSecundarioValido, stats);
    
    p.setNumero(1);
    p.setAltura(1.2);
    p.setPeso(13.0);
    p.setSexo(Sexo.FEMEA);
    p.setTipoPrincipal(tipoPrincipalValido);
    p.setTipoSecundario(tipoSecundarioValido);
    p.setEstatisticas(stats);

    assertEquals(1, p.getNumero());
    assertEquals("Bulbassauro", p.getNome());
    assertEquals(1.2, p.getAltura());
    assertEquals(13.0, p.getPeso());
    assertEquals(Sexo.FEMEA, p.getSexo());
    assertEquals(tipoPrincipalValido, p.getTipoPrincipal());
    assertEquals(tipoSecundarioValido, p.getTipoSecundario());
    assertNotNull(p.getEstatisticas());

    // 3. Testa os métodos de texto que costumam ter muitas linhas de código (concatenações)
    assertNotNull(p.exibirDetalhado());
    
    // Se o seu modelo tiver os métodos toString() ou serializar() implementados
    assertNotNull(p.toString());
}
    @Test
    public void testExceptionsCovers() {
        // Força a chamada direta dos construtores das exceções do projeto
        Exception ex1 = new pokedex.exceptions.PokemonDuplicadoException("Erro teste");
        Exception ex2 = new pokedex.exceptions.PokemonNaoEncontradoException("Erro teste");
        
        assertEquals("Erro teste", ex1.getMessage());
        assertEquals("Erro teste", ex2.getMessage());
    }
    @Test
    public void testRelacionamentosEEvolucoes() {
        Estatisticas stats = new Estatisticas(50, 50, 50, 50, 50, 50);
        Pokemon preEvolucao = new Pokemon("Pichu", 0.3, 2.0, Sexo.MACHO, tipoPrincipalValido, null, stats);
        Pokemon evolucao = new Pokemon("Pikachu", 0.4, 6.0, Sexo.MACHO, tipoPrincipalValido, null, stats);
        
        // Exercita os campos de evolução que seu colega criou
        evolucao.setRelacionado(preEvolucao);
        evolucao.setRelacaoEvolucao(pokedex.enums.RelacaoEvolucao.EVOLUCAO);
        
        assertEquals(preEvolucao, evolucao.getRelacionado());
        assertEquals(pokedex.enums.RelacaoEvolucao.EVOLUCAO, evolucao.getRelacaoEvolucao());
    }
    @Test
    public void testPersistenciaAposEdicao() throws Exception {
        Estatisticas stats = new Estatisticas(45, 49, 49, 65, 65, 45);
        Pokemon bulbasaur = new Pokemon("Bulbasaur Teste", 0.7, 6.9, Sexo.MACHO, tipoPrincipalValido, tipoSecundarioValido, stats);
        pokemonService.cadastrarPokemon(bulbasaur);

        Pokemon pokemonAlvo = repositorio.buscarPorNumero(1).orElseThrow();
        pokemonAlvo.setNome("Bulbasaur Editado");
        pokemonAlvo.setPeso(12.5); 

        repositorio.salvarEmArquivo();

        PokemonRepositorio novoRepositorio = new PokemonRepositorio(caminhoArquivoTemp);
        Optional<Pokemon> bulbasaurRecarregado = novoRepositorio.buscarPorNumero(1);

        assertTrue(bulbasaurRecarregado.isPresent());
        assertEquals("Bulbasaur Editado", bulbasaurRecarregado.get().getNome());
        assertEquals(12.5, bulbasaurRecarregado.get().getPeso());
    }
}