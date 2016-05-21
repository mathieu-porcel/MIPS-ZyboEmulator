package fr.javamat.mipszybo;

public class MemoryRAM {

	/**
	 * Taille de la mémoire en octets
	 */
	public static final int MEMORY_SIZE = 1000000;

	private int[] ram;

	private int addr;

	/**
	 * Mémoire RAM pour les programmes et données
	 */
	public MemoryRAM() {
		ram = new int[MEMORY_SIZE];
	}

	public int getDataIn() {
		return getDataAt(addr);
	}

	public void write(int data) {
		ram[addr / 4] = data;
		// ram[addr + 0] = ((dataOut & 0x000000FF) >> 0);
		// ram[addr + 1] = ((dataOut & 0x0000FF00) >> 8);
		// ram[addr + 2] = ((dataOut & 0x00FF0000) >> 16);
		// ram[addr + 3] = ((dataOut & 0xFF000000) >> 24);
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int[] getData() {
		return ram;
	}

	public int getDataAt(int addr) {
		int d = 0;
		// d += (ram[addr + 0] << 0);
		// d += (ram[addr + 1] << 8);
		// d += (ram[addr + 2] << 16);
		// d += (ram[addr + 3] << 24);
		d = ram[addr / 4];
		return d;
	}
}
