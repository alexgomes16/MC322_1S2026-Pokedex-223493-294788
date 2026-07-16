package pokedex.abstracts;

import pokedex.interfaces.Persistivel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base genérica para repositórios que persistem uma lista de objetos
 * {@link Persistivel} em um arquivo de texto, uma linha por objeto
 *
 * Cada subclasse concreta (ex: PokemonRepositorioArquivo) só precisa
 * saber como reconstruir seu tipo específico a partir de uma linha
 *
 * @param <T> tipo do objeto persistido, que deve saber se serializar
 */
public abstract class RepositorioArquivoBase<T extends Persistivel> {

    protected final String caminhoArquivo;

    protected RepositorioArquivoBase(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public void salvarTodos(List<T> itens) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            for (T item : itens) {
                writer.write(item.serializar());
                writer.newLine();
            }
        }
    }

    public List<T> carregarTodos() throws IOException {
        List<T> itens = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            return itens;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.isBlank()) {
                    itens.add(deserializar(linha));
                }
            }
        }
        return itens;
    }

    /** 
     * Cada subclasse sabe como reconstruir seu objeto a partir de uma linha do arquivo
     */
    protected abstract T deserializar(String linha);
}
