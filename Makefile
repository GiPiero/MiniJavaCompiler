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
	-/bin/rm *~ */*~
	-/bin/rm */*.class

clean_all:
	-/bin/rm */scan.jar
