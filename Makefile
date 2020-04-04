JAVAC := javac
JAVACC:= javacc


default	:	compiler

compiler:	scanner.jj scan.jar

scanner.jj:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/*.jj

scan.jar:
	$(JAVAC) */*.java
	jar cfe $@ main.Scan main/*.class parser/*.class global/*.class

clean:
	-/bin/rm -f *.class
	-/bin/rm -f */*.class
	-/bin/rm -f scan.jar

clean_all:
	-/bin/rm -f parser/*.java
