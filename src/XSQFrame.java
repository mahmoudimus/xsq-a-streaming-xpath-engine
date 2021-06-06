/* $Id: XSQFrame.java,v 1.3 2002/10/12 02:31:40 pengfeng Exp $
 * @(#) XSQFrame.java     1.0   06/12/2002
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

import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/** 
 * Class description goes here
 *
 * @version $Id: XSQFrame.java,v 1.3 2002/10/12 02:31:40 pengfeng Exp $
 * @author  Feng peng
 */

public class XSQFrame extends JFrame{
  
  XSQPanel    mPanel;
  XSQMenuBar  mMenubar  ;
  
  public XSQFrame( ) throws IOException{
    
    super("XSQ version 1.0");
    
    getContentPane() . setLayout( new BorderLayout() );
        
    addWindowListener( new WindowAdapter() {
	public void windowClosing( WindowEvent e ) {
	  dispose();
	}
      });
    
    //Add the panel that contains the controls
    mPanel = new XSQPanel( this );
    getContentPane() . add( mPanel );

    //Add the menus 
    mMenubar  = new XSQMenuBar( this );
    setJMenuBar( mMenubar );
    
  }


   public static void main( String[] args ) throws IOException {
    
    XSQFrame frame;
    
    frame = new XSQFrame( );

    frame.setSize( 640,400 );
    frame.setVisible( true );
    frame.addWindowListener( new WindowAdapter() {
	public void windowClosed( WindowEvent e ) {
	  System . exit (0);
	}
      });
  }
}
