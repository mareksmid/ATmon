package atmon;

import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;

public class CmdProcessor extends Thread {

	private MainFrame mf;
	private Comm comm;
	private Config cfg;
	
	private int addr, lng, val;
	private boolean wordMode;
	
	private boolean running;
	private ArrayList commandQueue;
	private FuseLockBitsCodec flbc;
	
	public CmdProcessor(MainFrame mf, Comm comm, Config cfg, FuseLockBitsCodec flbc) {
		this.mf = mf;
		this.comm = comm;
		this.cfg = cfg;
		this.flbc = flbc;
		commandQueue = new ArrayList();
		start();
	}
	
	public void procCmd(String cmd) {
		commandQueue.add(cmd);
/*		if (command == null) {
			command  = cmd;			
		} else {
			System.err.println("Another command already in progress");
		}*/
	}
	
	public void run() {
		running = true;
		//command = null;
		while (running) {
			//if (command != null) {
			if (commandQueue.size() > 0) {
				doCommand((String) commandQueue.remove(0));
				mf.pane.selectAll();
				//command = null;
			} else {
				try {sleep(100);}
				catch (Exception ex) { }
			}
		}
	}
	
	private void doCommand(String cmd) {
		StringTokenizer st = new StringTokenizer(cmd, " \t", false);
		
		if (!st.hasMoreTokens()) {
			System.err.println("No command");
			return;
		}
		
		String c = st.nextToken().toUpperCase();
		String s;
		byte[] data;
		
			
		if (c.equals("H")) {
			mf.showHelp();
		} else if (c.equals("WLCK")) {
			new LockBitsFrame(comm);
		} else if (c.equals("LCK") || c.equals("FUS")) {
			flbc.dispFLBits(comm.readFLBits());			
		} else if (c.equals("BL")) {
			if (comm.blankCheck()) {
				System.out.println("Blank");
			} else {
				System.out.println("Not blank");				
			}
		} else if (c.equals("ER")) {
			comm.eraseFlash();
		} else if (c.equals("BRKL")) {
			comm.bpm.listPts();
		} else if (c.equals("BRKR")) {
			if (decodeAddr(st, Comm.FLASH_SIZE)) {
				comm.bpm.removePt(addr);				
			}			
		} else if (c.equals("BRK")) {
			if (decodeAddr(st, Comm.FLASH_SIZE)) {
				comm.bpm.addPt(addr);				
			}
		} else if (c.equals("STEPQ")) {
			comm.stepQuit();
		} else if (c.equals("BRKQ")) {
			comm.bpm.quit();
		} else if (c.equals("STEPE")) {
			comm.stepEnter(false);
		} else if (c.equals("STEP")) {
			if (st.hasMoreTokens()) {
				val = mf.translateName(st.nextToken());
				if ((val < 1) || (val >= 0xFF)){
					System.err.println("Invalid value");
				} else {
					comm.step(val);					
				}
			} else {
				comm.step(1);
			}			
		} else if (c.equals("SEND")) {
			if (decodeValue(st)) {
				comm.send((byte) val);
			}			
		} else if (c.equals("HD")) {
			try {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(mf) != JFileChooser.APPROVE_OPTION) {return;}
				HexFileReader hfr = new HexFileReader(fc.getSelectedFile().getAbsolutePath());
				data = hfr.readBuffer();
				if (data == null) {
					System.err.println("Reading HEX file failed");
					return;
				}
				wordMode = st.hasMoreTokens() && st.nextToken().toUpperCase().startsWith("W");
				byte[] td = new byte[hfr.getEnd()-hfr.getBegin()];
				System.arraycopy(data, hfr.getBegin(), td, 0, hfr.getEnd()-hfr.getBegin());
				mf.dispData(hfr.getBegin(), td, wordMode);
			} catch (IOException ex) {
				System.err.println("Cannot read HEX file: " + ex);
			}						
		} else if (c.equals("LST")) {
			mf.showListing();
		} else if (c.equals("RS")) {
			comm.resume();
		} else if (c.equals("S")) {
			comm.stop();
		} else if (c.equals("VRE")) {			
			try {
				HexFileReader hfr = new HexFileReader(cfg.sourceFile + MainFrame.EEP_EXT);
				data = hfr.readBuffer();
				if (data == null) {
					System.err.println("Reading EEPROM file failed");
					return;
				}
				byte[] verify = comm.readEE(hfr.getBegin(), hfr.getEnd()-hfr.getBegin());
				if (verify == null) {
					System.err.println("Reading EEPROM for verify failed");				
				} else {
					if (compareData(data, hfr.getBegin(), verify, 0, hfr.getEnd()-hfr.getBegin())) {
						System.out.println("Verify: OK");											
					} else {
						System.err.println("Verify: failed");				
					}
				}
			} catch (IOException ex) {
				System.err.println("Cannot read EEPROM file "+ cfg.sourceFile+MainFrame.EEP_EXT+ ": " + ex);
			}
		} else if (c.equals("ED")) {			
			if (!decodeAddrAndLength(st, Comm.EE_SIZE, false, true)) {return;}
			data = comm.readEE(addr, lng);
			mf.dispData(addr, data, wordMode);
		} else if (c.equals("RDE")) {
			try {
				JFileChooser fc = new JFileChooser();
				if (fc.showSaveDialog(mf) != JFileChooser.APPROVE_OPTION) {return;}
				data = comm.readEE(0, Comm.EE_SIZE);
				if (data == null) {
					System.err.println("Reading EEPROM failed");
					return;
				}
				System.out.println("Reading EEPROM: done");					
				HexFileWriter hfw = new HexFileWriter(fc.getSelectedFile().getAbsolutePath() + MainFrame.EEP_EXT);
				hfw.writeBuffer(0, data, data.length, 0);
				System.out.println("Reading EEPROM: written to: " + fc.getSelectedFile().getName());
			} catch (IOException ex) {
				System.err.println("Cannot write file with EEPROM: " + ex);
			}			
		} else if (c.equals("WRE")) {			
			try {
				HexFileReader hfr = new HexFileReader(cfg.sourceFile + MainFrame.EEP_EXT);
				data = hfr.readBuffer();
				if (data == null) {
					System.err.println("Reading EEPROM file failed");
					return;
				}
				comm.writeEE(data, hfr.getBegin(), hfr.getEnd()-hfr.getBegin(), hfr.getBegin());
				System.out.println("Writing EEPROM: finished");
				
				if (st.hasMoreTokens() && (st.nextToken().toUpperCase().equals("V"))) {
					byte[] verify = comm.readEE(hfr.getBegin(), hfr.getEnd()-hfr.getBegin());
					if (verify == null) {
						System.err.println("Reading EEPROM for verify failed");				
					} else {
						if (compareData(data, hfr.getBegin(), verify, 0, hfr.getEnd()-hfr.getBegin())) {
							System.out.println("Verify: OK");											
						} else {
							System.err.println("Verify: failed");				
						}
					}
				}
			} catch (IOException ex) {
				System.err.println("Cannot read EEPROM file "+ cfg.sourceFile+MainFrame.EEP_EXT+ ": " + ex);
			}
		} else if (c.equals("VR")) {			
			try {
				HexFileReader hfr = new HexFileReader(cfg.sourceFile + MainFrame.HEX_EXT);
				data = hfr.readBuffer();
				if (data == null) {
					System.err.println("Reading flash file failed");
					return;
				}
				byte[] verify = comm.readFlash(hfr.getBegin(), hfr.getEnd()-hfr.getBegin());
				if (verify == null) {
					System.err.println("Reading flash for verify failed");				
				} else {
					if (compareData(data, hfr.getBegin(), verify, 0, hfr.getEnd()-hfr.getBegin())) {
						System.out.println("Verify: OK");											
					} else {
						System.err.println("Verify: failed");				
					}
				}
			} catch (IOException ex) {
				System.err.println("Cannot read flash file "+ cfg.sourceFile+MainFrame.HEX_EXT+ ": " + ex);
			}
		} else if (c.equals("FD")) {			
			if (!decodeAddrAndLength(st, Comm.FLASH_SIZE, false, true)) {return;}
			data = comm.readFlash(addr, lng);
			mf.dispData(addr, data, wordMode);
		} else if (c.equals("WR")) {
			try {
				HexFileReader hfr = new HexFileReader(cfg.sourceFile + MainFrame.HEX_EXT);
				data = hfr.readBuffer();
				if (data == null) {
					System.err.println("Reading flash file failed");
					return;
				}
				comm.writeFlash(data, hfr.getBegin(), hfr.getEnd()-hfr.getBegin(), false);
				System.out.println("Writing flash: finished");
				
				if (st.hasMoreTokens() && (st.nextToken().toUpperCase().equals("V"))) {
					byte[] verify = comm.readFlash(hfr.getBegin(), hfr.getEnd()-hfr.getBegin());
					if (verify == null) {
						System.err.println("Reading flash for verify failed");				
					} else {
						if (compareData(data, hfr.getBegin(), verify, 0, hfr.getEnd()-hfr.getBegin())) {
							System.out.println("Verify: OK");											
						} else {
							System.err.println("Verify: failed");				
						}
					}
				}
				comm.reset();
			} catch (IOException ex) {
				System.err.println("Cannot read flash file "+ cfg.sourceFile+MainFrame.HEX_EXT+ ": " + ex);
			}
		} else if (c.equals("RD")) {
				try {
					JFileChooser fc = new JFileChooser();
					if (fc.showSaveDialog(mf) != JFileChooser.APPROVE_OPTION) {return;}
					data = comm.readFlash(0, Comm.FLASH_SIZE);
					if (data == null) {
						System.err.println("Reading flash failed");
						return;
					}
					System.out.println("Reading flash: done");					
					HexFileWriter hfw = new HexFileWriter(fc.getSelectedFile().getAbsolutePath() + MainFrame.HEX_EXT);
					//hfw.writeBuffer(hfr.getBegin(), data, hfr.getEnd()-hfr.getBegin(), hfr.getBegin());
					hfw.writeBuffer(0, data, data.length, 0);
					System.out.println("Reading flash: written to: " + fc.getSelectedFile().getName());
				} catch (IOException ex) {
					System.err.println("Cannot write file with flash: " + ex);
				}			
		} else if (c.equals("SRC")) {
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File(cfg.sourceFile+MainFrame.HEX_EXT));
			if (fc.showOpenDialog(mf) == JFileChooser.APPROVE_OPTION) {
				mf.setSourceFiles(fc.getSelectedFile().getAbsolutePath());
			}
		} else if (c.equals("INCLF")) {
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File(cfg.includeFile));
			if (fc.showOpenDialog(mf) == JFileChooser.APPROVE_OPTION) {
				cfg.includeFile = fc.getSelectedFile().getAbsolutePath();
				mf.loadIncludeFile();
			}
		} else if (c.equals("T")) {
			new TermFrame(comm);
		} else if (c.equals("G")) {
			if (!st.hasMoreTokens()) {
				System.err.println("No address");
				return;
			}
			addr = mf.translateName(st.nextToken());
			if (addr < 0){
				System.err.println("Invalid address");
				return;
			}
			comm.goTo(addr);
		} else if (c.equals("DEFDEC")) {
			cfg.defaultHex = false;
		} else if (c.equals("DEFHEX")) {
			cfg.defaultHex = true;
		} else if (c.equals("EDREFR")) {
			if (!st.hasMoreTokens()) {
				System.out.println("Byte editor refresh time: " + cfg.edRefresh + " ms");
				return;
			}
			s = st.nextToken();
			try {
				val = Integer.parseInt(s);
				cfg.edRefresh = val;
			}
			catch (Exception ex) {
				System.err.println("Invalid value");
			}			
		} else if (c.equals("RESP")) {
			System.out.println("" + comm.isResponding());
		} else if (c.equals("TR")) {
			if (!st.hasMoreTokens()) {
				System.err.println("No symbol specified");
				return;
			}
			s = st.nextToken();
			addr = mf.translateName(s);
			lng = mf.translateNameToSize(s);
			if (addr < 0) {
				System.err.println(s + " unknown");
			} else {System.out.println(s + " = " + Utils.wordToHex(addr) + " (dec " + addr + ") [" + lng + "]");}
		} else if (c.equals("MAP")) {
			mf.showMap();
		} else if (c.equals("CL")) {
			comm.close();
		} else if (c.equals("OP")) {
			comm.open(cfg.portName);
		} else if (c.equals("PORT")) {
			if (!st.hasMoreTokens()) {
				System.err.println("No port specified");
				return;
			}
			cfg.portName = st.nextToken();
			System.out.println("Comm port changed to: " + cfg.portName);
			comm.open(cfg.portName);			
		} else if (c.equals("CFG")) {
			new ConfigFrame(mf, cfg);
		} else if (c.equals("C")) {
			mf.pane.setText(null);
		} else if (c.equals("A")) {
			mf.showAbout();
		} else if (c.equals("X")) {
			mf.dispose();
		} else if (c.equals("R")) {
			comm.reset();
			System.out.println("Reset");
		} else if (c.equals("I")) {
			if (!st.hasMoreTokens()) {
				System.err.println("No address");
				return;
			}
			addr = mf.translateName(st.nextToken());
			if ((addr < 0) || (addr >= Comm.IO_SIZE)){
				System.err.println("Invalid address");
				return;
			}
			val = comm.readIO(addr);
			mf.dispValue(addr, val);
		} else if (c.equals("O")) {
			if (!decodeAddr(st, Comm.IO_SIZE)) {return;}
			if (!decodeValue(st)) {return;}
			comm.writeIO(addr, val);
		} else if (c.equals("D")) {
			if (!decodeAddrAndLength(st, Comm.RAM_SIZE, false, true)) {return;}
			data = comm.readMem(addr, lng);
			mf.dispData(addr, data, wordMode);
		} else if (c.equals("M")) {
			if (!decodeAddr(st, Comm.RAM_SIZE)) {return;}
			if (!decodeValue(st)) {return;}
			data = new byte[1];
			data[0] = (byte) val;
			comm.writeMem(data, addr);
		} else if (c.equals("F")) {
			if (!decodeAddrAndLength(st, Comm.RAM_SIZE, true, false)) {return;}
			if (!decodeValue(st)) {return;}
			data = new byte[lng];
			Arrays.fill(data, (byte) val);
			comm.writeMem(data, addr);
		} else if (c.equals("IE") || c.equals("OE")) {
			if (!decodeAddr(st, Comm.IO_SIZE)) {return;}
			boolean auto = false;
			if (st.hasMoreTokens()) {
				s = st.nextToken().toUpperCase();
				if (s.equals("A")) {
					auto = true;
				} else {
					val = mf.translateName(s);
					if ((val < 0) || (val > 0xFF)) {
						System.err.println("Invalid value");
						return;
					} else {
						comm.writeIO(addr, val);
					}
					if (st.hasMoreTokens()) {
						s = st.nextToken().toUpperCase();
						if (s.equals("A")) {
							auto = true;
						}
					}
				}
			}
			ByteEdFrame be = new ByteEdFrame(comm, addr, true, cfg.edRefresh); 
			if (auto) {
				be.setAutoRefresh(true);
			}
		} else if (c.equals("E")) {
			if (!st.hasMoreTokens()) {
				System.err.println("No address");
				return;
			}
			String addrName = st.nextToken();
			if (!decodeAddr(addrName, Comm.RAM_SIZE)) {return;}
			boolean auto = false, bufferMode = false;
			int valcnt = 1;
			lng = -1;
			if (st.hasMoreTokens()) {
				s = st.nextToken().toUpperCase();
				if (s.equals("A")) {
					auto = true;
				} else {
					if (s.equals("B")) {
						bufferMode = true;
						if (st.hasMoreTokens() && st.nextToken().toUpperCase().equals("A")) {auto = true;}
					} else {
						lng = mf.translateName(s);
						if ((lng < ByteEdFrame.MIN_BYTE_CNT) || (lng > ByteEdFrame.MAX_BYTE_CNT)) {
							System.err.println("Invalid length in bytes");
							return;
						}
						if (st.hasMoreTokens()) {
							s = st.nextToken().toUpperCase();
							if (s.equals("A")) {
								auto = true;
							} else {
								valcnt = mf.translateName(s);
								if ((valcnt < ByteEdFrame.MIN_VAL_CNT) || (valcnt > ByteEdFrame.MAX_VAL_CNT)) {
									System.err.println("Invalid value count");
									return;
								}
								if (st.hasMoreTokens()) {
									s = st.nextToken().toUpperCase();
									if (s.equals("A")) {
										auto = true;
									}
								}
							}
						}						
					}
				}
			}
			if (lng < 0) {
				lng = mf.translateNameToSize(addrName);
				if (bufferMode) {
					valcnt = lng;
					if ((valcnt < ByteEdFrame.MIN_VAL_CNT) || (valcnt > ByteEdFrame.MAX_VAL_CNT)) {
						System.err.println("Invalid value count");
						return;
					}
					lng = 1;
				}
				if ((lng < ByteEdFrame.MIN_BYTE_CNT) || (lng > ByteEdFrame.MAX_BYTE_CNT)) {
					System.err.println("Variable too long");
					return;
				}
			}
			if (lng < 0) {
				lng = ByteEdFrame.DEF_BYTE_CNT;
			}
			ByteEdFrame be = new ByteEdFrame(comm, addr, false, cfg.edRefresh, lng, valcnt); 
			if (auto) {
				be.setAutoRefresh(true);
			}
		} else {
			System.err.println("Invalid command");
			return;
		}
		
	}
	
	public boolean compareData(byte[] data1, int off1, byte[] data2, int off2, int lng) {
		if ((data1 == null) || (data2 == null) || (off1 < 0) || (off2 < 0) || (lng < 0)) {return false;}
		if ((off1 + lng > data1.length) || (off2 + lng > data2.length)) {return false;}
		if (lng == 0) {return true;}
		for (int i = 0; i < lng; i++) {
			if (data1[i+off1] != data2[i+off2]) {return false;}
		}
		return true;
	}
	
	private boolean decodeAddr(String s, int length) {
		addr = mf.translateName(s);
		if ((addr < 0) || (addr >= length)) {
			System.err.println("Invalid address");
			return false;
		}
		return true;
		
	}

	private boolean decodeAddr(StringTokenizer st, int length) {
		if (!st.hasMoreTokens()) {
			System.err.println("No address");
			return false;
		}
		return decodeAddr(st.nextToken(), length);
	}
	
	private boolean decodeAddrAndLength(StringTokenizer st, int length, boolean needLength, boolean tryWM) {
		if (!decodeAddr(st, length)) {return false;}
		wordMode = false;
		if (st.hasMoreTokens()) {
			lng = mf.translateName(st.nextToken());
			if ((lng <= 0) || (addr + lng > length)) {
				System.err.println("Invalid length");
				return false;
			}
			if (tryWM && st.hasMoreTokens() && st.nextToken().toUpperCase().startsWith("W")) {
				wordMode = true;
				if ((lng%2 != 0) || (addr%2 != 0)) {
					System.err.println("Invalid address/length for word mode - not even");
					return false;
				}
			}
		} else {
			if (needLength) {return false;}
			lng = 1;
		}
		return true;
	}
	
	private boolean decodeValue(StringTokenizer st) {
		if (!st.hasMoreTokens()) {
			System.err.println("No value");
			return false;
		}
		val = mf.translateName(st.nextToken());
		if ((val < 0) || (val > 0xFF)){
			System.err.println("Invalid value");
			return false;
		}
		return true;
	}
	
	
}
