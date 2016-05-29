package fr.javamat.mipszybo;

public class MemoryRAM {

	/**
	 * Taille de la mémoire en octets
	 */
	public static final int MEMORY_SIZE = 0x000FFFFF;

	public int[] ram;

	/**
	 * Mémoire RAM pour les programmes et données
	 */
	public MemoryRAM() {
		ram = new int[MEMORY_SIZE];
	}

	public void write(int addr, int data) {
		addr = (0x7FFFFFFF & addr) % MEMORY_SIZE;
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

	public int read(int addr) {
		addr = (0x7FFFFFFF & addr) % MEMORY_SIZE;
		switch (addr % 4) {
		case 0:
			return ram[addr / 4];
		case 1:
			return (ram[addr / 4] << 8) + (ram[addr / 4 + 1] >>> 24);
		case 2:
			return (ram[addr / 4] << 16) + (ram[addr / 4 + 1] >>> 16);
		case 3:
			return (ram[addr / 4] << 24) + (ram[addr / 4 + 1] >>> 8);
		}
		return 0;
	}
}
