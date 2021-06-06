/* $Id: Evaluator.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
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
 * Evaluator provides the methods to evaluate all the filter functions allowed in the filter.
 *
 * @version $Id: Evaluator.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
 * @author  Feng peng
 */

public class Evaluator{
  
  public static int isEqualTo( String testVal, String constVal ){

      //Tools.out("The testVal is " + testVal + " AND the constVal is " + constVal);
    int result;
    if ( testVal . equals(constVal) )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;

    return result;
  }

  public static int isGreaterThan( String testVal, String constVal ){
    int result;
    float t = Float . valueOf( testVal  ) . floatValue( );
    float c = Float . valueOf( constVal ) . floatValue( );
    if ( t > c )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;
    return result;
  }
  
  public static int isLessThan( String testVal, String constVal ){
    int result;
    float t = Float . valueOf( testVal  ) . floatValue( );
    float c = Float . valueOf( constVal ) . floatValue( );
    if ( t < c )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;
    return result;
  }

  public static int isGreaterOREqualTo( String testVal, String constVal ){
    int result;
    float t = Float . valueOf( testVal  ) . floatValue( );
    float c = Float . valueOf( constVal ) . floatValue( );
    if ( t >= c )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;
    return result;
  }
  
  public static int isLessOREqualTo( String testVal, String constVal ){
    int result;
    float t = Float . valueOf( testVal  ) . floatValue( );
    float c = Float . valueOf( constVal ) . floatValue( );
    if ( t <= c )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;
    return result;
  }
  
  public static int isNotEqualTo( String testVal, String constVal ){
    int result;
    if ( testVal.compareTo(constVal) == 0 )
      result = Consts.FALSE;
    else
      result = Consts.TRUE;

    return result;
	
  }
  
  public static int isContaining( String testVal, String constVal ){
    int result;
    int pos = testVal.indexOf(constVal); 
    if ( pos != -1 )
      result = Consts.TRUE;
    else
      result = Consts.FALSE;

    return result;

  }

}










