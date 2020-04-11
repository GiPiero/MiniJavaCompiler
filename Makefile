JAVAC := javac
JAVACC:= javacc
SUPPORT := support.jar

default	:	compiler

compiler:	check.jar

parser.jj:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/*.jj

check.jar: parser.jj
	$(JAVAC) -classpath .:$(SUPPORT) global/*.java
	$(JAVAC) -classpath .:$(SUPPORT) main/*.java
	$(JAVAC) -classpath .:$(SUPPORT) parser/*.java
	$(JAVAC) -classpath .:$(SUPPORT) symbol/*.java
	$(JAVAC) -classpath .:$(SUPPORT) symbol/*/*.java
	echo "Manifest-Version: 1.0" > manifest.txt
	#echo "Main-Class: main.Main" >> manifest.txt
	echo "Class-Path: $(SUPPORT)" >> manifest.txt
	jar cmfe manifest.txt  $@ main.Parse main/*.class parser/*.class symbol/*.class symbol/*/*.class global/*.class

clean:
	-/bin/rm -f manifest.txt
	-/bin/rm -f *.class
	-/bin/rm -f */*.class
	-/bin/rm -f symbol/*/*.class
	-/bin/rm -f check.jar

clean_all: clean
	-/bin/rm -f parser/*.java
