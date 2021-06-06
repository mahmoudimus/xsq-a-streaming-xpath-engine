/* $Id: XSQPanel.java,v 1.4 2002/10/21 23:31:23 pengfeng Exp $
 * 
 * Copyright(c) 2002 Feng Peng and Sudarshan S. Chawathe;
 * http://www.cs.umd.edu/~pengfeng/xsq
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package edu.umd.cs.db.xsq;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import att.grappa.*;

/** 
 * Class description goes here
 *
 * @version $Id: XSQPanel.java,v 1.4 2002/10/21 23:31:23 pengfeng Exp $
 * @author  Feng peng
 */
public class XSQPanel extends JPanel{

  JFrame     mParentFrame;

  /** The panel contains the file selection, query input.*/
  JPanel     mPanelTop;

  JTextField mTextFileName;
  JButton    mButtonFileName;
  Action     mActionFileName;

  JTextField mTextQuery;
  JButton    mButtonQuery;
  Action     mActionQuery;

  JTextField mTextRootTag;
  JButton    mButtonSaveHPDT;
  Action     mActionSaveHPDT;

  /** The panel conatains the tabbed pages which will show the PDT and the results.*/
  JPanel     mPanelBottom;

  JTabbedPane mTab;
  JScrollPane mScrollResult;
  JScrollPane mScrollFile;
  JScrollPane mScrollPDT;
  JScrollPane mScrollSample;
  JScrollPane mScrollFigure;

  JTextArea  mTextResult;
  JTextArea  mTextFile;
  JTextArea  mTextPDT;
  JTextArea  mTextSample;
  GrappaPanel mGp;

  /** parameters for the panel layout */
  final double weightLabel = 0.0;
  final double weightField = 0.8;
  final double weightButton = 0.1;

  //Display the content of the file
  boolean mShowFile = true;

  public XSQPanel( JFrame parent ){

    mParentFrame = parent;
    initActions();

    mPanelTop = initPanelTop( );
    mPanelBottom = initPanelBottom( );
    
    add ( mPanelTop );
    add ( mPanelBottom );

    setLayout( new BorderLayout() );
    add( BorderLayout.NORTH, mPanelTop );
    add( BorderLayout.CENTER, mPanelBottom );
    
  }


  private void initActions(){
   
    mActionFileName = new AbstractAction("Filename") {
	public void actionPerformed(ActionEvent e) {
	  showFileLocator( );
	}
      };
    
    mActionQuery = new AbstractAction("Query") {
	public void actionPerformed(ActionEvent e) {
	  executeQuery( );
	}
      };
    
    mActionSaveHPDT = new AbstractAction("Save HPDT") {
	public void actionPerformed(ActionEvent e) {
	  saveHPDTGraph( );
	}
      };
    
  }

  /** init the components for the top panel. */
  private JPanel initPanelTop( ){

    JPanel panel = new JPanel( );
    GridBagHelper helper = new GridBagHelper(panel);
    
    // The first line is the text for the filename
    mTextFileName   = makeTextInput   ( );
    mTextFileName . setText ( Consts.TestFileName );
   
    mButtonFileName = makeButton ( mActionFileName );
    mButtonFileName.setText( "File..." );

    addRow ( helper, "Target filename:", mTextFileName, mButtonFileName );

    // The second line is the query
    mTextQuery   = makeTextInput   ( );
    mTextQuery . setText ( Consts.INIT_QUERY );
    mButtonQuery = makeButton ( mActionQuery);
    mButtonQuery.setText( "Execute" );
    
    addRow ( helper, "Input XPath query:", mTextQuery, mButtonQuery );

    // The third line is the root tag name
    mTextRootTag = makeTextInput   ( );
    mTextRootTag . setText ( "root" );
    // The "Save HPDT" button
    mButtonSaveHPDT = makeButton ( mActionSaveHPDT);
    mButtonSaveHPDT.setText( "Save HPDT" );
    addRow ( helper, "Root Tag:", mTextRootTag, mButtonSaveHPDT );
    return panel;

  }
  

  private String getSampleQuery( ){
    
    String s;

    try {
      File file = new File ( "sample.txt" );
      FileReader fr = new FileReader ( file );
      
      char[] buffer = new char[(int)file.length()];
      fr . read ( buffer, 0, (int)file.length() - 1 );
      
      s = new String( buffer );
      
    }catch(IOException e){
      System.err.println( e.toString() );
      return "";
    }
    
    return s;
  }
  
