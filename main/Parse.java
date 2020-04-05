package main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import parser.MiniJavaParser;
import parser.ParseException;
import parser.Token;
import static parser.MiniJavaParserConstants.EOF;
import static parser.MiniJavaParserConstants.INVALID;
import global.FieldNames;

public class Parse {
	public static void main(String [] args) {
		final MiniJavaParser parser;
		final boolean verbose;
		final String in_file;
		
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

		try { parser = new MiniJavaParser (new FileInputStream(in_file));} 
		catch (Exception e) { System.out.println(e); return; }

		if (verbose) parser.enable_tracing();
		else parser.disable_tracing();
		
		try {
			parser.in_file = in_file;
			parser.Program();
			System.out.println("filename=" + in_file + ", errors=" + parser.errors);
		} catch (ParseException e) {
			System.out.println(e.toString());
		}


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
