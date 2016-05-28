package fr.javamat.mipszybo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class PanelInOut extends JPanel implements ActionListener, MouseListener {
	private Zybo zybo;

	public static final int ADDR_LED = 0x4000;
	public static final int ADDR_BUTTON = 0x4004;

	JToggleButton switch1;
	JToggleButton switch2;
	JToggleButton switch3;
	JToggleButton switch4;

	JButton reset;
	JButton push1;
	JButton push2;
	JButton push3;

	JComponent led28;
	JComponent led4000;

	public PanelInOut(Zybo zybo) {
		this.zybo = zybo;

		setLayout(new GridLayout(2, 2));

		led28 = new JComponent() {
			public void paint(Graphics g) {
				int value = zybo.getCPU().reg[28];
				g.setColor(Color.BLACK);
				g.drawString("Registre 28: " + value, (led28.getWidth() - 100) / 2, 25);
				for (int i = 0; i < 32; i++) {
					if (((value >>> (31 - i)) & 1) == 1) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);
					}
					g.fillOval((led28.getWidth() - 8 * 32) / 2 + i * 8, 5, 7, 7);
				}
			}
		};
		add(led28);

		led4000 = new JComponent() {
			public void paint(Graphics g) {
				int value = zybo.getRAM().read(ADDR_LED);
				g.drawString("LED: " + value, (led4000.getWidth() - 100) / 2, 25);
				g.setColor(Color.BLACK);
				for (int i = 0; i < 32; i++) {
					if (((value >>> (31 - i)) & 1) == 1) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);
					}
					g.fillOval((led4000.getWidth() - 8 * 32) / 2 + i * 8, 5, 7, 7);
				}
			}
		};
		add(led4000);

		JPanel panelSwitch = new JPanel();
		switch1 = new JToggleButton("Sw1");
		switch1.addActionListener(this);
		panelSwitch.add(switch1);
		switch2 = new JToggleButton("Sw2");
		switch2.addActionListener(this);
		panelSwitch.add(switch2);
		switch3 = new JToggleButton("Sw3");
		switch3.addActionListener(this);
		panelSwitch.add(switch3);
		switch4 = new JToggleButton("Sw4");
		switch4.addActionListener(this);
		panelSwitch.add(switch4);
		add(panelSwitch);

		JPanel panelPush = new JPanel();
		push3 = new JButton("Psh3");
		push3.addMouseListener(this);
		panelPush.add(push3);
		push2 = new JButton("Psh2");
		push2.addMouseListener(this);
		panelPush.add(push2);
		push1 = new JButton("Psh1");
		push1.addMouseListener(this);
		panelPush.add(push1);
		reset = new JButton("Reset");
		reset.addMouseListener(this);
		panelPush.add(reset);
		add(panelPush);

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(switch1)) {
			if (switch1.isSelected()) {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00000001);
			} else {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00000001));
			}
		} else if (e.getSource().equals(switch2)) {
			if (switch2.isSelected()) {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00000002);
			} else {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00000002));
			}
		} else if (e.getSource().equals(switch3)) {
			if (switch3.isSelected()) {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00000004);
			} else {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00000004));
			}
		} else if (e.getSource().equals(switch4)) {
			if (switch4.isSelected()) {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00000008);
			} else {
				zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00000008));
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource().equals(push1)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00010000);
		} else if (e.getSource().equals(push2)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00020000);
		} else if (e.getSource().equals(push3)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) | 0x00040000);
		} else if (e.getSource().equals(reset)) {
			zybo.getCPU().reset();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource().equals(push1)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00010000));
		} else if (e.getSource().equals(push2)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00020000));
		} else if (e.getSource().equals(push3)) {
			zybo.getRAM().write(ADDR_BUTTON, zybo.getRAM().read(ADDR_BUTTON) & (~0x00040000));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}
}
