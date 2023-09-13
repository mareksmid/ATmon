package atmon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.jar.JarFile;
import javax.imageio.*;

public class Utils {

	public static String byteToHex(int value) {
		if (value < 0) {value += 0x100;}
		String s = Integer.toHexString(value);
		if (s.length() < 2) {s = "00"+s;}
		return s.substring(s.length()-2, s.length()).toUpperCase();		
	}

	public static String wordToHex(int value) {
		if (value < 0) {value += 0x10000;}
		String s = Integer.toHexString(value);
		if (s.length() < 4) {s = "0000"+s;}
		return s.substring(s.length()-4, s.length()).toUpperCase();		
	}
	
	public static void centerFrame(Window w) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		w.setLocation((tk.getScreenSize().width - w.getWidth())/2, (tk.getScreenSize().height - w.getHeight())/2);
	}
	
	public static String colorToString(Color cl) {
		String s = Integer.toHexString(cl.getRGB() & 0xFFFFFF);
		if (s.length() < 6) {
			s = ("000000"+s);
			s = s.substring(s.length()-6, s.length());			
		}
		return "#" + s.toUpperCase();
	}
	
	public static Image readImage(Component comp, String appPath, String fileName) {
	    try {
			InputStream is; 
			File f = new File(appPath);
			if (f.isFile()) {
				JarFile jf = new JarFile(f);
				is = jf.getInputStream(jf.getEntry(fileName));
			} else {
				f = new File(f, fileName);
				is = new FileInputStream(f);
			}
			BufferedImage img = ImageIO.read(is);
			return img;
	    } catch (Exception ex) {
	    	System.err.println("Loading image " + fileName + " failed");
	    	return null;
	    } 
	}

	public static int strToInt(String s) {
		return strToInt(s, false);
	}

	public static int strToInt(String s, boolean defHex) {
		try {
			s = s.toUpperCase();
			int i;
			if (s.startsWith("0X")) {
				i = Integer.parseInt(s.substring(2, s.length()), 16);				
			} else if (s.startsWith("0B")) {
					i = Integer.parseInt(s.substring(2, s.length()), 2);				
			} else if (s.startsWith("0D")) {
				i = Integer.parseInt(s.substring(2, s.length()));				
			} else if (s.startsWith("$")) {
				i = Integer.parseInt(s.substring(1, s.length()), 16);				
			} else {
				if (defHex) {i = Integer.parseInt(s, 16);}
				else {i = Integer.parseInt(s);}
			}
			return i;
		} catch (Exception ex) {return -1;}		
	}

	public static int ub(byte b) {
        if (b < 0) {return 0x100 + b;}
        else {return b;}
    }
}
