package utils;

public class PrintUtils {

    private static final String RESET = "\u001B[0m";

    public static void printError(String texto){
        System.out.println("\u001B[31m" + texto + RESET);
    }

    public static void printResultData(String texto){
        System.out.println("\u001B[35m" + texto + RESET);
    }

    public static void printAdditionaInformation(String texto){
        System.out.println("\u001B[32m" + texto + RESET);
    }

    public static void printLoadingInformation(String texto){
        System.out.println("\u001B[34m" + texto + RESET);
    }

    public static void printCyan(String texto){
        System.out.println("\u001B[36m" + texto + RESET);
    }



}
