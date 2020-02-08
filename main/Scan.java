package main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import parser.MiniJavaScanner;
import parser.Token;
import static parser.MiniJavaScannerConstants.EOF;
import static parser.MiniJavaScannerConstants.INVALID;
import global.FieldNames;

public class Scan {
	public static void main(String [] args){
		final FieldNames t_map = new FieldNames("parser.MiniJavaScannerConstants");
		final MiniJavaScanner lexer;
		final boolean verbose;
		final String in_file;
		int errs = 0;
		Token t;

		if (args.length = 1) { 
			in_file = args[0];
	       		verbose = false;
		} else if (args.length = 2 && args[0] == "-v") {
			in_file = args[1];
			verbose = true;
		} else {
			System.err.println("Invalid arguments");
			return;
		}

		try { lexer = new MiniJavaScanner (new FileInputStream(args[0]));} 
		catch (Exception e) { System.out.println(e); return; }

		do {
			t = lexer.getNextToken();

			switch(t.kind){
				case INVALID:
					errs += 1;
					System.err.printf("%s:%03d.%03d: ERROR -- illegal character %s\n", 
							args[0], t.beginLine, t.beginColumn, t.image);
					break;
				default:
					if ( verbose )
						System.out.printf("%s:%03d.%03d: %s \"%s\"\n", 
								args[0], t.beginLine, t.beginColumn, 
								t_map.get(t.kind), t.image);
			}
		} while (t.kind != EOF);

		System.out.println("filename=" + args[0] + ", errors=" + errs);
	}
}
