package atmon;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ConfigFrame extends JFrame implements ActionListener  {
	
	private static final long serialVersionUID = 8856554391630784432L;

	private MainFrame mf;
	private Config cfg;

	private JLabel portLab, includeLab, sourceLab, edRefrLab, edRefrMsLab;
	private JTextField includeTF, sourceTF;
	private JComboBox portBox;
	private JButton includeBut, sourceBut, cancelBut, okBut;
	private JCheckBox defHexBox;
	private JSpinner edRefrSpinner;
	
	public ConfigFrame(MainFrame mf, Config cfg) {
		this.mf = mf;
		this.cfg = cfg;
		setTitle("ATmon configuration");
		Container root = getContentPane();
		root.setLayout(new BorderLayout());
		JPanel main = new JPanel();
		root.add(main, BorderLayout.CENTER);
		main.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		portLab = new JLabel("Port: ");
		portBox = new JComboBox(mf.comm.getCommPorts());
		portBox.setEditable(true);
		portBox.setSelectedItem(cfg.portName);
		portBox.setMinimumSize(new Dimension(100, 20));
		portBox.setPreferredSize(new Dimension(100, 20));
		
		includeLab = new JLabel("Include file: ");
		includeTF = new JTextField(cfg.includeFile);
		includeTF.setMinimumSize(new Dimension(200, 20));
		includeTF.setPreferredSize(new Dimension(200, 20));
		includeBut = new JButton("select");
		includeBut.addActionListener(this);
		sourceLab = new JLabel("Source file: ");
		sourceTF = new JTextField(cfg.sourceFile + MainFrame.HEX_EXT); 
		sourceTF.setMinimumSize(new Dimension(200, 20));
		sourceTF.setPreferredSize(new Dimension(200, 20));
		sourceBut = new JButton("select");
		sourceBut.addActionListener(this);
		defHexBox = new JCheckBox("default hex values");
		defHexBox.setSelected(cfg.defaultHex);
		edRefrLab = new JLabel("Byte ed. refresh");
		edRefrMsLab = new JLabel("ms");
		edRefrSpinner = new JSpinner();
		edRefrSpinner.setValue(new Integer(cfg.edRefresh));
		edRefrSpinner.setMinimumSize(new Dimension(60, 20));
		edRefrSpinner.setPreferredSize(new Dimension(60, 20));
		cancelBut = new JButton("cancel");
		cancelBut.addActionListener(this);
		okBut = new JButton("ok");
		okBut.addActionListener(this);
		
		gbc.insets = new Insets(6, 6, 6, 6);
//		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(portLab, gbc);		
		gbc.gridx = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(portBox, gbc);
		
		gbc.gridx = 0; gbc.gridy++;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(includeLab, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(includeTF, gbc);		
		gbc.gridx = 3;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		main.add(includeBut, gbc);

		gbc.gridx = 0; gbc.gridy++;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(sourceLab, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		main.add(sourceTF, gbc);		
		gbc.gridx = 3;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		main.add(sourceBut, gbc);

		gbc.gridx = 1; gbc.gridy++;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		main.add(defHexBox, gbc);
		
		gbc.gridx = 0; gbc.gridy++;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		main.add(edRefrLab, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		main.add(edRefrSpinner, gbc);
		gbc.gridx = 2; 
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		main.add(edRefrMsLab, gbc);
		
		gbc.gridx = 1; gbc.gridy++;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.insets = new Insets(20, 6, 6, 6);
		gbc.anchor = GridBagConstraints.CENTER;
		main.add(cancelBut, gbc);		
		gbc.gridx = 2;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		main.add(okBut, gbc);
		
		setSize(500, 300);
		Utils.centerFrame(this);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == includeBut) {
			JFileChooser fc = new JFileChooser(includeTF.getText());
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				includeTF.setText(fc.getSelectedFile().getAbsolutePath());
			}
		} else if (ev.getSource() == sourceBut) {
			JFileChooser fc = new JFileChooser(sourceTF.getText());
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				sourceTF.setText(fc.getSelectedFile().getAbsolutePath());
			}
		} else if (ev.getSource() == cancelBut) {
			dispose();			
		} else if (ev.getSource() == okBut) {
			apply();
			dispose();			
		}
	}
	
	private void apply() {
		//cfg.portName = portTF.getText();
		cfg.portName = (String) portBox.getSelectedItem();
		cfg.includeFile = includeTF.getText();
		mf.setSourceFiles(sourceTF.getText());
		cfg.defaultHex = defHexBox.isSelected();
		cfg.edRefresh = ((Integer) edRefrSpinner.getValue()).intValue();
		
		mf.comm.open(cfg.portName);
		mf.loadIncludeFile();
	}

}
