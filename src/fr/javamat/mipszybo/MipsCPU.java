package fr.javamat.mipszybo;

import java.util.LinkedHashMap;

public class MipsCPU implements Sync {

	/**
	 * Registres
	 */
	private LinkedHashMap<String, Integer> reg;

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

	/**
	 * CPU MIPS
	 */
	public MipsCPU(Zybo zybo) {
		this.zybo = zybo;
		reg = new LinkedHashMap<String, Integer>();
		state = states.init;

		// Créations des registres
		reg.put("IR", 0);
		reg.put("PC", 0);
		reg.put("AD", 0);
		reg.put("DT", 0);
		for (int i = 0; i < 32; i++) {
			reg.put(i + "", 0);
		}
	}

	@Override
	public void tick() {
		MemoryRAM mem = zybo.getMem();

		int IR = reg.get("IR");
		int RS = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x03E00000)) >> 21);
		int RT = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x001F0000)) >> 16);
		int RD = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x0000F800)) >> 11);

		int imm16 = (int) (Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x0000FFFF));
		int imm16ext = (int) (Integer.toUnsignedLong(imm16)
				+ ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x00008000)) == 0 ? 0 : Integer.toUnsignedLong(0xFFFF0000)));
		int imm16extUp = (int) (Integer.toUnsignedLong(imm16) << 2
				+ ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x00008000)) == 0 ? 0 : Integer.toUnsignedLong(0xFC000000)));
		int imm24 = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x03FFFFFF)) << 2);

		switch (state) {
		case init:
			reg.put("PC", 0);
			state = states.fetch_wait;
			break;

		case fetch_wait:
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case fetch:
			reg.put("IR", mem.getDataIn());
			state = states.decode;
			break;

		case decode:
			reg.put("PC", (int) (Integer.toUnsignedLong(reg.get("PC")) + 4));
			int opcode = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0xFC000000)) >> 26);
			switch (opcode) {
			case 0:
				// Special
				int func = (int) (Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x000003F));
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
				int regimm = (int) ((Integer.toUnsignedLong(IR) & Integer.toUnsignedLong(0x001F0000)) >> 16);
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
				state = states.addi;
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

			break;

		case lui:
			reg.put(RT + "", imm16 << 16);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case ori:
			reg.put(RT + "", reg.get(RS + "") | imm16);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case add:
			reg.put(RD + "", reg.get(RT + "") + reg.get(RS + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case lw:
			reg.put("AD", (int) (Integer.toUnsignedLong(reg.get(RS + "")) + Integer.toUnsignedLong(imm16ext)));
			state = states.load;
			break;

		case load:
			mem.setAddr(reg.get("AD"));
			state = states.mem_to_dt;
			break;

		case mem_to_dt:
			reg.put("DT", mem.getDataIn());
			state = states.dt_to_rt;
			break;

		case dt_to_rt:
			reg.put(RT + "", reg.get("DT"));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case sw:
			reg.put("AD", (int) (Integer.toUnsignedLong(reg.get(RS + "")) + Integer.toUnsignedLong(imm16ext)));
			state = states.store;
			break;

		case store:
			mem.setAddr(reg.get("AD"));
			mem.write(reg.get(RT + ""));
			state = states.fetch_wait;
			break;

		default:
			state = states.init;
			break;
		}
	}
}
