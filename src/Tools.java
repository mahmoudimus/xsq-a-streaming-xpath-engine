/* $Id: Tools.java,v 1.8 2002/10/24 16:42:15 pengfeng Exp $
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
 * Tools provides all the constants and the enumerate values
 *
 * @version $Id: Tools.java,v 1.8 2002/10/24 16:42:15 pengfeng Exp $
 * @author  Feng peng
 */

public class Tools{
 
  /* Before the performance test, remember to turn off debug flag. It should be defined as final. */
  public final static boolean debug = false;
  public final static int debugLevel = 0;
  public final static int debugRunHPDT = 1;
  public final static int debugBuildHPDT = 2;
  public final static int debugXPathParser = 4;
  public final static int debugOutputQ = 8;
  public final static int debugStat = 16;

  public static final String   getEvalStr( int e ){
	
    String s = "";
    if ( ( e & Consts.TRUE  ) > 0 )	 s = s +  "TRUE ";
    if ( ( e & Consts.FALSE ) > 0 )      s = s +  "FALSE ";
    if ( ( e & Consts.NA )    > 0 )	 s = s +  "NA ";
    return s;
	
  }
    
  public static final String   getEventStr( int e ){
	
    String s = "";
    if ( ( e & Consts.BEGIN ) > 0 )     s = s +  "BEGIN ";
    if ( ( e & Consts.END   ) > 0 ) 	s = s +  "END ";
    if ( ( e & Consts.TEXT  ) > 0 )	s = s +  "TEXT ";
    return s;
  }
    
  public static final boolean  isCharInOp ( char ch ){
	
    char op[] = {'>','<','=','!','%'};
    int size =  op . length;
	
    for (int i=0; i<size; i++)
      if ( ch == op[i] )
	return true;

    return false;
  }
    
  public static final int      getOp ( String op ){
	
    String ops[]={"=",">","<",">=","<=","!=","%","exists"};
    int size = ops.length;
	
    for ( int i=0; i < size; i++ )
      if ( op . equals (ops[i])  )
	return i+1;
    return 0;
  }
    
  public static final long getCurTime ( ) {

    return System.currentTimeMillis();
    //long memoryBefore = Runtime.getRuntime().freeMemory();

  }
    
  public static final void out( String message ){
    System.err.print ( message );
  }
  
  public static final void outln( String message ){
    System.err.println ( message );
  }
  
  public static String normalize( String s ){

    int size = s.length( );

    StringBuffer str = new StringBuffer( 2 * size );
	
    for ( int i=0; i<size; i++){
      if ( s.charAt(i) == '&' )
	str = str.append("&amp;");
      else if ( s.charAt(i) == '>' )
	str = str.append("&gt;");
      else if ( s.charAt(i) == '<' )
	str = str.append("&lt;");
      else if ( s.charAt(i) == '\'' )
	str = str.append("&apos;");
      else if ( s.charAt(i) == '"' )
	str = str.append("&quot;");
      else
	str = str.append(s.charAt(i));
    }
	
    return str.toString();
  }
}








