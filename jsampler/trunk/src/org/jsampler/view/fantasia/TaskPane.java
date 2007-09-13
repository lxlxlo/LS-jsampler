/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2
 *   as published by the Free Software Foundation.
 *
 *   JSampler is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with JSampler; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *   MA  02111-1307  USA
 */

package org.jsampler.view.fantasia;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXTaskPane;
import org.jvnet.substance.swingx.SubstanceTaskPaneUI;


/**
 *
 * @author Grigor Iliev
 */
public class TaskPane extends JXTaskPane {
	
	/** Creates a new instance of <code>TaskPane</code> */
	public
	TaskPane() {
		setUI(new SubstanceTaskPaneUI());
		//this.getContentPane().setBackground(java.awt.Color.BLACK);
		if(getContentPane() instanceof JComponent) {
			((JComponent)getContentPane()).setOpaque(false);
		}
	}
	
}