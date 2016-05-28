package fr.javamat.mipszybo;

import java.util.HashMap;

public class MipsCPU extends Thread {

	/**
	 * Timer
	 */

	public static final int ADDR_TIMER_VALUE = 0x4010;
	public static final int ADDR_TIMER_PERIOD = 0x4010;
	public static final int ADDR_TIMER_SEUIL = 0x4014;

	private int timer;

	/**
	 * Registres
	 */
	public int IR;
	public int PC;
	public int AD;
	public int DT;
	public int[] reg;

	/**
	 * Etats de la partie contrôle
	 */
	private enum states {
		init, fetch_wait, fetch, decode,

		lui, ori, add, addi, and, andi, nor, or, xor, xori, sub, sll, sllv, srl, sra, srav, srlv,

		j, bj, beq, jal, jr, bne, blez, bgtz, bgez, bltz, jalr, bltzal, bgezal,

		lw, sw, slt, slti, sltu, sltiu, mem_to_dt, dt_to_rt, load, store, true_to_rd, false_to_rd, true_to_rt, false_to_rt
	};

	private states state;

	private Zybo zybo;

	HashMap<states, Integer> test = new HashMap<states, Integer>();
	int test2 = 0;

	/**
	 * CPU MIPS
	 */
	public MipsCPU(Zybo zybo) {
		this.zybo = zybo;
		reg = new int[32];
		state = states.init;

		timer = 0;

		// Créations des registres
		IR = 0;
		PC = 0;
		AD = 0;
		DT = 0;
		for (int i = 0; i < 32; i++) {
			reg[i] = 0;
		}
	}

	public void reset() {
		IR = 0;
		PC = 0;
		AD = 0;
		DT = 0;
		for (int i = 0; i < 32; i++) {
			reg[i] = 0;
		}
		state = states.init;
	}

