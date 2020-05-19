package main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import assem.Instruction;
import frame.Access;
import graph.FlowGraph;
import graph.InterferenceGraph;
import parser.MiniJavaParser;
import parser.ParseException;
import parser.Token;
import static parser.MiniJavaParserConstants.EOF;
import static parser.MiniJavaParserConstants.INVALID;
import global.FieldNames;
import main.CompileError;
import sparc.CodeGen;
import sparc.RegAlloc;
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

public class Compiler {
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
        if(CompileError.errors !=0) return;

        // Translate to IR tree
        TranslateVisitor tv = new TranslateVisitor(st,
                new SPARCFrame(new NameOfLabel("factory"), new ArrayList<Access>()));
        tv.visit(program);

        // Open assembly output file and, if verbose, debug output file
        FileWriter fwV = null;
        if(verbose) fwV = new FileWriter("debug08.txt");
        final FileWriter fwS = new FileWriter(in_file.split("\\.")[0] + ".s");
        fwS.write("        .global start\nstart:\n");

        // Iterate through fragments and canonicalize them
        for(Fragment f : tv.fragments){
            ArrayList<Instruction> instList = new ArrayList<>();

            // Canonicalize fragment body
            List<Stm> stm_list = canon.Main.transform(f.body);

            // Generate instructions
            CodeGen cg = new CodeGen((SPARCFrame) f.frame);

            for(Stm s : stm_list) {
                if(verbose) {
                    // Write canonicalize IR tree statement to debug output
                    if (s instanceof LABEL) fwV.write("\n");
                    fwV.write(s.toString());
                }

                // Generate instructions from statement.
                instList = cg.codegen(s);
            }

            instList = f.frame.procEntryExit3(instList);

            // Allocate registers
            RegAlloc ra = new RegAlloc((SPARCFrame) f.frame,
                    new graph.InterferenceGraph(new FlowGraph(instList)));
            ra.allocRegs();

            if(verbose){
                fwV.write("- - - -\n");
                for(Instruction inst : instList)
                    fwV.write(inst.format() + "\n");
                fwV.write("- - - -\n");
            }
            // Write completed assembly to file
            for(Instruction inst : instList){
                // Don't write mov instructions with the same dest and src register
                if(inst.isMove() && (inst.use().get(0) != null && inst.def().get(0) != null)
                    && (f.frame.tempMap.get(inst.use().get(0)) == f.frame.tempMap.get(inst.def().get(0))))
                            continue;

                fwS.write(inst.format(f.frame.tempMap) + "\n");
            }
        }
        try { fwS.close(); if(verbose) fwV.close();}
        catch (IOException e) { System.err.println("ERROR:main: IOException: " + e.toString()); }
    }

//    public static void lexical_analysis(MiniJavaParser parser, String in_file, boolean verbose) {
//        final FieldNames t_map = new FieldNames("parser.MiniJavaParserConstants");
//        int errs = 0;
//        Token t;
//
//        do {
//            t = parser.getNextToken();
//
//            switch(t.kind){
//                case INVALID:
//                    errs += 1;
//                    System.err.printf("%s:%03d.%03d: ERROR -- illegal character %s\n", in_file, t.beginLine, t.beginColumn, t.image);
//                    break;
//                default:
//                    if ( verbose )
//                        System.out.printf("%s:%03d.%03d: %s \"%s\"\n",
//                                in_file, t.beginLine, t.beginColumn,
//                                t_map.get(t.kind), t.image);
//            }
//        } while (t.kind != EOF);
//
//        System.out.println("filename=" + in_file + ", errors=" + errs);
//    }
}
