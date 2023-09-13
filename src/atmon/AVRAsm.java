package atmon;

public class AVRAsm {

	private AVRInstr adcI  = new AVRInstr("adc",    0x1C00, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr addI  = new AVRInstr("add",    0x0C00, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr adiwI = new AVRInstr("adiw",   0x9600, 0xFF00, AVRInstrTypes.RDLLD_KL);
	private AVRInstr andI  = new AVRInstr("and",    0x2000, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr andiI = new AVRInstr("andi",   0x7000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr asrI  = new AVRInstr("asr",    0x9405, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr bclrI = new AVRInstr("bclr",   0x9488, 0xFF8F, AVRInstrTypes.SBIT);
	private AVRInstr bldI  = new AVRInstr("bld",    0xF800, 0xFE08, AVRInstrTypes.RD_BIT);
	//private AVRInstr brbcI = new AVRInstr("brbc",   0xF400, 0xFC00, AVRInstrTypes.K_SBIT);
	//private AVRInstr brbsI = new AVRInstr("brbs",   0xF000, 0xFC00, AVRInstrTypes.K_SBIT);
	private AVRInstr breakI= new AVRInstr("break",  0x9598, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr breqI = new AVRInstr("breq",   0xF001, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brgeI = new AVRInstr("brge",   0xF404, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brhcI = new AVRInstr("brhc",   0xF405, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brhsI = new AVRInstr("brhs",   0xF005, 0xFC07, AVRInstrTypes.K);
	private AVRInstr bridI = new AVRInstr("brid",   0xF407, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brieI = new AVRInstr("brie",   0xF007, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brloI = new AVRInstr("brlo",   0xF000, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brltI = new AVRInstr("brlt",   0xF004, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brmiI = new AVRInstr("brmi",   0xF002, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brneI = new AVRInstr("brne",   0xF401, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brplI = new AVRInstr("brpl",   0xF402, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brshI = new AVRInstr("brsh",   0xF400, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brtcI = new AVRInstr("brtc",   0xF406, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brtsI = new AVRInstr("brts",   0xF006, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brvcI = new AVRInstr("brvc",   0xF403, 0xFC07, AVRInstrTypes.K);
	private AVRInstr brvsI = new AVRInstr("brvs",   0xF003, 0xFC07, AVRInstrTypes.K);
	private AVRInstr bsetI = new AVRInstr("bset",   0x9408, 0xFF8F, AVRInstrTypes.SBIT);
	private AVRInstr bstI  = new AVRInstr("bst",    0xFA00, 0xFE08, AVRInstrTypes.RD_BIT);
	private AVRInstr callI = new AVRInstr("call",   0x940E, 0xFE0E, AVRInstrTypes.LK);
	private AVRInstr cbiI  = new AVRInstr("cbi",    0x980E, 0xFF00, AVRInstrTypes.IO_BIT);
	private AVRInstr comI  = new AVRInstr("com",    0x9400, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr cpI   = new AVRInstr("cp",     0x1400, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr cpcI  = new AVRInstr("cpc",    0x0400, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr cpiI  = new AVRInstr("cpi",    0x3000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr cpseI = new AVRInstr("cpse",   0x1000, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr decI  = new AVRInstr("dec",    0x940A, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr eicallI=new AVRInstr("eicall", 0x9519, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr eijmpI= new AVRInstr("eijmp",  0x9419, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr elpm1I= new AVRInstr("elpm",   0x95D8, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr elpm2I= new AVRInstr("elpm","Z",0x9006,0xFE0F, AVRInstrTypes.RD);
	private AVRInstr elpm3I= new AVRInstr("elpm","Z+",0x9007,0xFE0F,AVRInstrTypes.RD);
	private AVRInstr eorI  = new AVRInstr("eor",    0x2400, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr fmulI = new AVRInstr("fmul",   0x0308, 0xFF88, AVRInstrTypes.RDLL_RRLL);
	private AVRInstr fmulsI= new AVRInstr("fmuls",  0x0380, 0xFF88, AVRInstrTypes.RDLL_RRLL);
	private AVRInstr fmulsuI=new AVRInstr("fmulsu", 0x0388, 0xFF88, AVRInstrTypes.RDLL_RRLL);
	private AVRInstr icallI= new AVRInstr("icall",  0x9509, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr ijmpI = new AVRInstr("ijmp",   0x9409, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr inI   = new AVRInstr("in",     0xB000, 0xF800, AVRInstrTypes.RD_IO);
	private AVRInstr incI  = new AVRInstr("inc",    0x9403, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr jmpI  = new AVRInstr("jmp",    0x940C, 0xFE0E, AVRInstrTypes.LK);
	private AVRInstr ldx1I = new AVRInstr("ld","X", 0x900C, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldx2I = new AVRInstr("ld","X+",0x900D, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldx3I = new AVRInstr("ld","-X",0x900E, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldy1I = new AVRInstr("ld","Y", 0x8008, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldy2I = new AVRInstr("ld","Y+",0x9009, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldy3I = new AVRInstr("ld","-Y",0x900A, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldy4I = new AVRInstr("ldd","Y+",0x8008,0xD208, AVRInstrTypes.RD_Q);
	private AVRInstr ldz1I = new AVRInstr("ld","Z", 0x8000, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldz2I = new AVRInstr("ld","Z+",0x9001, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldz3I = new AVRInstr("ld","-Z",0x9002, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr ldz4I = new AVRInstr("ldd","Z+",0x8000,0xD208, AVRInstrTypes.RD_Q);
	private AVRInstr ldiI  = new AVRInstr("ldi",    0xE000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr ldsI  = new AVRInstr("lds",    0x9000, 0xFE0F, AVRInstrTypes.RD_LK);
	private AVRInstr lpm1I = new AVRInstr("lpm",    0x95C8, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr lpm2I = new AVRInstr("lpm","Z",0x9004, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr lpm3I = new AVRInstr("lpm","Z+",0x9005,0xFE0F, AVRInstrTypes.RD);
	private AVRInstr lsrI  = new AVRInstr("lsr",    0x9406, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr movI  = new AVRInstr("mov",    0x2C00, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr movwI = new AVRInstr("movw",   0x0100, 0xFF00, AVRInstrTypes.RDL_RRL_D);
	private AVRInstr mulI  = new AVRInstr("mul",    0x9C00, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr mulsI = new AVRInstr("muls",   0x0200, 0xFF00, AVRInstrTypes.RDL_RRL);
	private AVRInstr mulsuI= new AVRInstr("mulsu",  0x0300, 0xFF88, AVRInstrTypes.RDLL_RRLL);
	private AVRInstr negI  = new AVRInstr("neg",    0x9401, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr nopI  = new AVRInstr("nop",    0x0000, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr orI   = new AVRInstr("or",     0x2800, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr oriI  = new AVRInstr("ori",    0x6000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr outI  = new AVRInstr("out",    0xB800, 0xF800, AVRInstrTypes.IO_RR);
	private AVRInstr popI  = new AVRInstr("pop",    0x900F, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr pushI = new AVRInstr("push",   0x920F, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr rcallI= new AVRInstr("rcall",  0xD000, 0xF000, AVRInstrTypes.K_2);
	private AVRInstr retI  = new AVRInstr("ret",    0x9508, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr retiI = new AVRInstr("reti",   0x9518, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr rjmpI = new AVRInstr("rjmp",   0xC000, 0xF000, AVRInstrTypes.K_2);
	private AVRInstr rorI  = new AVRInstr("ror",    0x9407, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr sbcI  = new AVRInstr("sbc",    0x0800, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr sbciI = new AVRInstr("sbci",   0x4000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr sbiI  = new AVRInstr("sbi",    0x9A00, 0xFF00, AVRInstrTypes.IO_BIT);
	private AVRInstr sbicI = new AVRInstr("sbic",   0x9900, 0xFF00, AVRInstrTypes.IO_BIT);
	private AVRInstr sbisI = new AVRInstr("sbis",   0x9B00, 0xFF00, AVRInstrTypes.IO_BIT);
	private AVRInstr sbiwI = new AVRInstr("sbiw",   0x9700, 0xFF00, AVRInstrTypes.RDLLD_KL);
	//private AVRInstr sbrI  = new AVRInstr("sbr",    0x6000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr sbrcI = new AVRInstr("sbrc",   0xFC00, 0xFE08, AVRInstrTypes.RD_BIT);
	private AVRInstr sbrsI = new AVRInstr("sbrs",   0xFE00, 0xFE08, AVRInstrTypes.RD_BIT);
	//private AVRInstr serI  = new AVRInstr("ser",    0xEF0F, 0xFF0F, AVRInstrTypes.RDL);
	private AVRInstr sleepI= new AVRInstr("sleep",  0x9588, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr spmI  = new AVRInstr("spm",    0x95E8, 0xFFFF, AVRInstrTypes.NONE);
	private AVRInstr stx1I = new AVRInstr("st X,",  0x920C, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr stx2I = new AVRInstr("st X+,", 0x920D, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr stx3I = new AVRInstr("st -X,", 0x920E, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr sty1I = new AVRInstr("st Y,",  0x8208, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr sty2I = new AVRInstr("st Y+,", 0x9209, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr sty3I = new AVRInstr("st -Y,", 0x920A, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr sty4I = new AVRInstr("std Y+", 0x8208, 0xD208, AVRInstrTypes.Q_RR);
	private AVRInstr stz1I = new AVRInstr("st Z,",  0x8200, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr stz2I = new AVRInstr("st Z+,", 0x9201, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr stz3I = new AVRInstr("st -Z,", 0x9202, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr stz4I = new AVRInstr("std Z+", 0x8200, 0xD208, AVRInstrTypes.Q_RR);
	private AVRInstr stsI  = new AVRInstr("sts",    0x9200, 0xFE0F, AVRInstrTypes.LK_RR);
	private AVRInstr subI  = new AVRInstr("sub",    0x1800, 0xFC00, AVRInstrTypes.RD_RR);
	private AVRInstr subiI = new AVRInstr("subi",   0x5000, 0xF000, AVRInstrTypes.RDL_K);
	private AVRInstr swapI = new AVRInstr("swap",   0x9402, 0xFE0F, AVRInstrTypes.RD);
	private AVRInstr wdrI  = new AVRInstr("wdr",    0x95A8, 0xFFFF, AVRInstrTypes.NONE);
	
	private AVRInstr[] instrs = {
			adcI, addI, adiwI, andI, andiI, asrI, bclrI, bldI, /*brbcI, brbsI,*/ breakI,
			breqI, brgeI, brhcI, brhsI, bridI, brieI, brloI, brltI, brmiI, brneI, brplI, brshI, brtcI, brtsI, brvcI, brvsI,
			bsetI, bstI, callI, cbiI, comI, cpI, cpcI, cpiI, cpseI, decI, eicallI, eijmpI, elpm1I, elpm2I, elpm3I, eorI,
			fmulI, fmulsI, fmulsuI, icallI, ijmpI, inI, incI, jmpI, ldx1I, ldx2I, ldx3I, ldy1I, ldy2I, ldy3I, ldy4I,
			ldz1I, ldz2I, ldz3I, ldz4I, ldiI, ldsI, lpm1I, lpm2I, lpm3I,
			lsrI, movI, movwI, mulI, mulsI, mulsuI, negI, nopI, orI, oriI, outI, popI, pushI, rcallI, retI, retiI,
			rjmpI, rorI, sbcI, sbciI, sbiI, sbicI, sbisI, sbiwI, /*sbrI,*/ sbrcI, sbrsI, /*serI,*/
			sleepI, spmI, stx1I, stx2I, stx3I, sty1I, sty2I, sty3I, sty4I, stz1I, stz2I, stz3I, stz4I, stsI,
			subI, subiI, swapI, wdrI
	};
	
	public AVRAsm() {
		/*
		//AVRInstr instr;
		int cnt;
		for (int i = 0; i < 0x10000; i++) {
			cnt = getInstrCntByOpcode(i);
			if (cnt == 0) {
				System.out.println("N " + Utils.wordToHex(i));
			} else if (cnt == 1) {
				//System.out.print(".");
			} else {
				//System.out.println("D" + i + ": " + getInstrByOpcode(i).getMnemonic());
				//System.out.println("D" + i + ":");
				//printInstrsByOpcode(i);
			}
			//instr = getInstrByOpcode(i);
		}
		*/
	}
	
	public AVRInstr getInstrByOpcode(int opcode) {
		for (int i = 0; i < instrs.length; i++) {
			if (instrs[i].matches(opcode)) {return instrs[i];}			
		}
		return null;
	}

	/*private int getInstrCntByOpcode(int opcode) {
		int cnt = 0;
		for (int i = 0; i < instrs.length; i++) {
			if (instrs[i].matches(opcode)) {cnt++;}			
		}
		return cnt;
	}*/

	/*private void printInstrsByOpcode(int opcode) {
		for (int i = 0; i < instrs.length; i++) {
			if (instrs[i].matches(opcode)) {
				System.out.println(">" + i + ": " + instrs[i].getMnemonic());				
			}			
		}
	}*/

}
