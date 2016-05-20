package fr.javamat.mipszybo;

import java.util.HashMap;

public class MipsCPU implements Sync {

	/**
	 * Registres
	 */
	private HashMap<String, Integer> reg;

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
		reg = new HashMap<String, Integer>();
		state = states.init;
	}

	@Override
	public void tick() {
		switch (state) {
		case init:
			reg.put("PC", 0);
			state = states.fetch_wait;
			break;

		case fetch_wait:
			zybo.getMem().setAddr(reg.get("PC"));
			state = states.fetch;
			break;

		case fetch:
			reg.put("IR", zybo.getMem().getDataIn());
			state = states.decode;
			break;

		case decode:
			int opcode = (reg.get("IR") & 0xFC00000) >> 26;
			switch (opcode) {
			case 0:
				// Special
				int func = (reg.get("IR") & 0x000003F);
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
				int regimm = (reg.get("IR") & 0x001F0000) >> 16;
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

		case lw:
			break;

		default:
			state = states.init;
			break;
		}
	}
}
