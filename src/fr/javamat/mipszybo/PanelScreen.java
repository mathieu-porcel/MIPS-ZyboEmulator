package fr.javamat.mipszybo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class PanelScreen extends JComponent {
	private Zybo zybo;

	private BufferedImage bufferedImage;
	private int[] videoMem;

	public PanelScreen(Zybo zybo) {
		this.zybo = zybo;

		setPreferredSize(new Dimension(640, 480));

		bufferedImage = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		videoMem = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
	}
}
