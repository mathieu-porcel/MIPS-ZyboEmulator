package fr.javamat.mipszybo;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainGUI {

	public static void main(String[] args) {
		Zybo zybo = new Zybo();
		PanelVGA panelVGA = new PanelVGA(zybo);
		PanelInOut panelInOut = new PanelInOut(zybo);
		zybo.getClock().addSyncListener(panelVGA);

		JFrame f = new JFrame("MIPS");
		f.setLayout(new BorderLayout());

		f.add(panelVGA, BorderLayout.CENTER);
		f.add(panelInOut, BorderLayout.SOUTH);

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

		// Programme test:
		int[] ram = zybo.getMem().getData();

		// ori $11, $10, 1
		// add $12, $12, $11
		// lui $13, 8
		// add $13, $13, $12
		// lui $14, 0xFFFF
		// sw $14, 0($13)

		ram[0] = 0x354b0004;
		ram[1] = 0x018b6020;
		ram[2] = 0x3c0d0008;
		ram[3] = 0x01ac6820;
		ram[4] = 0x3c0eFFFF;
		ram[5] = 0xadae0000;
		ram[6] = 0xffffffff;

		zybo.getClock().start();
	}
}
