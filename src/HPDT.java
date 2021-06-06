/* $Id: HPDT.java,v 1.8 2002/10/24 16:42:14 pengfeng Exp $
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
 * The hierarchical PDT system that connects all the basic PDTs.
 *
 * @version $Id: HPDT.java,v 1.8 2002/10/24 16:42:14 pengfeng Exp $
 * @author  Feng peng
 */

public class HPDT{

  public static final int MAX_NUMBER_BPDT = 255; 
  public static final int MAX_RESULT_SIZE = 64 * 1024;
  
  /** The vetor contains all the BPDTs. */
  private Vector mVectorBpdt; 

  /** The vector contains all the location steps. */
  private Vector mVectorXPathNodes;

  /** The vector contains all the states. */
  private Vector mVectorStates;
      
  /** The output buffer that is organized as a queueu. */
  private Vector mVectorOutputQ;
  
  /** The global output queue to hold all the actual values */
  private HPDTOutputQ mGlobalQueue;

  /** The statistics buffer that stores statistics for the aggregation functions. */
  private Vector mVectorStatBuffer;    

  /** The global statistics buffer. */
  private StatBuffer mGlobalStatBuffer;

  /** If we need to get the result in a string so that it can be used in the GUI. */
  private boolean mGetResult = false;

  /** The string that is used to store the result. */
  private StringBuffer mResult;

  /** The unique ID of an event in the stream. */
    //private long mCurId = 0;

  /** The current state. */
  private CurrentStateSet mCurrentStateSet;

  //private boolean mOutput = false;
  private QueueItem       mCurItem;
  private boolean         mCurItemTestedNull;  
  private boolean         mNeedCheck;

  /** If the HPDT is used for aggregation */
  private boolean mIsAggregation = false;

  /** Get the result of the query. */
  public String getResult(){
    return mResult . toString () ;
  };

  /** If the query is aggregation, return true. */
  private boolean isAggregation ( XPathNode node ){
    
    Output output = new Output( );
    
    //System . err . println ( "The output is " + node . getOutput( ));
    output . parseOutputString ( node . getOutput( ) );

    return output . isAggregation ( );
  }

  public HPDT( Vector vXP, boolean getResult ){

    mVectorOutputQ = new Vector ( MAX_NUMBER_BPDT );
    
    mVectorStates = new Vector ( Consts.MAX_PDT_STATES );
    
    mVectorBpdt = new Vector( MAX_NUMBER_BPDT );

    mGlobalQueue = new HPDTOutputQ ( );

    mVectorXPathNodes = vXP;
    
    //Only when the query is aggregation do we initialize the stat buffer.
    XPathNode node = (XPathNode) mVectorXPathNodes.get( mVectorXPathNodes.size() - 1  );
    mIsAggregation = isAggregation ( node );
    if ( mIsAggregation ){
      System . err . println ( "The Query is aggregation!");
      mVectorStatBuffer = new Vector ( MAX_NUMBER_BPDT );
      mGlobalStatBuffer = new StatBuffer ( );
    }

    mGetResult = getResult;
    if ( mGetResult )
      mResult = new StringBuffer( MAX_RESULT_SIZE );

    makeHPDT( );
  
  }

  private void makeHPDT( ){

    //construct the root PDT
    XPathNode node = (XPathNode) mVectorXPathNodes.get( 0 );
    
    if ( mIsAggregation ){

      XPathNode lastNode = (XPathNode) mVectorXPathNodes.get( mVectorXPathNodes.size() - 1  );
      node . setOutput ( lastNode . getOutput ( ) );

    }

    //public BasicPDT( XPathNode node, PDTState start, int parentID, int parentResult )
    BasicPDT bpdt = new BasicPDT( node, null, -1, 0 );
    mVectorBpdt . add ( 0, bpdt );
    
    PDTOutputQ outputQ = new PDTOutputQ( 0, mGlobalQueue );
    mVectorOutputQ . add ( 0, outputQ );

    if ( mIsAggregation ){
      PDTStatBuffer statBuf = new PDTStatBuffer( 0, mGlobalStatBuffer );
      mVectorStatBuffer . add ( 0, statBuf );
    }
    
    //Set the initial current state
    mCurrentStateSet = new CurrentStateSet ( bpdt.getStartState(), 0 );
    
    //Build the BPDTs recursively
    makeHPDTHelper( 1, bpdt.getTrueState(), 0, Consts.TRUE );
    
    //Combine the BPDTs into one HPDT
    combineHPDT( );
    
    if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 ){
      printCombinedHPDT( );
    }
    
