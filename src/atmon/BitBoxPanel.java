package atmon;

//import java.awt.*;
import javax.swing.*;

public class BitBoxPanel extends JPanel { //implements Scrollable {

	private static final long serialVersionUID = -7284710245320865611L;

	public static final int BIT_SP = 4;

	private ByteEdValPanel vp;
	private BitBox[] bits;
	private int bitCnt;
//	private JScrollBar bar;

	public BitBoxPanel(ByteEdValPanel vp, int bitCnt) {
		this.vp = vp;
		this.bitCnt = bitCnt;
		setLayout(null);
		bits = new BitBox[bitCnt];

		int x, bw = BitBox.W+BIT_SP;
		//if (bw.bitCnt)
		for (int i = 0; i < bitCnt; i++) {
			bits[i] = new BitBox(this, i);
			x = (bitCnt-1)*bw + BIT_SP - i*bw;
			bits[i].setLocation(x, 0);
			add(bits[i]);
		}
		setSize(bw*bitCnt, BitBox.H);

	
	
        //setAutoscrolls(true); //enable synthetic drag events
    //    addMouseMotionListener(this); //handle mouse drags
	}
	
	public void changeBit(int idx) {
		vp.changeBit(idx);
	}
	
	public void refreshValue(long value) {
		for (int i = 0; i < bitCnt; i++) {
			bits[i].setValue((value & (1L << i)) != 0);			
		}
		repaint();
	}
	
	
   // private int maxUnitIncrement = 1;
    //private boolean missingPicture = false;


/*    public void mouseMoved(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) {
        //The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }*/
/*
    public Dimension getPreferredSize() {
        if (missingPicture) {
            return new Dimension(320, 480);
        } else {
            return super.getPreferredSize();
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }*/
}
