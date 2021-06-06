/* $Id: BasicPDT.java,v 1.8 2002/10/23 23:03:38 pengfeng Exp $
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
 * The BPDT class implements the Basic PDT component.
 *
 * @version $Id: BasicPDT.java,v 1.8 2002/10/23 23:03:38 pengfeng Exp $
 * @author  Feng peng
 */
public class BasicPDT{

  private final static int MAX_STATES_IN_BPDT = 16;
  private final static int MAX_ARCS_IN_BPDT   = 32;

  private final static int CUR_ATTR = 1;
  private final static int CUR_TEXT = 2;
  private final static int CUR_CHILD = 4;
  private final static int CHILD_ATTR = 8;
  private final static int CHILD_TEXT = 16;

  private int  mFilterPos = 0;
  private int  mOutputPos = 0;

  /** The start state for this Basic PDT. It should be a state from the parent PDT */
  private PDTState mStartState;

  /** The number of the states in this PDT */
  private int mNumberStates ;

  /** The ID of the parent PDT. The ID actually encodes the evaluation result for the parent. */
  private int mParentPDTID ;

  /** The behavior of the current PDT will be based on the result of the parent PDT. */
  private int mParentResult;

  /** The ID for this basic PDT. It equals to the position of the basic PDT in the HPDT system.*/
  private int mPDTID;
  
  /** The location step in the XPath query that generates this basic PDT*/
  private XPathNode mLocationStep;

  /** The state that indicates current predicate is true. */
  private PDTState mTrueState;

  /** The state that indicates current predicate is not evaluated yest.*/ 
  private PDTState mNaState;

  /** The state that indicates the element has been started. we can receive text event from this state.*/
  private PDTState mTextState;
  
  /** The states in the basic PDT. */
  private Vector   mStates;

  /** Constructor. We have to specify all these information needed for the basic PDT. 
      @param XPathNode node: the context node that generates the basic PDT.
      @param PDTState start: the start state for this Basic PDT. It should be a state from the parent PDT.
      @param int parentID:   the ID of the parent PDT. The ID actually encodes the evaluation result for the parent.
      @param int parentResult: the behavior of the current PDT will be based on the result of the parent PDT.
  */
  public BasicPDT( XPathNode node, PDTState start, int parentID, int parentResult ){
   
    mStates = new Vector( MAX_STATES_IN_BPDT );

    if ( parentID == -1 ){
      setLocationStep  ( node );
      makeRootPDT();
    }else{
      setLocationStep  ( node );
      setStartState   ( start );
      setParentPDTID  ( parentID ); 
      setParentResult ( parentResult );
      setPDTID ( parentID, parentResult );
      
      mNumberStates = 1;
      makeBasicPDT( );
    }
  } 
  
  //Since all the three items has to be set at the constructor, others should not set them at other time.
  private void setStartState( PDTState start ) { mStartState = start; };
  
  /** Return the start state of the BPDT.*/
  public PDTState getStartState( ) { return mStartState; };
  
  private void setParentResult( int parentResult ) { mParentResult = parentResult; };

  /** Return the result of the parent BPDT, i.e., which state the current BPDT is connected to the parent.*/
  public int  getParentResult( ) { return mParentResult; };

  private void setParentPDTID( int parentID ) { mParentPDTID = parentID; };

  /** Get the unique ID of the parent BPDT.*/
  public int  getParentPDTID( ) { return mParentPDTID; };
  
  private void setPDTID( int parentID, int parentResult  ){
    
    if ( parentResult == Consts.TRUE ){
      mPDTID = parentID * 2 + 2;
    }else{
      mPDTID = parentID * 2 + 1;
    }
    return;
  } 

  private void setPDTID( int pdtid ) { mPDTID = pdtid; }
  
  /** Get the unique ID of the current BPDT.*/
  public int getPDTID( ) { return mPDTID; }

  private void setLocationStep( XPathNode node ) { mLocationStep = node; };

  /** Return the location step that the current BPDT is generated from.*/
  public XPathNode getLocationStep( ) { return mLocationStep; };

  private void setTrueState( PDTState state ) { mTrueState = state; }; 

  /** Return the TRUE state of the current BPDT.*/
  public PDTState getTrueState( ) { return mTrueState; };
  
  private void setNaState( PDTState state ) { mNaState = state; }; 

  /** Return the NA state of the current BPDT.*/
  public PDTState getNaState( ) { return mNaState; };
  
  private void setTextState( PDTState state ) { mTextState = state; }; 

  /** Return the state that process the text content.
      Note: Used for convenience of programming.*/
  //public PDTState getTextState( ) { return mTextState; };

  /** Get number of states in the current BPDT.*/
  public int getNumberOfStates( ) { return mNumberStates; };

