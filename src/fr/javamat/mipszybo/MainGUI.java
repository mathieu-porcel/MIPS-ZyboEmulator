package fr.javamat.mipszybo;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainGUI {

	public static void main(String[] args) {
		Zybo zybo = new Zybo();

		JFrame f = new JFrame("MIPS");
		f.setLayout(new BorderLayout());

		f.add(new PanelScreen(zybo), BorderLayout.CENTER);
		f.add(new PanelIO(zybo), BorderLayout.SOUTH);

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
