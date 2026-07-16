package pokedex.ui;

import java.util.Scanner;
import pokedex.abstracts.MenuBase;
import pokedex.repository.PokemonRepositorio;
import pokedex.service.PokemonService;

public class TelaPrincipal extends MenuBase {
   // Códigos de cores ANSI para destacar as informações
   private static final String RESET = "\u001B[0m";
   private static final String RED = "\u001B[31m";
   private static final String CYAN = "\u001B[36m";
   private static final String YELLOW = "\u001B[33m";
   private static final String GREEN = "\u001B[32m";
   private static final String BLUE = "\u001B[34m";
   private static final String BOLD = "\u001B[1m";

   // LOGO TOTALMENTE CALIBRADO (Espaços e barras invertidas corrigidos para o X)
   private static final String LOGO = 
      "      ____   ____  _  __ ______ _____  ______  __  __\n" +
      "     |  _ \\ / __ \\| |/ /|  ____|  __ \\|  ____| \\ \\/ /\n" +
      "     | |_) | |  | | ' / | |__  | |  | | |__     \\  / \n" +
      "     |  __/| |  | |  <  |  __| | |  | |  __|    /  \\ \n" +
      "     | |   | |__| | . \\ | |____| |__| | |____  / /\\ \\\n" +
      "     |_|    \\____/|_|\\_\\|______|_____/|______|/_/  \\_\\\n";

   private final PokemonService pokemonService;

   public TelaPrincipal(Scanner scanner) {
      super("Pokedex", scanner);
      PokemonRepositorio repositorio = new PokemonRepositorio();
      this.pokemonService = new PokemonService(repositorio);
   }

   private void limparConsole() {
      System.out.print("\033[H\033[2J");
      System.out.flush();
   }

   @Override
   protected void exibirOpcoes() {
      limparConsole();
      
      // Imprime o Logo perfeitamente em Vermelho Negrito
      System.out.println(RED + BOLD + LOGO + RESET);
      
      // Caixa do menu recalibrada caractere por caractere para ficar reta
      System.out.println(CYAN + "+---------------------------------------------------+" + RESET);
      System.out.println(CYAN + "| " + RESET + BOLD + "            Bem-vindo(a) a sua Pokedex!          " + RESET + CYAN + " |" + RESET);
      System.out.println(CYAN + "+---------------------------------------------------+" + RESET);
      System.out.println(CYAN + "| " + RESET + " " + GREEN + "[1]" + RESET + " Cadastrar novo Pokemon                       " + CYAN + "|" + RESET);
      System.out.println(CYAN + "| " + RESET + " " + BLUE + "[2]" + RESET + " Visualizar Pokemon                           " + CYAN + "|" + RESET);
      System.out.println(CYAN + "| " + RESET + " " + YELLOW + "[3]" + RESET + " Editar Pokemon                               " + CYAN + "|" + RESET);
      System.out.println(CYAN + "| " + RESET + " " + RED + "[0]" + RESET + " Sair                                         " + CYAN + "|" + RESET);
      System.out.println(CYAN + "+---------------------------------------------------+" + RESET);
      System.out.print("\nDigite sua opcao: ");
   }

   @Override
   protected boolean processarOpcao(String opcao) {
      switch (opcao) {
         case "0":
            System.out.println(GREEN + "\nAte a proxima, treinador(a)!" + RESET);
            return false;
         case "1":
            (new TelaCadastroPokemon(this.scanner, this.pokemonService)).iniciar();
            return true;
         case "2":
            (new TelaConsultaPokemon(this.scanner, this.pokemonService)).iniciar();
            return true;
         case "3":
            (new TelaEdicaoPokemon(this.scanner, this.pokemonService)).iniciar();
            return true;
            
      }

      System.out.println(RED + "\nOpcao invalida. Tente novamente." + RESET);
      this.pausar();
      return true;
   }

   private void pausar() {
      System.out.print("Pressione ENTER para continuar...");
      this.scanner.nextLine();
   }
}