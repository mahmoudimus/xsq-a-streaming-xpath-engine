/* $Id: PDTState.java,v 1.3 2002/10/21 23:31:23 pengfeng Exp $
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
 * PDTState is the state in the PDT.
 *
 * @version $Id: PDTState.java,v 1.3 2002/10/21 23:31:23 pengfeng Exp $
 * @author  Feng peng
 */

class PDTState 
{
  /**
   *The name of current node
   */
  private String mName;		
  
  /**
   * This is used to record all the text meet so far
   */
  private String  mTextString; 
  
  private Vector  mArcs;

  private static final int MAX_ARCS_PER_STATE = 8;
    
  public PDTState(){ 
    mArcs   = new Vector( MAX_ARCS_PER_STATE );
    mTextString = "";
    mName = "";
  };
  
  public PDTState(String name){
    mArcs   = new Vector( MAX_ARCS_PER_STATE );
    mTextString = "";
    mName = name;
  };
  
  public void addArc ( Object arc ){ 
    mArcs . add ( arc );
    return;
  }

  public Object getArc ( int i ){
    
    return mArcs.get(i);
    
  }
  
  public int getArcSize( ) {
    
    return mArcs.size();
  }
  
  public String getLabel ( ){

    return mName . replace( '.', '0' );
  }

  public int getBpdtId ( ){
    
    int pos = mName . indexOf ( '.' ) ;
    return ( Integer . valueOf ( mName . substring ( 0, pos ) ) ) . intValue ( );
    //int idInLayer = ( Integer . valueOf ( mName . substring ( pos + 1 ) ) ) . intValue ( );
    
    //return (  1 << layer + idInLayer );

  }

  public String getName(){return mName;};
  public void   setName( String name ){ mName = name; };
  
  public void   appendText( String val ) { mTextString = mTextString + val ; }
  public void   clearText( ) { mTextString = "";};
  
  public String getText( ) { return mTextString; };
  
  public String toString( ){
    
    String s = "Name: " + mName ;
    return s;
    
  }
  public void   printState(){
    System.out.print ( "N: "   + mName );
    //System.out.println ( "\tT: " + mTextString );
  }


};
