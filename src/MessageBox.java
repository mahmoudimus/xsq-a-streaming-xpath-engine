/* $Id: MessageBox.java,v 3.0 2002/11/14 00:34:25 pengfeng Exp $
 * 
 * Copyright(c) 2002 Feng Peng and Sudarshan S. Chawathe;
 * http://www.cs.umd.edu/~pengfeng/xsq
 *
 */

package edu.umd.cs.db.xsq;

import java.lang.*;
import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 * Class description goes here
 *
 * @version $Id: MessageBox.java,v 3.0 2002/11/14 00:34:25 pengfeng Exp $
 * @author  Feng peng
 */

public class MessageBox extends JDialog implements ActionListener{

  static JTextArea messArea = new JTextArea ();
  static JButton okButton = new JButton ("OK");

  public MessageBox ( JFrame parent, String title, String message ){
  
    super( parent, title, true );
    
    setSize  ( 200,100 );
    
    Container panel = getContentPane ();
    
    panel . setLayout ( new BorderLayout() );
    messArea . setText ( message );
    messArea .setBorder ( BorderFactory.createEtchedBorder() );
    panel . add ( messArea, BorderLayout.NORTH );
    panel . add ( okButton, BorderLayout.SOUTH );
    okButton.addActionListener (this);
    setVisible ( true );
  }

  /**
   * ActionListener event handler method.
   */
  public void actionPerformed ( ActionEvent event ) {
    Object object = event.getSource ();
    if (object == okButton){
      dispose ();
    }
  } 
  
  public void setText ( String sMessage ){
    messArea.setText (sMessage);
  }

} // class MessageBox


