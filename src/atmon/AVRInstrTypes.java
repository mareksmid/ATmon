package atmon;

public class AVRInstrTypes {
	/*
	public int RD_RR  = 0;
	public int RDLL_K = 1;
	public int RDL_K  = 2;
	public int RD     = 3;
	public int SBIT   = 4;
	public int RD_BIT = 5;
	public int K_SBIT = 6;
	public int NONE   = 7;
	public int K      = 8;
	public int LK     = 9;
	public int IO_BIT = 10;
	public int RDL_RRL= 11;
	public int IO_RD  = 12;
	public int RD_LK  = 13;
	public int RDL_RRL_2=14;
	public int K_2    = 15;
	public int RDL    = 16;
	*/
		
	public static final int MAX_WORD_CNT = 2;
	private static final char[] SREG_BITS = {'C', 'Z', 'N', 'V', 'S', 'H', 'T', 'I'};
	private static final String[] IO_REGS =
	   {"TWBR", "TWSR", "TWAR", "TWDR", "ADCL", "ADCH", "ADCSRA", "ADMUX", "ACSR", "UBRRL", "UCSRB", "UCSRA", "UDR", "SPCR", "SPSR", "SPDR",
		"PIND", "DDRD", "PORTD", "PINC", "DDRC", "PORTC", "PINB", "DDRB", "PORTB", "PINA", "DDRA", "PORTA", "EECR", "EEDR", "EEARL", "EEARH",
		"UBRRH", "WDTCR", "ASSR", "OCR2", "TCNT2", "TCCR2", "ICR1L", "ICR1H", "OCR1BL", "OCR1BH", "OCR1AL", "OCR1AH", "TCNT1L", "TCNT1H", "TCCR1B", "TCCR1A",
		"SFIOR", "OSCCAL", "TCNT0", "TCCR0", "MCUCSR", "MCUCR", "TWCR", "SPMCR", "TIFR", "TIMSK", "GIFR", "GICR", "OCR0", "SPL", "SPH", "SREG"};
	
	public static AVRInstrType RD_RR     = new AIT_RD_RR(); 
	public static AVRInstrType RDLLD_KL  = new AIT_RDLLD_KL(); 
	public static AVRInstrType RDL_K     = new AIT_RDL_K(); 
	public static AVRInstrType RD        = new AIT_RD(); 
	public static AVRInstrType SBIT      = new AIT_SBIT(); 
	public static AVRInstrType RD_BIT    = new AIT_RD_BIT(); 
	//public static AVRInstrType K_SBIT    = new AIT_K_SBIT(); 
	public static AVRInstrType NONE      = new AIT_NONE(); 
	public static AVRInstrType K         = new AIT_K(); 
	public static AVRInstrType LK        = new AIT_LK(); 
	public static AVRInstrType IO_BIT    = new AIT_IO_BIT(); 
	public static AVRInstrType RDL_RRL   = new AIT_RDL_RRL(); 
	public static AVRInstrType RDLL_RRLL = new AIT_RDLL_RRLL(); 
	public static AVRInstrType RD_IO     = new AIT_RD_IO(); 
	public static AVRInstrType IO_RR     = new AIT_IO_RR(); 
	public static AVRInstrType RD_LK     = new AIT_RD_LK(); 
	public static AVRInstrType LK_RR     = new AIT_LK_RR(); 
	public static AVRInstrType RDL_RRL_D = new AIT_RDL_RRL_D(); 
	public static AVRInstrType K_2       = new AIT_K_2(); 
	//public static AVRInstrType RDL       = new AIT_RDL(); 
	public static AVRInstrType RD_Q      = new AIT_RD_Q(); 
	public static AVRInstrType Q_RR      = new AIT_Q_RR(); 

