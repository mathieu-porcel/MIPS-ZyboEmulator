package fr.javamat.mipszybo;

public class MemoryRAM implements Sync {

	/**
	 * Taille de la m�moire en octets
	 */
	public static final int MEMORY_SIZE = 8192;

	private int[] ram;

	private int addr;
	private int dataIn;
	private int dataOut;
	private boolean memWE;

	/**
	 * M�moire RAM pour les programmes et donn�es
	 */
	public MemoryRAM() {
		ram = new int[MEMORY_SIZE / 4];
	}

	public void tick() {
		dataIn = ram[addr];
		if (memWE) {
			ram[addr] = dataOut;
			memWE = false;
		}
	}

	/**
	 * Ecrit le contenu de dataOut � l'adresse addr au prochain coup d'horloge
	 */
	public void write() {
		memWE = true;
	}

	public int getDataIn() {
		return dataIn;
	}

	public void setDataOut(int dataOut) {
		this.dataOut = dataOut;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int[] getRAM() {
		return ram;
	}
}
