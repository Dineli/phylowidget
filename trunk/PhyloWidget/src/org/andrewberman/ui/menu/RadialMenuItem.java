package org.andrewberman.ui.menu;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.andrewberman.ui.Color;
import org.andrewberman.ui.Point;
import org.andrewberman.ui.UIUtils;

import processing.core.PApplet;
import processing.core.PFont;


public final class RadialMenuItem extends MenuItem
{
	public static final float SIZE_DECAY = .9f;
	
	String displayLabel;
	
	float rLo,rHi,tLo,tHi = 0;
	float radius;
	
	float outerX,outerY,innerX,innerY;
	float rectX,rectY,rectW,rectH;
	float textX,textY;
	float textWidth, textHeight, pad;
	float hintX,hintY;
	float fontSize,hintSize;
	Area wedge;
	
	char hint;
	
	static Ellipse2D.Float tempCircle = new Ellipse2D.Float(0,0,0,0);
	static Arc2D.Float tempArc = new Arc2D.Float(Arc2D.PIE);
	static RoundRectangle2D.Float roundedRect = new RoundRectangle2D.Float(0,0,0,0,0,0);
	
	public RadialMenuItem(String label, char hint)
	{
		super(label);
		this.hint = hint;
	}
	
	public void drawUnder()
	{
		Graphics2D g2 = menu.buff.g2;
		float r = radius;
		roundedRect.setRoundRect(rectX, rectY, rectW, rectH,
				rectH/3, rectH/3);
		g2.setPaint(Color.white);
		g2.fill(roundedRect);
		g2.setPaint(Color.black);
		g2.setStroke(menu.style.stroke);
		g2.draw(roundedRect);
		super.draw();
	}
	
	public void draw()
	{
		super.draw();
		if (!isVisible()) return;
		if (!showingChildren())
		{
			drawUnder();
			drawText();
		}
		drawShape();
		drawHint();
	}
	
	void drawShape()
	{
		/*
		 * Draw the main wedge shape.
		 */
		Graphics2D g2 = menu.buff.g2;
//		this.isAncestorOf(menu.currentlyHovered);
		if (this.isAncestorOf(menu.currentlyHovered))
			g2.setPaint(menu.style.getGradient(Menu.OVER,x-rHi,y-rHi,x+rHi,y+rHi));
		else
			g2.setPaint(menu.style.getGradient(state,x-rHi,y-rHi,x+rHi,y+rHi));
		g2.fill(wedge);
		g2.setStroke(menu.style.stroke);
		g2.setPaint(menu.style.strokeColor);
		
		g2.draw(wedge);
		/*
		 * Draw the sub-items triangle, if necessary
		 */
		if (items.size() > 0 && !showingChildren())
		{
			float theta = (tLo + tHi)/2;
	//		AffineTransform at = AffineTransform.getRotateInstance(-radToDeg(theta));
	//		at.translate(x+outerX,y+outerY);
			float scale = (rHi-rLo)/2;
			AffineTransform at = AffineTransform.getTranslateInstance(outerX, outerY);
			at.scale(scale,scale);
			at.rotate(theta);
			Area tri = menu.style.subTriangle;
			Area newTri = tri.createTransformedArea(at);
			g2.setPaint(menu.style.strokeColor);
			g2.fill(newTri);
		}
	}
	
	void drawText()
	{
		Graphics2D g2 = menu.buff.g2;
		Font f = menu.style.font.font.deriveFont(fontSize);
		g2.setFont(f);
		g2.setPaint(menu.style.textColor);
		g2.drawString(displayLabel, textX, textY);
	}
	
	void drawHint()
	{
		Graphics2D g2 = menu.buff.g2;
		Font f = menu.style.font.font.deriveFont(fontSize);
		f = f.deriveFont(hintSize);
		g2.setFont(f);
		g2.setPaint(menu.style.textColor);
		g2.drawString(String.valueOf(hint), hintX,hintY);
	}
	
	public void layout()
	{
		this.layout(rLo,rHi,tLo,tHi);
	}
	
	void layout(float radLo, float radHi, float thLo, float thHi)
	{
		this.rLo=radLo;
		this.rHi=radHi;
		this.tLo=thLo;
		this.tHi=thHi;
		this.radius = radHi;
		
		this.layoutText();
		this.createShapes();
		
		float dTheta = thHi - thLo;
		float thetaStep = dTheta / items.size();
		for (int i=0; i < items.size(); i++)
		{
			RadialMenuItem seg = (RadialMenuItem) items.get(i);
			seg.setPosition(x, y);
			float theta = thLo + i*thetaStep;
			seg.layout(radHi,radHi+(radHi-radLo)*SIZE_DECAY,theta,theta+thetaStep);
		}
	}
	
	float radToDeg(float rad)
	{
		return PApplet.degrees(rad);
	}
	
	void createShapes()
	{
		tempCircle.setFrameFromCenter(x,y,x+rLo,y+rLo);
		tempArc.setFrameFromCenter(x, y, x+rHi, y+rHi);
		
		float degLo = radToDeg(-tLo);
		float degHi = radToDeg(-tHi);
		tempArc.setAngleStart(degLo);
		tempArc.setAngleExtent(degHi-degLo);
		wedge = new Area(tempArc);
		Area delete = new Area(tempCircle);
		wedge.subtract(delete);
	}
	
