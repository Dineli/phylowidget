package org.andrewberman.camera;

import org.andrewberman.tween.Tween;
import org.andrewberman.tween.TweenQuad;
import org.phylowidget.PhyloWidget;

import processing.core.PApplet;

/*
 * A tweaked camera, for use by the TreeRenderer class and subclasses, in order to allow a renderer to smoothly move
 * around the drawing area, scaling by width and height as necessary.
 */
public class RectMover extends MovableCamera
{
	protected SettableRect r;

	protected Tween wTween;
	protected Tween hTween;

	/**
	 * Convenience variables, storing the current position of each of the tweens.
	 */
	float cx, cy, w, h = 0;

	protected boolean constrainToScreen = true;
	
	public RectMover(PApplet app, SettableRect r)
	{
		super(app);
		this.r = r;

		wTween = new Tween(this, TweenQuad.instance, "out", 1f, 1f, FRAMES,
				false);
		hTween = new Tween(this, TweenQuad.instance, "out", 1f, 1f, FRAMES,
				false);

		/**
		 * Kind of important: call update() to make sure nothing here is null
		 * in case some mouse events happen before stuff is finished loading.
		 */
		update();
		
		super.makeResponsive();
	}

	public void zoomBy(float factor)
	{
		float newW = wTween.getFinish() * factor;
		float newH = hTween.getFinish() * factor;
		zoomCenterTo(cx, cy, newW, newH);
	}

	/**
	 * cx and cy are the CENTER coordinates of this TreeMover, in order to make
	 * it more closely resemble a camera.
	 */
	public void zoomCenterTo(float cx, float cy, float w, float h)
	{
		xTween.continueTo((float) cx, FRAMES);
		yTween.continueTo((float) cy, FRAMES);
		wTween.continueTo((float) w, FRAMES);
		hTween.continueTo((float) h, FRAMES);
	}

	public void fillScreen()
	{
		zoomCenterTo(0, 0, p.width, p.height);
	}

	public float getZ()
	{
		return w / p.width;
	}

	public void update()
	{
		/*
		 * No super.update() because we're updating the tweens on our own.
		 */

		super.scroll();

		xTween.update();
		yTween.update();
		wTween.update();
		hTween.update();
		
		updateConvenienceVariables();
		constrainToScreen();
		
		// Set our associated object's rectangle.
		r.setRect(-cx * getZ(), -cy * getZ(), w, h);
	}

	public void updateConvenienceVariables()
	{	
		/*
		 * Set the convenience variables.
		 */
		cx = xTween.position;
		cy = yTween.position;
		w = wTween.position;
		h = hTween.position;
	}
	
	public void constrainToScreen()
	{
		if (!this.constrainToScreen) return;
		
		float oX = (w - p.width)/2 - cx*getZ();
		if (oX <= 0)
		{
			xTween.continueTo((w - p.width)/2 / getZ());
			xTween.fforward();
		} else if (oX >= (w - p.width))
		{
			xTween.continueTo(-(w - p.width)/2 / getZ());
			xTween.fforward();
		}
		
		float oY = (h - p.height)/2 - cy*getZ();
		if (oY <= 0)
		{
			yTween.continueTo((h - p.height)/2 / getZ());
			yTween.fforward();
		} else if (oY >= (h - p.height))
		{
			yTween.continueTo(-(h - p.height)/2 / getZ());
			yTween.fforward();
		}
		
		
		/**
		 * Make the rectangle never shrink below the stage size.
		 */
		if (w < p.width || h < p.height)
		{
			xTween.continueTo(0);
			yTween.continueTo(0);
			wTween.continueTo(p.width);
			hTween.continueTo(p.height);
			xTween.fforward();
			yTween.fforward();
			wTween.fforward();
			hTween.fforward();
		}
		
		updateConvenienceVariables();
	}
}
