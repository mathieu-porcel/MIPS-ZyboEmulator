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
		switch (addr % 4) {
		case 0:
			ram[addr / 4] = data;
			break;
		case 1:
			ram[addr / 4] = (ram[addr / 4] & 0xFF000000) | (data >>> 8);
			ram[addr / 4 + 1] = (ram[addr / 4 + 1] & 0x00FFFFFF) | ((data & 0x000000FF) << 24);
			break;
		case 2:
			ram[addr / 4] = (ram[addr / 4] & 0xFFFF0000) | (data >>> 16);
			ram[addr / 4 + 1] = (ram[addr / 4 + 1] & 0x0000FFFF) | ((data & 0x0000FFFF) << 16);
			break;
		case 3:
			ram[addr / 4] = (ram[addr / 4] & 0xFFFFFF00) | (data >>> 24);
			ram[addr / 4 + 1] = (ram[addr / 4 + 1] & 0x000000FF) | ((data & 0x00FFFFFF) << 8);
			break;
		}
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int[] getData() {
		return ram;
	}

	public int getDataAt(int addr) {
		int d = 0;
		switch (addr % 4) {
		case 0:
			d = ram[addr / 4];
			break;
		case 1:
			d = (ram[addr / 4] << 8) + (ram[addr / 4 + 1] >>> 24);
			break;
		case 2:
			d = (ram[addr / 4] << 16) + (ram[addr / 4 + 1] >>> 16);
			break;
		case 3:
			d = (ram[addr / 4] << 24) + (ram[addr / 4 + 1] >>> 8);
			break;
		}
		return d;
	}
}
