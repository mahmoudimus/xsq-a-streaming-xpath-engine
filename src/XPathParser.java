/* $Id: XPathParser.java,v 1.6 2002/10/23 05:17:51 pengfeng Exp $
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
 * XPathParser will parse the XPath query and generate the PDT.
 *
 * @version $Id: XPathParser.java,v 1.6 2002/10/23 05:17:51 pengfeng Exp $
 * @author  Feng peng
 */

public class XPathParser  
{
  
  
  //public String mQuery;

  private boolean isTag ( String s ){

    if ( s.charAt(0) == '@' ){
      return false;
    }
    
    if ( s.indexOf ("()") >=0 ){
      return false;
    }
    
    return true;

  }
  
  public void getXPathVector( Vector vXP, String sXPathExp ){
    
    int i, len, state = 1;
    String sTmp = "";
    XPathNode curNode = null;
    XPathNode preNode = null;
    XPathNode node;
    boolean   closure = false;
    boolean   inString = false;

    len = sXPathExp.length();
    
    //mQuery = sXPathExp;
    
    for ( i=0; i < len; i++ ){

      if ( inString ){
	if ( sXPathExp . charAt( i ) == '"' ){
	  inString = false;
	}else{
	  sTmp = sTmp + sXPathExp . charAt( i );
	}
	if ( i != len - 1 )
	  continue;
      }else{
	if ( sXPathExp . charAt( i ) == '"' ){
	  if ( sTmp == "" ){
	    System.err.println( "Something wrong in the XPath expression. \" is not in right place." ) ;
	  }else{
	    inString = true;
	  }
	  if ( i != len - 1 )
	    continue;
	}else{
	  if ( sXPathExp . charAt( i ) != ' ' ) {
	    sTmp = sTmp + sXPathExp . charAt( i );
	    if ( i != len - 1 )
	      continue;
	  }
	}
      }

      if ( ( Tools.debugLevel & Tools . debugXPathParser ) > 0 )
	System.err.println( "Current state is " + state + ":The string is " + sTmp );

      //we've got the next token
      switch (state){
      
      case 1:{
	if ( sTmp . equals( "/" ) ){
	  closure = false;
	  state = 2;
	}else if ( sTmp . equals( "//" ) ){
	  closure = true;
	  state = 2;
	}else{
	  //Tools.out("The sTmp is " + sTmp + ":");
	  System.err.println( "Something wrong in the XPath expression. It should begin with either / or //." ) ;
	  state = 0;
	}
	node = new XPathNode( Consts.ROOT_TAG, "", "", closure );
	vXP  . add( node );
	curNode = node;
	
      }
      break;
      case 2:{
	node = new XPathNode( sTmp, "", "", closure );
	vXP  . add( node );
	preNode = curNode;
	curNode = node;
	if ( preNode != null )
	  preNode .  setNextIsClosure ( closure );
	state = 3;
      }
      break;
      case 3:{
	if ( sTmp . equals ( "/" )){
	  state = 5;
	  closure = false;
	}else if ( sTmp . equals ( "//" )){
	  state = 6;
	  closure = true;
	}else if ( sTmp . equals ( "[" ) ){
	  state = 40;
	}else{
	  System.err.println( "Something wrong in the XPath expression. Don't know how to process current character." );
	  state = 0;
	}
      }
      break;
      case 40:{
	curNode . setFilter( sTmp );
	state = 41;
      }
      break;
      case 41:{
	if ( sTmp . equals ( "]" ) ){
	  state = 4;
	}else{
	  System.err.println( "Unmatched ']' in the predicate." );
	  state = 0;
	}
      }
      break;
      case 4:{
	if ( sTmp . equals ( "/" )){
	  state = 5;
	  closure = false;
	}else if ( sTmp . equals ( "//" )){
	  state = 6;
	  closure = true;
	}else{
	  System.err.println( "Something wrong in the XPath expression. Don't know how to process current character." );
	  state = 0;
	}
      }
      break;
      case 5:{
	if ( isTag( sTmp ) ){
	  node = new XPathNode( sTmp, "", "", closure );
	  vXP  . add( node );
	  preNode = curNode;
	  curNode = node;
	  if ( preNode != null )
	    preNode .  setNextIsClosure ( closure );
	  state = 3;
	}else{
	  curNode . setOutput ( sTmp );
	  state = 7;
	}
      }
      break;
      case 6:{
	if ( isTag( sTmp ) ){
	  node = new XPathNode( sTmp, "", "", closure );
	  vXP  . add( node );
	  preNode = curNode;
	  curNode = node;
	  if ( preNode != null )
	    preNode .  setNextIsClosure ( closure );
	  state = 3;
	}else{
	  System.err.println( "Something wrong in the XPath expression. A tag name should be here instead of ." + sTmp );
	  state = 0;
	}
      }
      break;
      case 7:{
	
      }
      break;
      case 0:{
	return;
      }
      }//end of case
     
      sTmp = "";
    }//end of for

    if ( state != 7 ){
      curNode .  setOutput ( "**" );
      curNode .  setNextIsClosure ( true ); //Catchall event is considered the same as a closure.
    }


  };

