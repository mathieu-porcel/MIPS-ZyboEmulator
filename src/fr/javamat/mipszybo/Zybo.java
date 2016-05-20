package fr.javamat.mipszybo;

public class Zybo {
	public static final int VGA_WIDTH = 320;
	public static final int VGA_HEIGHT = 240;

	private MemoryRAM memory;

	public Zybo() {
		memory = new MemoryRAM();
	}

	public MemoryRAM getMemory() {
		return memory;
	}
}
