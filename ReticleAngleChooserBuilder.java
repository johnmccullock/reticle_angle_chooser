package main.gui.custom;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ReticleAngleChooserBuilder
{
	private static final Color DEFAULT_RETICLE_BORDER_COLOR = new Color(192, 192, 192, 255);
	private static final float DEFAULT_RETICLE_LINE_WIDTH = 1.0f;
	private static final float DEFAULT_GRIP_LENGTH_FACTOR = 0.7f;
	private static final int DEFAULT_GRIP_RADIUS = 4;
	private static final Color DEFAULT_GRIP_COLOR = new Color(0, 0, 0, 255);
	private static final float DEFAULT_GRIP_LINE_WIDTH = 1.0f;
	
	public static ReticleAngleChooser createStyle001(ReticleAngleChooser.Mode mode)
	{
		ReticleAngleChooser chooser = new ReticleAngleChooser(mode);
		chooser.setLayout(new BorderLayout());
		chooser.setFocusable(true);
		chooser.setBorderColor(DEFAULT_RETICLE_BORDER_COLOR);
		chooser.setReticleLineWidth(DEFAULT_RETICLE_LINE_WIDTH);
		chooser.setGripColor(DEFAULT_GRIP_COLOR);
		chooser.setGripLineWidth(DEFAULT_GRIP_LINE_WIDTH);
		chooser.setGripRadius(DEFAULT_GRIP_RADIUS);
		chooser.addComponentListener(createComponentListener(chooser));
		chooser.addMouseListener(createMouseListener(chooser));
		chooser.addMouseMotionListener(createMouseMotionListener(chooser));
		return chooser;
	}
	
	private static ComponentListener createComponentListener(ReticleAngleChooser chooser)
	{
		return new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				if(Math.min(chooser.getWidth(), chooser.getHeight()) < 1){
					return;
				}
				chooser.setGripLength((Math.min(chooser.getWidth(), chooser.getHeight()) / 2f) * DEFAULT_GRIP_LENGTH_FACTOR);
				return;
			}
		};
	}
	
	private static MouseListener createMouseListener(ReticleAngleChooser chooser)
	{
		return new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(e.getPoint() == null){
					return;
				}
				chooser.setValueIsAdjusting(true);
				chooser.setMousePos(e.getPoint());
				chooser.updateAngleValue();
				chooser.notifyAngleObservers();
				return;
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				chooser.setValueIsAdjusting(false);
				chooser.setMousePos(e.getPoint());
				chooser.updateAngleValue();
				chooser.notifyAngleObservers();
				return;
			}
		};
	}
	
	private static MouseMotionListener createMouseMotionListener(ReticleAngleChooser chooser)
	{
		return new MouseMotionListener()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				if(e.getPoint() == null){
					return;
				}
				chooser.setMousePos(e.getPoint());
				chooser.updateAngleValue();
				chooser.notifyAngleObservers();
				return;
			}
			
			@Override
			public void mouseMoved(MouseEvent e)
			{
				chooser.setMousePos(e.getPoint());
				return;
			}
		};
	}
}