  public String tokenize(String sXPathExp){
    char[] buffer= new char[Consts.MAX_XPATH_LEN];
    int i, len, count;
    boolean inString = false;
   
    len   = sXPathExp . length();
    count = 0;

    //remove all spaces in the expression
    for ( i=0; i<len; i++ ){
      if ( inString ){
	buffer[ count++ ] = sXPathExp.charAt(i);
	if ( sXPathExp . charAt( i ) == '"' ){
	  inString = false;
	}
      }else{
	if ( sXPathExp.charAt(i) != ' ' )
	  buffer[ count++ ] = sXPathExp.charAt(i);
	if ( sXPathExp . charAt( i ) == '"' ){
	  inString = true;
	}
	
      }
    }
    buffer[ count ] = '\0';
    
    String sTmp = new String( buffer, 0, count );
    
    //if the first element is not root
    if (sTmp.charAt(0)!='/')
      sTmp = "//" + sTmp;
    
    len   = sTmp . length( );
    count = 0;
    buffer[count++] = sTmp.charAt( 0 );

    for ( i = 1; i < len; i++ ){
      switch ( sTmp . charAt(i) ) {
      case '/':{
	if ( sTmp .charAt( i-1 ) == '/' ){
	  buffer[ count++ ] = '/';
	}
	else{
	  buffer[ count++ ] = ' ';
	  buffer[ count++ ] = '/';
	}
      };
      break;
      case '[':
      case ']':
      case '{':
      case '}':{
	buffer[ count++ ] = ' ';
	buffer[ count++ ] = sTmp.charAt(i);
      }
      break;
      
      default:
	if ( ( sTmp.charAt( i-1 ) == '/' ) || ( sTmp.charAt( i-1 ) == '[' ) || ( sTmp.charAt( i-1 ) == '{' ) ){
	  buffer[ count++ ] = ' ';
	  buffer[ count++ ] = sTmp.charAt(i);
	}else{
	  
	  //Tools.out( "Count is " + Integer.toString(count) + "\t i is " + Integer.toString(i) );
	  buffer[ count++ ] = sTmp.charAt(i);
	
	}
      }
    }//end of for
    buffer[count++] = '\0';
    
    sXPathExp = String . copyValueOf ( buffer );

    sXPathExp = sXPathExp . substring ( 0, count - 1 );

    return sXPathExp;
  };


  public void printXPathVector ( Vector v ){

    int i;
    int len = v . size();
    XPathNode curNode;

    for ( i=0; i<len;i++ ){
      curNode = (XPathNode)v.get(i);
      curNode . printNode (1);
    }

    return;
  }
  	
};
