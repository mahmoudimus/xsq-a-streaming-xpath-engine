/* $Id: PDTArc.java,v 1.5 2002/10/21 23:31:22 pengfeng Exp $
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
 * PDTArc is the transition arc in the PDT.
 *
 * @version $Id: PDTArc.java,v 1.5 2002/10/21 23:31:22 pengfeng Exp $
 * @author  Feng peng
 */

public class PDTArc
{
  private PDTState	mSrcState;
  private PDTState	mDestState;
  private String	mTag;
  private int		mEval;
  private int		mEvent;
  private Filter        mFilter;
  private Output	mOutput;
  private Output	mBuffer;
  private boolean       mClosure;
  private boolean       mCatchall;
  private boolean       mCrossLayer;

  private int mPDTID = -1;

  public int getPDTID( ){
    return mPDTID;
  };

  public void   setPDTID( int i ){
    mPDTID = i;
  };

  public void	setSrcState (PDTState srcState) { mSrcState  = srcState; };
  public void	setDestState(PDTState destState){ mDestState = destState; };
  public void	setTagString(String tag)        { mTag = tag; };
  public void	setEvent( int event)            { mEvent = event; };
  public void	setEval ( int eval )            { mEval = eval; };
  
  public void	setFilter(Filter filter)        { mFilter = filter; };
  public void	setOutput(Output output)        { mOutput = output; };
  public void	setBuffer(Output buffer)        { mBuffer = buffer; };
  
  public void   setClosure( boolean closure )   { mClosure = closure; };
  public void   setCrossLayer( boolean cross ) { mCrossLayer = cross; };

  public PDTState	getSrcState( )  { return mSrcState; };
  public PDTState	getDestState( ) { return mDestState; };
  public String 	getTagString( ) { return mTag; };
  public int	    	getEvent( )	{ return mEvent; };
  public int      	getEval ( )	{ return mEval; };
  
  public Filter   getFilter( ){ return mFilter; };
  public Output   getOutput( ){ return mOutput; };
  public Output   getBuffer( ){ return mBuffer; };

  public boolean  isClosure( ){  return mClosure; };
  public boolean  isCrossLayer( ){ return mCrossLayer; };

  public boolean  isCatchall( ){

    if ( mTag == "**" )
      return true;
    return false;
  }

  public void	addOutputOp( int op ){ 

    if ( mOutput == null ){
      System.err.println( "[PDTArc.addOutputOperation] The output object is null." );
      return;
    }
    
    int oldOP = mOutput .getOperation( );  

    if ( ( oldOP == Output.UPLOAD ) && ( op == Output.ENQUEUE )){
      mOutput . setOperation ( Output.ENQUEUE_UPLOAD );
      return;
    }
  
    if (( oldOP == Output.ENQUEUE ) && ( op == Output.UPLOAD )){
      mOutput . setOperation ( Output.ENQUEUE_UPLOAD );
      return;
    }
   
    if ( ( oldOP == Output.FLUSH ) && ( op == Output.OUTPUT )){
      mOutput . setOperation ( Output.FLUSH_OUTPUT );
      return;
    }

    if ( ( oldOP == Output.OUTPUT ) && ( op == Output.FLUSH )){
      mOutput . setOperation ( Output.FLUSH_OUTPUT );
      return;
    }

    System.err.println( "[PDTArc.addOutputOperation] Don't know how to combine " + Integer.toString( op ) + " to " + Integer.toString( oldOP ) );
    return;
  }

