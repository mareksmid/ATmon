package atmon;

import java.io.*;

public class HexFileWriter extends FileWriter {

	private static final int DATA_REC = 0x00;
	private static final int EOF_REC  = 0x01;
//	private static final int EXTSEGADDR_REC = 0x02;
//	private static final int EXTLINADDR_REC = 0x04;
	
	/*
	 * WARNING: Does not support writing more data than 64kB - does not use extended segment or linear address for switching to higher addesses !!! 
	 */

	private static final int LINE_SIZE = 16;
	
	private BufferedWriter bw;
	
	public HexFileWriter(String fileName) throws IOException {
		super(fileName);
		bw = new BufferedWriter(this);		
	}
	
	public void writeBuffer(int logAddr, byte[] buf, int lng, int offs) {
		int lines = lng / LINE_SIZE;
		int rest = lng % LINE_SIZE;
		
//		byte[] extSeg = {0, 0}; // {(byte) (logAddr/0x100), (byte) (logAddr%0x100)};
//		if (!writeHexLine(0, EXTSEGADDR_REC, extSeg, 2, 0)) {return;}
		
		if (lines > 0) {
			for (int i = 0; i < lines; i++) {
				if (!writeHexLine(logAddr+(i*LINE_SIZE), DATA_REC, buf, LINE_SIZE, i*LINE_SIZE+offs)) {return;}
			}
		}

		if (rest > 0) {
			if (!writeHexLine(logAddr+(lines*LINE_SIZE), DATA_REC, buf, rest, lines*LINE_SIZE+offs)) {return;}
		}
		
		if (!writeHexLine(0, EOF_REC, null, 0, 0)) {return;}
		
		try {
			bw.flush();
			bw.close();
		} catch (Exception ex) {
			System.err.println("Writing hex file failed: " + ex);
		}
	}
	
	private boolean writeHexLine(int addr, int type, byte[] data, int lng, int offs) {
		int sum = lng + (addr / 0x100) + (addr % 0x100) + type;
		StringBuffer line = new StringBuffer();
		line.append(':');
		line.append(Utils.byteToHex(lng));
		line.append(Utils.wordToHex(addr));
		line.append(Utils.byteToHex(type));
		if ((data != null) && (lng > 0)) {
			for (int i = 0; i < lng; i++) {
				line.append(Utils.byteToHex(data[i+offs]));
				sum += data[i+offs];
			}
		}
		int chck = (0-sum) % 0x100;
		line.append(Utils.byteToHex(chck));
		try {
			bw.write(line.toString());
			bw.newLine();
			return true;
		} catch (Exception ex) {
			System.err.println("Writing hex file line failed: " + ex);
			return false;
		}
	}

}
