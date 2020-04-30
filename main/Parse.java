package main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import assem.Instruction;
import frame.Access;
import parser.MiniJavaParser;
import parser.ParseException;
import parser.Token;
import static parser.MiniJavaParserConstants.EOF;
import static parser.MiniJavaParserConstants.INVALID;
import global.FieldNames;
import main.CompileError;
import sparc.CodeGen;
import sparc.SPARCFrame;
import syntax.Program;
import symbol.SymbolTableBuilder;
import symbol.SymbolTable;
import translate.Fragment;
import translate.TranslateVisitor;
import tree.LABEL;
import tree.NameOfLabel;
import canon.Main;
import tree.Stm;

public class Parse {
    public static void main(String [] args) throws IOException{
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
        parser = new MiniJavaParser (new FileInputStream(in_file));

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

        if(CompileError.errors !=0)
            return;

        // Translate to IR tree
        TranslateVisitor tv = new TranslateVisitor(st,
                new SPARCFrame(new NameOfLabel("factory"), new ArrayList<Access>()));

        tv.visit(program);

        FileWriter fwS = null;
        FileWriter fwV = null;
        fwS = new FileWriter(in_file.split("\\.")[0] + ".s");
        if(verbose) fwV = new FileWriter("debug08.txt");

        for(Fragment f : tv.fragments){
            ArrayList<Instruction> instlist = new ArrayList<>();
            List<Stm> stm_list = Main.transform(f.body);
            CodeGen cg = new CodeGen((SPARCFrame) f.frame);
            for(Stm s : stm_list) {
                if(verbose) {
                    if (s instanceof LABEL)
                        fwV.write("\n");
                    fwV.write(s.toString());
                }
                instlist = cg.codegen(s);

            }
            for(Instruction inst : f.frame.procEntryExit3(instlist)){
                try {
                    fwS.write(inst.format() + "\n");
                } catch (IOException e) {
                    System.err.println("ERROR:main: IOException: " + e.toString());
                    return;
                }
            }
        }
        try { fwS.close(); if(verbose) fwV.close();}
        catch (IOException e) { System.err.println("ERROR:main: IOException: " + e.toString()); }
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
