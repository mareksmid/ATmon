package atmon;

import java.awt.*;
import javax.swing.*;

public class ProgressDialog extends JDialog {
	
	private static final long serialVersionUID = -4500752896738799614L;

	private JProgressBar bar;
	
	public ProgressDialog(JFrame parent, String title, int min, int max) {
		super(parent, title, false);
		setSize(400, 80);
		setResizable(false);
		Utils.centerFrame(this);
		Container root = getContentPane();
		root.setLayout(null);
		
		bar = new JProgressBar(JProgressBar.HORIZONTAL, min, max);
		bar.setValue(min);		
		root.add(bar);
		bar.setSize(getWidth()-40, 20);
		setVisible(true);
		bar.setLocation((root.getWidth()-bar.getWidth())/2, (root.getHeight()-bar.getHeight())/2);
	}
	
	
	public void setPos(int pos) {
		bar.setValue(pos);
	}
	
	public void close() {
		setVisible(false);
	}
}
