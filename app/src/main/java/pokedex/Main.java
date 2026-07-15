package pokedex;

import pokedex.ui.TelaPrincipal;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TelaPrincipal telaPrincipal = new TelaPrincipal(scanner);
        telaPrincipal.iniciar();
        scanner.close();
    }
}
