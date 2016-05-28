package fr.javamat.mipszybo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class PanelVGA extends JComponent {
	private Zybo zybo;

	private BufferedImage bufferedImage;
	private int[] videoMem;

	public PanelVGA(Zybo zybo) {
		this.zybo = zybo;

		setPreferredSize(new Dimension(640, 480));

		bufferedImage = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		videoMem = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					repaint();
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void paint(Graphics g) {
		for (int i = 0; i < 320 * 240; i++) {
			int color = zybo.getRAM().read(0x80000 + i * 4);

			// Conversion couleur réel
			int red = (color & 0x0000F800) >> 11;
			int green = (color & 0x000007E0) >> 5;
			int blue = (color & 0x0000001F) >> 0;
			red = (int) (255.0 * red / 31.0);
			green = (int) (255.0 * green / 63.0);
			blue = (int) (255.0 * blue / 31.0);

			videoMem[i] = (red << 16) | (green << 8) | (blue << 0);
		}
		g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
	}
}
