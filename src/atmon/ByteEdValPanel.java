package atmon;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class ByteEdValPanel extends JPanel implements ActionListener, ChangeListener, KeyListener {

	private static final long serialVersionUID = 8202237332131685552L;

	public static final int BITS_PER_BYTE = 8;

	public static final int BIT_O = 4;
	public static final int BIT_TOP = 4;
	public static final int ED_TOP  = 4;
	public static final int HEX_O  = 20;
	public static final int DEC_O  = 12;
	public static final int EDH_W_O = 14;
	public static final int EDH_W_M = 16; 
	public static final int EDD_W_O = 22;
	public static final int EDD_W_M = 20;
	public static final int ED_H   = 26;
	public static final int WIDTH_O = 10;
	public static final int HEIGHT = 32;

	private Comm comm;
	private JFrame fr;
	private long value, range;
	private int addr;
	private boolean ioPort;

	private BitBoxPanel bits;
	private JTextField hexTF;
	private JSpinner decTF;

	private int bitCnt, byteCnt;

	public ByteEdValPanel(JFrame fr, Comm comm, int addr, boolean ioPort, int refresh, int byteCnt) {
		this.fr = fr;
		this.comm = comm;
		this.addr = addr;
		this.ioPort = ioPort;
		this.byteCnt = byteCnt;
		bitCnt = byteCnt * BITS_PER_BYTE;
		value = 0;
		range = (long) Math.pow(2, bitCnt);
		setLayout(null);

		bits = new BitBoxPanel(this, bitCnt);
		bits.setLocation(BIT_O, BIT_TOP);
		add(bits);
		/*JScrollPane bitsPane = new JScrollPane(bits);
		bitsPane.setLocation(BIT_O, BIT_TOP);
		bitsPane.setSize(bits.getWidth()-100, bits.getHeight()+40);
		bitsPane.setMinimumSize(new Dimension(200, 30));
		bitsPane.setBackground(Color.BLUE);
		bitsPane.setBorder(null);
		bitsPane.setViewportBorder(null);
		add(bitsPane);*/
		
		
		String s = "";
		for (int i = 0; i < byteCnt; i++) {
			s += "00";
		}
		hexTF = new JTextField(s);
		hexTF.setSize(byteCnt*EDH_W_M+EDH_W_O, ED_H);
		hexTF.setLocation(bits.getLocation().x + bits.getWidth() + HEX_O, ED_TOP);
		hexTF.addActionListener(this);
		hexTF.addKeyListener(this);
		hexTF.setMargin(new Insets(3, 4, 3, 4));
		hexTF.setFont(new Font("Monospaced", Font.PLAIN, 14));
		add(hexTF);
		
		decTF = new JSpinner(new SpinnerNumberModel(new Long(0L), new Long(0L), new Long(range-1L), new Long(1L)));
		decTF.setSize(byteCnt*EDD_W_M+EDD_W_O, ED_H);
		decTF.setLocation(hexTF.getLocation().x + hexTF.getWidth() + DEC_O, ED_TOP);
		decTF.addChangeListener(this);
		decTF.addKeyListener(this);
		add(decTF);
		
		setSize(decTF.getLocation().x + decTF.getWidth() + WIDTH_O, HEIGHT);
		//readValue();
	}

	public void changeBit(int idx) {
		value = value ^ (1L << idx);
		refreshValue();
		sendValue();
	}
	
	public void setValue(long val) {
		value = val;
		refreshValue();
	}
	
	private void refreshValue() {
		bits.refreshValue(value);
		String s = "";
		byte b;
		for (int i = 0; i < byteCnt; i++) {
			b = (byte) (value >>> (i*BITS_PER_BYTE));
			s = Utils.byteToHex(b) + s;
		}
		hexTF.setText(s);
		decTF.removeChangeListener(this);
		decTF.setValue(new Long(value));
		decTF.addChangeListener(this);
		hexTF.selectAll();
	}
	
	/*
	public void readValue() {
		long val = 0, v;
		if (ioPort) {
			for (int i = 0; i < byteCnt; i++) {
				v = comm.readIO(addr+i);
				if (v < 0) {return;}
				val |= v << (i*BITS_PER_BYTE); 
			}
			value = val;
		} else {
			byte[] data = comm.readMem(addr, byteCnt);
			if ((data == null) || (data.length < byteCnt)) {return;}
			for (int i = 0; i < byteCnt; i++) {
				v = Utils.ub(data[i]);
				val |= v << (i*BITS_PER_BYTE); 
			}
			value = val;			
		}		
		refreshValue();
	}
	*/
	
	private void sendValue() {
		long v;
		if (ioPort) {
			for (int i = 0; i < byteCnt; i++) {
				v = (value >>> (i*BITS_PER_BYTE)) & 0xFF; 
				comm.writeIO(addr+i, (int) v);
			}			
		} else {
			byte[] data = new byte[byteCnt];
			for (int i = 0; i < byteCnt; i++) {
				v = (value >>> (i*BITS_PER_BYTE)) & 0xFF;
				data[i] = (byte) v;
			}
			comm.writeMem(data, addr);		
		}
	}

	public void stateChanged(ChangeEvent ev) {
		if (ev.getSource() == decTF) {
			Long l = (Long) decTF.getValue();
			long val = l.longValue();
			if ((val >= 0) && (val < range)) {
				value = val;
				sendValue();
			}			
		}
		refreshValue();
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == hexTF) {
			try {
				long val = Long.parseLong(hexTF.getText(), 16);
				if ((val >= 0) && (val < range)) {
					value = val;
					sendValue();
				}
			} catch (Exception ex) { }
			refreshValue();		
		}
	}

	public void keyTyped(KeyEvent ev) { }

	public void keyPressed(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
			fr.dispose();
			ev.consume();
		} else if (ev.isAltDown()) {
			char key = ev.getKeyChar();
			if (Character.isDigit(key)) {
				int i = Character.getNumericValue(key);
				if ((i >= 0) && (i < bitCnt)) {
					ev.consume();
					changeBit(i);
				}
			}
		}
	}

	public void keyReleased(KeyEvent ev) { }

	public void focusHex() {
		hexTF.requestFocusInWindow();
	}
}
