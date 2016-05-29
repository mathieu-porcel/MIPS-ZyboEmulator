package fr.javamat.mipszybo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class PanelDebug extends JPanel implements ActionListener {
	private Zybo zybo;

	private JToggleButton start;
	private JButton next;

	private JSpinner freq;
	private JLabel realFreq;
	private JLabel instruction;

	public PanelDebug(Zybo zybo) {
		this.zybo = zybo;

		setLayout(new BorderLayout());

		JPanel panelNorth = new JPanel();
		panelNorth.setLayout(new GridLayout(4, 2));

		start = new JToggleButton("On / Off");
		start.addActionListener(this);
		panelNorth.add(start);

		next = new JButton("Step");
		next.addActionListener(this);
		panelNorth.add(next);

		panelNorth.add(new JLabel("Horloge (Hz): "));
		freq = new JSpinner(new SpinnerNumberModel(50e6, 1, 1e9, 1));
		zybo.getCPU().freq = (double) freq.getValue();
		freq.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				zybo.getCPU().freq = (double) freq.getValue();
			}
		});
		panelNorth.add(freq);
		panelNorth.add(new JLabel("Fréquence réelle: "));
		realFreq = new JLabel("0 Hz");
		panelNorth.add(realFreq);

		panelNorth.add(new JLabel("Prochaine instruction: "));
		instruction = new JLabel("");
		panelNorth.add(instruction);

		add(panelNorth, BorderLayout.NORTH);

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					// Clock
					DecimalFormat format = new DecimalFormat("###,### Hz");
					realFreq.setText(format.format(zybo.getCPU().realFreq));
					if (zybo.getCPU().realFreq < 0.99 * ((double) freq.getValue())) {
						realFreq.setForeground(Color.RED);
					} else {
						realFreq.setForeground(Color.BLACK);
					}
					
					// Instruction
					instruction.setText(decodeIR(zybo.getCPU().IR, zybo.getCPU().instruction));

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(start)) {
			zybo.getCPU().setDebug(!start.isSelected());
			next.setEnabled(!start.isSelected());
		} else if (e.getSource().equals(next)) {
			zybo.getCPU().step();
		}
	}

	private String decodeIR(int IR, MipsCPU.states state) {
		int RS = (IR & 0x03E00000) >>> 21;
		int RT = (IR & 0x001F0000) >>> 16;
		int RD = (IR & 0x0000F800) >>> 11;
		int SH = (IR & 0x000007C0) >>> 6;

		int imm16 = IR & 0x0000FFFF;
		int imm16ext = imm16 | ((imm16 & 0x00008000) == 0 ? 0 : 0xFFFF0000);

		int imm16extUp = (imm16 << 2) | ((imm16 & 0x00008000) == 0 ? 0 : 0xFFFC0000);
		String label = "0x" + Integer.toHexString((int) (Integer.toUnsignedLong(zybo.getCPU().PC) + imm16extUp));

		switch (zybo.getCPU().instruction) {

		/*
		 * Operations
		 */

		case add:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case addi:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", " + imm16ext;

		case sub:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case and:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case andi:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", 0x" + Integer.toHexString(imm16);

		case or:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case ori:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", 0x" + Integer.toHexString(imm16);

		case xor:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case xori:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", 0x" + Integer.toHexString(imm16);

		case nor:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case srl:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", " + SH;

		case sra:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", " + SH;

		case sll:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", " + SH;

		case sllv:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", $" + RS;

		case srlv:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", $" + RS;

		case srav:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RT + ", $" + RS;

		case slt:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case slti:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", " + imm16ext;

		case sltu:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS + ", $" + RT;

		case sltiu:
			return zybo.getCPU().instruction + " $" + RT + ", $" + RS + ", " + Integer.toUnsignedLong(imm16ext);

		/*
		 * Mémoire
		 */

		case lui:
			return zybo.getCPU().instruction + " $" + RT + ", " + imm16;

		case lw:
			return zybo.getCPU().instruction + " $" + RT + ", " + imm16 + "($" + RS + ")";

		case sw:
			return zybo.getCPU().instruction + " $" + RT + ", " + imm16 + "($" + RS + ")";

		/*
		 * Branchements
		 */

		case j:
			int imm26 = (IR & 0x03FFFFFF) << 2;
			return zybo.getCPU().instruction + " 0x" + Integer.toHexString(imm26);

		case jr:
			return zybo.getCPU().instruction + " $" + RS;

		case jalr:
			return zybo.getCPU().instruction + " $" + RD + ", $" + RS;

		case beq:
			return zybo.getCPU().instruction + " $" + RS + ", $" + RT + ", " + label;

		case bne:
			return zybo.getCPU().instruction + " $" + RS + ", $" + RT + ", " + label;

		case blez:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case bgtz:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case bltz:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case bltzal:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case bgez:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case bgezal:
			return zybo.getCPU().instruction + " $" + RS + ", " + label;

		case jal:
			return zybo.getCPU().instruction + " " + label;

		default:
			return "init";
		}
	}
}
