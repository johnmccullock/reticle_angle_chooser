package main.gui.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.Vector;

import javax.swing.JComponent;

/**
 * I chose to represent degrees and radians differently.  Degrees should start zero at the top of the circle and move clockwise.
 * Radians should start at the right side of the circle and move counter-clockwise.  The setAngle() and getAngle() methods use code 
 * which modifies values going in and out of this component to maintain these representations.
 * 
 * The mAngle field stores only radians.
 * 
 * Version 1.1 includes options to choose degrees or radians.  Also included is the use of AngleObserver objects, and the 
 * mValueIsAdjusting field.
 * 
 * @author John McCullock
 * @version 1.1 2020-08-21
 */

@SuppressWarnings("serial")
public class ReticleAngleChooser extends JComponent
{
	public enum Mode{DEGREES, RADIANS};
	
	private static final float PI = (float)Math.PI;
	private static final float PI2 = (float)(Math.PI * 2.0);
	private static final float HALF_PI = (float)(Math.PI * 0.5);
	
	private Mode mMode = Mode.RADIANS;
	private Point mMousePos = new Point();
	private float mAngle = 0.0f;
	private float mGripLength = 10.0f;
	private Color mReticleBorderColor = Color.BLACK;
	private float mReticleLineWidth = 1.0F;
	private Color mGripColor = Color.BLACK;
	private float mGripLineWidth = 1.0F;
	private int mGripRadius = 5; 
	private ComponentListener mComponentListener = null;
	private MouseListener mMouseListener = null;
	private MouseMotionListener mMouseMotionListener = null;
	private Vector<AngleObserver> mAngleObservers = new Vector<AngleObserver>();
	private boolean mValueIsAdjusting = false;
	
	public ReticleAngleChooser(Mode mode)
	{
		this.mMode = mode;
		return;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(this.getWidth() <= 0){
			return;
		}
		if(this.getHeight() <= 0){
			return;
		}
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.drawReticle(g2d);
		this.drawGrip(g2d);
		g2d.dispose();
		return;
	}
	
	private void drawReticle(Graphics2D g2d)
	{
		g2d.setStroke(new BasicStroke(this.mReticleLineWidth));
		g2d.setPaint(this.mReticleBorderColor);
		g2d.drawOval(4, 4, (int)Math.floor(this.getWidth() - (8 + this.mReticleLineWidth)), (int)Math.floor(this.getHeight() - (8 + this.mReticleLineWidth)));
		g2d.drawLine((int)Math.round(this.getWidth() / 2.0), 0, (int)Math.round(this.getWidth() / 2.0), this.getHeight());
		g2d.drawLine(0, (int)Math.round(this.getHeight() / 2.0), this.getWidth(), (int)Math.round(this.getHeight() / 2.0));
		return;
	}
	
	private void drawGrip(Graphics2D g2d)
	{
		int cx = (int)Math.round(this.getWidth() / 2.0);
		int cy = (int)Math.round(this.getHeight() / 2.0);
		float dirX = (float)Math.cos(this.mAngle);
		float dirY = -(float)Math.sin(this.mAngle);
		int x2 = cx + (int)Math.round(dirX * this.mGripLength);
		int y2 = cy + (int)Math.round(dirY * this.mGripLength);
		g2d.setStroke(new BasicStroke(this.mGripLineWidth));
		g2d.setPaint(this.mGripColor);
		g2d.drawLine(cx, cy, x2, y2);
		g2d.drawOval(cx - this.mGripRadius, cy - this.mGripRadius, this.mGripRadius * 2, this.mGripRadius * 2);
		this.drawRectangleFromCenter(g2d, x2, y2, this.mGripRadius, this.mGripRadius);
		
		return;
	}
	
	public void drawRectangleFromCenter(Graphics2D g2d, int cx, int cy, int xOffset, int yOffset)
	{
		// North.
		int x1 = cx - xOffset;
		int y1 = cy - yOffset;
		int x2 = cx + xOffset;
		int y2 = cy - yOffset;
		g2d.drawLine(x1, y1, x2, y2);
		// East
		x1 = cx + xOffset;
		y1 = cy - yOffset;
		x2 = cx + xOffset;
		y2 = cy + yOffset;
		g2d.drawLine(x1, y1, x2, y2);
		// South
		x1 = cx - xOffset;
		y1 = cy + yOffset;
		x2 = cx + xOffset;
		y2 = cy + yOffset;
		g2d.drawLine(x1, y1, x2, y2);
		// West
		x1 = cx - xOffset;
		y1 = cy - yOffset;
		x2 = cx - xOffset;
		y2 = cy + yOffset;
		g2d.drawLine(x1, y1, x2, y2);
		return;
	}
	