	void layoutText()
	{
		/*
		 * Calculate the sine and cosine, which we'll need to use often.
		 */
		float theta = (tLo + tHi) / 2;
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		outerX = x+cos*rHi;
		outerY = y+sin*rHi;
		innerX = x+cos*rLo;
		innerY = y+sin*rLo;
		PFont font = menu.style.font;
		FontMetrics fm = UIUtils.getMetrics(menu.buff, font.font, 1);
		float unitTextHeight = (float) fm.getMaxCharBounds(menu.buff.g2).getHeight();
		fontSize = (rHi-rLo)/unitTextHeight * .9f;
		fm = UIUtils.getMetrics(menu.buff, font.font, fontSize);
		float descent = fm.getDescent();
		float ascent = fm.getAscent();
		
//		Rectangle2D bounds = fm.getStringBounds(label, menu.buff.g2);
		
		if (items.size() > 0)
			displayLabel = label.concat("...");
		else
			displayLabel = label;
		
		textHeight = UIUtils.getTextHeight(menu.buff, font, fontSize, displayLabel, true);
//		textHeight = (float) bounds.getHeight();
//		textWidth = (float) bounds.getWidth();
		textWidth = UIUtils.getTextWidth(menu.buff, font, fontSize, displayLabel, true);
		// Calculate the necessary x and y offsets for the text.
		float outX = x+cos*(rHi+textHeight);
		float outY = y+sin*(rHi+textHeight);
		float pad = menu.style.padX;
		rectW = textWidth+2*pad;
		rectH = textHeight+2*pad;
		rectX = outX + cos * rectW / 2 - rectW/2;
		rectY = outY + sin * rectH / 2 - rectH/2;
		textX = rectX + pad;
		textY = rectY + pad + ascent;
//		textX = cos * textWidth/2;
//		textX += -textWidth / 2;
//		textX += outerX;
//		textY = sin * (textHeight)/2;
//		textY += -descent + (textHeight)/2;
//		textY += outerY;
		/*
		 * Set the background rectangle.
		 */
		
//		rectX = textX-pad;
//		rectY = textY + descent - textHeight - pad;

		/*
		 * Now, let's handle the hint characters.
		 */
		float rMid = (rLo + rHi) / 2;
		float centerX = x+cos * rMid;
		float centerY = y+sin * rMid;
		/*
		 * Measure the character at 1px, then scale up accordingly.
		 */
		fm = UIUtils.getMetrics(menu.buff, font.font, 1);
		String s = String.valueOf(hint);
		Rectangle2D charBounds = fm.getStringBounds(s, menu.buff.g2);
		float charHeight = (float) charBounds.getHeight();
		float charWidth = (float) charBounds.getWidth();
		float diagonal = PApplet.sqrt(charHeight*charHeight + charWidth*charWidth);
		hintSize = (rHi-rLo)/diagonal;
		fm = UIUtils.getMetrics(menu.buff, font.font, hintSize);
		charBounds = fm.getStringBounds(s, menu.buff.g2);
		charHeight = (float) charBounds.getHeight();
		charWidth = (float) charBounds.getWidth();
		float charDesc = fm.getDescent();
		
		hintX = centerX - charWidth / 2.0f;
		hintY = centerY - charDesc + charHeight / 2.0f;		
	}
	
	protected boolean alreadyContainsChar(char c)
	{
		if (hint == c) return true;
		for (int i=0; i < items.size(); i++)
		{
			RadialMenuItem rmi = (RadialMenuItem) items.get(i);
			if (rmi.alreadyContainsChar(c)) return true;
		}
		return false;
	}
	
	float getMaxRadius()
	{
		if (!isVisible()) return 0;
		float max = this.rHi;
		for (int i=0; i < items.size(); i++)
		{
			RadialMenuItem rmi = (RadialMenuItem) items.get(i);
			float cur = rmi.getMaxRadius();
			if (cur > max)
				max = cur;
		}
		return max;
	}
	
	public boolean containsPoint(Point pt)
	{
		if (!isVisible()) return false;
//		if (wedge == null) return false;
		boolean contained = false;
		
		if (wedge.contains(pt.x,pt.y))
			contained = true;
		Rectangle2D.Float temp = new Rectangle2D.Float(rectX,rectY,rectW,rectH);
		if (temp.contains(pt.x,pt.y))
			contained = true;
		return contained;
	}
	
	protected void keyHintEvent(KeyEvent e)
	{
		if (!isVisible()) return;
		char c = (char)e.getKeyChar();
		if (Character.toLowerCase(c) == Character.toLowerCase(hint))
		{
			this.performAction();
			e.consume();
		} else
		{
			for (int i=0; i < items.size(); i++)
			{
				RadialMenuItem rmi = (RadialMenuItem) items.get(i);
				rmi.keyHintEvent(e);
			}
		}
	}
	
	public void getRect(Rectangle2D.Float rect, Rectangle2D.Float buff)
	{
		if (isVisible())
		{
			buff.setRect(wedge.getBounds2D());
			Rectangle2D.union(rect, buff, rect);
			buff.setRect(rectX,rectY,rectW,rectH);
			Rectangle2D.union(rect, buff, rect);
		}
		super.getRect(rect, buff);
	}
	
}