	@Override
	public void run() {
		while (true) {
			MemoryRAM mem = zybo.getRAM();

			int RS = (IR & 0x03E00000) >>> 21;
			int RT = (IR & 0x001F0000) >>> 16;
			int RD = (IR & 0x0000F800) >>> 11;
			int SH = (IR & 0x000007C0) >>> 6;

			int imm16 = IR & 0x0000FFFF;
			int imm16ext = imm16 | ((imm16 & 0x00008000) == 0 ? 0 : 0xFFFF0000);
			int imm16extUp = (imm16 << 2) | ((imm16 & 0x00008000) == 0 ? 0 : 0xFFFC0000);
			int imm26 = (IR & 0x03FFFFFF) << 2;

			switch (state) {
			case init:
				PC = 0;
				state = states.fetch_wait;
				break;

			case fetch_wait:
				state = states.fetch;
				break;

			case fetch:
				IR = mem.read(PC);
				state = states.decode;
				break;

			case decode:
				PC = (int) (Integer.toUnsignedLong(PC) + 4);
				int opcode = (IR & 0xFC000000) >>> 26;
				switch (opcode) {
				case 0:
					// Special
					int func = IR & 0x000003F;
					switch (func) {
					case 0:
						state = states.sll;
						break;
					case 2:
						state = states.srl;
						break;
					case 3:
						state = states.sra;
						break;
					case 4:
						state = states.sllv;
						break;
					case 6:
						state = states.srlv;
						break;
					case 7:
						state = states.srav;
						break;
					case 8:
						state = states.jr;
						break;
					case 9:
						state = states.jalr;
						break;
					case 32:
						state = states.add;
						break;
					case 33:
						state = states.add; // ADDU
						break;
					case 34:
						state = states.sub;
						break;
					case 35:
						state = states.sub; // SUBU
						break;
					case 36:
						state = states.and;
						break;
					case 37:
						state = states.or;
						break;
					case 38:
						state = states.xor;
						break;
					case 39:
						state = states.nor;
						break;
					case 42:
						state = states.slt;
						break;
					case 43:
						state = states.sltu;
						break;
					default:
						state = states.init;
						break;
					}
					break;
				case 1:
					// Regimm
					int regimm = (IR & 0x001F0000) >>> 16;
					switch (regimm) {
					case 0:
						state = states.bltz;
						break;
					case 1:
						state = states.bgez;
						break;
					case 8:
						state = states.bltzal;
						break;
					case 9:
						state = states.bgezal;
						break;
					default:
						state = states.init;
						break;
					}
					break;
				case 2:
					state = states.j;
					break;
				case 3:
					state = states.jal;
					break;
				case 4:
					state = states.beq;
					break;
				case 5:
					state = states.bne;
					break;
				case 6:
					state = states.blez;
					break;
				case 7:
					state = states.bgtz;
					break;
				case 8:
					state = states.addi;
					break;
				case 9:
					state = states.addi; // addiu
					break;
				case 10:
					state = states.slti;
					break;
				case 11:
					state = states.sltiu;
					break;
				case 12:
					state = states.andi;
					break;
				case 13:
					state = states.ori;
					break;
				case 14:
					state = states.xori;
					break;
				case 15:
					state = states.lui;
					break;
				case 35:
					state = states.lw;
					break;
				case 43:
					state = states.sw;
					break;
				default:
					state = states.init;
					break;
				}

				// if(state == states.jal){
				// test2++;
				// }
				//
				// if(test2 > 3){
				// test.put(state, test.containsKey(state) ? test.get(state) + 1 : 0);
				// System.out.println((PC - 4) / 4 + " " + state + " " + Integer.toBinaryString((int) IR) + " RS:" + reg[RS] + " RT:" + reg[RT] + "
				// RD:"
				// + reg[RD] + " SH:" + reg[SH] + " $0:" + reg[0] + " $1:" + reg[1] + " $2:" + reg[2] + " $3:" + reg[3] + " $28:" + reg[28]
				// + " $29:" + reg[29] + " $30:" + reg[30] + " $31:" + reg[31]);
				// }
				//
				// if ((PC - 4) / 4 - 1003 == 0) {
				// System.out.println(test);
				// System.exit(0);
				// }
				break;

			/*
			 * Operations
			 */

			case add:
				reg[RD] = reg[RT] + reg[RS];
				state = states.fetch;
				break;

			case addi:
				reg[RT] = imm16ext + reg[RS];
				state = states.fetch;
				break;

			case sub:
				reg[RD] = reg[RS] - reg[RT];
				state = states.fetch;
				break;

			case and:
				reg[RD] = reg[RS] & reg[RT];
				state = states.fetch;
				break;

			case andi:
				reg[RT] = reg[RS] & imm16;
				state = states.fetch;
				break;

			case or:
				reg[RD] = reg[RS] | reg[RT];
				state = states.fetch;
				break;

			case ori:
				reg[RT] = reg[RS] | imm16;
				state = states.fetch;
				break;

			case xor:
				reg[RD] = reg[RS] ^ reg[RT];
				state = states.fetch;
				break;

			case xori:
				reg[RT] = reg[RS] ^ imm16;
				state = states.fetch;
				break;

			case nor:
				reg[RD] = ~(reg[RS] | reg[RT]);
				state = states.fetch;
				break;

			case srl:
				reg[RD] = reg[RT] >>> SH;
				state = states.fetch;
				break;

			case sra:
				reg[RD] = reg[RT] >> SH;
				state = states.fetch;
				break;

			case sll:
				reg[RD] = reg[RT] << SH;
				state = states.fetch;
				break;

			case sllv:
				reg[RD] = reg[RT] << reg[RS];
				state = states.fetch;
				break;

			case srlv:
				reg[RD] = reg[RT] >>> reg[RS];
				state = states.fetch;
				break;

			case srav:
				reg[RD] = reg[RT] >> reg[RS];
				state = states.fetch;
				break;

			case slt:
				if (reg[RS] < reg[RT]) {
					state = states.true_to_rd;
				} else {
					state = states.false_to_rd;
				}
				break;

			case slti:
				if (reg[RS] < imm16ext) {
					state = states.true_to_rt;
				} else {
					state = states.false_to_rt;
				}
				break;

			case sltu:
				if (Integer.toUnsignedLong(reg[RS]) < Integer.toUnsignedLong(reg[RT])) {
					state = states.true_to_rd;
				} else {
					state = states.false_to_rd;
				}
				break;

			case sltiu:
				if (Integer.toUnsignedLong(reg[RS]) < Integer.toUnsignedLong(imm16ext)) {
					state = states.true_to_rt;
				} else {
					state = states.false_to_rt;
				}
				break;

			case true_to_rd:
				reg[RD] = 1;
				state = states.fetch;
				break;

			case false_to_rd:
				reg[RD] = 0;
				state = states.fetch;
				break;

			case true_to_rt:
				reg[RT] = 1;
				state = states.fetch;
				break;

			case false_to_rt:
				reg[RT] = 0;
				state = states.fetch;
				break;

			/*
			 * Mémoire
			 */

			case lui:
				reg[RT] = (int) (imm16 << 16);
				state = states.fetch;
				break;

			case lw:
				AD = (int) (Integer.toUnsignedLong(reg[RS]) + imm16ext);
				state = states.load;
				break;

			case load:
				state = states.mem_to_dt;
				break;

			case mem_to_dt:
				if (AD == ADDR_TIMER_VALUE) {
					// Timer
					DT = timer > mem.read(0x4014) ? 1 : 0;
				} else {
					DT = mem.read(AD);
				}
				state = states.dt_to_rt;
				break;

			case dt_to_rt:
				reg[RT] = DT;
				state = states.fetch;
				break;

			case sw:
				AD = (int) (Integer.toUnsignedLong(reg[RS]) + imm16ext);
				state = states.store;
				break;

			case store:
				mem.write(AD, reg[RT]);
				state = states.fetch_wait;
				break;

			/*
			 * Branchements
			 */

			case j:
				PC = (PC & 0xF0000000) | imm26;
				state = states.fetch_wait;
				break;

			case bj:
				PC = (int) (Integer.toUnsignedLong(PC) + imm16extUp);
				state = states.fetch_wait;
				break;

			case jr:
				PC = reg[RS];
				state = states.fetch_wait;
				break;

			case jalr:
				reg[RD] = PC;
				state = states.jr;
				break;

			case beq:
				if (reg[RT] - reg[RS] == 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case bne:
				if (reg[RT] - reg[RS] != 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case blez:
				if (reg[RS] <= 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case bgtz:
				if (reg[RS] > 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case bltz:
				if (reg[RS] < 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case bltzal:
				reg[31] = PC;
				state = states.bltz;
				break;

			case bgez:
				if (reg[RS] >= 0) {
					state = states.bj;
				} else {
					state = states.fetch;
				}
				break;

			case bgezal:
				reg[31] = PC;
				state = states.bgez;
				break;

			case jal:
				reg[31] = PC;
				state = states.j;
				break;

			default:
				state = states.init;
				break;
			}

			// Timer
			if (timer >= mem.read(ADDR_TIMER_PERIOD)) {
				timer = 0;
			} else {
				timer++;
			}
		}
	}
}
