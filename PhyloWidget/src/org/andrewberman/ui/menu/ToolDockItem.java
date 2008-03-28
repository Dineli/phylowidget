/**************************************************************************
 * Copyright (c) 2007, 2008 Gregory Jordan
 * 
 * This file is part of PhyloWidget.
 * 
 * PhyloWidget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PhyloWidget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PhyloWidget.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.andrewberman.ui.menu;

import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;

import org.andrewberman.ui.UIGlobals;
import org.andrewberman.ui.tools.Tool;

import processing.core.PApplet;

public class ToolDockItem extends DockItem
{
	private String toolString;
	private Tool tool;

	String shortcutString;
	
	public void setTool(String s)
	{
		toolString = s;
		if (menu != null)
		{
			PApplet p = menu.canvas;
			try
			{
				String packageName = Tool.class.getPackage().getName();
				Class toolClass = Class.forName(packageName+"."+s);
				Constructor c = toolClass
						.getConstructor(new Class[] { PApplet.class });
				Object instance = c.newInstance(new Object[] { p });
				this.tool = (Tool) instance;
				if (shortcutString != null)
					tool.setShortcut(shortcutString);
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	public Tool getTool()
	{
		return tool;
	}
	
	public void setMenu(Menu menu)
	{
		super.setMenu(menu);
		if (tool == null && toolString != null)
		{
			setTool(toolString);
		}
	}
	
	public MenuItem setShortcut(String s)
	{
		// Tools have "global" shortcuts, so we don't add a menu-specific one here.
		// Instead, we store a separate string and use that to build the tool's shortcut.
		shortcutString = s;
		if (tool != null)
			tool.setShortcut(shortcutString);
		return this;
	}
	
	@Override
	public void keyEvent(KeyEvent e)
	{
		super.keyEvent(e);
	}
	
	public String getLabel()
	{
		return getName() + " (" + tool.getShortcut().label + ")";
	}
	
	public void performAction()
	{
		super.performAction();
		if (nearestMenu instanceof ToolDock)
		{
			ToolDock td = (ToolDock) nearestMenu;
			UIGlobals.g.tools().switchTool(tool);
		}
	}
}