  private JPanel initPanelBottom( ){
  
    JPanel panel = new JPanel( );
    panel . setLayout( new BorderLayout());
    
    mTab = new JTabbedPane();
    
    mTextResult = makeStaticTextArea( );
    mScrollResult = makeScrollPane( mTextResult );
    mTab.addTab("Query result", mScrollResult);
    mTab.setSelectedIndex(0);

    mTextFile = makeStaticTextArea( );
    mScrollFile = makeScrollPane( mTextFile );
    mTab.addTab("File", mScrollFile);
    showFile ( Consts.TestFileName );
    
    mTextPDT = makeStaticTextArea( );
    mScrollPDT = makeScrollPane ( mTextPDT );
    mTab.addTab("HPDT", mScrollPDT);

    mTextSample = makeStaticTextArea( );
    mTextSample . setText ( getSampleQuery() ) ;
    mScrollSample = makeScrollPane ( mTextSample );
    mTab.addTab("Sample Queries", mScrollSample);


    mGp = makeGrappaPanel( );
    mGp.setScaleToFit(false);
    mScrollFigure = makeScrollPane ( mGp );
    mScrollFigure . getViewport( ).setBackingStoreEnabled( true );
    //mScrollFigure . setViewportView( mGp );
    mTab.addTab("HPDT Figure", mScrollFigure);

    panel.add( BorderLayout.CENTER, mTab );

    return panel;

  }

  
  private void addRow(GridBagHelper helper, String label, Component component){
    
    addRow( helper, label, component, null );
    
  }
  
  private void addRow(GridBagHelper helper, String label, Component component, JButton button){
  
    helper.add(new JLabel(label, SwingConstants.RIGHT), weightLabel);
    
    if (button == null) {
      helper.add(component, weightField, 2);
    }else{
      helper.add(component, weightField);
      helper.add(button, weightButton);
    }
    
    helper.nextRow();
  }
  
  private JTextField makeStaticText( ){

    JTextField field = new JTextField();
    field.setEditable(false);
    field.setBackground(Color.white);
    return field;

  }

  private GrappaPanel makeGrappaPanel( ){
   
    try {
	
      String script1 = "dot -ottt " + Consts. HPDTFigureName;
      System.err.println( "Begin output the grpah:" + script1 );
      Process p = Runtime.getRuntime().exec( script1 );
      int rtn_val = p.waitFor ();

      if ( rtn_val != 0 )
	System.err.println( "Warning: Something wrong with the dot program! The return value is " + rtn_val );

      InputStream input =  new FileInputStream( "ttt" );
      
      Parser program = new Parser(input, System.err);
      
      program.parse();
      
      Graph graph = null;
      
      graph = program.getGraph();
      
      System.err.println("The graph contains " + graph.countOfElements(Grappa.NODE|Grappa.EDGE|Grappa.SUBGRAPH) + " elements.");
      
      graph.setErrorWriter(new PrintWriter(System.err,true));
      
      System.err.println("bbox=" + graph.getBoundingBox().getBounds().toString());

      //layout ( graph );
      
      return new GrappaPanel(graph);
      
    }catch( Exception e ){
      System . err. println ( e );
      System . exit ( 1 );
    }
 
    return null;
  }

  private void layout( Graph graph ){
    Object connector = null;

    String SCRIPT = "formatGraph";
    try {
      connector = Runtime.getRuntime().exec( SCRIPT );
    } catch(Exception ex) {
      System.err.println("Exception while setting up Process: " + ex.getMessage() + "\n");
      connector = null;
      System . exit(1);
    }
    
    if(connector != null) {
      if(!GrappaSupport.filterGraph(graph,connector)) {
	System.err.println("ERROR: somewhere in filterGraph");
      }
      if(connector instanceof Process) {
	try {
	  int code = ((Process)connector).waitFor();
	  if(code != 0) {
	    System.err.println("WARNING: proc exit code is: " + code);
	  }
	} catch(InterruptedException ex) {
	  System.err.println("Exception while closing down proc: " + ex.getMessage());
	  ex.printStackTrace(System.err);
	}
      }
      connector = null;
    }
    //System.err.println("The formatDemo is OK.");
    graph.repaint();
  }

