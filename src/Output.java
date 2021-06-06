/* $Id: Output.java,v 1.7 2002/10/23 23:03:39 pengfeng Exp $
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
 * Class Output provides method to handle the output portion of the context node in the XPath expression.
 * 
 * It will perform mOperation on the mFuncName of the attribute mAttrName of the element mTagName.
 * 
 * @version $Id: Output.java,v 1.7 2002/10/23 23:03:39 pengfeng Exp $
 * @author  Feng peng
 */

public class Output implements Cloneable{

  // The operation that could happen in the transitions
  public static final int     ENQUEUE   = 1;
  public static final int     CLEAR     = 2;
  public static final int     UPLOAD    = 3;
  public static final int     FLUSH     = 4;
  public static final int     OUTPUT    = 5;
  public static final int     UPDATE    = 6;
  public static final int     AGGREGATE = 7;
  public static final int     ENQUEUE_UPLOAD = 8;
  public static final int     FLUSH_OUTPUT   = 9;
  public static final int     ENQUEUE_UP     = 10;
  public static final int     AGGREGATE_UP   = 11;
  
  private int           mOperation = 0;
  private String	mFuncName = "";
  private String	mAttrName = "";
  private String        mTagName  = "";

  //public Output( ){
  //  return;
  //}

  public Object clone( ){
    //mOperation = o.mOperation;
    //mFuncNamem = o.mFuncName;
    //AttrName
    //mTagName 
    try{
      return super.clone();
    }catch(Exception e){
      System . err . println ( e );
      return null;
    }
  }

  /**
   * Get the value of mTagName.
   * @return value of mTagName.
   */
  public String getTagName() {
    return mTagName;
  }
  
  /**
   * Set the value of mTagName.
   * @param v  Value to assign to mTagName.
   */
  public void setTagName(String  v) {
    this.mTagName = v;
  }
  
  
  /**
   * Get the value of mOperation.
   * @return value of mOperation.
   */
  public int getOperation() {
    return mOperation;
  }
  
  public String getOperationName(){
    
    String s = "";
    switch ( mOperation ){
      
    case 0:
      s = "NOTHING";
      break;

    case ENQUEUE:
      s = "ENQUEUE";
      break;

    case CLEAR:
      s = "CLEAR";
      break;
      
    case UPLOAD:
      s = "UPLOAD";
      break;
      
    case FLUSH:
      s = "FLUSH";
      break;
      
    case OUTPUT:
      s = "OUTPUT";
      break;

    case UPDATE:
      s = "UPDATE";
      break;

    case AGGREGATE:
      s = "AGGREGATE";
      break;

    case ENQUEUE_UPLOAD:
      s = "ENQUEUE_UPLOAD";
      break;

    case FLUSH_OUTPUT:
      s = "FLUSH_OUTPUT";
      break;

    case ENQUEUE_UP:
      s = "ENQUEUE_UP";
      break;

    case AGGREGATE_UP:
      s = "AGGREGATE_UP";
      break;
      
    default:
      s = "UNKNOWN OPERATION";
    }
    
    return s;
  }
  /**
   * Set the value of mOperation.
   * @param v  Value to assign to mOperation.
   */
  public void setOperation(int  v) {
    this.mOperation = v;
  }
  

  
  /**
   * Get the value of mFuncName.
   * @return value of mFuncName.
   */
  public String getFuncName() {
    return mFuncName;
  }
  
  /**
   * Set the value of mFuncName.
   * @param v  Value to assign to mFuncName.
   */
  public void setFuncName(String  v) {
    this.mFuncName = v;
  }
  
  /**
   * Get the value of mAttrName.
   * @return value of mAttrName.
   */
  public String getAttrName() {
    return mAttrName;
  }
  
  /**
   * Set the value of mAttrName.
   * @param v  Value to assign to mAttrName.
   */
  public void setAttrName(String  v) {
    this.mAttrName = v;
  }
  
  public boolean isEmpty()
  {
    if ( mFuncName . equals ( "" ) ) 
      return true;
    return false;
  }

  public boolean isText()
  {
    if ( mAttrName . equals ( Consts.TEXT_ATTR_NAME ) )
      return true;
    return false;
  }

  
  public static boolean isAggregation ( String s ){

    String aggFuncs[] = { "avg","max","min", "sum", "count" };
    int i, size;

    size = aggFuncs . length;

    for ( i=1 ; i <= size; i++ ){
	if ( s . equals ( aggFuncs[ i - 1 ] ) ) 
	  return true;	
      }
	
    return false; 

  }

  public boolean isAggregation(){
    String aggFuncs[] = { "avg","max","min", "sum", "count" };
    int i, size;

    size = aggFuncs . length;

    for ( i=1 ; i <= size; i++ ){
	if ( mFuncName . equals ( aggFuncs[ i - 1 ] ) ) 
	  return true;	
      }
	
    return false; 
  }
  
  public int  getOutputFunction( ){
    return getOutputFunction( mFuncName ); 
  }

  public int  getOutputFunction( String s ){

    String outputFuncs[] = { "value", "avg","max","min", "sum", "count","catchall" };
    int outputFuncValue[] = { Consts.VALUE, Consts.AVG, Consts.MAX, Consts.MIN, Consts.SUM, Consts.COUNT, Consts.CATCHALL };
    int i, size;

    size = outputFuncs . length;

    for ( i=1; i<=size; i++ ){
	if ( mFuncName . equals ( outputFuncs[ i - 1 ] ) ) 
	  return outputFuncValue[ i - 1 ];	
      }
    
    return Consts.NONE;
  }

  public void  parseOutputString( String output )
  {
    if ( output . equals ( "" ) )
      return;
    
    if ( output.charAt(0) == '@' ){
      setFuncName ( "value" );
      setAttrName ( output.substring ( 1 ) );
      return;
    };
    
    if ( output.compareTo( "text()" ) == 0 ){
      setFuncName ( "value" );
      setAttrName ( Consts.TEXT_ATTR_NAME );
      return;
    };

    if ( output.compareTo( "**" ) == 0 ){
      setTagName ( "**" );
      setFuncName ( "catchall" );
      setAttrName ( Consts.ATTR_CATCHALL );
      return;
    };

    //This is an aggragation
    int pos  = output.indexOf( '(' );
    int pos2 = output.indexOf( ')' );

    String funcName = output.substring( 0, pos );
    setFuncName ( funcName );

    if ( pos2 == ( pos + 1 ) ){
      //No child name and attribute name is specified, the aggregation is for current text
      setTagName  ( "" );
      setAttrName ( Consts.TEXT_ATTR_NAME );
      //If the function is count, the aggregation is for any attribute in the start event
      if ( funcName.compareTo( "count" ) == 0)
	setAttrName ( Consts.ANY_ATTR_NAME );
    }else{
      String s = output.substring( pos + 1, pos2 );
      pos = s.indexOf('@');
      if ( pos == -1 ){
	setTagName  ( s );
	setAttrName ( Consts.TEXT_ATTR_NAME );
	if ( funcName.compareTo( "count" ) == 0)
	  setAttrName ( Consts.ANY_ATTR_NAME );
      }else{
	setTagName  ( s . substring( 0, pos ) );
	setAttrName ( s . substring( pos + 1 ) );
      }
    }
    return;
  }

}



