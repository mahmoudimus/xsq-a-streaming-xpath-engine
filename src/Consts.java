/* $Id: Consts.java,v 1.5 2002/10/23 05:17:50 pengfeng Exp $
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
 * @version $Id: Consts.java,v 1.5 2002/10/23 05:17:50 pengfeng Exp $
 * @author  Feng peng
 */

public class Consts{

  //
  public static final int     MAX_XPATH_LEN = 1024;
  public static final int     MAX_XPATH_NODES = 32;
  public static final int     MAX_PDT_DEPTH = 32;
  public static final int     MAX_NUM_ATTR = 32;
  public static final int     MAX_TAG_LENGTH = 1024;
  public static final int     MAX_PDT_STATES = 1024;
  public static final int     MAX_BPDT_NUMBER = 256;
  public static final int     MAX_OVERLAPPED_BPDT = 1024;
  public static final int     MAX_ITEM_IN_QUEUE = 1024;
  public static final String  TEXT_ATTR_NAME = "text";
  public static final String  ANY_ATTR_NAME = "any";
  public static final String  ATTR_CATCHALL = "catchall";
 
  public static final String  BEGIN_STATE = "BEGIN";
  public static final String  END_STATE = "END";

  //A list of all comparison operators
  public static final int     EQ = 1;			//=
  public static final int     GT = 2;			//>
  public static final int     LT = 3;			//<
  public static final int     GET = 4; 		        //>=
  public static final int     LET = 5;		        //<=
  public static final int     NET = 6;		        //!=
  public static final int     CONTAINS = 7;	        //%;	
  public static final int     EXISTS = 8;

  //A list of logical results
  public static final int     TRUE  = 1;
  public static final int     FALSE = 2;
  public static final int     NA    = 4;
  public static final int     ALL   = 7;

  //A list of SAX events
  public static final int     BEGIN    = 1;
  public static final int     TEXT     = 2;
  public static final int     END      = 4;
  public static final int     CATCHALL = 7;

  //A list of operation flag in the output buffer
  public static final int     IMMEDIATE = 1;
  public static final int     ERASE	= 2;
  public static final int     OUTTRUE   = 3;
  public static final int     OUTFALSE  = 4;

  // The list of aggregation function: "value", "avg","max","min", "sum", "count"
  public static final int     NONE	= 0;
  public static final int     VALUE	= 1;
  public static final int     AVG	= 2;
  public static final int     MAX	= 3;
  public static final int     MIN	= 4;
  public static final int     SUM	= 5;
  public static final int     COUNT	= 6;


  public static String  ROOT_TAG = "root";
  
  public static void setRootTag( String s ){
      //System.err.println("The root tag is set to " + s ); 
    ROOT_TAG = s;
  }
  
  public static final String INIT_QUERY = "//pub[year>2000]//book[author]/name{text()}";
  public static final String TestFileName = "closure.xml";
  
  public static final String HPDTFigureName = "hpdt.dot";
}



