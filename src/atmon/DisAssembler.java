package atmon;

public class DisAssembler {

	private Comm comm;
	private AVRAsm asm;
	private MainFrame mf;
	
	public DisAssembler(MainFrame mf, Comm comm) {
		this.mf = mf;
		this.comm = comm;
		asm = new AVRAsm();
	}
	
	public String readInstr(int addr) {
		byte[] flash = comm.readFlash(addr*2, AVRInstrTypes.MAX_WORD_CNT*2);
		if ((flash == null) || (flash.length != AVRInstrTypes.MAX_WORD_CNT*2)) {
			System.err.println("Reached: reading instruction failed");
			return "[---]";
		}		
		return decodeInstr(flash, 0, addr);
	}


	public String decodeInstr(byte[] buf, int off, int addr) {
		if (buf.length < off+2) {return "[error]";}
//		return Utils.wordToHex(opcode);
		int opcode = Utils.ub(buf[off]) | (Utils.ub(buf[off+1]) << 8);
		AVRInstr instr = asm.getInstrByOpcode(opcode);
		if (instr == null) {return "[opcode not recognized]";}
		int wc = instr.getType().getWordCnt();
		if (buf.length < off+(wc*2)) {return "[not enough data for attributes]";}		
		return instr.getMnemonic() + instr.getType().getAttribs(mf, instr, addr+off/2, buf, off); 
	}
}