	// adc, add, and, cp, cpc, cpse, eor, mov, mul, or, sbc, sub
	private static class AIT_RD_RR extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int rr = ((buf[off+1] & 0x02) << 3) | (buf[off] & 0x0F);
			return " r"+rd+",r"+rr;
		}		
	}

	// adiw, sbiw
	private static class AIT_RDLLD_KL extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0xF0) >>> 4;
			int k = ((buf[off] & 0xC0) >>> 2) | (buf[off] & 0x0F);
			rd = (rd*2)+24;
			return " r"+(rd+1)+":r"+rd+", "+k;
		}		
	}

	// andi, cpi, ldi, ori, sbci, subi
	private static class AIT_RDL_K extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0xF0) >>> 4;
			int k = ((buf[off+1] & 0x0F) << 4) | (buf[off] & 0x0F);
			rd |= 0x10;
			return " r"+rd+", "+k;
		}		
	}

	// asr, com, dec, elpm Z, elpm Z+, inc, ld ..., lpm Z, lpm Z+, pop, push, ror, st ..., swap 
	private static class AIT_RD extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			if (instr.getAttributesSuffix() == null) {return " r"+rd;}
			else {return " r"+rd+", "+instr.getAttributesSuffix();}
		}		
	}
	
	// bclr, bset
	private static class AIT_SBIT extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int sbit = (buf[off] & 0x70) >>> 4;
			return " "+SREG_BITS[sbit];
		}		
	}

	// bld, bst, sbrc, sbrs
	private static class AIT_RD_BIT extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int bit = buf[off] & 0x07;
			return " r"+rd+", "+bit;
		}		
	}
	
	// brbc, brbs
	/*private static class AIT_K_SBIT extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int k = ((buf[off+1] & 0x03) << 5) | ((buf[off] & 0xF8) >>> 3);
			int sbit = buf[off] & 0x07;
			return " "+SREG_BITS[sbit]+", "+k;
		}		
	}*/

	// break, eicall, eijmp, elpm, icall, ijmp, lpm, nop, ret, reti, sleep, spm, wdr
	private static class AIT_NONE extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			return "";
		}		
	}

	// breq, brge, brhc, brhs, brid, brie, brlo, brlt, brmi, brne, brpl, brsh, brtc, brts, brvc, brvs
	private static class AIT_K extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int k = ((buf[off+1] & 0x03) << 5) | ((buf[off] & 0xF8) >>> 3);
			if (k >= 64) {k = (k&63)-64;}
			k = k + addr + 1;
			return " "+mf.convFlashAddrToString(k);
		}		
	}

	// call, jmp
	private static class AIT_LK extends AVRInstrType {
		public int getWordCnt() {return 2;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int k = (buf[off+1] & 0x01) << 5;
			k |= (buf[off] & 0xF0) >>> 3;
			k |= buf[off] & 0x01;
			k = (k<<16) | (buf[off+3] << 8) | buf[off+2];  
			return " "+mf.convFlashAddrToString(k);
		}		
	}

	// cbi, sbi, sbic, sbis
	private static class AIT_IO_BIT extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int io = (buf[off] & 0xF8) >>> 3;
			int bit = buf[off] & 0x07;
			return " "+IO_REGS[io]+", "+bit;
		}		
	}

	// muls
	private static class AIT_RDL_RRL extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0xF0) >>> 4;
			int rr = buf[off] & 0x0F;
			rd |= 0x10;
			rr |= 0x10;
			return " r"+rd+", r"+rr;
		}		
	}

	// fmul, fmuls, fmulsu, mulsu
	private static class AIT_RDLL_RRLL extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0x70) >>> 4;
			int rr = buf[off] & 0x07;
			rd |= 0x10;
			rr |= 0x10;
			return " r"+rd+", r"+rr;
		}		
	}

	// in
	private static class AIT_RD_IO extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int io = ((buf[off+1] & 0x06) << 3) | (buf[off] & 0x0F);
			return " r"+rd+", "+IO_REGS[io];
		}		
	}

	// out
	private static class AIT_IO_RR extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int io = ((buf[off+1] & 0x06) << 3) | (buf[off] & 0x0F);
			return " "+IO_REGS[io]+", r"+rd;
		}		
	}

	// FIX
	// lds
	private static class AIT_RD_LK extends AVRInstrType {
		public int getWordCnt() {return 2;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int k = (buf[off+3] << 8) | buf[off+2];  
			return " r"+rd+", 0x"+Utils.wordToHex(k);
		}		
	}

	// FIX
	// sts
	private static class AIT_LK_RR extends AVRInstrType {
		public int getWordCnt() {return 2;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rr = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int k = (buf[off+3] << 8) | buf[off+2];  
			return " 0x"+Utils.wordToHex(k)+", r"+rr;
		}		
	}

	// movw
	private static class AIT_RDL_RRL_D extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0xF0) >>> 4;
			int rr = buf[off] & 0x0F;
			rd *= 2;
			rr *= 2;
			return " r"+(rd+1)+":r"+rd+", r"+(rr+1)+":r"+rr;
		}		
	}

	// rcall, rjmp
	private static class AIT_K_2 extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int k = ((buf[off+1] & 0x0F) << 8) | buf[off];  
			if (k >= 2048) {k = (k&2047)-2048;}
			k = k + addr + 1;
			return " "+mf.convFlashAddrToString(k);
		}		
	}

	// ser
	/*private static class AIT_RDL extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = (buf[off] & 0xF0) >>> 4;
			rd |= 0x10;
			return " r"+rd;
		}		
	}*/
	
	// ldd Y+, ldd Z+
	private static class AIT_RD_Q extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int q = (buf[off+1] & 0x20) | ((buf[off+1] & 0x0C) << 1) | (buf[off] & 0x07);
			return " r"+rd+", "+instr.getAttributesSuffix()+q;
		}		
	}

	// std Y+, std Z+
	private static class AIT_Q_RR extends AVRInstrType {
		public int getWordCnt() {return 1;}
		public String getAttribs(MainFrame mf, AVRInstr instr, int addr, byte[] buf, int off) {
			int rd = ((buf[off+1] & 0x01) << 4) | ((buf[off] & 0xF0) >>> 4);
			int q = (buf[off+1] & 0x20) | ((buf[off+1] & 0x0C) << 1) | (buf[off] & 0x07);
			return ""+q+", r"+rd;
		}		
	}

}
