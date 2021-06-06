/* $Id: QueueItem.java,v 1.1 2002/10/21 23:31:23 pengfeng Exp $
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
 * The item in the queue. The item is stored in the HPDTOutputQ. The PDTOutputQ is only storing the references.
 *
 * @version $Id: QueueItem.java,v 1.1 2002/10/21 23:31:23 pengfeng Exp $
 * @author  Feng peng
 */

public class QueueItem{
  
  /**The unique ID for the object*/
    //private long      mId;         

  /**Number of depth flags of this item.*/
  private int       mCount;     
  
  /**If the content has been evaluated to be in the result, the output value is true or else it is false.*/
  private boolean   mOutput;     

  /**The actual content. */
  private String    mContent;

  public QueueItem( ){
    
      //mId = globalId;
    mContent = null;
    mCount = 0;
    mOutput = false;

  }

  public QueueItem( long globalId, String s ){
      //mId = globalId;
    mContent = s;
    mCount = 0;
    mOutput = false;
  }

  public void setContent( String content ){

    mContent = content;

    return;
  }

  /**
   * Get the value of Output.
   * @return value of Output.
   */
  public boolean isOutput() {
    return mOutput;
  }
  
  /**
   * Set the value of Output.
   * @param v  Value to assign to Output.
   */
  public void setOutput(boolean  v) {
    mOutput = v;
  }
  
  /**
   * If the reference count is zero, return true. 
   */
  public boolean isZeroCount() {
    if ( mCount == 0 )
      return true;
    return false;
  }
  
  /**
   * Increase the value of count by 1.
   * 
   */
  public void increaseCount( ) {
    mCount = mCount + 1;
  }
  
  /**
   * Decrease the value of count by 1.
   * 
   */
  public void decreaseCount( ) {
    mCount = mCount - 1;
  }

  /** Return the count of the item. */
  public int getCount( ){
    return mCount;
  }
  
  public String toString(){
    return mContent;
  }
}
  
