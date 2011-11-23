/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2011 Grigor Iliev <grigor@grigoriliev.com>
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
package org.jsampler.view.std;

import org.jsampler.AudioDeviceModel;
import org.jsampler.CC;

/**
 *
 * @author Grigor Iliev
 */
public abstract class StdViewConfig extends org.jsampler.view.swing.ViewConfig {
	public static StdViewConfig
	getViewConfig() { return (StdViewConfig)CC.getViewConfig(); }
	
	public JSDestEffectChooser
	createDestEffectChooser(AudioDeviceModel dev) { return new JSDestEffectChooser(dev); }
}
