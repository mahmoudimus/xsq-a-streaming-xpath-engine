/* $Id: XSQ.java,v 1.5 2002/10/24 16:42:15 pengfeng Exp $
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


/** 
 * The XSQ program takes two parameters as following:
 *	
 * xsq [XMLFileName] [XPathExpression]
 *
 * It will evaluate the XPathExpression on the XMLFile. The result will be sent to the STDOUT. 
 * 
 * @version $Id: XSQ.java,v 1.5 2002/10/24 16:42:15 pengfeng Exp $
 * @author  Feng peng
 */


public class XSQ{
  
  public static final String USAGE_INFO = "Usage: xsq [-r RootTag] XMLFileName XPathExpression";

  public static void usage(){
    System.err.println( USAGE_INFO );
  }

  public static void main(String argv[]) {
      
    boolean logTime = true;

    if ( argv.length < 2 ){
      usage();
      return;
    }

    if ( argv.length == 4 ){
	Consts.setRootTag( argv[1] );
    }
   
    if ( Tools.debugLevel == 1 ){
      System.err.println( "The first  arg is " + argv[2] );
      System.err.println( "The second arg is " + argv[3] );
    }

    XPathParser parser = new XPathParser();
    Vector  vXP = new Vector( Consts.MAX_XPATH_NODES );
        
    HPDT   newHPDT;
    String sTest = argv[3];
    sTest = parser . tokenize ( sTest );
    
    if ( Tools.debugLevel == 1 ){
      System.out.println( "The query string is:" + sTest );
    }

    long startTime = Tools.getCurTime( );
    long endTime = Tools.getCurTime( );

    if ( logTime ){
	//System.err.println( "Begin parsing the XPath expression and buile the HPDT: " + Long.toString(endTime) );
    }

    parser . getXPathVector( vXP, sTest);
    
    newHPDT = new HPDT ( vXP, false );

    if ( logTime ){
      endTime = Tools.getCurTime( );
      //System.err.println( "HPDT built: " + Long.toString(endTime) );
      //System.err.println( "Time elapsed: " + Long.toString ( endTime - startTime ) + " ms " );
      System.err.println( Long.toString ( endTime - startTime ) );
    }
    
    
    if ( Tools.debugLevel == 1 ){
      System.err.println( "Opening file: " + argv[2] );
    }
        
    FileReader xmlFile;
    
    try {
      
      xmlFile = new FileReader ( argv[2] );
      if ( Tools.debugLevel == 1 ){
	System.err.println( "Begin to parse the file: " + argv[2] );
      }
      
    }catch(IOException e){
      System.err.println( e.toString() );
      return;
    }

    
    XSQParser xsqParser = new XSQParser ( xmlFile, newHPDT );
    
    xsqParser . run ();

    if ( logTime ){
	endTime = Tools.getCurTime( );
	//System.err.println( "End at " + Long.toString(endTime) );
	//System.err.println( "Parsing successed!" );
    }
    return;

  }


}

