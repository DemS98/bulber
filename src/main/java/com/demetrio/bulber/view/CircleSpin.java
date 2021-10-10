package com.demetrio.bulber.view;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class CircleSpin extends JComponent
{

	private final String message;
	
	private int start;
	
	public CircleSpin(String message, int size)
	{
		this.message = message;
		start = 0;
		setPreferredSize(new Dimension(size, size));
	}

	public CircleSpin(int size) {
		this.message = null;
		start = 0;
		setPreferredSize(new Dimension(size, size));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		RenderingHints qualityHints = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(
			RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);
		
		g2.setStroke(new BasicStroke((getWidth() * 15f) / 500f));
		int circleSize = Math.min(getWidth(),getHeight()) / 4;
		int spaceX = (getWidth() - circleSize) / 2;
		int spaceY = (getHeight() - circleSize) / 2;

		g2.drawArc(spaceX, spaceY, circleSize, circleSize, start, 270);
		start = (start + 2) % 360;

		if (message != null) {
			Font font = this.getFont().deriveFont(Font.BOLD, 28);
			FontMetrics metrics = g2.getFontMetrics(font);
			g2.setFont(font);
			g2.setColor(Color.BLACK);
			g2.drawString(message, spaceX + (circleSize - metrics.stringWidth(message)) / 2, spaceY + circleSize + 100);
		}
		g2.dispose();
	}

}
