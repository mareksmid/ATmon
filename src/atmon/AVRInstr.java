package atmon;

public class AVRInstr {
	
	private String mnem, attrSuffix;
	private int ocVal, ocMask;
	private AVRInstrType type;

	public AVRInstr(String mnemonic, String attributesSuffix, int opcodeVal, int opcodeMask, AVRInstrType type) {
		mnem = mnemonic;
		attrSuffix = attributesSuffix;
		ocVal = opcodeVal;
		ocMask = opcodeMask;
		this.type = type;
	}

	public AVRInstr(String mnemonic, int opcodeVal, int opcodeMask, AVRInstrType type) {
		mnem = mnemonic;
		attrSuffix = null;
		ocVal = opcodeVal;
		ocMask = opcodeMask;
		this.type = type;
	}
	
	public boolean matches(int instr) {
		return (instr & ocMask) == ocVal;
	}
	
	public String toString() {return mnem;}	
	public String getMnemonic() {return mnem;}
	
	public AVRInstrType getType() {return type;}
	
	public String getAttributesSuffix() {return attrSuffix;}
	
}
