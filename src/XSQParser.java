/* $Id: XSQParser.java,v 1.3 2002/10/20 04:37:28 pengfeng Exp $
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

import java.lang.*;
import java.util.*;
import java.io.*;
import org.xml.sax.Attributes;
//import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
//import org.xml.sax.helpers.ParserFactory;
import org.apache.xerces.parsers.SAXParser;


/** 
 * XSQParser defines the callbacks called by the SAX parser.
 *
 * @version $Id: XSQParser.java,v 1.3 2002/10/20 04:37:28 pengfeng Exp $
 * @author  Feng peng
 */
public class XSQParser extends DefaultHandler {

  private int        mCurDepth;
  private Stack      mCurTagStack;
  private HPDT       mHPDT;
  private FileReader mFile;
  private boolean    mPreProcess = false;
  
  public XSQParser(){
    
    mCurDepth = -1;
    mCurTagStack = new Stack ( ) ;
    mFile = null;
    mHPDT = null;
  
  };
  
  public XSQParser( FileReader fr, HPDT hpdt ){
	
    mCurDepth = -1;
    mCurTagStack = new Stack ( ) ;
    mFile = fr;
    mHPDT = hpdt;

  };
  
  
  public void startDocument() throws SAXException {
  }

  public void endDocument() throws SAXException {
    
      mHPDT . finish();
    
  }

  /** Start element. */
  public void startElement(String uri, String local, String raw,
			   Attributes attrs) throws SAXException {
    
    mCurDepth++;
    
    Event newEvent = new Event( local, Consts.BEGIN, attrs . getLength(), mCurDepth );
    
    getAttrPair( newEvent, attrs );
    
    mHPDT . processEvent ( newEvent );
    
    mCurTagStack . push ( local ); 
    
  } // startElement(String,String,StringAttributes)
  
  public void endElement( String uri, String localName, String qName) throws SAXException{
    
    Event newEvent = new Event( (String) mCurTagStack . peek(),  Consts.END, 0, mCurDepth);

    mCurDepth--;

    mHPDT . processEvent ( newEvent );

    mCurTagStack . pop();
    
  }
  
  
  public void characters(char[] ch, int start, int length) throws SAXException{
  
    String text = new String( ch, start, length );

    if ( mPreProcess ) {
      text = text . replace ( '\n', ' ');
      text = text . replace ( '\t', ' ');
      text = text . trim ( );
      
      if ( text . length ( ) == 0 ) 
	return;  // called with whitespace between elements
    }
    
    Event newEvent = new Event((String) mCurTagStack . peek ( ),  Consts.TEXT, 1, mCurDepth );
    
    newEvent . addAttribute ( Consts.TEXT_ATTR_NAME, text );
    
    mHPDT . processEvent ( newEvent );
    
  }
  
  public String getResult ( ) {

    if ( mHPDT != null ) 
      return mHPDT . getResult ( );
    else
      return "Result is not availbake now!\n";

  }
  
  public void run(){

    if ( mHPDT == null ) {
      System.err.println( "No HPDT is defined." ) ;
      return;
    }

    if ( mFile == null ){
      System.err.println( "No file reader is defined." );
      return;
    }

    try {
      
      XMLReader xr = XMLReaderFactory.createXMLReader();
      
      xr . setContentHandler(this);
      xr . setErrorHandler(this);
      
      long startTime = Tools.getCurTime( );
      //System.err.println( "Start at " + Long.toString(startTime) );
      
      xr . parse(new InputSource(mFile));

      long endTime = Tools.getCurTime( );
      //System.err.println( "End at " + Long.toString(endTime) );

      //System.err.println ( "The duration is :" + Long.toString ( endTime - startTime ) + " ms " );
      System.err.println ( Long.toString ( endTime - startTime ) );
    }
    catch (SAXParseException e) {
      // ignore
    }
    catch (Exception e) {
      System.err.println("error: Parse error occurred - "+e.getMessage());
      Exception se = e;
      if (e instanceof SAXException) {
	se = ((SAXException)e).getException();
      }
      if (se != null)
	se.printStackTrace(System.err);
      else
	e.printStackTrace(System.err);
      
    }
  }
  
  public void   setFile ( FileReader fr ){ 
    mFile = fr;
  };

  /**Translate the string array to the AttrPair vector */
  private void  getAttrPair( Event event, Attributes attrs  ){

    if ( attrs != null ) {
      
      int attrCount = attrs . getLength();
      
      for (int i = 0; i < attrCount; i++) {
	
	String name  = attrs . getQName( i );
	String value = attrs . getValue( i );
	
	event . addAttribute ( name, value );
      }
    } else{
      
      event . clearAttrList ( ) ;
      
    }
      
    return;
  }

  public static void main( String[] args ){

    return;

  }

};