  public String   getLabel( ){
    
    String label = "";
    
    if ( mEvent == Consts. BEGIN ){
      label = label + "<" + mTag + ">";
    }else if ( mEvent == Consts. END ){
      label = label + "</" + mTag + ">";
    }else if ( mEvent == Consts. TEXT ){
      label = label + "<" + mTag + ":text()>";
    }else if ( mEvent == Consts. CATCHALL){
      label = label + "<" + mTag + ":CATCHALL>";
    }else{
      label = label + "<" + mTag + ":unknown>";
    }
    
    if ( mFilter != null ){
      if ( mEval == Consts.FALSE ){
	label = label + "\\n![" + mFilter.mAttrName + " " + mFilter.mOp + " " + mFilter.mConst + "]";
      }else{
	label = label + "\\n[" + mFilter.mAttrName + " " + mFilter.mOp + " " + mFilter.mConst + "]";
      }
    }
    
    if ( mOutput != null ){
      label = label + "\\n{"+ mOutput.getOperationName( ) + " " + mOutput.getFuncName() + " " + mOutput.getAttrName() + "}";
    }
    
    return label;
  }
 
  
    public boolean  matchArc (	PDTState   srcState,
				String	   tag,
				int	   event,
				int	   eval ){
      
      if ( mTag . equals ( tag ) ) 
	  if ( ( mEvent & event ) > 0 ) 
	      if ( ( mEval & eval ) > 0 ) 
		  if ( mTag . equals ( tag ) ) 
		      if ( mSrcState.getName().equals( srcState.getName() ) )
			  return true;
      
      return false;
  }
  

    public boolean  matchArc (	String	   tag,
				int	   event,
				int	   eval ){
      if ( ( mEvent & event ) > 0 ){ 
	if ( ( mEval & eval ) > 0 ){ 
	  if ( mTag . equals ( tag ) ){ 
	    return true;
	  }
	}
      }

      if ( mEvent == Consts.CATCHALL ){
	if ( ( mEval & eval ) > 0 ){ 
	  return true;
	}
      }
      return false;
    }  
  
  public String toString( int depth ){

    String s = "";
    for ( int i=0; i<depth; i++ )
      s = s + "\t";
    
    s = s + "Src :\t"  + mSrcState.getName() + "\n";
    s = s + "Dest:\t"  + mDestState.getName() + "\n";
    s = s + "Tag:\t"   + mTag + "\n" ;
    s = s + "Eval:\t"  + Tools.getEvalStr( mEval ) + "\n";
    s = s + "Event:\t" + Tools.getEventStr ( mEvent ) + "\n";
    s = s + "Closure:\t" + mClosure + "\n" ;

    if ( mFilter != null ){
      s = s + "Filter:\t";
      s = s + "\tObj: " + mFilter.mAttrName + "\t";
      s = s + "Op: " + mFilter.mOp + "\t";
      s = s + "Const: " + mFilter.mConst + "\n";
    }
    
    if ( mOutput != null ){
      s = s + "Output:\t" + mOutput.getOperationName( );
      s = s + "\t" + mOutput.getFuncName();
      s = s + "\t" + mOutput.getAttrName() + "\n";
    }
    
    if ( mBuffer != null ){
      s = s + "Buffer\t" + mBuffer.getFuncName();
      s = s + "\t" + mBuffer.getAttrName()  + "\n";
    }
    
    return s; 
  };
  
  public void printArc( int depth ){
   
    String s = "";
    for ( int i=0; i<depth; i++ )
      s = s + "\t";
    
    System.out.println( s + "Src :\t"  + mSrcState.getName() );
    System.out.println( s + "Dest:\t"  + mDestState.getName() );
    System.out.println( s + "Tag:\t"   + mTag );
    System.out.println( s + "Eval:\t"  + Tools.getEvalStr( mEval ) );
    System.out.println( s + "Event:\t" + Tools.getEventStr ( mEvent ) );
    System.out.println( s + "Closure:\t" + mClosure );
    
    if ( mFilter != null ){
      System.out.print( s + "Filter:\t" );
      System.out.print( s + "\tObj: " + mFilter.mAttrName + "\t" );
      System.out.print( "Op: " + mFilter.mOp + "\t" );
      System.out.println( "Const: " + mFilter.mConst );
    }
    
    if ( mOutput != null ){
      System.out.print( s + "Output:\t" + mOutput.getOperationName( ) );
      System.out.print( s + "\t" + mOutput.getFuncName() );
      System.out.println( "\t" + mOutput.getAttrName() );
    }
    
    if ( mBuffer != null ){
      System.out.print( s + "Buffer\t" + mBuffer.getFuncName() );
      System.out.println( "\t" + mBuffer.getAttrName() );
    }
    
    return; 
  };


};
