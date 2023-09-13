package atmon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TermFrame extends JFrame implements WindowListener, KeyListener, Runnable {
	
	private static final long serialVersionUID = 5411727236337430498L;

	private JTextArea term;
	private Comm comm;	
	private boolean running;
	
	public TermFrame(Comm comm) {
		this.comm = comm;
		setTitle("ATmon terminal");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		Container root = getContentPane();
		root.setLayout(new BorderLayout());
		term = new JTextArea();
		
		term.addKeyListener(this);
		term.setEditable(false);
		root.add(term);
		
		
		setSize(400, 300);
		Utils.centerFrame(this);
		setVisible(true);
		comm.setTermMode(true);
		Thread t = new Thread(this);
		t.start();
	}

	public void windowOpened(WindowEvent ev) { }

	public void windowClosing(WindowEvent ev) { }

	public void windowClosed(WindowEvent ev) {
		running = false;
	}

	public void windowIconified(WindowEvent ev) { }

	public void windowDeiconified(WindowEvent ev) { }

	public void windowActivated(WindowEvent ev) { }

	public void windowDeactivated(WindowEvent ev) { }

	public void keyTyped(KeyEvent ev) {
		comm.send((byte) ev.getKeyChar());
	}

	public void keyPressed(KeyEvent ev) {
	}

	public void keyReleased(KeyEvent ev) {
	}
	
	public void run() {
		running = true;
		int v;
		while (running) {
			v = comm.read();
			if (v < 0) {
				try {Thread.sleep(2);}
				catch (Exception ex) {
					System.err.println("Term thread interrupted");
					break;
				}
			} else {
				term.append(""+(char) v);
			}
		}
		comm.setTermMode(false);
	}

}
