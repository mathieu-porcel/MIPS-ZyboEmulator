package fr.javamat.mipszybo;

import java.util.HashMap;
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

	HashMap<states, Integer> test = new HashMap<states, Integer>();

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

		if (mem.getData()[4100] != 0) {
			System.out.println(mem.getData()[4100]);
			mem.getData()[4100] = 0;
		}

		int IR = reg.get("IR");
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
			// System.out.println((reg.get("PC")-4)/4 + " " + state + " " + Integer.toBinaryString((int) IR) + " RS:" + reg.get(RS + "") + " RT:" +
			// reg.get(RT+"") + " RD:" + reg.get(RD + "") + " SH:" + reg.get(SH + "") + " $0:" + reg.get("0") + " $1:" + reg.get("1") + " $2:" +
			// reg.get("2") + " $3:" + reg.get("3") + " $28:" + reg.get("28") + " $29:" + reg.get("29") + " $30:" + reg.get("30") + " $31:" +
			// reg.get("31"));
		//	test.put(state, test.containsKey(state) ? test.get(state) + 1 : 0);
			//if ((reg.get("PC") - 4) / 4 - 1003 == 0) {
//				 System.out.println(test);
//				 System.exit(0);
			//}
			break;

		/*
		 * Operations
		 */

		case add:
			reg.put(RD + "", reg.get(RT + "") + reg.get(RS + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case addi:
			reg.put(RT + "", reg.get(RS + "") +  imm16ext);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case sub:
			reg.put(RD + "", reg.get(RS + "") - reg.get(RT + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case and:
			reg.put(RD + "", reg.get(RS + "") & reg.get(RT + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case andi:
			reg.put(RT + "", reg.get(RS + "") & imm16);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case or:
			reg.put(RD + "", reg.get(RS + "") | reg.get(RT + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case ori:
			reg.put(RT + "", reg.get(RS + "") | imm16);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case xor:
			reg.put(RD + "", reg.get(RS + "") ^ reg.get(RT + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case xori:
			reg.put(RT + "", reg.get(RS + "") ^ imm16);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case nor:
			reg.put(RD + "", ~(reg.get(RS + "") | reg.get(RT + "")));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case srl:
			reg.put(RD + "", reg.get(RT + "") >>> SH);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case sra:
			reg.put(RD + "", reg.get(RT + "") >> SH);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case sll:
			reg.put(RD + "", reg.get(RT + "") << SH);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case sllv:
			reg.put(RD + "", reg.get(RT + "") << reg.get(RS + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case srlv:
			reg.put(RD + "", reg.get(RT + "") >>> reg.get(RS + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case srav:
			reg.put(RD + "", reg.get(RT + "") >> reg.get(RS + ""));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case slt:
			if (reg.get(RS + "") < reg.get(RT + "")) {
				state = states.true_to_rd;
			} else {
				state = states.false_to_rd;
			}
			break;

		case slti:
			if (reg.get(RS + "") < imm16ext) {
				state = states.true_to_rt;
			} else {
				state = states.false_to_rt;
			}
			break;

		case sltu:
			if (Integer.toUnsignedLong(reg.get(RS + "")) < Integer.toUnsignedLong(reg.get(RT + ""))) {
				state = states.true_to_rd;
			} else {
				state = states.false_to_rd;
			}
			break;

		case sltiu:
			if (Integer.toUnsignedLong(reg.get(RS + "")) < Integer.toUnsignedLong((int) imm16ext)) {
				state = states.true_to_rt;
			} else {
				state = states.false_to_rt;
			}
			break;

		case true_to_rd:
			reg.put(RD + "", 1);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case false_to_rd:
			reg.put(RD + "", 0);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case true_to_rt:
			reg.put(RT + "", 1);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case false_to_rt:
			reg.put(RT + "", 0);
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		/*
		 * Mémoire
		 */

		case lui:
			reg.put(RT + "", (int) (imm16 << 16));
			mem.setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case lw:
			reg.put("AD", (int) (Integer.toUnsignedLong(reg.get(RS + "")) + imm16ext));
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
			reg.put("AD", (int) (Integer.toUnsignedLong(reg.get(RS + "")) + imm16ext));
			state = states.store;
			break;

		case store:
			mem.setAddr(reg.get("AD"));
			mem.write(reg.get(RT + ""));
			// Timer
			if (reg.get("AD") == 0x4010) {
			}
			state = states.fetch_wait;
			break;

		/*
		 * Branchements
		 */

		case j:
			reg.put("PC", (reg.get("PC") & 0xF0000000) | imm26);
			state = states.fetch_wait;
			break;

		case bj:
			reg.put("PC", (int) (Integer.toUnsignedLong(reg.get("PC")) + imm16extUp));
			state = states.fetch_wait;
			break;

		case jr:
			reg.put("PC", reg.get(RS + ""));
			state = states.fetch_wait;
			break;

		case jalr:
			reg.put(RD + "", reg.get("PC"));
			state = states.jr;
			break;

		case beq:
			if (reg.get(RT + "") - reg.get(RS + "") == 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case bne:
			if (reg.get(RT + "") - reg.get(RS + "") != 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case blez:
			if (reg.get(RS + "") <= 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case bgtz:
			if (reg.get(RS + "") > 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case bltz:
			if (reg.get(RS + "") < 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case bltzal:
			reg.put("31", reg.get("PC"));
			state = states.bltz;
			break;

		case bgez:
			if (reg.get(RS + "") >= 0) {
				state = states.bj;
			} else {
				mem.setAddr(reg.get("PC"));
				state = states.fetch;
			}
			break;

		case bgezal:
			reg.put("31", reg.get("PC"));
			state = states.bgez;
			break;

		case jal:
			reg.put("31", reg.get("PC"));
			state = states.j;
			break;

		default:
			state = states.init;
			break;
		}
	}
}
