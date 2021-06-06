/* $Id: XSQMenuBar.java,v 1.2 2002/10/12 02:31:40 pengfeng Exp $
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

import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** 
 * Class description goes here
 *
 * @version $Id: XSQMenuBar.java,v 1.2 2002/10/12 02:31:40 pengfeng Exp $
 * @author  Feng peng
 */

public class XSQMenuBar extends JMenuBar implements ActionListener{
  
  private JMenu    mMenuFile;
  private JMenu    mMenuHelp;
  private JFrame   mFrameParent;

  public XSQMenuBar( JFrame parent ){
    
    JMenuItem menuItem;
    
    setParentFrame ( parent );

    mMenuFile = new JMenu( "File" );
    menuItem = new JMenuItem( "Exit" );
    menuItem . addActionListener( this );
    mMenuFile . add ( menuItem );
    
    add(mMenuFile);
    
    mMenuHelp = new JMenu( "Help" );
    menuItem = new JMenuItem( "About XSQ" );
    menuItem . addActionListener( this );
    mMenuHelp . add ( menuItem );

    add(mMenuHelp);
  }
  
  public void setParentFrame( JFrame parent ){
    mFrameParent = parent;
  }

  public JFrame getParentFrame( ){
    return mFrameParent;
  }

  public void actionPerformed(ActionEvent e) {

        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected.\n Event source: " + source.getText( );
	//MessageBox m = new MessageBox( mFrameParent, "ACTION", s );
	//System.out.println(s);

	if ( source.getText() == "Exit" ){
	  System . exit( 0 );
	}else if ( source.getText() == "About XSQ" ){
	  String about = "XSQ Version 1.0";
	  MessageBox m = new MessageBox( mFrameParent, "ABOUT XSQ", about );
	}else{
	  //System . err . println( "No event handler defined!" );
	}
        
  }
  
}
