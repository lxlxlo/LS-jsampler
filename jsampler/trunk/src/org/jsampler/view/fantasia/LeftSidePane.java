/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2008 Grigor Iliev <grigor@grigoriliev.com>
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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jvnet.substance.SubstanceLookAndFeel;

import static org.jsampler.view.fantasia.FantasiaI18n.i18n;
import static org.jsampler.view.fantasia.FantasiaPrefs.preferences;

/**
 *
 * @author Grigor Iliev
 */
public class LeftSidePane extends PixmapPane {
	JTabbedPane tabbedPane = new JTabbedPane();
	private final OrchestrasPane orchestraPane = new OrchestrasPane();
	private final MidiInstrumentsPane midiInstrumentsPane = new MidiInstrumentsPane();
	
	/**
	 * Creates a new instance of <code>LeftSidePane</code>
	 */
	public
	LeftSidePane() {
		super(Res.gfxRoundBg14);
		setOpaque(false);
		setPixmapInsets(new java.awt.Insets(6, 6, 6, 6));
		setLayout(new BorderLayout());
		JTabbedPane tp = tabbedPane;
		tp.addTab(i18n.getLabel("LeftSidePane.tabOrchestras"), orchestraPane);
		tp.addTab(i18n.getLabel("LeftSidePane.tabMidiInstruments"), midiInstrumentsPane);
		tp.setBackground(new java.awt.Color(0x828282));
		tp.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
		add(tp);
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		int i = preferences().getIntProperty("leftSidePane.tabIndex", 0);
		if(tp.getTabCount() > i) tp.setSelectedIndex(i);
	}
	
	public void
	savePreferences() {
		orchestraPane.savePreferences();
		midiInstrumentsPane.savePreferences();
		int i = tabbedPane.getSelectedIndex();
		if(i != -1) preferences().setIntProperty("leftSidePane.tabIndex", i);
	}
}