    drawHPDT();

  }
  
  private void makeHPDTHelper( int count, PDTState start, int parentID, int parentResult ){
  
    //System.err.print ( "count is :" + Integer.toString(count) );
    if ( count == mVectorXPathNodes.size() ) 
      return;

    XPathNode node = (XPathNode) mVectorXPathNodes.get( count );
    
    BasicPDT bpdt = new BasicPDT( node, start, parentID, parentResult );
   
    int      pdtid = bpdt.getPDTID();
    
    //Tools.out ( "\tPDT id is :" + Integer.toString(pdtid) + "\t The capacity of the vector is:" +  Integer.toString(mVectorBpdt.size()));

    //The position of the basic PDT in the vector is just the PDTID
    if ( pdtid >= mVectorBpdt . size () ) {
	if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	    System . err . println ( "The BPDT vector is resized." );
	mVectorBpdt . setSize ( 2 * ( mVectorBpdt . size() + 1 ) ) ;
	mVectorOutputQ . setSize ( 2 * ( mVectorBpdt . size() + 1 ) ) ;
	if ( mIsAggregation ){
	    mVectorStatBuffer . setSize ( 2 * ( mVectorBpdt . size() + 1 ) ) ;
	}
    }
    
    //Tools.out("[HPDT:makeHPDTHelper]Add the " + pdtid + "th Bpdt to the vector in the HPDT. The parent ID is " + parentID );
    mVectorBpdt . set ( pdtid, bpdt );
    
    PDTOutputQ outputQ = new PDTOutputQ( pdtid, mGlobalQueue );
    mVectorOutputQ . set ( pdtid, outputQ );

    if ( mIsAggregation ){
      PDTStatBuffer statBuf = new PDTStatBuffer( pdtid, mGlobalStatBuffer );
      mVectorStatBuffer . set ( pdtid, statBuf );
    }


    PDTState state;
  
    state = bpdt.getNaState( );
    if (  state != null ){
      makeHPDTHelper( count + 1, state, pdtid, Consts.NA ); 
    }
    
    state = bpdt.getTrueState( );
    if (  state != null ){
      makeHPDTHelper( count + 1, state, pdtid, Consts.TRUE ); 
    }
    
  }

  private void combineHPDT( ){

    BasicPDT bpdt;
    int size = mVectorBpdt . size ( ) ;
    for ( int i=0; i < size; i++ ){
      
      bpdt = (BasicPDT) mVectorBpdt.get(i) ;
      if ( bpdt != null ){
	if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	    Tools.outln("[HPDT:combineHPDT]Add the " + i + "th Bpdt to the HPDT.");
	mVectorStates . addAll ( bpdt . getStates () );
      }
    }
    
    return;
  }

  private PDTOutputQ getOutputQ ( int  bpdtId ){

    return (PDTOutputQ)mVectorOutputQ.get(bpdtId);
 
  }

  private PDTStatBuffer getStatBuffer ( int bpdtId ){
    
    return (PDTStatBuffer)mVectorStatBuffer.get(bpdtId);
    
  }
  
  /** Generate a graph in the format of .DOT file that can be used by the Graphviz program.*/
  public void drawHPDT( ){
    
    int size = mVectorStates . size ();
    PDTState state, nextState;
    String curName;
    String nextName;
    String label;
    PDTArc arc;
    
    BasicPDT bpdt;

    try {
      
      FileWriter out = new FileWriter(new File(Consts.HPDTFigureName));
      
      out . write ( "digraph HPDT { \n\trank_sep = 2; \n\t rankdir=LR; \n");
      
      int bpdtNum = mVectorBpdt . size ( ) ;
      int curLayer = 0;
      String rank = "\t{\n\trank = same;";
      boolean firstNode = true;
      String subgraph = "";
      String otherArcs = "";
      
      
      for ( int bpdtId=0; bpdtId < bpdtNum; bpdtId++ ){
	
	int layer = BasicPDT.getLayerOfBpdt ( bpdtId );
	
	if ( layer != curLayer ){
	  //new layer
	  rank = rank + "}\n\t{rank = same;";
	  firstNode = true;
	  curLayer++;
	};
	
	bpdt = (BasicPDT) mVectorBpdt.get( bpdtId ) ;
	if ( bpdt != null ){
	  
	  subgraph = subgraph + "subgraph cluster" + Integer.toString ( bpdtId ) + " {";
	  subgraph = subgraph + "\n\tlabel = \"BPDT " + BasicPDT.getNameOfBpdt( bpdtId ) + "\";\n";
	  
	  Vector states = bpdt . getStates ( );
	  int    numStates = states . size ();
	  for ( int i=0; i<numStates; i++ ){
	    
	    state = (PDTState)states . get( i );
	    curName = state . getLabel ();
	    
	    if ( firstNode ){
	      rank = rank + curName + ";";
	      firstNode = false;
	    }
	    
	    rank = rank + "\""+ curName + "\";";
	    int arcSize = state . getArcSize ( );
	    for ( int j=0; j < arcSize ; j++ ){
	      arc = (PDTArc)state . getArc ( j );
	      nextState = arc . getDestState();
	      nextName = nextState . getLabel ( );
	      label = arc . getLabel( );
	      if ( bpdt . isStateInBpdt ( nextState ) ){
		subgraph = subgraph + "\t" + curName + " -> " + nextName + "[ label=\"" + label + "\"];\n";
	      }else{
		subgraph = subgraph + "\t" + curName + ";\n";
		otherArcs = otherArcs + "\t" + curName + " -> " + nextName + "[ label=\"" + label + "\"];\n";
	      }
	    }
	  }
	  subgraph = subgraph + "}\n";
	}
      }
      rank = rank + "}";
      //out . write ( rank );
      //System . err . println ( rank );
      out . write ( subgraph );
      out . write ( otherArcs );
      out . write ( "}");
      
      out . close();
    }catch( Exception e ){
      System . err . println ( e );
    }
  }


  /** Print the BPDTs to the stdout. The states are organized into group according to the BPDTs.*/
  public void printHPDT( ){

    BasicPDT bpdt;
    int size = mVectorBpdt . size ( ) ;
    System.out.println ( "There are " + Integer.toString( size ) + " basic PDTs in the system." );
    
    for ( int i=0; i<size; i++ ){
      
      bpdt = (BasicPDT) mVectorBpdt.get(i) ;
      
      if ( bpdt != null ){
	System.out.println ( "\tThe " + Integer.toString( i ) + "th basic PDTs in the system is as following:" );
	bpdt.printBpdt( );
      }else{
	System.out.println ( "\tThe " + Integer.toString( i ) + "th basic PDTs in the system is EMPTY." );
      }
    }
  }
  
  public String  toString( ){
    
    String s = "";
    int sizeState,sizeArc;
    PDTState state;

    sizeState = mVectorStates.size();
    
    s = s +  "The states are:\n" ;
    for ( int i=0; i<sizeState; i++ ){
      state = (PDTState)mVectorStates.get(i);
      s = s + state . toString( ) + "\n";
      s= s + "\n";
	
      sizeArc = state.getArcSize();
      s = s + ( "\tThe arcs are:\n" );
      for ( int j=0; j<sizeArc; j++ ){
	s = s + "#" + Integer.toString ( j ) + "\n";
	s = s + ( (PDTArc) state.getArc(j)) . toString( 0 ) + "\n";
      }
    }
    s = s + "******************************************\n";
    
    return s;
  }

  /** Print the HPDT as a single PDT. The states are shown in sequence according to the ID.*/
  public void printCombinedHPDT( ){
    
    int sizeState,sizeArc;
    sizeState = mVectorStates.size();
    
    System.out.println( "The states are:" );
    for ( int i=0; i<sizeState; i++ ){
      PDTState state = (PDTState)mVectorStates.get(i);
      state . printState( );
	
      sizeArc = state.getArcSize();
      System.out.println( "\tThe arcs are:" );
	
      for ( int j=0; j<sizeArc; j++ ){
	System.out.println( "#" + j );
	( (PDTArc) state.getArc(j)) . printArc( 1 );
      }
      System.out.println( "\n**********************************************" );
    }
    
  }
  
  private PDTArc getTransitionArc(   PDTState  srcState,
				     String    tag,
				     int       event,
				     int       eval ){
    
    int size = srcState . getArcSize ();
    
    for (int i=0; i<size; i++ ){
      if ( ( (PDTArc) srcState.getArc(i) ) . matchArc( tag, event, eval ) )
	return ( (PDTArc) srcState.getArc(i) );
    }
    /*
      int size = mArcs.size();
      
      for (int i=0; i<size; i++ ){
      if ( ( (PDTArc) mArcs.get(i) ) . matchArc( srcState, tag, event, eval ) )
      return ( (PDTArc) mArcs.get(i) );
      }
    */
    /*
      for ( int i=0; i<size; i++ ){
      if ( ( (PDTArc) mArcs.get(i) ) . matchArc( srcState, "//", event, eval ) )
      return  ( (PDTArc) mArcs.get(i) );
      }
    */
    return null;
  };
  
  private boolean addStateWithCheck ( PDTState state, long flag ){
    
    boolean keep = false;

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools . outln ( "\t\t\t[addStateWithCheck]The state is " + state + ":The flag is:" + ItemFlag.toString ( flag ) );
    if ( !mCurrentStateSet . addState ( state, flag ) ){
      //The state is already in the set
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools . outln ( "\t\t\t[addStateWithCheck]The state is in the current state set." );
      keep = true;
    }else{
      //The state is added into the set
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools . outln ( "\t\t\t[addStateWithCheck]The state is NOT in the current state set." );
    }
    
    return keep;
  }
  
  /** This function is called by the events in the SAX parser. */
  public void processEvent( Event event ){
    
    //For each state in the current state set, 
    //  For every transition arc that matches the incoming evet
    //      if the transition is a self-closure transition "//"
    //        Add the current state to the current set;
    //      else
    //        if the transition is a closure transition ( which means it can accpet tag from any depth )
    //              Append the depth of the event to the flag of the state, add the result to the current state.
    //        else
    //              /*check the depth of the incoming event,*/
    //              if the event is begin event, 
    //                  The depth of the event should be the the depth of the state plus one.
    //                  Add the depth of the event to the flag of the state, add the result to the current set.
    //              if the event is end event, the depth of the event should be equal to the depth of the state.
    //                  Remove the depht of the event from the flag of the state, add the result to the current set.
    //              if the event is text event, the depth of the event shoudl be equal to the depth of the state.
    //                  Add the current state to the the current state.
    //PDTStatBuffer statBuffer;
    //BasicPDT      curBpdt;
    PDTState      curState, newState;
    long          curFlag, newFlag;
    int           size = mCurrentStateSet.size( );
    PDTArc        nextArc;
    
    
    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools . outln ( "\n[ProcessEvents]The size of the current state set is " + size + ".\t" + event );
    
    //This will mark all the states to be removed
    mCurrentStateSet . removeAll ( );
    
    //Global Unique ItemID
    //mCurId ++;
    //mOutput = false;
    
    //mCurItem = new QueueItem ( mCurId );
    mCurItem = null;
    mCurItemTestedNull = false;
    mNeedCheck = false;

    //mGlobalQueue . add ( mCurItem );
    
    for ( int i=0; i< size; i++ ){
	
	//If the item is process by OUTPUT or FLUSH, no need to proceed.
	//if ( mOutput ){
	//    break;
	//}
	
	//Get a current state and its depth vector 
	curState = mCurrentStateSet . getState ( i );
	curFlag  = mCurrentStateSet . getFlag  ( i );
	
	if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	  Tools . outln ( "[ProcessEvents]The #" + i + " state is " + curState . getLabel ( ) + ". The flag is " + ItemFlag.toString( curFlag )  );
	
	int num = curState . getArcSize();
	//Tools . outln ( "\t[ProcessEvents]The size of the arc set of the current state is " + num  );
	boolean keep = false;
	boolean processed = false;
	
	for (int j=0; j<num; j++ ){
	  
	  nextArc = (PDTArc) curState . getArc( j );
	  if ( nextArc . getTagString( ) . compareTo ( "//" ) == 0 ){
	    // if the transition is a self-closure transition "//" and the event is a begin event.
	    // Add the current state to the current set;
	    if ( event.mEvent == Consts.BEGIN ){
	      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		Tools . outln ( "\t[ProcessEvents]This is a self-closure transition." );
	      keep = true;
	    }
	  }else{
	    if ( nextArc . matchArc( event.mTag, event.mEvent, Consts.TRUE ) ){
	      processed = true;
	      if ( nextArc . isClosure ( ) ){
		// if the transition is a closure transition ( which means it can accpet tag from any depth )
		// Append the depth of the event to the flag of the state, add the result to the current state.
		if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		  Tools . outln ( "\t[ProcessEvents]This is a closure transition." );
		newState = executeArc ( nextArc, event, curFlag );
		newFlag  = ItemFlag . setDepthBitAt ( curFlag, event.mDepth );
		
		keep =  keep | addStateWithCheck ( newState, newFlag );
	      }else if ( nextArc . isCatchall ( ) ){
		if ( event.mEvent == Consts.BEGIN ){
		  if ( event . mDepth > ItemFlag . getLowestDepth ( curFlag ) ) {
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is a begin transition in a catchall transition." );
		    executeArc ( nextArc, event, curFlag );
		    keep = true;
		  }
		}else if ( event.mEvent == Consts.END ){
		  if ( event . mDepth > ItemFlag . getLowestDepth ( curFlag ) ) {
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is an end transition in a catchall transition." );
		    executeArc ( nextArc, event, curFlag );
		    keep = true;
		  }
		}else if ( event.mEvent == Consts.TEXT ){
		  if ( event . mDepth >= ItemFlag . getLowestDepth ( curFlag ) ) {
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is an text transition in a catchall transition." );
		    executeArc ( nextArc, event, curFlag );
		    keep = true;
		  }
		}else{
		  System . err . println ( "[HPDT:processEvent] Unknown type of event.");
		}
	      }else{
		if ( event.mEvent == Consts.BEGIN ){
		  // if the event is begin event, 
		  // The depth of the event should be the the depth of the state plus one.
		  // Add the depth of the event to the flag of the state, add the result to the current set.
		  if ( event . mDepth == ( ItemFlag . getLowestDepth ( curFlag ) ) + 1 ){
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is a begin crosslayer transition." );
		    newState = executeArc ( nextArc, event, curFlag );
		    newFlag  = ItemFlag . setDepthBitAt ( curFlag, event.mDepth );
		    addStateWithCheck ( newState, newFlag );
		  }else{
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is a begin crosslayer transition. But the depths can't match."+ event . mDepth + ":" + ItemFlag . getLowestDepth ( curFlag ));
		    keep = true;
		    }
		}else if ( event.mEvent == Consts.END ){
		  // if the event is end event, the depth of the event should be equal to the depth of the state.
		  // Remove the depht of the event from the flag of the state, add the result to the current set.
		  if ( event . mDepth == ItemFlag . getLowestDepth ( curFlag ) ){
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is an end crosslayer transition." );
		    newState = executeArc ( nextArc, event, curFlag );
		    newFlag  = ItemFlag . removeLowestDepth ( curFlag );
		    addStateWithCheck ( newState, newFlag );
		  }else{
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is an end crosslayer transition. But the depths can't match." + event . mDepth + ":" + ItemFlag . getLowestDepth ( curFlag ));
		    keep = true;
		  }
		}else if ( event.mEvent == Consts.TEXT ){
		  // if the event is text event, the depth of the event shoudl be equal to the depth of the state.
		  // Add the current state to the the current state.
		  int curPDTID = nextArc . getPDTID ( );
		  if ( event . mDepth == ItemFlag . getLowestDepth ( curFlag ) ){
		    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		      Tools . outln ( "\t[ProcessEvents]This is a text transition." );
		    newState = executeArc ( nextArc, event, curFlag );
		    newFlag  = curFlag;
		    keep = keep | addStateWithCheck ( newState, newFlag );
		  }else{
		    Tools . outln ( "\t[ProcessEvents]This is a text transition. But the depths can't match." + event . mDepth + ":" + ItemFlag . getLowestDepth ( curFlag ) );
		    keep = true;
		  }
		}else if ( event.mEvent == Consts.CATCHALL ){
		  if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) {
		    // If the event is a CATCHALL event, the depth of the event should be equal 
		    //Tools . outln( "\t[ProcessEvents]This is a catchall event." + event.mEvent );
		  }
		} else{
		  if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) {
		    //Tools . outln( "\t[ProcessEvents]This is an unknown event." + event.mEvent );
		  }
		}
	      }
	    }else{
	      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) {
		//Tools . outln( "\t[ProcessEvents]This is an arc that doesn't need process." );
	      }
		
	    }
	  }
	}
	  
	  
	if ( !processed )
	  keep = true;
	//If remove is set, one of the arcs from the current state is executed. The current state will be removed from the set.
	//Or else, none of the arcs from the current state is matched, it will stay in the same state.
	if ( !keep ){
	  if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	    Tools . outln ( "\t[ProcessEvents]Remove current state." );
	  mCurrentStateSet . remove ( i );
	}else{
	  mCurrentStateSet . keep ( i );
	}
	  
      }

      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools . outln ( "\t[ProcessEvents]After process all events, the size of the global queue is " +  mGlobalQueue . size ( ) );
      
      if ( ( mGlobalQueue . size ( ) > 0 ) &&  ( mNeedCheck ) ){
	if ( mGetResult )
	  mResult . append ( mGlobalQueue . checkAll ( true ) );
	else
	  mGlobalQueue . checkAll ( false );
      }

      mCurrentStateSet . checkAll ( );
  
      return;
  }
  
  
  private void enqueue( int curPDTID, Event event, long flag,  Output output, boolean crossLayer ){
    
    String indent = "\t\t";
    String value = ""; 
    long   newFlag;

    if ( mCurItemTestedNull )
	return;
    
    //When we enter down into a new layer, the flags for the items should be the flag for the dest state.
    if ( ( event.mEvent == Consts.BEGIN ) && ( crossLayer ) ){
	newFlag  = ItemFlag . setDepthBitAt ( flag, event.mDepth );
    }else{
	newFlag = flag;
    }
    
    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools.outln ( indent + "[ENQUEUE]The current BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + ". The flag is " + ItemFlag . toString ( flag ) );
    
    if ( mCurItem != null ){
	getOutputQ(curPDTID) . enqueue( mCurItem, newFlag );
    }else{
	value = event . findAttrValue ( output . getAttrName() );
	if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	    Tools.outln ( indent + "[ENQUEUE]The current value " + value );
	if ( value != null ) {
	    mCurItem = new QueueItem( );
	    mCurItem . setContent ( value );
	    mGlobalQueue . add ( mCurItem ); 
	    getOutputQ(curPDTID) . enqueue( mCurItem, newFlag );
	}else{
	    mCurItemTestedNull = true;
	}
    }
    return;
  }

  private void enqueue_up( int curPDTID, Event event, long flag,  Output output, boolean crossLayer ){
    
    String    indent = "\t\t";
    String    value = ""; 
        
    int       anceId = BasicPDT.findUnknownAncester( curPDTID );
    int       dif = BasicPDT . findLayerDifference ( curPDTID, anceId );
    long      newFlag = ItemFlag . removeLowestNBits ( flag, dif );

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ){
      Tools.outln ( indent + "[ENQUEUE_UP]The current BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ) );
      Tools.outln ( indent + "[ENQUEUE_UP]The items are uploaded from " + curPDTID + "  to " + anceId + " with flag " + newFlag );
    }

    if ( mCurItemTestedNull )
	return;
    
    if ( mCurItem != null ){
	getOutputQ(anceId) . enqueue( mCurItem, newFlag );
    }else{
	value = event . findAttrValue ( output . getAttrName() );
	if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	    Tools.outln ( indent + "[ENQUEUE]The current value " + value );
	if ( value != null ) {
	    mCurItem = new QueueItem ( );
	    mCurItem . setContent ( value );
	    mGlobalQueue . add ( mCurItem ); 
	    getOutputQ(anceId) . enqueue( mCurItem, newFlag );
	}else{
	    mCurItemTestedNull =  true;
	}
    }
    return;
  }

  private void clear( int curPDTID, long flag ){
 
    String indent = "\t\t";

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools.outln ( indent + "[CLEAR]The current BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ));
    
    if ( mIsAggregation ){
	StatBuffer sb = getStatBuffer( curPDTID ) . getBufferWithFlag ( flag ); ;
	if ( sb != null )
	    sb . clear ( );
    }else{
	getOutputQ(curPDTID) . clear( flag );
    }

    mNeedCheck = true;
    
  }
    
    
    
  private void upload( int curPDTID, long flag, Event event, boolean crossLayer ){
      
    String indent = "\t\t";
    
    long   newFlag;
    
    if (  !crossLayer ){
      newFlag  = ItemFlag .  removeLowestDepth ( flag, event.mDepth );
    }else{
      newFlag = flag;
    }
    
    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools.outln ( indent + "[UPLOAD]The current BPDT is " +  BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ));
    if ( mIsAggregation ){
      StatBuffer sb = getStatBuffer( curPDTID ) . getBufferWithFlag ( newFlag ); 
      if ( ( sb == null ) || ( sb . isInitStatus ( ) == true ) )
	return;
      
      int       anceId = BasicPDT.findUnknownAncester( curPDTID );
      int       dif = BasicPDT . findLayerDifference ( curPDTID, anceId );
      long      anceFlag = ItemFlag . removeLowestNBits ( newFlag, dif );
      
      StatBuffer destSb = getStatBuffer( anceId ) . addBufferWithFlag ( anceFlag ); ;
      
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) {
	Tools.outln ( indent + "[UPLOAD]Upload the items in" + BasicPDT.getNameOfBpdt( curPDTID ) + " to new BPDT " + BasicPDT.getNameOfBpdt( anceId ) );
	Tools.outln ( indent + "[UPLOAD]Upload the items with flag " + ItemFlag . toString ( newFlag ) + " to new flag " + ItemFlag . toString ( anceFlag ));
      }
      
      destSb . merge ( sb );
      sb . clear ( );
    }else{
      getOutputQ(curPDTID) . upload ( newFlag, getOutputQ( BasicPDT.findUnknownAncester( curPDTID ) ) );
    }
  
  }

  private void flush ( int curPDTID, long flag, Event event, Output output, boolean crossLayer ){
    
    String indent = "\t\t";
    long   newFlag;

    if ( !crossLayer ){
      newFlag  = ItemFlag .  removeLowestDepth ( flag, event.mDepth );
    }else{
      newFlag = flag;
    }

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools.outln ( indent + "[FLUSH]The current Cur BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ) );
    if ( mIsAggregation ){
      if ( curPDTID == 0 ){
	//If this is the root BPDT, we should output the aggregation result.
	if ( mGetResult ){
	  mResult = mResult . append ( mGlobalStatBuffer . flush( output . getOutputFunction (), true ) );
	}else{
	  mGlobalStatBuffer . flush( output . getOutputFunction (), false );
	}
      }else{
	StatBuffer sb = getStatBuffer ( curPDTID ) . getBufferWithFlag ( newFlag );
	
	if ( ( sb == null ) || ( sb.isInitStatus() == true ) )
	  return;
	mGlobalStatBuffer . merge ( sb);
	sb . clear ();
      }
    }else{
      getOutputQ(curPDTID) . flush( newFlag );
    }

    mNeedCheck = true;
  }
  
  private void output ( int curPDTID, Event event, Output output ){

    String indent = "\t\t";
    String value = "";

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools.outln ( indent + "[Output]The current Cur BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) );
    
    if ( mCurItemTestedNull )
	return;
    
    if ( mIsAggregation ){
      
    }else{
	if ( mCurItem != null ){
	    mCurItem . setOutput ( true );
	}else{
	    value = event . findAttrValue ( output . getAttrName() );
	    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
		Tools.outln ( indent + "[Output]The current value " + value );
	    if ( value != null ) {
		mCurItem = new QueueItem( );
		mCurItem . setContent ( value );
		mGlobalQueue . add ( mCurItem );
		mCurItem . setOutput ( true );
	    }else{
		mCurItemTestedNull = true;
	    }
	}
    }

    mNeedCheck = true;
    
  }

  private void update( int curPDTID, long flag, Event event, Output output, boolean crossLayer ){

    String indent = "\t\t";
    String value = "";

    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	  Tools.outln ( indent + "[UPDTAE]The current Cur BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ) );
    
    if (  output . getAttrName() . compareTo ( Consts.ANY_ATTR_NAME ) == 0 ) 
      value = "1";
    else
      value = event . findAttrValue( output . getAttrName() );
    
    if ( value != null  ){
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools.outln ( indent + "[UPDATE]The value of function name '" + output . getFuncName() + "' is " + value );
      mGlobalStatBuffer . updateItem( output . getOutputFunction( output . getFuncName() ), value );
    }
  }

  private void aggregate( int curPDTID, Event event,  long flag, Output output, boolean crossLayer ){

    String indent = "\t\t";
    String  value = "";
    long   newFlag; 
    
    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools.outln ( indent + "[AGGREGATE]The current Cur BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ) );
    
    if ( ( event.mEvent == Consts.BEGIN ) && ( crossLayer ) ){
      newFlag  = ItemFlag . setDepthBitAt ( flag, event.mDepth );
    }else{
      newFlag = flag;
    }
    
    PDTStatBuffer statBuf = getStatBuffer( curPDTID );
    
    if (  output . getAttrName() . compareTo ( Consts.ANY_ATTR_NAME ) == 0 ) 
      value = "1";
    else
      value = event . findAttrValue( output . getAttrName() );
    
    if ( value != null  ) {
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools.outln ( indent + "[AGGREGATE]The value is " + value );
      statBuf. updateItem( output . getOutputFunction( output . getFuncName() ), value, newFlag  );
    }
    
    return;
  }

  private void aggregate_up( int curPDTID,  Event event, long flag, Output output, boolean crossLayer ){
    
    String indent = "\t\t";
    String    value = "";
    long   newFlag; 
     
    if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
      Tools.outln ( indent + "[AGGREGATE_UP]The current Cur BPDT is " + BasicPDT.getNameOfBpdt( curPDTID ) + " with flag " + ItemFlag . toString ( flag ) );
    
    if ( ( event.mEvent == Consts.BEGIN ) && ( crossLayer ) ){
      newFlag  = ItemFlag . setDepthBitAt ( flag, event.mDepth );
    }else{
      newFlag = flag;
    }
    
    int       anceId = BasicPDT.findUnknownAncester( curPDTID );
    int       dif = BasicPDT . findLayerDifference ( curPDTID, anceId );
        
    newFlag = ItemFlag . removeLowestNBits ( newFlag, dif );
    
    PDTStatBuffer statBuf = getStatBuffer( anceId );
    
    if (  output . getAttrName() . compareTo ( Consts.ANY_ATTR_NAME ) == 0 ) 
      value = "1";
    else
      value = event . findAttrValue( output . getAttrName() );
    
    if ( value != null  ) {
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	Tools.outln ( indent + "[AGGREGATE]The value is " + value );
      statBuf. updateItem( output . getOutputFunction( output . getFuncName() ), value, newFlag  );
    }
    
    return;
  }
  
  private PDTState executeArc ( PDTArc nextArc, Event event, long flag ){
    
    PDTStatBuffer statBuffer;
    BasicPDT      curBpdt;
    int           curPDTID; 
    String        indent = "\t\t";
    
    Filter filter = nextArc . getFilter( );
    PDTState curState = nextArc.getSrcState();
    
    if ( filter != null ) {
      int result;
      result = filter . evaluate( event );
      
      if ( result != Consts.TRUE ) 
	nextArc = getTransitionArc( curState, event.mTag, event.mEvent, Consts.FALSE );
      
      if ( nextArc == null ){
	Tools.outln( "Evaluation result has to be paired for true and false.");
	return null;
      }
    }
    
    curPDTID = nextArc . getPDTID ( );
    if ( curPDTID == -1 ){
      Tools.outln( "The PDTID for the arc is not correct." );
      return null;
    }
    
    Output output = nextArc . getOutput( );
    
    if ( output != null ) {
      
      String value = ""; 
      int operation = output.getOperation( );
      
      switch ( operation ) {
      case 0:
	break;
	
      case Output.ENQUEUE:{
	
	enqueue( curPDTID, event, flag, output, nextArc . isCrossLayer() );
	
      }
      break;
      
      case Output.CLEAR:{
	clear ( curPDTID, flag );
      }
      break;
      
      case Output.UPLOAD:{
	upload ( curPDTID, flag, event, nextArc . isCrossLayer());
      }
      break;
      
      case Output.ENQUEUE_UP:{
	enqueue_up( curPDTID, event, flag, output, nextArc . isCrossLayer() );
      }
      break;

      case Output.FLUSH:{
	flush ( curPDTID, flag, event, output, nextArc . isCrossLayer() );
      }
      break;
      
      case Output.OUTPUT:{
	
	output( curPDTID, event, output );
      }
      break;
      
      case Output.UPDATE:{
	
	update( curPDTID, flag, event, output,  nextArc . isCrossLayer() );

      }
      break;
      
      case Output.AGGREGATE:{
	
	aggregate( curPDTID, event, flag, output, nextArc . isCrossLayer() );
	
      }
      break;
      
      case Output.AGGREGATE_UP:{
	
	aggregate_up( curPDTID, event, flag, output, nextArc . isCrossLayer() );
	
      }
      break;

      case Output.ENQUEUE_UPLOAD:{
	enqueue( curPDTID, event, flag, output, nextArc . isCrossLayer() );
	upload ( curPDTID, flag, event, nextArc . isCrossLayer());
      }
      break;
      
      case  Output.FLUSH_OUTPUT:{
	flush ( curPDTID, flag, event, output, nextArc . isCrossLayer());
	output( curPDTID, event, output );
      }
      break;
      default:{
	if ( ( Tools . debugLevel & Tools . debugRunHPDT ) > 0  ) 
	  Tools.outln ( indent + "[executeArc]UNKNOWN Operation!" );
      }
      
      }
      
    }
    
    return nextArc . getDestState ( );
  }
    
  public void printContent( ){

    int size = mVectorOutputQ . size ( );
    PDTOutputQ q;
    QueueItem qi;

    for ( int i=0; i<size; i++ ){
      System . err . println ( "The queue of BPDT" +  BasicPDT.getNameOfBpdt( i ) + " is :" );
      q = (PDTOutputQ) mVectorOutputQ . get ( i );
      if ( q != null )
	q . printContent( );
    }
    
    size = mGlobalQueue . size ( );
    
    System . err . println ( "The global queue of HPDT has " + size + " items." );

    for ( int i=0; i<size; i++ ){
      
      qi = (QueueItem) mGlobalQueue . get ( i );
      if ( qi != null )
	System . err . println ( "The #" + i + " item is '" + qi.toString() + "' with count " + qi.getCount() + ":Output flag is " + qi.isOutput() ); 
    }
    
    return;
  }
  public void finish( ){
  
    //if ( mOutputBuffer.size() != 0 ){
      //mOutputBuffer.printContent();
    //}
      if ( ( Tools.debugLevel & Tools.debugRunHPDT ) > 0 ) 
	  printContent();
    //mOutputBuffer.close( );
    
  }

}


