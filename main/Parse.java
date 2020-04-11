package main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import parser.MiniJavaParser;
import parser.ParseException;
import parser.Token;
import static parser.MiniJavaParserConstants.EOF;
import static parser.MiniJavaParserConstants.INVALID;
import global.FieldNames;
import main.CompileError;
import syntax.Program;
import symbol.SymbolTableBuilder;
import symbol.SymbolTable;

public class Parse {
    public static void main(String [] args) {
        final MiniJavaParser parser;
        final boolean verbose;
        final String in_file;
        Program program = null;

        // Parse arguments
        if (args.length == 1) {
            in_file = args[0];
            verbose = false;
        } else if (args.length == 2 && args[0].equals("-v")) {
            in_file = args[1];
            verbose = true;
        } else {
            System.err.println("Invalid arguments");
            return;
        }

        // Init error handler
        CompileError.in_file = in_file;
        CompileError.errors = 0;

        // Init parser
        try { parser = new MiniJavaParser (new FileInputStream(in_file));}
        catch (Exception e) { System.out.println(e); return; }

        // Enable parser debug output
        if (verbose) parser.enable_tracing();
        else parser.disable_tracing();

        // Analyze input lexemes
		/*
		// This does not work
		lexical_analysis(parser, in_file, false);
		if(CompileError.errors != 0){
			System.out.println("filename=" + in_file + ", errors=" + CompileError.errors);
			return;
		}*/

        // Parse input tokens
        try { program = parser.JProgram(); }
        catch (ParseException e) { System.out.println(e.toString()); }
        if(CompileError.errors != 0){
            System.out.println("filename=" + in_file + ", errors=" + CompileError.errors);
            return;
        }

        // Analyze semantics and build symbol table
        SymbolTableBuilder st_builder = new SymbolTableBuilder();
        SymbolTable st = st_builder.buildSymbolTable(program);

        System.out.println("filename=" + in_file + ", errors=" + CompileError.errors);
    }

    public static void lexical_analysis(MiniJavaParser parser, String in_file, boolean verbose) {
        final FieldNames t_map = new FieldNames("parser.MiniJavaParserConstants");
        int errs = 0;
        Token t;

        do {
            t = parser.getNextToken();

            switch(t.kind){
                case INVALID:
                    errs += 1;
                    System.err.printf("%s:%03d.%03d: ERROR -- illegal character %s\n", in_file, t.beginLine, t.beginColumn, t.image);
                    break;
                default:
                    if ( verbose )
                        System.out.printf("%s:%03d.%03d: %s \"%s\"\n",
                                in_file, t.beginLine, t.beginColumn,
                                t_map.get(t.kind), t.image);
            }
        } while (t.kind != EOF);

        System.out.println("filename=" + in_file + ", errors=" + errs);
    }
}
