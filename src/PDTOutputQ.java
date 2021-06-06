/* $Id: PDTOutputQ.java,v 1.7 2002/10/24 16:42:15 pengfeng Exp $
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
 * Each BPDT has its own output queue. However, the content in the queue are actually references to the items in the global queue.
 * The HPDT will put every possible answer into the global queue. 
 * The output in the queue will be flushed to the stdout in the flush() function by default.
 * You can specify that the flush() function send the content to a buffer.   
 *
 * @version $Id: PDTOutputQ.java,v 1.7 2002/10/24 16:42:15 pengfeng Exp $
 * @author  Feng peng
 */

public class PDTOutputQ
{
  /** Maximum number of items in the queue. The default value is 1024. */
  
  private final static String indent = "\t\t";
  
  private int          mBpdtId; 
  private HPDTOutputQ  mGlobalQ;
  

  private long[]       mFlagIndex;
  private Vector       mBuffers; 
  private int          mCount;
  
  /** 
   * @bpdtId The ID of the BPDT that the queue belongs to. 
   * @q      The global Output queue.
   */ 
  public PDTOutputQ( int bpdtId, HPDTOutputQ q ){

    mBpdtId  = bpdtId;
    mGlobalQ = q;

    mBuffers = new Vector ( Consts . MAX_OVERLAPPED_BPDT );
    mCount = 0;

    mFlagIndex = new long [ Consts . MAX_OVERLAPPED_BPDT ];

    for ( int i=0; i<Consts . MAX_OVERLAPPED_BPDT; i++ ){

      mFlagIndex [ i ] = 0;
      //Vector outputQ = new Vector ( MAX_ITEM_IN_QUEUE );
      //mBuffers . set ( i, outputQ );
      
    }

  }

  public int getBpdtId () {
    return mBpdtId;
  }
  
  private Vector addBufferWithFlag ( long flag ){
    
    for ( int i=0; i < mCount; i++ ){
      if ( mFlagIndex[ i ] == flag )
	return (Vector)mBuffers . get ( i );
    }
    
    Vector buffer = new Vector ( Consts. MAX_ITEM_IN_QUEUE );
    mFlagIndex [ mCount ] = flag;
    mBuffers . add ( buffer );

    mCount = mCount + 1;
    
    if ( mCount >= Consts . MAX_OVERLAPPED_BPDT ){
      System . err . println ( "There are more than " + Consts . MAX_OVERLAPPED_BPDT + " path combinations for one element." );
    }

    return buffer;
  }
  
  public Vector getBufferWithFlag ( long flag ){
    
    for ( int i=0; i < mCount; i++ ){
      if ( mFlagIndex[ i ] == flag )
	return (Vector)mBuffers . get ( i );
    }
    
    return null;
  }


  /** 
   * @qi   The queue item we need to put into the queue.
   * @flag The depth vector of the item.
   */
  public void enqueue( QueueItem qi, long flag ){
    
    Vector outputQ = addBufferWithFlag ( flag );
    
    outputQ . add( qi );
    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 ) 
      Tools.outln ( indent + "[PDTOutputQ:ENQUEUE]The item '" + qi + "' has been enqueued into  BPDT " + BasicPDT.getNameOfBpdt( mBpdtId ) + "with flag:" + ItemFlag . toString( flag ));
    
    qi . increaseCount( );

  } 
  
  /** 
   * @flag Items that has the depth vector will be cleared.
   */
  public void clear( long flag ){

    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 )  
      Tools.outln ( indent + "[PDTOutputQ:CLEAR]Clear the items with flag" + ItemFlag . toString ( flag ) );

    Vector outputQ = getBufferWithFlag ( flag );
    int size;
    QueueItem qi;

    if ( outputQ != null ){
      size = outputQ . size ();
    }else{
      return;
    }
    
    for ( int i=size-1; i>=0; i-- ){
      qi = (QueueItem) outputQ . get ( i );
      qi . decreaseCount ( );
      outputQ . removeElementAt ( i ); 
    }
    
    return;
  }
  
  /** The function will upload the items in the current queue that has the flag to the queue of its parent. */
  public void upload ( long flag, PDTOutputQ anceQ ){
    
    Vector    outputQ = getBufferWithFlag ( flag );
    QueueItem qi;
    int       size;
    int       dif = BasicPDT . findLayerDifference ( mBpdtId, anceQ . getBpdtId( ) );
    long      newFlag = ItemFlag . removeLowestNBits ( flag, dif ) ;
    
    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 )  
      Tools.outln ( indent + "[PDTOutputQ:UPLOAD]Upload the items with flag " + ItemFlag . toString ( flag ) + " to new flag " + ItemFlag . toString ( newFlag )+ " The diff is " + dif);

    if ( outputQ != null ){
      size = outputQ . size ();
    }else{
      return;
    }
    
    Vector   q = anceQ . addBufferWithFlag ( newFlag );
    
    if ( size > 0 ){
      for ( int i=0; i < size; i++ ){
	qi = (QueueItem)outputQ . get ( i );
	q . add ( qi );
      }
      for ( int i=size-1; i>=0; i-- ){
	qi = (QueueItem) outputQ . get ( i );
	outputQ . removeElementAt ( i ); 
      }
    }
  }
  
  /** Mark all items in the queue with flag as output. Remove the references for these items from current queue.*/
  public void flush( long flag ){
    
    Vector      outputQ = getBufferWithFlag ( flag );
    QueueItem   qi;

    if ( outputQ == null ){
      return;
    }

    int         size = outputQ.size();
    
    if ( (Tools.debugLevel & Tools.debugOutputQ ) > 0 )  
      Tools.outln ( indent + "[PDTOutputQ:FLUSH]FLUSH the items with flag " + ItemFlag . toString ( flag ) );
    
    for ( int i=size - 1; i >= 0; i-- ){
      
      qi = (QueueItem)outputQ . get ( i );
      qi . setOutput ( true );
      outputQ . removeElementAt ( i );
    }
    
  }
  
  /** If the item qi is in the queue, mark it as output and remove its reference from current queue. */
  public void output( QueueItem qi ){
    
    qi . setOutput ( true );
  
  }
    
  public void printContent( ){
   
    for ( int j=0; j<mCount; j++ ){
      Vector    outputQ = (Vector)mBuffers . get ( j );
      int       size  = outputQ . size(); 
      
      System.err.println( "There are " + size + " items in the queue with item flag :" + ItemFlag.toString ( mFlagIndex[ j ] ) );
      for ( int i=0; i<size; i++ ){
	System.err.println(((QueueItem)outputQ.get(i)).toString());
      }
      return;
    }
  }
    
};

