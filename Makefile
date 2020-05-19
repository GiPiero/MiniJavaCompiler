JAVAC := javac
JAVACC:= javacc
GCC	:= sparc-linux-gcc
SUPPORT := support.jar

default	:	compiler

compiler:	compile.jar

permissions: compile
	chmod a+x compile

parser.jj:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/*.jj

runtime.o : runtime.c
	$(GCC) -Wall -c runtime.c -o runtime.o

compile.jar: parser.jj runtime.o
	$(JAVAC) -classpath .:$(SUPPORT) global/*.java
	$(JAVAC) -classpath .:$(SUPPORT) main/*.java
	$(JAVAC) -classpath .:$(SUPPORT) parser/*.java
	$(JAVAC) -classpath .:$(SUPPORT) symbol/*.java
	$(JAVAC) -classpath .:$(SUPPORT) symbol/*/*.java
	$(JAVAC) -classpath .:$(SUPPORT) frame/*.java
	$(JAVAC) -classpath .:$(SUPPORT) sparc/*.java
	$(JAVAC) -classpath .:$(SUPPORT) translate/*.java
	$(JAVAC) -classpath .:$(SUPPORT) translate/*/*.java
	$(JAVAC) -classpath .:$(SUPPORT) graph/*.java
	echo "Manifest-Version: 1.0" > manifest.txt
	#echo "Main-Class: main.Compiler" >> manifest.txt
	echo "Class-Path: $(SUPPORT)" >> manifest.txt
	jar cmfe manifest.txt  $@ main.Compiler main/*.class parser/*.class symbol/*.class symbol/*/*.class global/*.class frame/*.class sparc/*.class translate/*.class translate/*/*.class graph/*.class

clean:
	-/bin/rm -f manifest.txt
	-/bin/rm -f *.class
	-/bin/rm -f */*.class
	-/bin/rm -f symbol/*/*.class
	-/bin/rm -f translate/*/*.class
	-/bin/rm -f compile.jar
	-/bin/rm -f graph/*.class
	-/bin/rm -f runtime.o

clean_all: clean
	-/bin/rm -f parser/*.java
