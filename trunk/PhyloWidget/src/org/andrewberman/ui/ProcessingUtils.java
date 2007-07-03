package org.andrewberman.ui;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.andrewberman.phyloinfo.PhyloWidget;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
import processing.core.PMatrix;

public class ProcessingUtils
{
	
	private static PMatrix camera = new PMatrix();
	private static PMatrix cameraInv = new PMatrix();
	private static PMatrix modelview = new PMatrix();
	private static PMatrix modelviewInv = new PMatrix();

	/**
	 * This should be called at the end of every draw() run.
	 * @param mat the modelview matrix.
	 */
	public static void setMatrix(PApplet p)
	{
		if (p.g.getClass() == PGraphicsJava2D.class)
		{
			PGraphicsJava2D g = (PGraphicsJava2D) p.g;
			AffineTransform tr = g.g2.getTransform();
			try
			{
				affineToPMatrix(tr, modelview);
				tr.invert();
				affineToPMatrix(tr, modelviewInv);
				camera.reset();
				cameraInv.reset();
			} catch (NoninvertibleTransformException e)
			{
				return;
			}
		} else
		{
			camera.set(p.g.camera);
			cameraInv.set(p.g.cameraInv);
			modelview.set(p.g.modelview);
			modelviewInv.set(p.g.modelviewInv);
			
		}
	}

	private static double[] temp = new double[6];
	public static void affineToPMatrix(AffineTransform tr, PMatrix mat)
	{
		tr.getMatrix(temp);
		mat.set((float) temp[0], (float) temp[2], 0, (float) temp[4],
				(float) temp[1], (float) temp[3], 0, (float) temp[5],
				0, 0, 0, 0,
				0, 0, 0, 0);
	}

	public static void transform(PMatrix mat, Point2D.Float pt)
	{
		float x = pt.x;
		float y = pt.y;
		float z = 0;
		
		pt.x = mat.m00*x + mat.m01*y + mat.m02*z + mat.m03;
		pt.y = mat.m10*x + mat.m11*y + mat.m12*z + mat.m13;
	}
	
	/**
	 * 
	 * @param p The PApplet from which to base the transformation.
	 * @param pt The point to transform in place. Should currently contain the mouse
	 * coordinates.
	 */
	public static void screenToModel(PApplet p, Point2D.Float pt)
	{		
		transform(camera,pt);
		transform(modelviewInv,pt);
	}

	public static void modelToScreen(PApplet p, Point2D.Float pt)
	{
		transform(modelview,pt);
		transform(cameraInv,pt);
	}
}