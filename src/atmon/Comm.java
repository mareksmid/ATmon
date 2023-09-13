package atmon;

public class Comm implements Runnable {

	private static final byte RESET_CMD    = 'r';
	private static final byte GOTO_CMD     = 'g';
	private static final byte READMEM_CMD  = 'd';
	private static final byte WRITEMEM_CMD = 'w';
	private static final byte READIO_CMD   = 'i';
	private static final byte WRITEIO_CMD  = 'o';
	private static final byte RESP_CMD     = 'O';
	private static final byte RESP_RESP    = 'K';
	private static final byte READEE_CMD   = 'e';
	private static final byte WRITEEE_CMD  = 'E';

	private static final byte RESP_CHAR    = 13;
	private static final byte NOT_RESP_CHAR= 10;
	private static final byte STOP_CMD     = 'P';
	private static final byte WRITEFA_CMD  = 'A';
	private static final byte WRITEFD_CMD  = 'C';
	private static final byte WRITEFP_CMD  = 'M';

	private static final byte READF_CMD    = 'R';
	private static final byte TERMMD_CMD   = 'T';
	private static final byte TERMMDQ_CMD  = 2;
	private static final byte RESUME_CMD   = 'p';

	private static final byte ENTER_STEP_CMD = 'S';
	private static final byte STEP_CMD	   = 's';
	private static final byte SET_BREAK_PT_CMD = 'b';
	private static final byte STEPPED_CMD  = 'B';
	private static final byte TRANSMIT_LNG = 3;
	private static final byte BLANK_CHK_CMD = 'l';
	private static final byte ERASE_CMD = 'a';
	private static final byte READ_FL_BITS_CMD = 'L';
	private static final byte WRITE_L_BITS_CMD = 30;

	public static final int RW_PAGE    = 256;
	public static final int FLASHW_PAGE= 64*2;
	
	public static final int RAM_SIZE   = 0x860;  // 2144 = 32 + 64 + 2048
	public static final int EE_SIZE    = 0x400;  // 1024;
	public static final int IO_SIZE    = 0x40;   // 64
	public static final int FLASH_SIZE = 0x8000; // 32*1024;
	
	public static final int QUIT_STEP_VAL = 255;
	public static final int BREAK_PT_CNT = 4;	
	public static final byte STEP_ENTER_BREAKMODE_VAL     = (byte) 0xFF;
	public static final byte STEP_ENTER_NOT_BREAKMODE_VAL = 0;
	public static final int FL_BITS_BYTE_CNT = 4;


	private CommPort cp;
	private boolean termMode, stopped, stepMode;
	private MainFrame mf;
	private boolean running;
	private DisAssembler dassm;
	BreakpointManager bpm;
		
	public Comm(MainFrame mf) {
		this.mf = mf;
		cp = new CommPort();
		termMode = false;
		stopped = false;
		stepMode = false;
		dassm = new DisAssembler(mf, this);
		bpm = new BreakpointManager(mf, this);
	}
	
	public void open(String port) {
		try {
			cp.open(port);
			Thread t = new Thread(this);
			t.start();
		} catch (Exception ex) {System.err.println("Opening port " + port + " failed");}
	}
	
	public void close() {
		try {
			running = false;
			cp.close();
		} catch (Exception ex) {System.err.println("Closing port failed");}
	}
	
