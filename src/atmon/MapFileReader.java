package atmon;

import java.io.*;
import java.util.*;

public class MapFileReader {

	private static final String C_MAP_SIZE_KEYWORD = "Common symbol";
		
	public static void loadIncludeMap(Set set, String fileName, String delimiters, String prefix) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s, c, n, v;
			StringTokenizer st;
			MapItem it;
			int type;
			set.clear();
			do {
				s = br.readLine();
				if (s == null) {break;}
				
				st = new StringTokenizer(s, delimiters, false);
				
				if (!st.hasMoreTokens()) {continue;}
				c = st.nextToken().toUpperCase();

				if (c.equals(prefix+"EQU")) {type = MapItem.TYPE_GENERAL;}
				else if (c.equals(prefix+"DEF")) {type = MapItem.TYPE_REGISTER;}
				else if (c.equals(prefix+"CSEG")) {type = MapItem.TYPE_FLASH;}
				else if (c.equals(prefix+"DSEG")) {type = MapItem.TYPE_RAM;}
				else if (c.equals(prefix+"ESEG")) {type = MapItem.TYPE_EEPROM;}
				else {continue;}	

				if (!st.hasMoreTokens()) {continue;}
				n = st.nextToken();
				if (!st.hasMoreTokens()) {continue;}
				v = st.nextToken();
				
				it = new MapItem(n, v, type);
				//map.put(n.toUpperCase(), v.toUpperCase());
				set.add(it);
				
			} while (s != null);
		} catch (FileNotFoundException fnfex) {
			System.err.println("Map file not found: " + fileName);
			return;
		} catch (Exception ex) {
			System.err.println("Map file reading error: " + ex);
			return;
		}
	}

	public static void loadCMap(Set set, String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s, n, v;
			MapItem it;
			HashMap sizeMap = new HashMap();
			int val;
			StringTokenizer st;
			int type;
			set.clear();
			//sourceVarSizeMap.clear();
			s = br.readLine();
			while (s != null) {				// found sizes begin
				
				if (s.indexOf(C_MAP_SIZE_KEYWORD) >= 0) {
					break;
				}
				s = br.readLine();
			}
			while ((s = br.readLine()) != null) {				// read sizes
				if (s.equals("")) {continue;}
				
				st = new StringTokenizer(s, " \t", false);
				
				if (!st.hasMoreTokens()) {break;}
				n = st.nextToken();
				
				if (!st.hasMoreTokens()) {break;}
				v = st.nextToken();
				
				val = Utils.strToInt(v, true);
				if (val < 1) {break;}
					
				sizeMap.put(n, new Integer(val));				
			}
			type = -1;
			while ((s = br.readLine()) != null) {				// read addresses
				if (s.equals("")) {continue;}
				
				st = new StringTokenizer(s, " \t", false);
				
				//if (!st.hasMoreTokens()) {continue;}
				//c = st.nextToken().toUpperCase();				
				//if (c.equals(prefix+"EQU") || c.equals(prefix+"DEF") || c.equals(prefix+"CSEG") || c.equals(prefix+"DSEG") || c.equals(prefix+"ESEG")) {	
				if (!st.hasMoreTokens()) {continue;}
				v = st.nextToken().toUpperCase();
				
				if (v.startsWith(".TEXT") || v.startsWith(".INIT") || v.startsWith(".PROGMEM") || v.startsWith(".VECTORS")) {type = MapItem.TYPE_FLASH; continue;}
				else if (v.startsWith(".BSS") || v.startsWith(".DATA") || v.startsWith("COMMON")) {type = MapItem.TYPE_RAM; continue;}
				else if (v.startsWith(".EEPROM")) {type = MapItem.TYPE_EEPROM; continue;}
				else if (v.startsWith(".")) {type = -1; continue;}
				
				if (type < 0) {continue;}
				
				if (!st.hasMoreTokens()) {continue;}
				n = st.nextToken();
				
				if (st.hasMoreTokens()) {continue;}
				
				val = Utils.strToInt(v, true);
				if (val < 0) {continue;}
				val = val & 0xFFFF;
				
				//sourceMap.put(n.toUpperCase(), v.toUpperCase());
				//sourceMap.put(n.toUpperCase(), "0x"+Utils.wordToHex(val));
				
				it = new MapItem(n, "0x"+Utils.wordToHex(val), type);
				set.add(it);
			}
			Iterator iter = sizeMap.keySet().iterator();
			Integer integ;
			while (iter.hasNext()) {
				s = (String) iter.next();
				it = MapItem.findItemInSet(set, s);
				if (it == null) {
					System.err.println("C Map file: Found a size not corresponding to any variable");
				} else {
					integ = (Integer) sizeMap.get(s);
					it.setSize(integ.intValue());
				}
			}
		} catch (FileNotFoundException fnfex) {
			System.err.println("C map file not found: " + fileName);
			return;
		} catch (Exception ex) {
			System.err.println("C map file reading error: " + ex);
			return;
		}
	}

}
