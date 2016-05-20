package fr.javamat.mipszybo;

public class Zybo {
	public static final int VGA_WIDTH = 320;
	public static final int VGA_HEIGHT = 240;

	private MipsCPU cpu;
	private MemoryRAM memory;

	public Zybo() {
		cpu = new MipsCPU(this);
		memory = new MemoryRAM();

		Clock clock = new Clock();
		clock.addSyncListener(cpu);
		clock.addSyncListener(memory);
		clock.start();
	}

	public MemoryRAM getMem() {
		return memory;
	}
}
