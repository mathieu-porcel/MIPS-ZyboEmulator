package fr.javamat.mipszybo;

public class Zybo {
	public static final int VGA_WIDTH = 320;
	public static final int VGA_HEIGHT = 240;

	private MipsCPU cpu;
	private MemoryRAM ram;

	public Zybo() {
		cpu = new MipsCPU(this);
		ram = new MemoryRAM();
	}

	public MemoryRAM getRAM() {
		return ram;
	}

	public MipsCPU getCPU() {
		return cpu;
	}
}
