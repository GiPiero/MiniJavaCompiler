JAVAC := javac
JAVACC:= javacc


default	:	compiler

compiler:	parser.jj parse.jar

parser.jj:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/*.jj

parse.jar:
	$(JAVAC) */*.java
	jar cfe $@ main.Parse main/*.class parser/*.class global/*.class

clean:
	-/bin/rm -f *.class
	-/bin/rm -f */*.class
	-/bin/rm -f parse.jar

clean_all:
	-/bin/rm -f parser/*.java