	public void setMousePos(Point pos)
	{
		this.mMousePos = pos;
		return;
	}
	
	public Point getMousePos()
	{
		return this.mMousePos;
	}
	
	public float norm(float angle)
	{
		angle = angle % (PI * 2.0f);
		return angle = angle < 0 ? angle + (PI * 2.0f) : angle;
	}
	
	public float getAngleFromPoints(final float x1, final float y1, final float x2, final float y2)
	{
		return (float)Math.atan2(-(y2 - y1), x2 - x1);
	}
	
	public float toDegrees(float radians)
	{
		return 360.0F * (radians / PI2);
	}
	
	public float toRadians(float degrees)
	{
		return PI2 * (degrees / 360.0F);
	}
	
	/**
	 * Used in response to mouse event related angle changes.  Not the same as the setAngle() function.
	 */
	public void updateAngleValue()
	{
		float cx = this.getWidth() / 2f;
		float cy = this.getHeight() / 2f;
		this.mAngle = this.norm(this.getAngleFromPoints(cx, cy, this.mMousePos.x, this.mMousePos.y));
		this.repaint();
		return;
	}
	
	/**
	 * Used to manually set material angle.  Not connected to mouse events: see updateAngleValue().
	 */
	public void setAngle(float angle)
	{
		if(this.mMode.equals(Mode.DEGREES)){
			this.mAngle = this.toRadians(-angle) + HALF_PI;
		}else{
			this.mAngle = angle;
		}
		this.repaint();
		return;
	}
	
	public float getAngle()
	{
		if(this.mMode.equals(Mode.DEGREES)){
			return (360.0F - Math.abs(this.toDegrees(this.mAngle)) + 90F) % 360.0F;
		}else{
			return this.mAngle;
		}
	}
	
	public void addAngleObserver(AngleObserver obs)
	{
		this.mAngleObservers.add(obs);
		return;
	}
	
	public void removeAngleObserver(AngleObserver obs)
	{
		/*
		 * For some unknown reason, the Vector.remove(Object obj) method was "removing" the object, but still leaving a null element.
		 * So, I resorted to this, to ensure no null elements.
		 */
		for(int i = this.mAngleObservers.size() - 1; i >=0; i--)
		{
			if(this.mAngleObservers.get(i) == null){
				this.mAngleObservers.remove(i);
				continue;
			}
			if(this.mAngleObservers.get(i).equals(obs)){
				this.mAngleObservers.removeElementAt(i);
				break;
			}
		}
		//System.out.println(this.mAngleObservers.size());
		return;
	}
	
	public void notifyAngleObservers()
	{
		for(AngleObserver obs : this.mAngleObservers)
		{
			if(obs == null){
				continue;
			}
			if(this.mMode.equals(Mode.DEGREES)){
				obs.angleChanged((360.0F - Math.abs(this.toDegrees(this.mAngle)) + 90F) % 360.0F);
			}else{
				obs.angleChanged(this.mAngle);
			}
		}
		return;
	}
	
	public void setGripLength(float length)
	{
		this.mGripLength = length;
		return;
	}
	
	public float getGripLength()
	{
		return this.mGripLength;
	}
	
	public void setValueIsAdjusting(boolean isAdjusting)
	{
		this.mValueIsAdjusting = isAdjusting;
		return;
	}
	
	public boolean getValueIsAdjusting()
	{
		return this.mValueIsAdjusting;
	}
	
	public void enableListeners()
	{
		this.addComponentListener(this.mComponentListener);
		this.addMouseListener(this.mMouseListener);
		this.addMouseMotionListener(this.mMouseMotionListener);
		return;
	}
	
	public void disableListeners()
	{
		this.removeComponentListener(this.mComponentListener);
		this.removeMouseListener(this.mMouseListener);
		this.removeMouseMotionListener(this.mMouseMotionListener);
		return;
	}
	
	public void setBorderColor(Color color)
	{
		this.mReticleBorderColor = color;
		return;
	}
	
	public Color getBorderColor()
	{
		return this.mReticleBorderColor;
	}
	
	public void setReticleLineWidth(float width)
	{
		this.mReticleLineWidth = width;
		return;
	}
	
	public float getReticleLineWidth()
	{
		return this.mReticleLineWidth;
	}
	
	public void setGripColor(Color color)
	{
		this.mGripColor = color;
		return;
	}
	
	public Color getGripColor()
	{
		return this.mGripColor;
	}
	
	public void setGripLineWidth(float width)
	{
		this.mGripLineWidth = width;
		return;
	}
	
	public float getGripLineWidth()
	{
		return this.mGripLineWidth;
	}
	
	public void setGripRadius(int radius)
	{
		this.mGripRadius = radius;
		return;
	}
	
	public int getGripRadius()
	{
		return this.mGripRadius;
	}
}
