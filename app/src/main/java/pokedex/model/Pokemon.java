package pokedex.model;

import pokedex.enums.RelacaoEvolucao;
import pokedex.enums.Sexo;
import pokedex.enums.TipoPokemon;
import pokedex.interfaces.Buscavel;
import pokedex.interfaces.Exibivel;
import pokedex.interfaces.Persistivel;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Pokémon catalogado na Pokédex.
 *
 * Implementa três interfaces do projeto:
 *  - Exibivel: sabe se mostrar resumido (listagens) ou detalhado;
 *  - Buscavel: sabe dizer se corresponde a um termo de pesquisa;
 *  - Persistivel: sabe se converter em uma linha de arquivo.
 *
 * Também mantém uma associação (relacionado) com outro Pokémon já
 * cadastrado, representando uma relação de evolução/desevolução.
 */
public class Pokemon implements Exibivel, Buscavel, Persistivel {

    private int numero;
    private String nome;
    private double altura;
    private double peso;
    private Sexo sexo;
    private TipoPokemon tipoPrincipal;
    private TipoPokemon tipoSecundario;

    private Estatisticas estatisticas;

    // Associação com outro Pokémon já cadastrado (evolução ou desevolução)
    private Pokemon relacionado;
    private RelacaoEvolucao relacaoEvolucao = RelacaoEvolucao.NENHUMA;

    // Usado apenas durante a leitura do arquivo, para religar a associação
    // acima depois que todos os Pokémon já tiverem sido carregados.
    private int numeroRelacionado = -1;

    // Calculados automaticamente pela CalculadoraTipo com base no(s) tipo(s)
    private List<TipoPokemon> vantagens = new ArrayList<>();
    private List<TipoPokemon> desvantagens = new ArrayList<>();

    public Pokemon(String nome, double altura, double peso, Sexo sexo,
                   TipoPokemon tipoPrincipal, TipoPokemon tipoSecundario,
                   Estatisticas estatisticas) {
        this.nome = nome;
        this.altura = altura;
        this.peso = peso;
        this.sexo = sexo;
        this.tipoPrincipal = tipoPrincipal;
        this.tipoSecundario = tipoSecundario;
        this.estatisticas = estatisticas;
    }

    //Implementação das interfaces

    @Override
    public boolean correspondeATermo(String termo) {
        return nome != null && termo != null && nome.toLowerCase().contains(termo.toLowerCase());
    }

    @Override
    public String exibirResumo() {
        return String.format("#%03d - %s", numero, nome);
    }

    @Override
    public String exibirDetalhado() {
        StringBuilder sb = new StringBuilder();
        sb.append("Numero: #").append(String.format("%03d", numero)).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Altura: ").append(altura).append(" m\n");
        sb.append("Peso: ").append(peso).append(" kg\n");
        sb.append("Sexo: ").append(sexo).append("\n");
        sb.append("Tipo: ").append(tipoPrincipal.getNomeExibicao());
        if (tipoSecundario != null) {
            sb.append(" / ").append(tipoSecundario.getNomeExibicao());
        }
        sb.append("\n");
        sb.append("Estatisticas -> ").append(estatisticas.exibir()).append("\n");
        if (relacionado != null && relacaoEvolucao != RelacaoEvolucao.NENHUMA) {
            String rotulo = relacaoEvolucao == RelacaoEvolucao.EVOLUCAO
                    ? "Evolui de"
                    : "Forma anterior (desevolucao) de";
            sb.append(rotulo).append(": ").append(relacionado.getNome()).append("\n");
        }
        sb.append("Vantagem contra: ").append(formatarTipos(vantagens)).append("\n");
        sb.append("Desvantagem contra: ").append(formatarTipos(desvantagens)).append("\n");
        return sb.toString();
    }

    private String formatarTipos(List<TipoPokemon> tipos) {
        if (tipos == null || tipos.isEmpty()) {
            return "Nenhuma";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tipos.size(); i++) {
            sb.append(tipos.get(i).getNomeExibicao());
            if (i < tipos.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String serializar() {
        String tipoSecStr = tipoSecundario == null ? "" : tipoSecundario.name();
        String relacionadoStr = relacionado == null ? "" : String.valueOf(relacionado.getNumero());
        return String.join("|",
                String.valueOf(numero),
                nome,
                String.valueOf(altura),
                String.valueOf(peso),
                sexo.name(),
                tipoPrincipal.name(),
                tipoSecStr,
                estatisticas.serializar(),
                relacaoEvolucao.name(),
                relacionadoStr
        );
    }

    //Getters e Setters

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public TipoPokemon getTipoPrincipal() { return tipoPrincipal; }
    public void setTipoPrincipal(TipoPokemon tipoPrincipal) { this.tipoPrincipal = tipoPrincipal; }

    public TipoPokemon getTipoSecundario() { return tipoSecundario; }
    public void setTipoSecundario(TipoPokemon tipoSecundario) { this.tipoSecundario = tipoSecundario; }

    public Estatisticas getEstatisticas() { return estatisticas; }
    public void setEstatisticas(Estatisticas estatisticas) { this.estatisticas = estatisticas; }

    public Pokemon getRelacionado() { return relacionado; }
    public void setRelacionado(Pokemon relacionado) { this.relacionado = relacionado; }

    public RelacaoEvolucao getRelacaoEvolucao() { return relacaoEvolucao; }
    public void setRelacaoEvolucao(RelacaoEvolucao relacaoEvolucao) { this.relacaoEvolucao = relacaoEvolucao; }

    public int getNumeroRelacionado() { return numeroRelacionado; }
    public void setNumeroRelacionado(int numeroRelacionado) { this.numeroRelacionado = numeroRelacionado; }

    public List<TipoPokemon> getVantagens() { return vantagens; }
    public void setVantagens(List<TipoPokemon> vantagens) { this.vantagens = vantagens; }

    public List<TipoPokemon> getDesvantagens() { return desvantagens; }
    public void setDesvantagens(List<TipoPokemon> desvantagens) { this.desvantagens = desvantagens; }
}
