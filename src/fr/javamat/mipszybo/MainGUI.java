package fr.javamat.mipszybo;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;

public class MainGUI {

	public static void main(String[] args) {
		Zybo zybo = new Zybo();
		PanelVGA panelVGA = new PanelVGA(zybo);
		PanelInOut panelInOut = new PanelInOut(zybo);
		PanelDebug panelDebug = new PanelDebug(zybo);

		JFrame f = new JFrame("MIPS");
		f.setLayout(new BorderLayout());

		f.add(panelVGA, BorderLayout.CENTER);
		f.add(panelInOut, BorderLayout.SOUTH);
		f.add(panelDebug, BorderLayout.EAST);

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

		// Lecture programme
		String file = "C:/Users/Javamat/Desktop/cep_abbeyd_porcelma/mem/mips_invader_zybo.mem";
		try {
			InputStream ips = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String l;
			int addr = 0;
			while ((l = br.readLine()) != null) {
				if (l.startsWith("@")) {
					addr = (int) (Long.decode("0x" + l.subSequence(1, 9)) / 4);
				} else {
					zybo.getRAM().ram[addr] = (int) (Long.decode("0x" + l) * 1);
					addr += 1;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		zybo.getCPU().start();
	}
}