  /** Get all the states in the current BPDT.*/
  public Vector getStates( ) { return mStates; };

  /** Determine if the state is in this BPDT.*/
  public boolean isStateInBpdt ( PDTState state ){

    int sizeState = mStates.size();

    for ( int i=0; i<sizeState; i++ ){
	PDTState s = (PDTState)mStates.get(i);
	if ( s == state ){
	  return true;
	}
    }

    return false;
  }

  /**Return the layer of the BPDT that has unique ID bpdtid.*/
  public static int getLayerOfBpdt( int bpdtId ){
    
    int layer = 0;
    int k = bpdtId >> 1;
    while ( k != 0 ){
      layer ++;
      k = k >> 1;
    }
    return layer;
  
  }
  
  /**Return the unique ID of the ancestor that has the BPDT in its right subtree.*/
  public static int findUnknownAncester( int bpdtid ){
    
    //The BPDT is numbered from 0.
    int i = bpdtid + 1;
    
    while ( ( i & 1 ) == 1 )
      i = i >> 1;
    
    return ( i >> 1 ) - 1;
    
  }

  /**This function works only when BPDT lower is in the right tree of BPDT higher.*/
  public static int findLayerDifference( int lower, int higher ){

    int i = 0;

    lower ++;
    higher ++;

    while ( lower != higher ){
      lower = lower >> 1;
      i ++;
    }
    
    return i;

  }

  /** Get the seqeunce number of the bpdtid in the same layer.*/
  public static int getNumberInLayerOfBpdt( int bpdtId ){

    int layer = getLayerOfBpdt ( bpdtId );
    return ( bpdtId + 1 ) % ( 1 << layer );

  }

  /** Return a name of the BPDT in the form of "layer.seqnum".*/
  public static String getNameOfBpdt ( int bpdtId ){
    
    return Integer.toString ( getLayerOfBpdt( bpdtId ) ) + "." + Integer.toString ( getNumberInLayerOfBpdt ( bpdtId ) );

  }


  /** Print all the states in the BPDT.*/
  public void printBpdt( ){
      
    int sizeState,sizeArc;
    sizeState = mStates.size();
    
    System.out.println( "The states are:" );
    for ( int i=0; i<sizeState; i++ ){
	PDTState state = (PDTState)mStates.get(i);
	state . printState( );
	
	sizeArc = state.getArcSize();
	System.out.println( "\tThe arcs are:" );
	
	for ( int j=0; j<sizeArc; j++ ){
	    System.out.println( "#" + j );
	    ( (PDTArc) state.getArc(j)) . printArc( 1 );
	}
	System.out.println( "\n**********************************************" );
    }
    
    System.out.println( "The true state is:" + mTrueState.getName() );
    if ( mNaState != null ) {
      System.out.println( "The na state is:" + mNaState.getName() );
    }
  }

  private boolean isParentTrue(){

    //Since the vector is zero-based, the right most BPDT will have a PDTID of 2^n-2.
    int i =  mPDTID + 2;

    while ( ( i % 2 ) == 0 ){
      i = i / 2;
    }
    
    if ( i == 1 )
      return true;
    
    return false;
  }

  /** This is the first basic PDT. */
  private void makeRootPDT( ){
    
    
    setParentPDTID  ( -1 ); 
    setParentResult ( Consts.TRUE );
    setPDTID ( 0 );
        
    PDTState newState1 = createNewState( );
    PDTState newState2 = createNewState( );

    setStartState( newState1 );
    setTrueState( newState2 );
    setNaState  ( null );
    setTextState ( newState2 );
    
    addTransitionArc ( newState1,
		       newState2,
		       Consts.ROOT_TAG,
		       Consts.BEGIN,
		       Consts.ALL,
		       null,
		       null,
		       null);

    PDTArc arc = addTransitionArc ( newState2,
				    newState1,
				    Consts.ROOT_TAG,
				    Consts.END,
				    Consts.ALL,
				    null,
				    null,
				    null);

    //If the output function is aggregation, we have to set a flush at the end of the PDT.
    Output output = new Output();
    String curOutput = mLocationStep . getOutput();
    output . parseOutputString( curOutput );
    if ( output . isAggregation() ){
      output . setOperation ( Output.FLUSH );
      arc . setOutput ( output );
    }else if ( !output.isEmpty() ){
      addOutput( output );
      arc . addOutputOp ( Output.FLUSH ); 
    }else{
      output . setOperation ( Output.FLUSH );
      arc . setOutput ( output );
    }
  }
  
