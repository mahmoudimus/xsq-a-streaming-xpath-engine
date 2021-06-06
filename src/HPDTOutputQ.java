/* $Id: HPDTOutputQ.java,v 1.3 2002/10/24 16:42:15 pengfeng Exp $
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
 * The HPDT keep every possible answer in the HPDT queue. The actual content is stored here. 
 * Associated with each content is a unique item id, a reference count, and the actual string content.
 * Each enqueue operation will insert the item into the global queue. If the item is already in the queue, 
 * the reference count is increased by one. The clear operation will decrease the reference count by one.
 * Only when the reference count is zero do we remove the item from the queue. 
 * When a BPDT is FLUSH the content, the items are marked as 'output'. However, the content cannot be sent
 * to output until all the items before it have been send to output. 
 * @version $Id: HPDTOutputQ.java,v 1.3 2002/10/24 16:42:15 pengfeng Exp $
 * @author  Feng peng
 */

public class HPDTOutputQ{
  
  /** Maximum number of items in the queue. The default value is 1024. */
  private final static int MAX_ITEM_IN_GLOBAL_QUEUE = 1024 * 1024;

  private final static int MAX_LEN_OUT_STR = 64 * 1024;

  /** The indent string used for debug information. */
  private static String indent = "\t\t";

  /** The vector used to store the QueueItems. */  
  private Vector      mOutputQ;
  
    public  boolean     isEmpty = true;
  /** The file used to flush the contents. */
  private FileWriter  mFile;

  public HPDTOutputQ( ){
    mOutputQ = new Vector ( MAX_ITEM_IN_GLOBAL_QUEUE );
  }

  public void add ( QueueItem qi ){
    mOutputQ . add ( qi );
    isEmpty = false;
  }
  
  private void checkWithoutResult( ){
    
    QueueItem    item;
    int          size = mOutputQ.size();
    boolean      next = true; 

    if ( size == 0 )
      return;
    //This is the item we just added.
    item = (QueueItem)mOutputQ . get ( 0 );
    
    if ( item . isOutput( ) ){
	if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	    Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been outputed. ");
	}
      System . out . println ( item . toString ( ) );
      mOutputQ.removeElementAt( 0 );
      size --;
    }else if ( item . isZeroCount ( ) ){
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been removed. ");
      }
      mOutputQ.removeElementAt( 0 );
      size --;     
    }else{
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) 
	Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has count " + item . getCount() );
      next = false;
    }
    
    while ( next && ( size > 0 ) ){
      
      item = (QueueItem)mOutputQ . get ( 0 );
            
      if ( item . isOutput( ) ){
	if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been outputed. ");
	}
	System . out . println ( item . toString ( ) );
	mOutputQ.removeElementAt( 0 );
	size --;
      }else if ( item . isZeroCount ( ) ){
	if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]Something wrong: a zero-count item remains in the queue!" + item . getCount( ) );
	}
	mOutputQ.removeElementAt( 0 );
	size --;
      }else{
	next = false;
      }
    }

    if ( size == 0 )
	isEmpty = true;
    return;
  }
  
  private String checkWithResult( ){
    
    QueueItem    item;
    int          size = mOutputQ.size();
    boolean      next = true; 
    StringBuffer s = new StringBuffer( MAX_LEN_OUT_STR );

    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
      Tools.outln ( indent + "[HPDTOutputQ:Check]Begin check with result. ");
    }
    
    //This is the item we just added.
    if ( size == 0 ){
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]The size of the global output queue is zero. ");
      }
      return "";
    }

    item = (QueueItem)mOutputQ . get ( 0 );
    
    if ( item . isOutput( ) ){
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been outputed. ");
      }
      s .  append ( "\n" ) . append ( item . toString ( ) );
      mOutputQ.removeElementAt( 0 );
      size --;
    }else if ( item . isZeroCount ( ) ){
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been removed. ");
      }
      mOutputQ.removeElementAt( 0 );
      size --;     
    }else{
      if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has count " + item . getCount() );
      }
      next = false;
    }
    
    while ( next && ( size > 0 ) ){
      
      item = (QueueItem)mOutputQ . get ( 0 );
      
      if ( item . isOutput( ) ){
	if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]The item '" + item + "' has been outputed. ");
	}
	s . append ( "\n" ) . append ( item . toString ( ) );
	mOutputQ.removeElementAt( 0 );
	size --;
      }else if ( item . isZeroCount ( ) ){
	if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
	  Tools.outln ( indent + "[HPDTOutputQ:Check]A zero-count item remains in the queue!" + item );
	}
	mOutputQ.removeElementAt( 0 );
	size --;
      }else{
	next = false;
      }
      
    }
    if ( size == 0 )
	isEmpty = true;
    return s.toString();
  }
  
  
  /**
     @param result If result is true, the return value will the string that sent to the output. Or else the return value is null.
  */
  public String checkAll ( boolean result ){
    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) {
      Tools.outln ( indent + "[HPDTOutputQ:CheckAll]The size of the global queue is " + mOutputQ.size() );
    }
    if ( result ) {
      return checkWithResult( );
    }else{
      checkWithoutResult( );
      return null;
    }
  }

  /** Return all the content in the queue. */
  public String toString( ){
    int size  = mOutputQ . size(); 
    int count = 0;
    StringBuffer s = new StringBuffer ( MAX_LEN_OUT_STR );
    
    for ( int i=0; i<size; i++ ){
      s . append ( "\n" ) . append ( ((QueueItem)mOutputQ.get(i)).toString() );
    }
    return s . toString ();
    
  };
  
  public QueueItem get ( int i ){

    return (QueueItem)mOutputQ.get(i);
  }
  /** Return the size of the output queue. */
  public int  size( ){ 
    return mOutputQ . size(); 
  };
  
  
};


