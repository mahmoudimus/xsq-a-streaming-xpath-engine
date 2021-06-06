/* $Id: XPathNode.java,v 1.4 2002/10/20 04:37:28 pengfeng Exp $
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
 * XPathNode is the functional unit, location step, in the XPath query expression. 
 * Please refer to README for general information about the XPath query expression.
 * For example, the XPath expression "/publication//book[@price>20]/title" contains three context nodes:
 * 1. /publication
 * 2. //book[@price>20]
 * 3. /title
 * 
 * @version $Id: XPathNode.java,v 1.4 2002/10/20 04:37:28 pengfeng Exp $
 * @author  Feng peng
 */

public class XPathNode{

  public XPathNode(){
  }

  public XPathNode( String tag, String filter, String output, boolean closure ){

    mTag = new String ( tag );
    mFilter = new String ( filter );
    mOutput = new String ( output );
    mClosure = closure;

  }

  /**
   * The tag String in the context node. It could be the wildcard '*' or '//'.
   */
  private String mTag;

  /**
   * The filter String in the context node.
   */
  private String mFilter;

  /**
   * The output String in the context node.
   */
  private String mOutput;

  private boolean mClosure;

  private boolean mNextIsClosure;

  /**
   * Get the value of mNextIsClosure.
   * @return value of mNextIsClosure.
   */
  public boolean nextIsClosure() {
    return mNextIsClosure;
  }
  
  /**
   * Set the value of nextIsClosure.
   * @param v  Value to assign to nextIsClosure.
   */
  public void setNextIsClosure(boolean  v) {
    this.mNextIsClosure = v;
  }
  
  
  /**
   * Get the value of closure.
   * @return value of closure.
   */
  public boolean isClosure() {
    return mClosure;
  }
  
  /**
   * Set the value of closure.
   * @param v  Value to assign to closure.
   */
  public void setClosure(boolean  v) {
    mClosure = v;
  }
  
  /**
   * Set the tag.
   */
  public void setTag( String newTag ){
    mTag = newTag;
  }

  /**
   * Get the tag.
   */
  public String getTag( ){
    return mTag;
  }

  /**
   * Set the filter String.
   */
  public void setFilter( String newFilter ){
    mFilter = newFilter;
  }

  /**
   * Get the filter String.
   */
  public String getFilter( ){
    return mFilter;
  }

  /**
   * Set the output String.
   */
  public void setOutput( String newOutput ){
    mOutput = newOutput;
  }

  /**
   * Get the output String.
   */
  public String getOutput( ){
    return mOutput;
  }


  public  void printNode( int depth ){
    String s = "";
    for ( int i=0; i<depth; i++ )
      s = s + "\t";
    
    System.err.println( s + "Tag:\t" + mTag );
    System.err.println( s + "Filter:\t" + mFilter );
    System.err.println( s + "Output:\t" + mOutput );
    return;
  }
}