  /** Generate the basic PDT based on the type of the context node.
      There are five possible situations for the selection:
      1. Selection on the attribute for the current node.
      2. Selection on the text for the current node.
      3. Selection on the child ( if a certain child exists ).
      4. Selection on the child's attribute.
      5. Selection on the child's text.
  */
  private void makeBasicPDT( ){
    
    String curFilter, curOutput, curTag;
   
    Filter filter = new Filter();
    Output output = new Output();
        
    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    curOutput = mLocationStep . getOutput();

    filter . parseFilterString( curFilter );
    output . parseOutputString( curOutput );

    if ( mLocationStep.isClosure() ){
      //This is a closure. We need only add a self-transition to the start node 
      //There is no need to add the arc for the text event and the end event
      //since the begin event won't change anything, the state and depth will just kept in the set.
      addTransitionArc ( mStartState,
			 mStartState,
			 "//",
			 Consts.BEGIN,
			 Consts.ALL,
			 null,
			 null,
			 null);
      
    }
      
    if ( !filter . isEmpty() ){
      if ( filter . mTagName . equals ( "" )  ){
	// The filter is working on current node
	if ( !filter . isText() ){
	  selOnCurAttr( );
	}else{
	  selOnCurText( );
	}
      }else{   
	// The filter is working on some child
	if ( !filter . isText() ){
	  if ( Tools.getOp ( filter . mOp ) == Consts.EXISTS ){
	    selOnChild( );
	  }else{
	      selOnChildAtrr( );
	  }
	}else{
	  selOnChildText( );
	}
      }
    }else{
      noSelection( );
    }
    
    if ( !output.isEmpty() )
      addOutput( output );
  }

  private PDTState createNewState ( ){

    String stateName = Integer.toString( mPDTID ) + "." + Integer.toString( mNumberStates );

    PDTState newState = new PDTState( stateName );
    
    mStates . add ( newState );
    
    mNumberStates ++;

    return newState;
  }
  

  private PDTArc addTransitionArc(	PDTState   srcState,
					PDTState   destState,
					String	   tag,
					int	   event,
					int	   eval,
					Filter     filter,
					Output     output,
					Output     buffer){
    PDTArc   newArc = new PDTArc();
    
    newArc . setDestState( destState );
    newArc . setSrcState ( srcState );
    newArc . setTagString( tag );
    newArc . setEvent	 ( event );
    newArc . setEval	 ( eval );
    newArc . setOutput   ( output );
    newArc . setBuffer   ( buffer );
    newArc . setFilter   ( filter );
    
    newArc . setPDTID ( mPDTID );
    newArc . setClosure ( false );

    if ( mStartState == null ){
      System . err . println ( "[BasicPDT:addTransitionArc]Warning: the start state is empty!");
    }else{

      if ( ( srcState == mStartState ) && ( destState != mStartState ) ){
	newArc . setCrossLayer ( true );
      }else if ( ( destState == mStartState ) && ( srcState != mStartState ) ){
	newArc . setCrossLayer ( true );
      }else{
	newArc . setCrossLayer ( false );
      }
    }

    
    srcState . addArc ( newArc );
    return newArc;

   
  };

  private PDTArc getTransitionArc(  PDTState  srcState,
				    String    tag,
				    int       event,
				    int       eval ){
      int size = srcState . getArcSize();

      for (int i=0; i<size; i++ ){
	  if ( ( (PDTArc) srcState.getArc(i) ) . matchArc( tag, event, eval ) )
	      return ( (PDTArc) srcState.getArc(i) );
      }
      /*
	    for ( int i=0; i<size; i++ ){
	    if ( ( (PDTArc) mArcs.get(i) ) . matchArc( srcState, "//", event, eval ) )
	    return  ( (PDTArc) mArcs.get(i) );
	    }
      */
      return null;
  };

  /** Build the basic PDT which the selection is on the attribute of current node.*/
  private void selOnCurAttr( ){
    
    String curFilter, curTag;
    
    Filter filter = new Filter();

    mFilterPos = CUR_ATTR;

    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    
    filter.parseFilterString( curFilter );
    
    PDTState newState1 = createNewState( );
    PDTState newState2 = createNewState( );
    Output output;

    setTrueState ( newState1 );
    setNaState ( null );
    setTextState ( newState1 );

    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation ( Output.FLUSH );
    }else{
      output = new Output();
      output . setOperation ( Output.UPLOAD );
    }

