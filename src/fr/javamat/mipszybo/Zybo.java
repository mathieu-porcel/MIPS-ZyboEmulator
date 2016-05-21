package fr.javamat.mipszybo;

public class Zybo {
	public static final int VGA_WIDTH = 320;
	public static final int VGA_HEIGHT = 240;

	private MipsCPU cpu;
	private MemoryRAM memory;
	private Clock clock;

	public Zybo() {
		cpu = new MipsCPU(this);
		memory = new MemoryRAM();

		clock = new Clock();
		clock.addSyncListener(cpu);
	}

	public MemoryRAM getMem() {
		return memory;
	}

	public Clock getClock() {
		return clock;
	}
}
