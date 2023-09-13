package atmon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LockBitsFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 8655826347623602408L;

	private static final String[] GEN_PROT_STRINGS = {
		"Prog. and verif. of the Flash and EEPROM is disabled",
		"[Reserved]",
		"Prog. of the Flash and EEPROM is disabled",
		"No memory lock features enabled"
	};
	
	private static final String[] APP_PROT_STRINGS = {
		"SPM not allowed to write App. sect., LPM in Boot sect. not allowed to read App. sect.",
		"LPM in Boot sect. not allowed to read from App. sect.", 
		"SPM not allowed to write App. sect.",
		"No restrictions for SPM or LPM accessing App. sect."
	};
	                            
	private static final String[] BOOT_PROT_STRINGS = {
		"SPM not allowed to write Boot sect., LPM in App. sect. not allowed to read Boot sect.",
		"LPM in App. sect. not allowed to read Boot sect.",
		"SPM not allowed to write Boot sect.",
		"No restrictions for SPM or LPM accessing Boot sect."
	};
	
	private static final int CB_W = 520;
	private static final int CB_H = 26;
	                            
	private Comm comm;
	
	private JButton refreshBut, writeBut;
	private JComboBox genProtCB, appProtCB, bootProtCB;

	public LockBitsFrame(Comm comm) {
		this.comm = comm;
		setTitle("Lock bits editor");
		Container root = getContentPane();
		root.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		Dimension cbDim = new Dimension(CB_W, CB_H);
		refreshBut = new JButton("refresh");
		refreshBut.addActionListener(this);
		writeBut = new JButton("write");
		writeBut.addActionListener(this);
		genProtCB = new JComboBox(GEN_PROT_STRINGS);
		genProtCB.setSize(cbDim);
		genProtCB.setPreferredSize(cbDim);
		genProtCB.setMaximumSize(cbDim);
		genProtCB.setMinimumSize(cbDim);
		appProtCB = new JComboBox(APP_PROT_STRINGS);
		appProtCB.setSize(cbDim);
		appProtCB.setPreferredSize(cbDim);
		appProtCB.setMaximumSize(cbDim);
		appProtCB.setMinimumSize(cbDim);
		bootProtCB = new JComboBox(BOOT_PROT_STRINGS);
		bootProtCB.setSize(cbDim);
		bootProtCB.setPreferredSize(cbDim);
		bootProtCB.setMaximumSize(cbDim);
		bootProtCB.setMinimumSize(cbDim);
		
		gbc.gridy = 0;
		gbc.insets = new Insets(6, 8, 6, 8);
		
		gbc.gridx = 0; gbc.gridwidth = 2;
		root.add(genProtCB, gbc);
		gbc.gridy++;
		root.add(appProtCB, gbc);
		gbc.gridy++;
		root.add(bootProtCB, gbc);

		gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 1;
		root.add(refreshBut, gbc);
		gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_END;
		root.add(writeBut, gbc);
		
		setSize(560, 220);
		Utils.centerFrame(this);
		setVisible(true);
		refreshLocks();
		
		//val = mf.translateName(st.nextToken());
		//comm.writeLBits(val);

	}
	
	private void refreshLocks() {
		byte[] bits = comm.readFLBits();
		if ((bits == null) || (bits.length < 4)) {
			System.err.println("Fuses / lock bits not read");
			return;
		}
		int locks = Utils.ub(bits[1]);

		genProtCB.setSelectedIndex(locks & 3);
		appProtCB.setSelectedIndex((locks >>> 2) & 3);
		bootProtCB.setSelectedIndex((locks >>> 4) & 3);
	}

	private void writeLocks() {
		int locks = 3<<6;

		locks |= genProtCB.getSelectedIndex();
		locks |= appProtCB.getSelectedIndex() << 2;
		locks |= bootProtCB.getSelectedIndex() << 4;
		
		comm.writeLBits(locks);
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == refreshBut) {
			refreshLocks();
		} else if (ev.getSource() == writeBut) {
			writeLocks();
		}
	}
	
}
