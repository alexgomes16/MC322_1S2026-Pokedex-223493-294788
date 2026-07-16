package pokedex.interfaces;

/**
 * Contrato para qualquer elemento que saiba se converter em uma
 * linha de texto para ser salvo em arquivo
 */
public interface Persistivel {
    String serializar();
}
