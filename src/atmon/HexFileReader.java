package atmon;

import java.io.*;
import java.util.Arrays;

public class HexFileReader extends FileReader {
	
	private static final int MAX_BUF_SIZE = 256*1024; // 256kB
	
	private static final int DATA_REC       = 0x00;
	private static final int EOF_REC        = 0x01;
	private static final int EXTSEGADDR_REC = 0x02;
	private static final int EXTLINADDR_REC = 0x04;
	
	private static final int DATA_LN  = 0;
	private static final int EOF_LN   = 1;
	private static final int ERROR_LN = 2;
	private static final int EMPTY_LN = 3;
	private static final int OTHER_LN = 4;
	

	private BufferedReader br;
	private int beg, end;
	private int extLinAddr, extSegAddr;
	
	public HexFileReader(String fileName) throws FileNotFoundException {
		super(fileName);
		br = new BufferedReader(this);		
		beg = Integer.MAX_VALUE;
		end = Integer.MIN_VALUE;
		extLinAddr = 0;
		extSegAddr = 0;
	}
	
	public int getBegin() {return beg;}
	public int getEnd() {return end;}
	
	public byte[] readBuffer() {
		try {
			byte[] buf = new byte[MAX_BUF_SIZE];
			Arrays.fill(buf, (byte) 0xFF);
			String line = br.readLine();
			int ln = -1;
			while (line != null) {
				
				ln = decodeHexLine(line, buf);
				if (ln == ERROR_LN) {return null;}
				else if (ln == EOF_LN) {break;}
				
				line = br.readLine();			
			} 
			if (ln != EOF_LN) {
				System.err.println("Hex file is not terminated with EOF record");
				return null;
			}
			if (beg >= end) {
				System.err.println("No data in hex file");
				return null;
			}
			byte[] buf2 = new byte[end];
			System.arraycopy(buf, 0, buf2, 0, end);
			return buf2;
			
		} catch (Exception ex) {
			System.err.println("Error reading hex file: " + ex);
			return null;
		}
	}
	
	private int decodeHexLine(String line, byte[] buf) {
		if ((line == null) || (line.equals(""))) {return EMPTY_LN;}
		
		if (!line.startsWith(":")) {
			System.err.println("Unknown line in hex file: " + line);
			return ERROR_LN;
		}
		line = line.substring(1);
		if (line.length() < 10) {
			System.err.println("Hex file line too short: " + line);
			return ERROR_LN;
		}
		int lineL = line.length();
		String lngS = line.substring(0, 2);
		String addrS = line.substring(2, 6);
		String typeS = line.substring(6, 8);
		String dataS = line.substring(8, lineL-2);
		String chckS = line.substring(lineL-2, lineL);

		int lng, addr, type, chck, sum;
		byte[] data;
		try {
			lng = Integer.parseInt(lngS, 16);
			addr = Integer.parseInt(addrS, 16);
			type = Integer.parseInt(typeS, 16);
			chck = Integer.parseInt(chckS, 16);
			
			sum = lng + (addr / 0x100) + (addr % 0x100) + type + chck; 
			
			if (dataS.length() !=  lng*2) {
				System.err.println("Wrong length of hex file line: " + line);
				return ERROR_LN;
			}
			data = new byte[lng];
			for (int i = 0; i < lng; i++) {
				data[i] = (byte) Integer.parseInt(dataS.substring(i*2, i*2+2), 16);
				sum += data[i];
			}			
		} catch (Exception ex) {
			System.err.println("Incorrect number format in hex file line: " + line);
			return ERROR_LN;
		}
		if ((sum % 0x100) != 0) {
			System.err.println("Incorrect check-sum of hex file line: " + line);
			return ERROR_LN;
		}
		
		switch (type) {
			case DATA_REC:
				addr = addr + (extLinAddr * 0x10000) + (extSegAddr * 0x10);
				
				if (lng == 0) {return DATA_LN;}
				
				if (addr+lng > MAX_BUF_SIZE) {
					System.err.println("Address in hex file line too big: " + line);
					return ERROR_LN;
				}
				
				if (addr < beg) {beg = addr;}
				if (addr+lng > end) {end = addr+lng;}
				for (int i = 0; i < lng; i++) {
					buf[addr+i] = data[i];  										
				}
				return DATA_LN;
			
			case EOF_REC:
				return EOF_LN;

			case EXTSEGADDR_REC:
				if ((addr != 0) || (lng != 2)) {
					System.err.println("Wrong extended segment address command in hex file line: " + line);
					return ERROR_LN;
				}
				extSegAddr = (Utils.ub(data[0]) << 8) + data[1];
				return OTHER_LN;

			case EXTLINADDR_REC:
				if ((addr != 0) || (lng != 2)) {
					System.err.println("Wrong extended linear address command in hex file line: " + line);
					return ERROR_LN;
				}
				extLinAddr = (Utils.ub(data[0]) << 8) + data[1];
				return OTHER_LN;

			default:
				System.err.println("Unsupported command in hex file line: " + line);
				return ERROR_LN;
		}
	}

}