	public String[] getCommPorts() {
		return cp.getCommPorts();
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public synchronized void reset() {
		if (termMode) {
			System.err.println("Cannot reset in term mode");
			return;
		}
		byte[] data = {RESET_CMD};
		sendBuf(data);
		termMode = false;
		stopped = false;
		stepMode = false;
	}
	
	public synchronized void goTo(int addr) {
		if (termMode) {
			System.err.println("Cannot go to address in term mode");
			return;
		}
		if (stepMode) {
			System.err.println("Cannot go to address in stepping/breakpoint mode");
			return;
		}		
		byte[] data = {GOTO_CMD, (byte) addr, (byte)(addr >> 8)};
		sendBuf(data);
	}	
	
	public synchronized byte[] readMem(int beg, int lng) {		
		if ((lng <= 0) || (beg < 0) || (lng + beg > RAM_SIZE)) {return null;}
		return readBlock(beg, lng, READMEM_CMD);
	}
	
	public synchronized byte[] readEE(int beg, int lng) {
		if ((lng <= 0) || (beg < 0) || (lng + beg > EE_SIZE)) {return null;} 
		return readBlock(beg, lng, READEE_CMD);
	}
	
	public synchronized byte[] readFlash(int beg, int lng) {
		if ((lng <= 0) || (beg < 0) || (lng + beg > FLASH_SIZE)) {return null;} 		
		return readBlock(beg, lng, READF_CMD);
	}


	public synchronized byte[] readBlock(int beg, int lng, byte cmd) {
		if (termMode) {
			System.err.println("Cannot read in terminal mode");
			return null;
		}
		byte[] data = new byte[lng];
		ProgressDialog prog = null;
		boolean error = false;
				
		
		int pages = lng / RW_PAGE;
		int rest  = lng % RW_PAGE;
		
		int off = beg;
		byte[] req = {cmd, 0, 0, 0};
			
		if (pages > 0) {
			prog = new ProgressDialog(mf, "Reading:", 0, pages-1);		

			for (int i = 0; i < pages; i++) {
				req[1] = (byte) off;
				req[2] = (byte)(off >> 8);
				req[3] = 0;
				sendBuf(req);				
				if (!read(data, off-beg, RW_PAGE)) {error = true; break;}
				off += RW_PAGE;

				prog.setPos(i);		
			}
		}
		
		if (!error && (rest > 0)) {
			req[1] = (byte) off;
			req[2] = (byte)(off >> 8);
			req[3] = (byte) rest;
			sendBuf(req);

			if (!read(data, off-beg, rest)) {error = true;}
		}
				
		if (prog != null) {prog.close();}
		if (error) {
			System.err.println("Reading failed");
			return null;
		} else {return data;}		
	}
	
	public synchronized boolean writeMem(byte[] data, int beg) {
		if (termMode) {
			System.err.println("Cannot write SRAM in term mode");
			return false;
		}
		int lng = data.length;
		if ((lng <= 0) || (beg < 0) || (beg + lng > RAM_SIZE)) {
			System.err.println("Writing SRAM: invalid address / length");
			return false;
		} 
		ProgressDialog prog = null;
		
		int pages = lng / RW_PAGE;
		int rest  = lng % RW_PAGE;
		
		int off = beg;
		byte[] req = {WRITEMEM_CMD, 0, 0, 0};
				
		if (pages > 0) {
			prog = new ProgressDialog(mf, "Writing RAM:", 0, pages-1);		

			for (int i = 0; i < pages; i++) {
				req[1] = (byte) off;
				req[2] = (byte)(off >> 8);
				req[3] = 0;
				sendBuf(req);
				sendBuf(data, off-beg, RW_PAGE);						
				off += RW_PAGE;
				
				prog.setPos(i);
			}			
		}
		
		if (rest > 0) {
			req[1] = (byte) off;
			req[2] = (byte)(off >> 8);
			req[3] = (byte) rest;
			sendBuf(req);
			sendBuf(data, off-beg, rest);
		}
		if (prog != null) {prog.close();}		
		return true;
	}
	
	public synchronized int readIO(int addr) {
		if (termMode) {
			System.err.println("Cannot read IO in term mode");
			return -1;
		}
		if ((addr < 0) || (addr >= IO_SIZE)) {
			System.err.println("Reading I/O failed");
			return -1;
		} 
				
		byte[] req = {READIO_CMD, (byte) addr};
		sendBuf(req);
		
		return read();
	}	

	public synchronized void writeIO(int addr, int value) {
		if (termMode) {
			System.err.println("Cannot write IO in term mode");
			return;
		}
		if ((addr < 0) || (addr >= IO_SIZE)) {return;} 
			
		byte[] req = {WRITEIO_CMD, (byte) addr, (byte) value};
		sendBuf(req);
	}	
	
	public synchronized boolean isResponding() {
		if (termMode) {return false;}
		byte[] req = {RESP_CMD};
		sendBuf(req);
		int resp = read();
		return (resp == RESP_RESP);		
	}
	
	public synchronized boolean writeFlash(byte[] data, int off, int lng, boolean reset) {
		if (termMode) {
			System.err.println("Cannot write in terminal mode");
			return false;
		}
		if ((data == null) || (lng <= 0) || (off < 0) || (off + lng > data.length) || (off + lng > FLASH_SIZE)) { 
			System.err.println("Writing flash: incorrect address / length");
			return false;
		}
				
		byte[] reqE = {STOP_CMD};
		sendBuf(reqE);
		boolean error = false;
		
		int beg = (off / FLASHW_PAGE) * FLASHW_PAGE;
		int end = (((off+lng-1) / FLASHW_PAGE) + 1) * FLASHW_PAGE;
		int pages = (end-beg) / FLASHW_PAGE;
		int addr = beg;
		
		ProgressDialog prog = new ProgressDialog(mf, "Writing flash:", beg, end);		
		
		byte[] reqA = {WRITEFA_CMD, 0, 0};		
		byte[] reqD = {WRITEFD_CMD, 0, 0};
		byte[] reqP = {WRITEFP_CMD};
		
		for (int i = 0; i < pages; i++) {
			reqA[1] = (byte) addr;
			reqA[2] = (byte) (addr >> 8);
			sendBuf(reqA);
			if (read() != RESP_CHAR) {
				System.err.println("Writing flash: no response after setting address");
				error = true;
				break;
			}
			for (int j = 0; j < FLASHW_PAGE/2; j++) {
				if (addr+1 >= off+lng) {
					reqD[1] = (byte) 0xFF;
					reqD[2] = (byte) 0xFF;					
				} else {
					reqD[1] = data[addr];
					reqD[2] = data[addr+1];					
				}
				sendBuf(reqD);
				addr += 2;
			}			
			
			sendBuf(reqP);
			if (read() != RESP_CHAR) {
				System.err.println("Writing flash: no response after writing page");
				error = true;
				break;
			}

			prog.setPos(addr);		
		}
		if (reset) {reset();}
		prog.close();
		return !error;
	}
	
	public synchronized boolean writeEE(byte[] data, int off, int lng, int addr) {
		if (termMode) {
			System.err.println("Cannot write in terminal mode");
			return false;
		}
		if ((data == null) || (lng <= 0) || (off < 0) || (off + lng > data.length) || (addr + lng > EE_SIZE)) {
			System.err.println("Writing EEPROM: incorrect address / length");
			return false;
		} 
				
		byte[] req = {WRITEEE_CMD, 0, 0, 0};
		

		ProgressDialog prog = new ProgressDialog(mf, "Writing EEPROM:", 0, lng-1);		
				
		for (int i = 0; i < lng; i++) {
			req[1] = (byte) addr;
			req[2] = (byte)(addr >> 8);
			req[3] = data[off+i];
			sendBuf(req);
			if (read() != RESP_CHAR) {
				System.err.println("Writing eeprom: no response after writing byte");
				prog.close();
				return false;
			}			
			addr++;	
			prog.setPos(i);
		}
		prog.close();
		return true;
	}
	
		
	public synchronized void setTermMode(boolean state) {
		if (state == termMode) {return;}
		byte[] req = new byte[1];
		if (state) {
			termMode = true;
			req[0] = TERMMD_CMD;
			sendBuf(req);
		} else {
			termMode = false;
			req[0] = TERMMDQ_CMD;
			sendBuf(req);
		}
	}
	
	public synchronized void stop() {
		if (termMode) {
			System.err.println("Cannot stop in terminal mode");
			return;
		}
		if (stepMode) {
			System.err.println("Cannot stop in stepping/breakpoint mode");
			return;
		}		
		if (stopped) {
			System.out.println("Already stopped");
			return;
		}
		byte[] req = {STOP_CMD};
		sendBuf(req);
		stopped = true;
	}
	
	public synchronized void resume() {
		if (termMode) {
			System.err.println("Cannot resume in terminal mode");
			return;
		}
		if (stepMode) {
			System.err.println("Cannot resume in stepping/breakpoint mode");
			return;
		}		
		if (!stopped) {
			System.out.println("Not stopped yet");
			return;
		}
		byte[] req = {RESUME_CMD};
		sendBuf(req);
		stopped = false;
	}

	public synchronized void stepEnter(boolean breakPtMode) {
		if (termMode) {
			System.err.println("Cannot enter stepping mode in terminal mode");
			return;
		}
		byte[] req = {ENTER_STEP_CMD, (breakPtMode ? STEP_ENTER_BREAKMODE_VAL : STEP_ENTER_NOT_BREAKMODE_VAL)};
		sendBuf(req);
		stepMode = true;
	}

	public synchronized void stepQuit() {
		if (termMode) {
			System.err.println("Cannot quit stepping/breakpoint mode in terminal mode");
			return;
		}		
		if (!stepMode) {
			System.out.println("Not in stepping/breakpoint mode");
			return;
		}
		byte[] req = {STEP_CMD, (byte) QUIT_STEP_VAL};
		sendBuf(req);
		stepMode = false;
	}


	public synchronized void step(int cnt) {
		if (termMode) {
			System.err.println("Cannot step in terminal mode");
			return;
		}
		if (!stepMode) {
			System.out.println("Not in stepping/breakpoint mode");
			return;
		}
		byte[] req = {STEP_CMD, (byte) cnt};
		sendBuf(req);
	}

	public synchronized void setBreakpoints(int[] breakpoint) {
		if (termMode) {
			System.err.println("Cannot set breakpoints in terminal mode");
			return;
		}
		byte[] req = new byte[1+BREAK_PT_CNT*2]; 
		req[0] = SET_BREAK_PT_CMD;
		for (int i = 0; i < BREAK_PT_CNT; i++) {
			req[1+i*2] = (byte) breakpoint[i];
			req[2+i*2] = (byte) (breakpoint[i] >> 8);
		}	
		sendBuf(req);
		stepMode = true;
	}

	public synchronized boolean blankCheck() {
		if (termMode) {
			System.err.println("Cannot check in terminal mode");
			return false;
		}
		byte[] req = {BLANK_CHK_CMD};
		sendBuf(req);
		int resp = read(4);
		if (resp == RESP_CHAR) {return true;}
		else if (resp == NOT_RESP_CHAR) {return false;}
		else {
			System.err.println("Invalid response to blank check");
			return false;
		}
	}

	public synchronized void eraseFlash() {
		if (termMode) {
			System.err.println("Cannot erase in terminal mode");
			return;
		}
		byte[] req = {ERASE_CMD};
		sendBuf(req);
		int resp = read(10);
		if (resp != RESP_CHAR) {
			System.err.println("Invalid response after erase");
		} else {
			System.out.println("Erased");
		}
	}
	
	public synchronized byte[] readFLBits() {
		if (termMode) {
			System.err.println("Cannot read fuses / lock bits in terminal mode");
			return null;
		}
		byte[] req = {READ_FL_BITS_CMD};
		sendBuf(req);

		byte[] data = new byte[FL_BITS_BYTE_CNT];
		if (read(data, 0, FL_BITS_BYTE_CNT)) {
			return data;
		} else {
			System.err.println("Reading fuses / lock bits failed");
			return null;
		}		
	}

	public synchronized void writeLBits(int locks) {
		if (termMode) {
			System.err.println("Cannot write lock bits in terminal mode");
			return;
		}
		byte[] req = {WRITE_L_BITS_CMD, (byte) locks};
		sendBuf(req);

		if (read() == RESP_CHAR) {
			System.out.println("Lock bits written");
		} else {
			System.err.println("No response after writing lock bits");
		}
	}

	public synchronized void send(byte b) {
		cp.send(b);
	}
	
	private synchronized void sendBuf(byte[] buf, int off, int len) {
		doReceive();
		cp.sendBuf(buf, off, len);
	}

	private synchronized void sendBuf(byte[] buf) {
		doReceive();
		cp.sendBuf(buf);
	}

	
	public synchronized int read() {
		return cp.read();
	}
	
	public synchronized int read(int retries) {
		int val, ctr = 0;
		while (ctr < retries) {
			val = cp.read();
			ctr++;
			if (val >= 0) {return val;}
		}
		return -1;
	}

	public synchronized boolean read(byte[] buf, int off, int len) {
		return cp.read(buf, off, len);
	}
	
//	-------------------------------------------------------------------------------------------------------------------

	public void run() {
		running = true;
		do {
			if (!termMode) {
				doReceive();
			}
			try {Thread.sleep(100);} catch (Exception ex) { }
		} while (running);
	}
	
	private synchronized void doReceive() {
		if (cp.getAvailable() == 0) {return;}
		byte[] buf = new byte[TRANSMIT_LNG];
		int cmd, val;
		if (!read(buf, 0, TRANSMIT_LNG)) {return;}
		cmd = buf[0];
		val = Utils.ub(buf[1]) | (Utils.ub(buf[2]) << 8);
		switch (cmd) {
			case STEPPED_CMD:
				stepped(val);
				break;
				
			default:
				System.err.println("Received unknown command: " + cmd);
		}
		/*
		val = read();
		if (val >= 0) {
			System.out.println(">" + val);
			
		}*/
	}
	
	private void stepped(int addr) {
		System.out.println("Reached: " + mf.convFlashAddrToString(addr) + " > " + dassm.readInstr(addr));		
	}
	
}
