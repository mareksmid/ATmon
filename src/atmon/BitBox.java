package atmon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BitBox extends JComponent implements MouseListener {

	private static final long serialVersionUID = 7947389859136429223L;

	private boolean value;
	private BitBoxPanel bp;
	private int idx;
	
	public static final int W = 16;
	public static final int H = 26;
	
	private static final int ONE_W = 4;
	private static final int ONE_O = 4;
	private static final int ZERO_W  = 1;
	private static final int ZERO_XO = W/5;
	private static final int ZERO_YO = 6;

	private static final Color BACK0_CL = new Color(0x007AA8);
	private static final Color BACK1_CL = new Color(0xB31600);
	private static final Color FONT0_CL = new Color(0xFFA800);
	private static final Color FONT1_CL = new Color(0xFFD480);
	private static final Color FRAME_CL = Color.BLACK;
	
	public BitBox(BitBoxPanel bp, int idx) {
		this.bp = bp;
		this.idx = idx;
		value = false;
		setSize(W, H);
		this.addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		g.setColor(value ? BACK1_CL : BACK0_CL);
		g.fillRect(0, 0, W, H);
		g.setColor(FRAME_CL);
		g.drawRect(0, 0, W-1, H-1);
		
		g.setColor(value ? FONT1_CL : FONT0_CL);
		
		if (value) {
			g.fillRect(W/2 - ONE_W/2, ONE_O, ONE_W, H-2*ONE_O);
		} else {
			g.fillOval(ZERO_XO, ZERO_YO, W-2*ZERO_XO, H-2*ZERO_YO);
			g.setColor(BACK0_CL);
			g.fillOval(ZERO_XO+ZERO_W, ZERO_YO+ZERO_W, W-2*(ZERO_XO+ZERO_W), H-2*(ZERO_YO+ZERO_W));
		}
	}
	
	public void setValue(boolean val) {
		value = val;
//		this.repaint();
	}

	public void mouseClicked(MouseEvent ev) {
		bp.changeBit(idx);		
	}

	public void mousePressed(MouseEvent ev) { }

	public void mouseReleased(MouseEvent ev) { }

	public void mouseEntered(MouseEvent ev) { }

	public void mouseExited(MouseEvent ev) { }
	
}
