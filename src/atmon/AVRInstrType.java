package atmon;

public abstract class AVRInstrType {
	
	abstract public int getWordCnt();
	
	abstract public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off);
	
}

