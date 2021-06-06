# XSQ: A Streaming XPath Engine

Thanks to the authors Feng Peng and Sudarshan S. Chawathe!

XSQ evaluates XPath queries over streaming XML data. That is, it makes only pass over the data, in an order determined by the data source. This behavior is useful for streaming data sources such as news feeds and RSS channels, and also for disk-resident data that is best accessed using a sequential scan. XSQ provides high throughput with minimal buffering.

XSQ is implemented using Java and a SAX parser. The design is based on generating an automaton from the given XPath query. The automaton may be described briefly as an hierarchical arrangement of pushdown transducers augmented with buffers. (For details, please refer to the paper referenced below.) Unlike DOM-based query engines, XSQ does not need to load the entire dataset into memory. As a result, it has a small memory footprint and provides high throughput and low response times. For example, XSQ easily processes datasets 2GB and larger on a modest PC-class machine. (We're always looking for larger and more interesting datasets; please contact us if you'd like to share yours.)

The screenshot below illustrates XSQ's graphical interface being used to query XML files. In addition to the query results, XSQ presents the automaton it uses to process the query. Each box is a standalone BPDT (basic PDT) that has a separate buffer. The buffer operations are labeled on the transitions. The HPDT is essentially a network of BPDTs that can communicate using the buffer operation.

![gui](/big.shot.jpg)

## README

I. Introduction

XSQ is a streaming XPath engine. After you submit an XPath query over
an XML file, it reads in the XML files sequentially and emits the
result while the data is still "streaming" in.  Due to the one-pass
requirement, it is difficult to answer XPath queries  with multiple
predicates, closure axes (descendants), and aggregations.

This program is mainly used for preliminary tests. XPath queries
allowed in this system is described in our paper available at
www.cs.umd.edu/projects/xsq.

II. Status

Since we are developing a new XPath engine, this program is provided
AS IS. The performance is not fine tuned, although it is already
pretty good right now.  We are no longer working on this version.

III. Required packages

1. You need to have Java SDK 1.3 or higher.
2. You need to get the Xerces2 XML parser at:
     http://xml.apache.org/xerces2-j/index.html
3. You need to get the Graphviz package at:
    http://www.research.att.com/sw/tools/graphviz/ (It is a very
cool tool, and you will like it!)

IV. Running XSQ

First you need to install the above packages. After set up the correct
classpath, just go to the src directory and make. The binaries are put
in the bin directory.

There are two ways to invoke the XSQ XPath engine.
The first is to use it in command line:

`java -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser edu.umd.cs.db.xsq.XSQ -r "root" dblp.xml "//pub//book//name/text()"`

We also provide a GUI in which you can submit queries, watch the
files, and see the structure of the HPDTs generated.

`java -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser edu.umd.cs.db.xsq.XSQFrame`

It is recommended that use the first form for larger files (say, more than 10MB).
The GUI is handy when you are working with smaller files.

V. Notes:

1. In the GUI, you can type in the file name and the query in the
input box. Then you can  click the "Execute" bottom to run the query
on the file. The result will be shown in the "Result" tab below. You
can find some sample queries in the "Sample Query" tab. After you
execute a query. You can click the "HPDT Figure" tab to see the
visualized HPDT. You can also check the target XML file in "File"
tab. The process of the the query is shown on the console.

2. Currently, the predicate cannot use the same element/attribute as
the pattern/output. For example:

```xpath
//book[name]/name
//book[@price>100]/@price
```

are not allowed in the queries.

3. The layout of the HPDT generated will be saved in HPDT.ps.
