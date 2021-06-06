/* $Id: CurrentStateSet.java,v 1.3 2002/10/20 04:37:28 pengfeng Exp $
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
 * @version $Id: CurrentStateSet.java,v 1.3 2002/10/20 04:37:28 pengfeng Exp $
 * @author  Feng peng
 */

public class CurrentStateSet{

  class StateWithFlag{
    
    public PDTState state;
    public long     flag;
    public boolean  remove;

    public StateWithFlag ( PDTState s, long l ){
      state = s;
      flag  = l;
      remove = false;
    }

    public boolean equals ( StateWithFlag swf ){
      
      if ( ( state.getName() == swf . state .getName() ) && ( flag == swf . flag ) ){
	return true;
      }
      
      return false;
    }
  }

  private Vector mCurrentStates;

  private final static int MAX_NUM_CURRENT_STATES = 64;

  public CurrentStateSet( PDTState startState, long initFlag ){
    
    StateWithFlag swf = new StateWithFlag ( startState, initFlag );

    mCurrentStates = new Vector( MAX_NUM_CURRENT_STATES );

    mCurrentStates . add ( swf );

  }

  public boolean addState ( PDTState state, long flag ){

    int size = mCurrentStates.size( );

    StateWithFlag swf;
    StateWithFlag newSwf = new StateWithFlag ( state, flag );
    for ( int i=0; i<size; i++){
      swf = (StateWithFlag)mCurrentStates . get( i );
      if ( swf . equals ( newSwf ) ){
	swf . remove = false;
	return false;
      }
    }

    mCurrentStates . add ( newSwf );

    return true;

  }
  
  public boolean removeState ( PDTState state, long flag ){
    
    int size = mCurrentStates.size( );

    StateWithFlag swf;
    StateWithFlag newSwf = new StateWithFlag ( state, flag );

    for ( int i=0; i<size; i++){
      swf = (StateWithFlag)mCurrentStates . get( i );
      if ( swf . equals ( newSwf ) ){
      
	//mCurrentStates . removeElementAt ( i );
	swf . remove = true;
	return true;
	
      }
    }

    return false;

  }

  public PDTState getState ( int count ){
    
    StateWithFlag swf = (StateWithFlag)mCurrentStates . get( count );
    return swf . state;
    
  }

  public long  getFlag  ( int count ){

    StateWithFlag swf = (StateWithFlag)mCurrentStates . get( count );
    return swf . flag;
    
  }

  public void remove ( int count ){
    //mCurrentStates . remove ( count );
    StateWithFlag swf = (StateWithFlag)mCurrentStates . get( count );
    swf . remove = true;
  }

  public void keep ( int count ){
    StateWithFlag swf = (StateWithFlag)mCurrentStates . get( count );
    swf . remove = false;
  }

  public void removeAll ( ){
    
    int size = mCurrentStates.size( );

    StateWithFlag swf;

    for ( int i=0; i<size; i++){
      swf = (StateWithFlag)mCurrentStates . get( i );
      swf . remove = true;
    }
  }
  
  public void checkAll ( ){
    
    int size = mCurrentStates.size( );

    StateWithFlag swf;
    int count = 0;

    for ( int i=0; i<size; i++){
      swf = (StateWithFlag)mCurrentStates . get( count );
      if ( swf . remove ){
	mCurrentStates . removeElementAt ( count );
      }else{
	count ++;
      }
    }
  }
  
  
  public int size ( ){
    return mCurrentStates . size ( );
  }
  

}



