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
package org.phylowidget.render;

import java.lang.reflect.Field;
import java.util.Properties;

import org.andrewberman.ui.Color;
import org.phylowidget.PhyloWidget;

public class RenderStyleSet
{
	public Color backgroundColor;
	public Color foregroundColor;
	
	public float regStroke;

	public Color hoverColor;
	public float hoverStroke;

	public Color dimColor;
	public float dimStroke;

	public Color copyColor;
	public float copyStroke;

	public float nodeSizeMultiplier;
	public float lineThicknessMultiplier;
	
	private static RenderStyleSet defaultSet;

	public static RenderStyleSet defaultStyle()
	{
		if (defaultSet == null)
		{
			defaultSet = new RenderStyleSet();
			defaultSet.loadDefaults();
		}
		return defaultSet;
	}

	private void loadDefaults()
	{
		foregroundColor = Color.parseColor(PhyloWidget.ui.foreground);
		backgroundColor = Color.parseColor(PhyloWidget.ui.background);
		regStroke = 1f;

		dimColor = foregroundColor.brighter(200);
		dimStroke = 2f;

		hoverColor = new Color(100, 150, 255);
		hoverStroke = 2f;

		copyColor = new Color(255, 0, 0);
		copyStroke = 2f;

		nodeSizeMultiplier = 0.5f;
		lineThicknessMultiplier = 1f;
	}

	
	
//	private void loadFromProperties()
//	{
//		Properties p = PhyloWidget.props;
//		Class c = this.getClass();
//		Field[] fields = c.getFields();
//		for (int i = 0; i < fields.length; i++)
//		{
//			Field f = fields[i];
//			String s = f.getName();
//			if (p.containsKey(s))
//			{
//				try
//				{
//					String value = (String) p.get(s);
//					Class fieldType = f.getType();
//					if (fieldType == Float.TYPE)
//					{
//						f.setFloat(this, Float.parseFloat(value));
//					} else if (fieldType == Color.class)
//					{
//						// parse the color triplet.
//						String[] rgb = value.split(",");
//						Color color = new Color(Integer.parseInt(rgb[0]),
//								Integer.parseInt(rgb[1]), Integer
//										.parseInt(rgb[2]));
//						f.set(this, color);
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//					continue;
//				}
//			}
//		}
//	}

}