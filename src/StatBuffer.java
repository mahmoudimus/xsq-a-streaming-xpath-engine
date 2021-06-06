/* $Id: StatBuffer.java,v 1.1 2002/10/23 23:03:39 pengfeng Exp $
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
 * Class description goes here
 *
 * @version $Id: StatBuffer.java,v 1.1 2002/10/23 23:03:39 pengfeng Exp $
 * @author  Feng peng
 */

public class StatBuffer{
  
  private Vector   mStatBuffer;
  private boolean  initStatus;

  private final static String indent = "\t\t\t";
  
  /**
   * Constructor. It will initialize all the initial value of the statistics
   */ 
  public StatBuffer(){
    
    initStatus = true;
    mStatBuffer = new Vector( );
    mStatBuffer . setSize ( 7 ) ;
    mStatBuffer.set(Consts.VALUE, "" );
    mStatBuffer.set(Consts.AVG,   "0");
    mStatBuffer.set(Consts.MAX,   "0");
    mStatBuffer.set(Consts.MIN,   "0");
    mStatBuffer.set(Consts.SUM,   "0");
    mStatBuffer.set(Consts.COUNT, "0");
  }
    
  public void clear(){
    initStatus = true;
    mStatBuffer.set(Consts.VALUE, "" );
    mStatBuffer.set(Consts.AVG,   "0");
    mStatBuffer.set(Consts.MAX,   "0");
    mStatBuffer.set(Consts.MIN,   "0");
    mStatBuffer.set(Consts.SUM,   "0");
    mStatBuffer.set(Consts.COUNT, "0");
  }
  
  /**
   * Currently there are at most two item in the StatBuffer.
   * We suppose that there is only on aggregation in the XPath expression.
   * So the operation here is simplified version. No need to match the path.
   */
  public void putItem   ( int outputFunc, String curValue ){
    mStatBuffer.set(outputFunc, curValue);
    return;
  }
    
  public boolean isInitStatus ( ){
    return initStatus;
  }
    
  /** Update the content in the buffer */
  public void updateItem( int outputFunc, String curValue ){
      
    if ( initStatus ){
      if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	System.err.println( indent + "[STAT]The first UPDATE" );
      initStatus = false;
      putItem ( outputFunc, curValue );
      return;
    }
      
    switch ( outputFunc ){
	
    case Consts.VALUE:
      mStatBuffer.set ( outputFunc, curValue );
      break;
	
    case Consts.AVG:{
      int count = Integer.valueOf( (String)mStatBuffer.get( Consts.COUNT ) ).intValue();
	
      float sum = Float.valueOf( (String)mStatBuffer.get( Consts.SUM ) ).floatValue();

      float cur = Float.valueOf( (String)curValue).floatValue();
	
      sum += cur;
      count++;
	
      putItem( Consts.SUM,	Float.toString( sum ) );
      putItem( Consts.COUNT,  Integer.toString( count) );
      putItem( Consts.AVG,    Float.toString( sum/count) );

      if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	System . err . println ( indent + "[STAT]The AVG is updated to " + Float.toString( sum/count) );
    };
    break;
    
    case Consts.MAX:{
      float cur_max = Float.valueOf( (String)mStatBuffer.get( Consts.MAX ) ).floatValue();
      float cur = Float.valueOf ( curValue ).floatValue();
      if ( cur > cur_max ){
	putItem( Consts.MAX, Float.toString( cur ) );
	
	if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	  System . err . println ( indent + "[STAT]The MAX is updated to " +  Float.toString( cur ) );
      }
    };
    break;
    
    case Consts.MIN:{
      float cur_min = Float.valueOf( (String)mStatBuffer.get( Consts.MIN ) ).floatValue();
      float cur = Float.valueOf ( curValue ).floatValue();
      if ( cur < cur_min ){
	putItem( Consts.MIN, Float.toString( cur ) );
	if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	  System . err . println ( indent + "[STAT]The MIN is updated to " +  Float.toString( cur ) );
      }
    }; 
    break;
    
    case Consts.SUM:{
      float sum = Float.valueOf( (String)mStatBuffer.get( Consts.SUM ) ).floatValue();
      //System.err.println("[STAT]The value in the buffer is " + sum );
      float cur = Float.valueOf( curValue ).floatValue();
      //System.err.println("[STAT]The new value is " + cur );
      sum += cur;
      putItem( Consts.SUM, Float.toString( sum ) );
      if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	  System . err . println ( indent + "[STAT]The SUM is updated to " +  Float.toString( sum ) );
    };
    break;
      
    case Consts.COUNT:{
      int count = Integer.valueOf( (String)mStatBuffer.get( Consts.COUNT )).intValue();
      count ++;
      putItem( Consts.COUNT, Integer.toString( count ) );
      if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	  System . err . println ( indent + "[STAT]The COUNT is updated to " +  Float.toString( count ) );
    };
    break;
      
    default:
      System.out.println( indent + "[STAT]Unknown aggregation function.");
      break;
	
    } 
    return;
  };
    
  public void merge ( StatBuffer sb ){
      
    if ( sb.isInitStatus() )
      return;
    
    boolean oldStatus = initStatus;
    //System.err.println("Updating the statistics.");
    
    String s = sb.getItem( Consts.COUNT );
    updateItem ( Consts.COUNT, s );
    initStatus = oldStatus;
    
    s = sb.getItem( Consts.SUM );
    updateItem ( Consts.SUM, s );
    initStatus = oldStatus;
      
    s = sb.getItem( Consts.MAX );
    updateItem ( Consts.MAX, s );
    initStatus = oldStatus;
      
    s = sb.getItem( Consts.MIN );
    updateItem ( Consts.MIN, s );
    initStatus = oldStatus;
      
    int count = Integer.valueOf( (String)mStatBuffer.get( Consts.COUNT ) ).intValue();
    float sum = Float.valueOf( (String)mStatBuffer.get( Consts.SUM ) ).floatValue();
    putItem( Consts.AVG, Float.toString( sum/count) );
      
    initStatus = false;
      
    sb.clear();

    return;
  }
    
  public String getItem   ( int outputFunc ){
    return  (String)mStatBuffer.get( outputFunc );
  }
  
  public String flush ( int outputFunc, boolean getResult ){
    
    if ( getResult ){
      if ( (Tools.debugLevel & Tools.debugStat ) > 0 ) 
	  System . err . println ( indent + "[STAT]The result " + getItem( outputFunc ) + " is flushed to output." );
      return getItem( outputFunc );
    }else{
      System.out.println( getItem( outputFunc ) );
      return null;
    }
  
  }
    
}
