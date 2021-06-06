/* $Id: Filter.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
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
 * Filter object is corresponding to the filter function in the XPath Expression
 *
 * @version $Id: Filter.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
 * @author  Feng peng
 */

public class Filter{

  public String    mTagName  = "" ;
  public String    mAttrName = "" ;
  public String    mOp       = "" ;
  public String    mConst    = "" ;

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
  
  
  
  public boolean isText( )
  {
    if ( mAttrName . equals ( Consts.TEXT_ATTR_NAME ) )
      return true;
    return false;
  }

  public boolean isEmpty()
  {
    if ( mAttrName . equals ( "" ) )
      return true;
    return false;
  }

  //The logic to evaluate text is different with the logic to evaluate the attribute!
  public int evaluateText( Event event, String str )
  {
    if ( event.mEvent == Consts.END )
      {
	//we have to let the filter know the current string
	//pair<string, string> newPair;
	//newPair.first = TEXT_ATTR_NAME;
	//newPair.second = str;
	//event.tVectorAttrPair.push_back(newPair);
		
	return evaluate( event );
      }

    int result; 
	
    int op = Tools.getOp( mOp );
	
    switch ( op )
      {
      case Consts.EQ:		//=
      case Consts.GT:		//>
      case Consts.LT:		//<
      case Consts.GET:		//>=
      case Consts.LET:		//<=
      case Consts.NET:		//!=
	result = Consts.NA;
	break;
      case Consts.CONTAINS:	//%;	
	{
	  result = Evaluator.isContaining ( str, mConst );
	  if ( result == Consts.FALSE )
	    result = Consts.NA;
	};
	break;
      case Consts.EXISTS:
	{
	  result = Consts.TRUE;
	};
	break;
      default:
	result = Consts.FALSE;
      };
	
    return result;
	
  }

  public int evaluate( Event event )
  {
    int pos, result; 
    String attrVal = "";

    attrVal = event.findAttrValue( mAttrName );

    if ( attrVal == null ) 
      return Consts.FALSE;

    int op = Tools.getOp( mOp );
	
    switch ( op )
      {
      case Consts.EQ:		//=
	{
	  result = Evaluator.isEqualTo ( attrVal, mConst );
	};
	break;
      case Consts.GT:			//>
	{
	  result = Evaluator.isGreaterThan ( attrVal, mConst );
	};
	break;
      case Consts.LT:			//<
	{
	  result = Evaluator.isLessThan ( attrVal, mConst );
	};
	break;
      case Consts.GET:		//>=
	{
	  result = Evaluator.isGreaterOREqualTo ( attrVal, mConst );
	};
	break;
      case Consts.LET:		//<=
	{
	  result = Evaluator.isLessOREqualTo ( attrVal, mConst );
	};
	break;
      case Consts.NET:		//!=
	{
	  result = Evaluator.isNotEqualTo ( attrVal, mConst );
	};
	break;
      case Consts.CONTAINS:	//%;	
	{
	  result = Evaluator.isContaining ( attrVal, mConst );
	};
	break;
      case Consts.EXISTS:
	{
	  result = Consts.TRUE;
	};
	break;
      default:
	result = Consts.FALSE;
      };
	
    return result;

  }

  public void parseFilterString( String filter )
  {
    int i, cur_state = 0, size;

    if ( filter . equals ( "" )  )
      return;

    //parse the filter string
    size = filter.length();

    int start = 0;

    if ( filter.charAt(0) == '@' )
      {
	mTagName = "";  	
	start = 1;
	cur_state = 1; // we are looking for the attr name directly
      }

    for ( i=start; i<size; i++)
      {
	switch ( cur_state )
	  {
	  case 0: // the first is the object
	    {
	      if ( Tools.isCharInOp(filter.charAt(i) ) )
		{
		  mOp = filter.substring(i,i+1);
		  mAttrName = Consts.TEXT_ATTR_NAME;
		  cur_state = 2;
		}else if ( filter.charAt(i) == '@' )
		  {
		    cur_state = 1;
		  }else if ( filter.charAt(i) == ' ' )
		    {	
		      cur_state = 2;
		    }
	      else
		{
		  mTagName = mTagName + filter.charAt(i);
		}
	    };
	    break;
	  case 1: // this is the attribute
	    {
	      if ( Tools.isCharInOp(filter.charAt(i) ) )
		{
		  mOp = filter.substring(i,i+1);
		  cur_state = 2;
		}else if ( filter.charAt(i) == ' ' )
		  {	
		    cur_state = 2;
		  }
	      else
		{
		  mAttrName = mAttrName + filter.charAt(i);
		}
	    };
	    break;
	  case 2: // this is the operator
	    {
	      if ( Tools.isCharInOp(filter.charAt(i) ) )
		{
		  mOp = mOp + filter.charAt(i);
		}
	      else
		{
		  if ( filter.charAt(i) == ' ' )
		    cur_state = 3;
		  else
		    {
		      mConst = filter.substring(i,i+1);
		      cur_state = 3;
		    }
		};
	    };
	    break;
	  case 3: // this is the constant
	    {
	      mConst = mConst + filter.charAt(i);
	    };
	    break;
	  default:
	    System.err.println( "Error in the filter function: " + filter );
	  }
      }

    if ( cur_state == 2 )
      System.err.println( "Error in the filter function: missing constant" + filter );
	
    if ( cur_state == 0 )
      {
	mAttrName = "any";
	mOp = "exists";
        mConst = "somewhere";
      }

    if ( cur_state == 1 ) 
      {
        mOp = "exists";
	mConst = "somewhere";
      }

    if ( mAttrName . equals ( "" ) ) 
      System.err.println( "Attrbiute empty: " + filter );

    if ( mOp . equals ( "" ) )
      System.err.println( "Operator empty: " + filter );

    if ( mConst . equals ( "" ) )
      System.err.println( "Const empty: " + filter );
	
    return;
  }

  
}
