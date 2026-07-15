package pokedex.model;

/**
 * Estatísticas de combate de um Pokémon: Vida, Ataque, Defesa,
 * Ataque Especial, Defesa Especial e Velocidade.
 */
public class Estatisticas {

    private int vida;
    private int ataque;
    private int defesa;
    private int ataqueEspecial;
    private int defesaEspecial;
    private int velocidade;

    public Estatisticas(int vida, int ataque, int defesa, int ataqueEspecial,
                         int defesaEspecial, int velocidade) {
        this.vida = validar(vida, "Vida");
        this.ataque = validar(ataque, "Ataque");
        this.defesa = validar(defesa, "Defesa");
        this.ataqueEspecial = validar(ataqueEspecial, "Ataque Especial");
        this.defesaEspecial = validar(defesaEspecial, "Defesa Especial");
        this.velocidade = validar(velocidade, "Velocidade");
    }

    private int validar(int valor, String nomeCampo) {
        if (valor < 0) {
            throw new IllegalArgumentException(nomeCampo + " nao pode ser negativo.");
        }
        return valor;
    }

    public String exibir() {
        return String.format(
                "Vida: %d | Ataque: %d | Defesa: %d | Atq. Especial: %d | Def. Especial: %d | Velocidade: %d",
                vida, ataque, defesa, ataqueEspecial, defesaEspecial, velocidade
        );
    }

    /** Serializa os campos separados por ";" (usado dentro do "campo" de estatísticas no arquivo). */
    public String serializar() {
        return vida + ";" + ataque + ";" + defesa + ";" + ataqueEspecial + ";" + defesaEspecial + ";" + velocidade;
    }

    public static Estatisticas deserializar(String dados) {
        String[] partes = dados.split(";", -1);
        return new Estatisticas(
                Integer.parseInt(partes[0]),
                Integer.parseInt(partes[1]),
                Integer.parseInt(partes[2]),
                Integer.parseInt(partes[3]),
                Integer.parseInt(partes[4]),
                Integer.parseInt(partes[5])
        );
    }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = validar(vida, "Vida"); }

    public int getAtaque() { return ataque; }
    public void setAtaque(int ataque) { this.ataque = validar(ataque, "Ataque"); }

    public int getDefesa() { return defesa; }
    public void setDefesa(int defesa) { this.defesa = validar(defesa, "Defesa"); }

    public int getAtaqueEspecial() { return ataqueEspecial; }
    public void setAtaqueEspecial(int ataqueEspecial) { this.ataqueEspecial = validar(ataqueEspecial, "Ataque Especial"); }

    public int getDefesaEspecial() { return defesaEspecial; }
    public void setDefesaEspecial(int defesaEspecial) { this.defesaEspecial = validar(defesaEspecial, "Defesa Especial"); }

    public int getVelocidade() { return velocidade; }
    public void setVelocidade(int velocidade) { this.velocidade = validar(velocidade, "Velocidade"); }
}
