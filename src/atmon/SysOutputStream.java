package atmon;

import java.io.*;
import java.awt.*;

public class SysOutputStream extends OutputStream {
	
	MainFrame mf;
	Color cl;
	Font font;
	
	public SysOutputStream(MainFrame mf, Color cl) {
		this.mf = mf;		
		this.cl = cl;
		font = new Font("SansSerif", Font.PLAIN, 14);
	}

	public void close() throws IOException {		
	}
	
	public void flush() throws IOException {

	}
	
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		mf.showMsg(new String(b, off, len), cl, font);
	}

	public void write(int b) throws IOException {
		mf.showMsg("" + (char) b, cl, font);
	}
	
}
