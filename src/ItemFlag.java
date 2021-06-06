/* $Id: ItemFlag.java,v 1.3 2002/10/23 05:17:50 pengfeng Exp $
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

/** 
 * The item flag is used to determin which BPDT the item belongs to.
 * It records the stack that leads to the current BPDT.
 *
 * @version $Id: ItemFlag.java,v 1.3 2002/10/23 05:17:50 pengfeng Exp $
 * @author  Feng peng
 */
package edu.umd.cs.db.xsq;

import java.io.*;

public class ItemFlag{

  public static long setDepthBitAt ( long flag, int d ){
    
    long l = 1;
    flag = flag | ( l << d );
    
    return flag;
  }

  /** 
      Get the lowest depth in the flag.
      The depth is zero-based.
  */
  public static int  getLowestDepth( long flag ){
    
    if ( flag == 0 )
      return -1;

    int  count = 0;

    while ( flag != 1 ){
      flag = flag >> 1;
      count ++;
    }

    return count;
    
  }
  
  /**
     Remove the highest bit from the flag if the lowest bit is at depth d.
  */
  public static long removeLowestDepth( long flag, int d ){

    long l = flag;
    if ( l == 0 )
      return flag;
    
    int  count = 0;
    
    while ( l != 1 ){
      l = l >> 1;
      count ++;
    }
    
    if ( count == d ){
	flag = flag ^ ( l << count );
    }

    return flag;
  }

  public static long removeLowestNBits( long flag, int i ){

    long l = flag;
    if ( l == 0 )
      return flag;
    
    int  count = 0;
    
    while ( l != 1 ){
      l = l >> 1;
      count ++;
    }
    
    for ( int j=0; j < i; j++ ){
      flag = flag ^ ( l << ( count - j ) );
    }

    return flag;
  }

  /**
     Remove the highest bit from the flag.
  */
  public static long removeLowestDepth( long flag ){

    long l = flag;
    if ( l == 0 )
      return flag;
    
    int  count = 0;
    
    while ( l != 1 ){
      l = l >> 1;
      count ++;
    }
    
    flag = flag ^ ( l << count );
    
    return flag;
  }
  
  public static String toString( long flag ){
    
    long l = flag;
    if ( l == 0 )
      return "-1";

    int  count = 0;
    String s = "";
    
    while ( l != 1 ){
      l = l >> 1;
      count ++;
    }

    for ( int i=0; i <= count; i++ ){
      if ( ( flag & ( 1 << i ) ) == ( 1 << i ) ){
	s = s + Integer.toString( i );
	if ( i < count ) 
	  s = s + ",";
      }
    }
    
    return s;
  }

  public static void main( String[] args ){

    //DepthFlag df = new DepthFlag () ;
    long      flag = 0;
    System.err.println( " The lowest depth  is " + Integer.toString( ItemFlag . getLowestDepth( flag ) ) );

    flag = ItemFlag . setDepthBitAt ( flag, 0 );
    flag = ItemFlag . setDepthBitAt ( flag, 4 );

    System.err.println( " The flag is " + Long.toString( flag ) );
    System.err.println( " The flag is " + ItemFlag . toString ( flag ) );
    
    flag = ItemFlag . setDepthBitAt ( flag, 10 );
    System.err.println( " The flag is " + Long.toString( flag ) );
    System.err.println( " The flag is " + ItemFlag . toString ( flag ) );
    System.err.println( " The lowest depth  is " + Integer.toString( ItemFlag . getLowestDepth( flag ) ) );
    
    flag = ItemFlag . removeLowestDepth( flag );
    System.err.println( " The flag is " + Long.toString( flag ) );
    System.err.println( " The lowest depth  is " + Integer.toString( ItemFlag . getLowestDepth( flag ) ) );
    System.err.println( " The flag is " + ItemFlag . toString ( flag ) );
  }
}




