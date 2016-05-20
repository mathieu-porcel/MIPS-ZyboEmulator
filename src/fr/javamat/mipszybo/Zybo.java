package fr.javamat.mipszybo;

public class Zybo {
	public static final int VGA_WIDTH = 320;
	public static final int VGA_HEIGHT = 240;

	public static final int MEMORY_SIZE = 240;

	private int[] memory;

	public Zybo() {
		memory = new int[MEMORY_SIZE];
	}

	public int[] getMemory() {
		return memory;
	}
}
