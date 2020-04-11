package main;

public class CompileError {
    public static int errors = 0;
    public static String in_file = null;

    public static void printError(int line, int column, String msg) {
        errors++;
        System.err.println(String.format("%s:%d:%d: %s", in_file, line, column, msg));
    }
}
