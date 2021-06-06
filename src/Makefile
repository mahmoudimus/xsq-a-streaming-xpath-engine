#$Id: Makefile,v 1.6 2002/10/24 16:42:15 pengfeng Exp $#

JDKHOME = /usr/java/j2sdk1.4.0_01

JAVAC   = $(JDKHOME)/bin/javac
JAVA    = $(JDKHOME)/bin/java
JAVADOC = $(JDKHOME)/bin/javadoc

XSQHOME = $(shell cd ..; pwd)
XSQSRC = $(XSQHOME)/src

CLASSPATH=$(JDKHOME)/lib/rt.jar:$(XSQSRC):$(XSQHOME)/lib/xerces.jar:$(XSQHOME)/lib:$(XSQHOME)/lib/grappa.jar:$(XSQHOME)/bin:$(XSQHOME)

CLASS  = ItemFlag.class Consts.class Tools.class Evaluator.class Output.class Event.class Filter.class XPathNode.class QueueItem.class  PDTState.class PDTArc.class CurrentStateSet.class XPathParser.class BasicPDT.class  StatBuffer.class PDTStatBuffer.class HPDTOutputQ.class PDTOutputQ.class HPDT.class XSQParser.class XSQ.class ExampleFileFilter.class GridBagHelper.class MessageBox.class XSQMenuBar.class  XSQPanel.class XSQFrame.class 
SOURCE = ItemFlag.java  Consts.java  Tools.java  Evaluator.java  Output.java  Event.java  Filter.java  XPathNode.java  QueueItem.java    PDTState.java  PDTArc.java  CurrentStateSet.java  XPathParser.java  BasicPDT.java  StatBuffer.java  PDTStatBuffer.java  HPDTOutputQ.java  PDTOutputQ.java  HPDT.java  XSQParser.java  XSQ.java  ExampleFileFilter.java  GridBagHelper.java  MessageBox.java  XSQMenuBar.java   XSQPanel.java  XSQFrame.java 

all : $(CLASS)
	cp $(XSQHOME)/bin/edu/umd/cs/db/xsq/*.class .
	#mv xsq $(XSQHOME)/bin

javadoc : 
	$(JAVADOC) -d $(XSQHOME)/doc -classpath $(CLASSPATH)  $(SOURCE) 

clean: cleanclasses cleanbin cleantmp

cleanall : cleanclasses cleanbin cleanjavadoc

cleanbin:
	rm -f $(XSQHOME)/bin/xsq 

cleanclasses : 
	rm -f *.class
	rm -f $(XSQHOME)/bin/edu/umd/cs/db/xsq/*.class

cleanjavadoc : 
	rm -rf $(XSQHOME)/doc/* 

cleantmp:
	rm -f *~
	rm -f $(XSQHOME)/bin/*~
	rm -f $(XSQHOME)/bin/*.log

.SUFFIXES: .java .class 

.java.class :
	$(JAVAC) -classpath $(CLASSPATH) -g:none -d $(XSQHOME)/bin  $*.java 