  private JTextArea makeStaticTextArea( ){

    JTextArea area = new JTextArea();
    area.setEditable(false);
    area.setBackground(Color.white);
    return area;

  }
  
  private JTextField makeTextInput(){
    
    JTextField field = new JTextField();
    field.setEditable(true);
    field.setBackground(Color.white);
    return field;
  
  }

   private JScrollPane makeScrollPane(Component component){
     
     JScrollPane scroll = new JScrollPane(component);
     scroll.setMinimumSize(new Dimension(630,255));
     scroll.setPreferredSize(new Dimension(630,255));
     return scroll;
   }
  
  private JButton makeButton ( Action act ){

    JButton button = new JButton( act );
    return button;
  }
  
  public void showFileLocator( ){
    //Create a file chooser
    final JFileChooser fc = new JFileChooser(".");
    ExampleFileFilter filter = new ExampleFileFilter( new String("xml"), "xml files");
    fc.addChoosableFileFilter(filter);
    int returnVal = fc.showOpenDialog( this );
    
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      mTextFileName . setText ( file.getAbsolutePath());
      
    } else {
    }
  }

  
  public void saveHPDTGraph( ){
    String script1 = "dot -Tps " + Consts. HPDTFigureName + " -o HPDT.ps";
    String script2 = "gv HPDT.ps";
    try {
      Process p = Runtime.getRuntime().exec( script1 );
      int rtn_val = p.waitFor ();

      if ( rtn_val != 0 )
	System.err.println( "Warning: Something wrong with the dot program! The return value is " + rtn_val );
      
       p =  Runtime.getRuntime().exec( script2 );
       rtn_val = p.waitFor ();
       if ( rtn_val != 0 )
	 System.err.println( "Warning: Something wrong with the gv program! The return value is " + rtn_val );
       
    } catch(Exception e) {
      System . err. println ( e );
    }
  }

  private void showFile( String filename ){
    
    if ( mShowFile ){
      String sFile;
      try {
	FileReader xmlFile;
	File file = new File ( filename );
	xmlFile = new FileReader ( file );
	
	char[] buffer = new char[(int)file.length()];
	xmlFile . read ( buffer, 0, (int)file.length() - 1 );

	sFile = new String( buffer );
	xmlFile . close();
		
      }catch(IOException e){
	System.err.println( e.toString() );
	return;
      }
      mTextFile . setText ( sFile );
      
    }
    
  }
  
  public void executeQuery( ){

    String filename;
    String query;
    String rootTag;
    
    filename = mTextFileName . getText ( );
    query    = mTextQuery . getText ( );
    rootTag  = mTextRootTag . getText( );
    
    Consts.setRootTag ( rootTag );
    showFile ( filename );
    
    XPathParser parser = new XPathParser();
    Vector  vXP = new Vector( Consts.MAX_XPATH_NODES );
    
    HPDT   newHPDT;
    
    query = parser . tokenize ( query );
    String mess = "The query string is:" + query + "\n";
    
    parser . getXPathVector( vXP, query );
    
    if ( ( Tools.debugLevel & Tools . debugXPathParser ) > 0 )
      parser . printXPathVector( vXP ); 

    newHPDT = new HPDT ( vXP, true );
    
    mess = mess + "The HPDT is:\n" + newHPDT . toString();
    
    FileReader xmlFile;
    
    try {
      
      xmlFile = new FileReader ( filename );
      System.err.println( "Begin to parse the file: " + filename );
    
      XSQParser xsqParser = new XSQParser ( xmlFile, newHPDT );
    
      xsqParser . run ();
      
      System.err.println( "Parsing successed!" );
      
      xmlFile . close();
      
      mTextResult . setText ( "Filename is\t: " + filename + "\n" + "The query is:\t" + query + "\n"); 
      mTextResult . append  ( "The result is:\n" );
      mTextResult . append ( xsqParser . getResult ( ) );
      mTextPDT . setText ( mess );
      
    }catch(IOException e){
      System.err.println( e.toString() );
      return;
    }
    
    //Show the layout of the HPDT 
    mGp = null;
    mGp = makeGrappaPanel ( );
    mGp.setScaleToFit(true);
    mScrollFigure . getViewport( ).setBackingStoreEnabled( true );
    mScrollFigure . setViewportView( mGp );
    
    mTab.setSelectedIndex(0);
  }
 
}

