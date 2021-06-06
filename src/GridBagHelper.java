/* $Id: GridBagHelper.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
 *
 * Modified from the GridBagHelper.java from XPE project. 
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

import java.awt.*;

/** 
 * A helper class to help organize the components in the panel.
 *
 * @version $Id: GridBagHelper.java,v 1.2 2002/10/12 02:31:38 pengfeng Exp $
 * @author  Feng peng
 */

public class GridBagHelper{

  GridBagLayout gridbag;
  Container container;
  GridBagConstraints c;
  int x = 0;
  int y = 0;
	
  public GridBagHelper(Container container){

    this.container = container;

    gridbag = new GridBagLayout();
    container.setLayout(gridbag);
	
    c = new GridBagConstraints();
    c.insets = new Insets(2,2,2,2);
    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.HORIZONTAL;
  }

  void add(Component component, double weightx){
    add(component, weightx, 1);
  }
	
  void add(Component component, double weightx, int width){
    c.gridx = x;
    c.gridy = y;
    c.weightx = weightx;
    c.gridwidth = width;
    gridbag.setConstraints(component, c);
    container.add(component);
    x++;
  }

  void nextRow() {
    y++;
    x=0;
  }
}
