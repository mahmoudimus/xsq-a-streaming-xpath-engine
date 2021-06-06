/* $Id: PDTStatBuffer.java,v 1.4 2002/10/23 23:03:39 pengfeng Exp $
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
 * PDTStatBuffer is the buffer that holds necessary statistics for the query.
 *
 * @version $Id: PDTStatBuffer.java,v 1.4 2002/10/23 23:03:39 pengfeng Exp $
 * @author  Feng peng
 */

public class PDTStatBuffer{

  /** Maximum number of items in the queue. The default value is 1024. */
  
  private final static String indent = "\t\t";
  
  private int          mBpdtId; 
  private StatBuffer   mGlobalStatBuffer;
  
  
  private long[]       mFlagIndex;
  private Vector       mBuffers; 
  private int          mCount;
    
  public PDTStatBuffer(int bpdtId, StatBuffer b ){
    
    mBpdtId  = bpdtId;
    mGlobalStatBuffer = b;
    
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
  
  public StatBuffer addBufferWithFlag ( long flag ){
    
    for ( int i=0; i < mCount; i++ ){
      if ( mFlagIndex[ i ] == flag )
	return (StatBuffer)mBuffers . get ( i );
    }
    
    StatBuffer buffer = new StatBuffer( );
    mFlagIndex [ mCount ] = flag;
    mBuffers . add ( buffer );
    
    mCount = mCount + 1;
    
    if ( mCount >= Consts . MAX_OVERLAPPED_BPDT ){
      System . err . println ( "There are more than " + Consts . MAX_OVERLAPPED_BPDT + " path combinations for one element." );
    }
    
    return buffer;
  }
  
  public StatBuffer getBufferWithFlag ( long flag ){
    
    for ( int i=0; i < mCount; i++ ){
      if ( mFlagIndex[ i ] == flag )
	return (StatBuffer)mBuffers . get ( i );
    }
    
    return null;
  }

  public void updateItem( int outputFunc, String curValue, long flag ){
    
    StatBuffer buffer = addBufferWithFlag ( flag );

    buffer . updateItem ( outputFunc, curValue );
    
    if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
      Tools.outln ( indent + "[PDTStatBuffer:UPDATE]The item '" + curValue + "' has been updated in the  BPDT " + BasicPDT.getNameOfBpdt( mBpdtId ) + "with flag:" + ItemFlag . toString( flag ));
  
  }
  
};





