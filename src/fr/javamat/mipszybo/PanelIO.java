package fr.javamat.mipszybo;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelIO extends JPanel {
	private Zybo zybo;

	public PanelIO(Zybo zybo) {
		this.zybo = zybo;
	}
}