    //if the evaluation result is true
    PDTArc newArc = addTransitionArc ( mStartState,
				       newState1,
				       curTag,
				       Consts.BEGIN,
				       Consts.TRUE,
				       filter,
				       null,
				       null);

    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }
    
    addTransitionArc ( newState1,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    //If the evalution result is wrong
    newArc = addTransitionArc ( mStartState,
		       newState2,
		       curTag,
		       Consts.BEGIN,
		       Consts.FALSE,
		       filter,
		       null,
		       null);

    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }
    
    addTransitionArc ( newState2,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       null,
		       null);
    
  }
  
  /** Build the basic PDT which the selection is on the text of current node.*/
  private void selOnCurText( ){

    String curFilter, curTag;
    
    Filter filter = new Filter();
    
    mFilterPos = CUR_TEXT;
    
    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    
    filter.parseFilterString( curFilter );
    
    PDTState newStateNa    = createNewState( );
    PDTState newStateTrue  = createNewState( );
    PDTState newStateFalse = createNewState( );
    Output output;
    
    //The begin event
    PDTArc newArc = addTransitionArc ( mStartState,
				       newStateNa,
				       curTag,
				       Consts.BEGIN,
				       Consts.ALL,
				       null,
				       null,
				       null);
    
    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }

    //The end event is no text is met
    output = new Output();
    output . setOperation ( Output.CLEAR );
    addTransitionArc ( newStateNa,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    //The text event and the evalation is false
    output = new Output();
    output . setOperation ( Output.CLEAR );
    addTransitionArc ( newStateNa,
		       newStateFalse,
		       curTag,
		       Consts.TEXT,
		       Consts.FALSE,
		       filter,
		       output,
		       null);
    
    //The end event and the evaluation is false.
    addTransitionArc ( newStateFalse,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       null,
		       null);
        
    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    
    //The text event and the evaluation is true
    addTransitionArc ( newStateNa,
		       newStateTrue,
		       curTag,
		       Consts.TEXT,
		       Consts.TRUE,
		       filter,
		       output,
		       null); 
    
    //The end event and the evaluation is true
    addTransitionArc ( newStateTrue,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    setTrueState ( newStateTrue );
    setNaState   ( newStateNa );
    setTextState ( newStateNa );
    
  }
  
  /** Build the basic PDT which the selection is the test if a certain child exists.*/
  private void selOnChild( ){
    
    String curFilter, curTag;
    
    Filter filter = new Filter();
    
    mFilterPos = CUR_CHILD;

    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    
    filter.parseFilterString( curFilter );
    
    PDTState newStateNa   = createNewState( );
    PDTState newStateTrue = createNewState( );
    PDTState newStateStartChild = createNewState( );
    Output output;
    
    //The begin event
    PDTArc newArc = addTransitionArc ( mStartState,
				       newStateNa,
				       curTag,
				       Consts.BEGIN,
				       Consts.ALL,
				       null,
				       null,
				       null);

    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }

    //The end event without the child
    output = new Output();
    output . setOperation ( Output.CLEAR );
    
    addTransitionArc ( newStateNa,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);

    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    
    //The begin event of the child 
    addTransitionArc ( newStateNa,
		       newStateStartChild,
		       filter.getTagName(),
		       Consts.BEGIN,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    //The end event of the child
    if ( !mLocationStep . nextIsClosure() ){
      output = null; // only when the next location step contains closure do we add an extra upload here.
    }

    addTransitionArc ( newStateStartChild,
		       newStateTrue,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    

    //The end event of current node with the child
    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    addTransitionArc ( newStateTrue,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);

    setTrueState ( newStateTrue );
    setNaState   ( newStateNa );
    setTextState ( newStateNa );
  }

  /** Build the basic PDT which the selection is on the attribute of a child of current node.*/
  private void selOnChildAtrr( ){
    
    String curFilter, curTag;
    
    Filter filter = new Filter();
    
    mFilterPos = CHILD_ATTR;

    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    
    filter.parseFilterString( curFilter );
    
    PDTState newStateNa    = createNewState( );
    PDTState newStateTrue  = createNewState( );
    PDTState newStateFalse = createNewState( );
    PDTState newStateStartChild = createNewState( );
    Output output;
    
    //The begin event
    PDTArc newArc = addTransitionArc ( mStartState,
				       newStateNa,
				       curTag,
				       Consts.BEGIN,
				       Consts.ALL,
				       null,
				       null,
				       null);
    
    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }
    
    //The end event is no child satisfies the condition
    output = new Output();
    output . setOperation ( Output.CLEAR );
    addTransitionArc ( newStateNa,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    //The start event of the child and the evalation is false
    addTransitionArc ( newStateNa,
		       newStateFalse,
		       filter.getTagName(),
		       Consts.BEGIN,
		       Consts.FALSE,
		       filter,
		       null,
		       null);
    
    //The end event of the child and the evaluation is false.
    //output = new Output();
    //output . setOperation ( Output.CLEAR );
    /*This statement assumes that if one child is false, then the element fails the test
    addTransitionArc ( newState3,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    */
    //Now we assume if one child satisfy the condition, the element is selected.
    addTransitionArc ( newStateFalse,
		       newStateNa,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       null,
		       null);
    
    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    
    //The start event of the child and the evaluation is true
    addTransitionArc ( newStateNa,
		       newStateStartChild,
		       filter.getTagName(),
		       Consts.BEGIN,
		       Consts.TRUE,
		       filter,
		       output,
		       null); 
    
    //The end event of the child and the evaluation is true
    if ( !mLocationStep . nextIsClosure() ){
      output = null; // only when the next location step contains closure do we add an extra upload here.
    }

    addTransitionArc ( newStateStartChild,
		       newStateTrue,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null); 
    
    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    //The end event of the current element
    addTransitionArc ( newStateTrue,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    setTrueState ( newStateTrue );
    setNaState   ( newStateNa );
    setTextState ( newStateNa );
  }
  
  /** Build the basic PDT which the selection is on the text of a child of current node.*/
  private void selOnChildText( ){
    
    String curFilter, curTag;
    
    Filter filter = new Filter();
    
    mFilterPos = CHILD_TEXT;

    curTag    = mLocationStep . getTag();
    curFilter = mLocationStep . getFilter();
    
    filter.parseFilterString( curFilter );
    
    PDTState newStateNa    = createNewState( );
    PDTState newStateChild = createNewState( );
    PDTState newStateTrue  = createNewState( );
    PDTState newStateFalse = createNewState( );
    PDTState newStateChildText = createNewState( );

    Output output;
    
    //The begin event
    PDTArc newArc = addTransitionArc ( mStartState,
		       newStateNa,
		       curTag,
		       Consts.BEGIN,
		       Consts.ALL,
		       null,
		       null,
		       null);
    
    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    }

    //The end event is no child is met
    output = new Output();
    output . setOperation ( Output.CLEAR );
    addTransitionArc ( newStateNa,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);

    //The being event of the child
    addTransitionArc ( newStateNa,
		       newStateChild,
		       filter.getTagName(),
		       Consts.BEGIN,
		       Consts.ALL,
		       null,
		       null,
		       null);

    //The end event of the child with no text is met
    addTransitionArc ( newStateChild,
		       newStateNa,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       null,
		       null);

    //The text event of the child with evaluation is false
    addTransitionArc ( newStateChild,
		       newStateFalse,
		       filter.getTagName(),
		       Consts.TEXT,
		       Consts.FALSE,
		       filter,
		       null,
		       null);
    
    addTransitionArc ( newStateFalse,
		       newStateNa,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       null,
		       null);

    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    //The text event of the child with the evaluation is true
    addTransitionArc ( newStateChild,
		       newStateChildText,
		       filter.getTagName(),
		       Consts.TEXT,
		       Consts.TRUE,
		       filter,
		       output,
		       null);

    //The end event of the child
    if ( !mLocationStep . nextIsClosure() ){
      output = null; // only when the next location step contains closure do we add an extra upload here.
    }

    //The end event of the child with the evaluation is true
    addTransitionArc ( newStateChildText,
		       newStateTrue,
		       filter.getTagName(),
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);

    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }
    addTransitionArc ( newStateTrue,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);
    
    setTrueState ( newStateTrue );
    setNaState   ( newStateNa );
    setTextState ( newStateNa );
  }
  
  /** Build the basic PDT which has no selection */
  private void noSelection( ){
    
    String curTag    = mLocationStep . getTag();
        
    PDTState newStateTrue = createNewState( );
    
    //In the final version, this BPDT should not have upload() function since the contents should be uploaded to upper level directly.
    Output output;

    if ( isParentTrue() ) {
      output = new Output();
      output . setOperation( Output.FLUSH );
    }else{
      output =  new Output();
      output . setOperation( Output.UPLOAD );
    }


    PDTArc newArc = addTransitionArc ( mStartState,
				       newStateTrue,
				       curTag,
				       Consts.BEGIN,
				       Consts.ALL,
				       null,
				       null,
				       null);
    
    if ( mLocationStep.isClosure() ){
      newArc . setClosure ( true );
    } 
    
    addTransitionArc ( newStateTrue,
		       mStartState,
		       curTag,
		       Consts.END,
		       Consts.ALL,
		       null,
		       output,
		       null);

    setTrueState ( newStateTrue );
    setNaState   ( null );
    setTextState ( newStateTrue );
  }

  private void addAggregation( Output output ){

    Output newOutput;
    int    newOp;

    if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 ){
      System.err.println("The Tag name of the output is " + output.getTagName() +".");
    }
    
    if ( output.getTagName( ) == "" ){
      
      if ( output.isText( ) ){
	//aggegation of the text for current element  
	if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 )
	  System.err.println("Aggregation of current text.");
	PDTArc arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.TEXT, Consts.TRUE );
	if ( ( isParentTrue() ) && (  ( mFilterPos | CUR_TEXT ) == CUR_TEXT ) ){
	  newOp = Output.UPDATE;
	}else{
	  if ( mNaState != null )
	    newOp = Output . AGGREGATE_UP;
	  else
	    newOp = Output . AGGREGATE;
	}
	
	newOutput = (Output)output.clone();
	newOutput.setOperation( newOp  );

	if ( arc != null ) {
	  arc . setOutput ( newOutput );
	}else{
	  addTransitionArc ( mTrueState,
			     mTrueState,
			     mLocationStep . getTag(),
			     Consts.TEXT,
			     Consts.ALL,
			     null,
			     newOutput,
			     null);
	  
	  if ( mNaState != null ){
	    
	    arc = getTransitionArc( mNaState, mLocationStep . getTag(), Consts.TEXT, Consts.TRUE );
	    newOutput = (Output)output . clone(); 
	    newOutput . setOperation ( Output.AGGREGATE );
	    if ( arc != null ) {
	      arc . setOutput ( newOutput );
	    }else{
	      arc = addTransitionArc ( mNaState,
				       mNaState,
				       mLocationStep . getTag(),
				       Consts.TEXT,
				       Consts.ALL,
				       null,
				       newOutput,
				       null);
	      
	    }
	  }
	}
      }else{
	//aggregation the attribute for current element, for example sum( )
	if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 )
	  System.err.println("Aggregation of current attribute.");
	PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
	newOutput = (Output)output . clone(); 
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos | CUR_ATTR ) == CUR_ATTR ) ){
	  newOutput.setOperation( Output.UPDATE );
	}else{
	  newOutput.setOperation( Output.AGGREGATE );
	}
	
	arc . setOutput ( newOutput );
      }
    }else{
      if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 )
	System.err.println("Aggregation of text for some child.");
      if ( output.isText( ) ){
	//aggregation the text for some child 
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos |  ( CUR_CHILD + CHILD_ATTR + CHILD_TEXT ) )  == ( CUR_CHILD + CHILD_ATTR + CHILD_TEXT ) ) ){
	  output.setOperation( Output.UPDATE );
	}else{
	  output.setOperation( Output.AGGREGATE );
	}
	
	addOutputChildTextToState ( mTrueState, output );
	
	//If the text state is no the true state, we have to do all this again
	if ( mTextState != mTrueState ){
	  
	  output.setOperation( Output.AGGREGATE );
	  addOutputChildTextToState ( mTextState, output );
	}
	
      }else{
	if ( ( Tools.debugLevel & Tools.debugBuildHPDT ) > 0 )
	  System.err.println("Aggregation of text for some child.");
	
	//The aggregation is for the attribute for a child
	//System.out.println ( "The parent id is " + Integer.toString( mParentPDTID ) + "\tThe filter position is :" + Integer.toString( mFilterPos ) );
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos | ( CUR_CHILD + CHILD_ATTR ) ) == ( CUR_CHILD + CHILD_ATTR ) ) ){
	  output.setOperation( Output.UPDATE );
	}else{
	  output.setOperation( Output.AGGREGATE );
	}
	
	addOutputChildAttrToState( mTrueState, output );
	if ( mTrueState != mTextState ){
	  
	  output.setOperation( Output.AGGREGATE );
	  addOutputChildAttrToState( mTextState, output );
	}
      }
    }

  }

  private void addCatchallNoSel( Output output  ){

    int newOP, newBP; 
    Output newOutput;

    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);

    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallNoSel]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( newOP );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);
    arc = getTransitionArc( mTextState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallNoSel]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP ); //Since the end event has to upload the content
    }
    
    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);
    addTransitionArc ( mTextState,
		       mTextState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    return;
  
  }
  
  private void addCatchallCurAttr( Output output  ){

    int newOP, newBP; 
    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }
    
    Output newOutput;
    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);
    
    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurAttr]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( newOP );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);

    arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurAttr]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP );
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);
    addTransitionArc ( mTextState,
		       mTextState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    return;
  
  }
  
  private void addCatchallCurText( Output output  ){

    int newOP, newBP; 
    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }


    Output newOutput;
    newOutput = (Output)output . clone();
    newOutput.setOperation( Output . ENQUEUE );

    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurText]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( Output . ENQUEUE );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation( newOP);

    arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurText]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP );
    }
    
    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output.ENQUEUE ); 
    
    addTransitionArc ( mNaState,
		       mNaState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );

    
    newOutput = (Output)output . clone();
    if ( newOP == Output . ENQUEUE )
      newOP = Output . ENQUEUE_UP;
    newOutput.setOperation ( newOP ); 
    
    addTransitionArc ( mTrueState,
		       mTrueState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    return;
    
  }

  
  private void addCatchallCurChild( Output output  ){
    
    int newOP, newBP; 
    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }
    
    Output newOutput;
    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output . ENQUEUE ); 
    
    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurChild]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( Output . ENQUEUE );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation ( newOP ); 
    arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallCurChild]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP );
    }

    
    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output.ENQUEUE );
    addTransitionArc ( mNaState,
		       mNaState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );

    
    newOutput = (Output)output . clone();
    if ( newOP == Output . ENQUEUE )
      newOP = Output . ENQUEUE_UP;
    newOutput.setOperation ( newOP );
    addTransitionArc ( mTrueState,
		       mTrueState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    
    return;
    
  }
  
  private void addCatchallChildText( Output output  ){
    
    int newOP, newBP; 
    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }

    Output newOutput;

    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output . ENQUEUE ); // The start state will link to the NA state, thus it should be enqueue.

    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallChildText]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( Output . ENQUEUE );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation ( newOP );

    arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallChildText]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP );
    }
    
    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output.ENQUEUE );

    addTransitionArc ( mNaState,
		       mNaState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );

    newOutput = (Output)output . clone();
    if ( newOP == Output . ENQUEUE )
      newOP = Output . ENQUEUE_UP;
    newOutput.setOperation ( newOP );
    
    addTransitionArc ( mTrueState,
		       mTrueState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    return;
    
  }
  
  private void addCatchallChildAttr( Output output  ){
    
    int newOP, newBP; 
    if ( isParentTrue() ){
      newOP = Output . OUTPUT ;
      newBP = Output . FLUSH ; 
    }else{
      newOP = Output . ENQUEUE;
      newBP = Output . UPLOAD;
    }

    Output newOutput;
    newOutput = (Output)output . clone();
    newOutput.setOperation ( Output . ENQUEUE );

    PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallChildAttr]This should not happen!");
      return;
    }else{
      if ( arc . getOutput() == null ){
	arc . setOutput ( newOutput );
      }else{
	arc . addOutputOp ( Output . ENQUEUE );
      }
    }

    newOutput = (Output)output . clone();
    newOutput.setOperation ( newOP );
    
    arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.END, Consts.TRUE );
    if ( arc == null ){
      System.err.println( "[addCatchallChildAttr]This should not happen!");
      return;
    }else{
      arc . setOutput ( newOutput );
      arc . addOutputOp ( newBP );
    }
    
    newOutput = (Output)output . clone();
    newOutput.setOperation (  Output.ENQUEUE ); 
    
    addTransitionArc ( mNaState,
		       mNaState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );

    newOutput = (Output)output . clone();
    if ( newOP == Output . ENQUEUE )
      newOP = Output . ENQUEUE_UP;
    newOutput.setOperation ( newOP ); 

    addTransitionArc ( mTrueState,
		       mTrueState,
		       "**",
		       Consts.CATCHALL,
		       Consts.ALL,
		       null,
		       newOutput,
		       null );
    return;
  
  }

  private void addCatchall( Output output  ){

    switch ( mFilterPos ) {
    case 0:
      addCatchallNoSel( output );
      break;
    case CUR_ATTR:
      addCatchallCurAttr( output );
      break;
    case CUR_TEXT:
      addCatchallCurText( output );
      break;
    case CUR_CHILD:
      addCatchallCurChild( output );
      break;
    case CHILD_ATTR:
      addCatchallChildAttr( output );
      break;
    case CHILD_TEXT:
      addCatchallChildText( output );
      break;
    default:
      return;
    }
    return;
  }

  private void addOutput( Output output ){
    
    Output newOutput; 

    if ( output.isAggregation( ) ){
      addAggregation( output );
      return;
    }

    if ( output.getTagName( ) == "**" ){
      addCatchall( output );
      return;
    }

    if ( output.getTagName( ) == "" ){
      
      if ( output.isText( ) ){

	//output the text for current element 
	PDTArc arc = getTransitionArc( mTrueState, mLocationStep . getTag(), Consts.TEXT, Consts.TRUE );
	int    newOp;
	if ( ( isParentTrue() ) && (  ( mFilterPos | CUR_TEXT ) == CUR_TEXT ) ){
	  newOp = Output.OUTPUT ;
	}else{
	  if ( mNaState != null )
	    newOp = Output.ENQUEUE_UP ;
	  else
	    newOp = Output.ENQUEUE;
	}
	
	newOutput = (Output)output . clone(); 
	newOutput . setOperation ( newOp );
	if ( arc != null ) {
	  arc . setOutput ( newOutput );
	}else{
	  
	  arc = addTransitionArc ( mTrueState,
				   mTrueState,
				   mLocationStep . getTag(),
				   Consts.TEXT,
				   Consts.ALL,
				   null,
				   newOutput,
				   null);
	}
	
	if ( mNaState != null ){
	  
	  arc = getTransitionArc( mNaState, mLocationStep . getTag(), Consts.TEXT, Consts.TRUE );
	  newOutput = (Output)output . clone(); 
	  newOutput . setOperation ( Output.ENQUEUE );
	  if ( arc != null ) {
	    arc . setOutput ( newOutput );
	  }else{
	    arc = addTransitionArc ( mNaState,
				     mNaState,
				     mLocationStep . getTag(),
				     Consts.TEXT,
				     Consts.ALL,
				     null,
				     newOutput,
				     null);
	    
	  }
	}
      }else{
	//output the attribute for current element
	PDTArc arc = getTransitionArc( mStartState, mLocationStep . getTag(), Consts.BEGIN, Consts.TRUE );
	newOutput = (Output)output . clone(); 
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos | CUR_ATTR ) == CUR_ATTR ) ){
	  newOutput.setOperation( Output.OUTPUT );
	}else{
	  if ( mNaState == null )
	    newOutput.setOperation( Output.ENQUEUE_UP );
	  else
	    newOutput.setOperation( Output.ENQUEUE );
	}
	
	arc . setOutput ( newOutput );
      }
    }else{
      if ( output.isText( ) ){
	//output the text for some child 
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos |  ( CUR_CHILD + CHILD_ATTR + CHILD_TEXT ) )  == ( CUR_CHILD + CHILD_ATTR + CHILD_TEXT ) ) ){
	  output.setOperation( Output.OUTPUT );
	}else{
	  output.setOperation( Output.ENQUEUE );
	}
	
	addOutputChildTextToState ( mTrueState, output );

	//If the text state is no the true state, we have to do all this again
	if ( mTextState != mTrueState ){
	  newOutput = (Output)output.clone();
	  newOutput.setOperation( Output.ENQUEUE );
	  addOutputChildTextToState ( mTextState, newOutput );
	}
       
      }else{
	//The output is for the attribute for a child
	  //System.out.println ( "The parent id is " + Integer.toString( mParentPDTID ) + "\tThe filter position is :" + Integer.toString( mFilterPos ) );
	if ( ( isParentTrue() ) && 
	     ( ( mFilterPos | ( CUR_CHILD + CHILD_ATTR ) ) == ( CUR_CHILD + CHILD_ATTR ) ) ){
	  output.setOperation( Output.OUTPUT );
	}else{
	  output.setOperation( Output.ENQUEUE );
	}
	
	addOutputChildAttrToState( mTrueState, output );
	if ( mTrueState != mTextState ){
	  newOutput = (Output)output.clone();
	  newOutput.setOperation( Output.ENQUEUE );
	  addOutputChildAttrToState( mTextState, newOutput );
	}
      }
    }
  }

  private void addOutputChildAttrToState( PDTState state, Output output ){
    
    PDTArc arc = getTransitionArc( state, output.getTagName(), Consts.BEGIN, Consts.TRUE );
    if ( arc != null ) {
      arc . setOutput( output );
    }else{
      
      PDTState newState = createNewState();
      addTransitionArc ( state,
			 newState,
			 output . getTagName(),
			 Consts.BEGIN,
			 Consts.ALL,
			 null,
			 output,
			 null);
      addTransitionArc ( newState,
			 state,
			 output . getTagName(),
			 Consts.END,
			 Consts.ALL,
			 null,
			 null,
			 null);
    }
    return;
  }


  private void addOutputChildTextToState( PDTState state, Output output ){

    PDTArc arc = getTransitionArc( state, output.getTagName(), Consts.BEGIN, Consts.TRUE );
    
    if ( arc != null ) {
      //The child is already in the PDT
      PDTState destState = arc . getDestState( );
      arc = getTransitionArc( destState, output.getTagName(), Consts.TEXT, Consts.TRUE );
      if ( arc != null ) {
	arc . setOutput ( output );
      }else{
	addTransitionArc ( destState,
			   destState,
			   output . getTagName(),
			   Consts.TEXT,
			   Consts.ALL,
			   null,
			   output,
			   null);
      }
    }else{
      //The child is not in the PDT yet
      PDTState newState = createNewState();
      addTransitionArc ( state,
			 newState,
			 output . getTagName(),
			 Consts.BEGIN,
			 Consts.ALL,
			 null,
			 null,
			 null);
      addTransitionArc ( newState,
			 state,
			 output . getTagName(),
			 Consts.END,
			 Consts.ALL,
			 null,
			 null,
			 null);
      addTransitionArc ( newState,
			 newState,
			 output . getTagName(),
			 Consts.TEXT,
			 Consts.ALL,
			 null,
			 output,
			 null);
      
    }
    return; 
  }

}







