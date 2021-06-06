/* $Id: Event.java,v 1.4 2002/10/20 04:37:28 pengfeng Exp $
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
 * Event object is corresponding to the 
 *
 * @version $Id: Event.java,v 1.4 2002/10/20 04:37:28 pengfeng Exp $
 * @author  Feng peng
 */

class AttrPair{
  
    String mName;
    String mValue;
    
    public AttrPair( String name, String value ){
	
	mName = name;
	mValue = value;
    }
}

public class Event{
  
  public String          mTag;
  public int             mEvent;
  public Vector          mVectorAttrPair;
  public int             mDepth;

  private int            mNumAttrs;

  public Event( ){
    
    mTag      = "";
    mEvent    = 0;
    mNumAttrs = 0;
    mDepth    = 0;
    mVectorAttrPair = new Vector( Consts.MAX_NUM_ATTR);
    
    return;
  }

  public Event( String tag, int event, int numAttrs, int depth ){

    mTag = tag;
    mEvent = event;
    mNumAttrs = 0;
    mDepth = depth;
    mVectorAttrPair = new Vector( numAttrs );
    return;
  }
  
  public void addAttribute ( String name, String value ){

      //Tools.out( "Attribute added: Name -- " + name + ":Value -- " + value  );
  
    AttrPair ap = new AttrPair( name, value );

    //ap . mName  = name;
    //ap . mValue = value;

    mVectorAttrPair . add ( ap );

    mNumAttrs ++;

    return;
  }

  public void clearAttrList ( ) {

    mVectorAttrPair . clear ( );

  }

  /**
   * Return the index of the required attrName. The value is stored in the attrValue.
   * If the return value is -1, the attrName is not found in the list.
   */
  public String findAttrValue ( String attrName ){
    
    if ( attrName . equals( Consts.ANY_ATTR_NAME ) )
      return "";
    
    if ( attrName . equals( Consts.ATTR_CATCHALL ) ){
      return toString( );
    }

    for ( int i=0; i<mNumAttrs; i++ ){
	AttrPair curAttrPair = (AttrPair)mVectorAttrPair.get ( i );
	if ( curAttrPair.mName.equals( attrName ) ) {	
	    return curAttrPair.mValue;
	  }
      }
    
    return null;
  }
  
  public String getAttrNameAt ( int index ) {
    
    AttrPair curAttrPair = (AttrPair)mVectorAttrPair.get ( index );
    return curAttrPair.mName;
    
  }

  public String getAttrValueAt ( int index ) {
    
    AttrPair curAttrPair = (AttrPair)mVectorAttrPair.get ( index );
    return curAttrPair.mValue;
    
  }

  public void printEvent( ){
    System.out.print ( "Tag: " + mTag );
    System.out.print ( "\tEvent: " + Tools . getEventStr( mEvent ) );
    return;
  }

  public String toString( ){

    if ( mEvent == Consts.BEGIN ){
      AttrPair curAttrPair;
      StringBuffer s = new StringBuffer ( Consts.MAX_TAG_LENGTH );
      s . append ( "<" ) . append ( mTag );
      for ( int i = 0; i < mNumAttrs; i++ ){
	curAttrPair = (AttrPair)mVectorAttrPair.get ( i );
	s . append ( " " ) . append ( curAttrPair.mName ) . append ( "=" ). append ( "\"" ) . append ( curAttrPair.mValue ). append ( "\"" );
      }
      s . append ( ">" );
      return s.toString();
    }

    if ( mEvent == Consts.TEXT ){
      String s = findAttrValue( Consts.TEXT_ATTR_NAME );
      return s;
    }

    if ( mEvent == Consts.END ){
      String s = "</"+ mTag + ">";
      return s;
    }

    return "ERROR";
  }
}
