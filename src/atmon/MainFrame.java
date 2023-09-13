package atmon;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.text.*;

import java.io.*;

public class MainFrame extends JFrame implements ActionListener, KeyListener, WindowListener {

	private static final long serialVersionUID = -3193129466141597606L;

	private static final String VERSION_TEXT = "version: 2.0";

	private static final String MSOE_LOGO_FILE = "msoe-small.gif";
	
//	private static final int MAX_PANE_LENGTH = 20000;
	
	private static final int MAX_CMD_HIST_SIZE = 20;
	
	private static final Color SYS_OUT_CL = Color.BLUE;
	private static final Color SYS_ERR_CL = Color.RED;
	private static final Color CMD_CL = new Color(0x3366CC);
	
	private static final Color DATA_CL = Color.DARK_GRAY;
	private static final Color DATA_HDR_CL = new Color(0xCC6666);
	private static final Font DATA_FONT = new Font("Monospaced", Font.PLAIN, 12);
	private static final Color HEAD_CL = new Color(0x990000);
	private static final Font MAP_FONT = new Font("Serif", Font.PLAIN, 10);	
	private static final Color DEFAULT_CL = Color.BLACK;
	private static final Color ABOUT_CL = new Color(0x3333ff);
	private static final Color HELP_CL = new Color(0x660066);
	private static final Font CMD_LIST_FONT = new Font("SansSerif", Font.PLAIN, 12);
	private static final Color LISTING_CL = new Color(0x003399);
	private static final Font LISTING_FONT = new Font("Monospaced", Font.PLAIN, 12);
	
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 14);

	
	public static final String HEX_EXT = ".hex";
	public static final String MAP_EXT = ".map";
	public static final String EEP_EXT = ".eep";
	public static final String LST_EXT = ".lst";
	public static final String LST_C_EXT = ".lss";
		
	//private HashMap globalMap, sourceMap, sourceVarSizeMap;
	private HashSet globalMap, sourceMap;
		
	Comm comm;
		
	JTextField cmdTF;
	private String[][] buttons = {
		{"help", "h"},
		{"about", "a"},
		{"config", "cfg"},
		{"clear", "c"},
		{"reset", "r"},
		{"close", "cl"},
		{"open", "op"},
		{"term", "t"},
		{"exit", "x"}
	};
	private String[][] hotkeys = {
		{"F5", "step"}	
	};
	ArrayList cmdHist;
	int cmdHistPos;
	JTextPane pane;
	MutableAttributeSet paneAttrs;
	StyledDocument paneDoc;
	JScrollPane scrollPane;
	Image msoeLogo;
	
	private String appPath;
	private Config cfg;
	private CmdProcessor cmdProc;
	private FuseLockBitsCodec flbc;
		
	public MainFrame(String appPath) {
		this.appPath = appPath;
		Container root = getContentPane();
		root.setLayout(new BorderLayout());
		setTitle("ATmon");
		addWindowListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		cmdTF = new JTextField();
		pane = new JTextPane();
		pane.setEditable(false);
        paneAttrs = pane.getInputAttributes();		
        paneDoc = pane.getStyledDocument();
		JPanel cmdPanel = new JPanel(), butPanel = new JPanel();
		cmdPanel.setLayout(new BorderLayout());
		butPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		butPanel.setPreferredSize(new Dimension(80, 0));
		cmdPanel.add(cmdTF);
		root.add(butPanel, BorderLayout.EAST);
		scrollPane = new JScrollPane(pane);
		root.add(scrollPane, BorderLayout.CENTER);
		root.add(cmdPanel, BorderLayout.SOUTH);
		cmdTF.addActionListener(this);
		cmdTF.addKeyListener(this);
		pane.addKeyListener(this);
		System.setOut(new PrintStream(new SysOutputStream(this, SYS_OUT_CL), true));
		System.setErr(new PrintStream(new SysOutputStream(this, SYS_ERR_CL), true));

		msoeLogo = Utils.readImage(this, appPath, MSOE_LOGO_FILE);
		if (msoeLogo != null) {butPanel.add(new JLabel(new ImageIcon(msoeLogo)));}
		JButton but;
		for (int i = 0; i < buttons.length; i++) {
			but = new JButton(buttons[i][0]);
			but.setActionCommand(buttons[i][1]);
			butPanel.add(but);
			but.addActionListener(this);
			but.setMargin(new Insets(4, 4, 4, 4));
			but.setPreferredSize(new Dimension(64, 25));
			but.addKeyListener(this);
		}
		
		setSize(800, 600);
		Utils.centerFrame(this);
		setVisible(true);
		cmdTF.requestFocusInWindow();
	}

	public void actionPerformed(ActionEvent e) {		
		if (e.getSource() == cmdTF) {
			String cmd = cmdTF.getText();
			cmdTF.setText(null);
			
			pane.selectAll();
			if ((cmd == null) || (cmd.equals(""))) {return;}

			int i;
			if ((i = cmdHist.indexOf(cmd)) >= 0) {
				cmdHist.remove(i);
			}
			cmdHist.add(0, cmd);
			if ((i = cmdHist.size()) > MAX_CMD_HIST_SIZE) {
				cmdHist.remove(i-1);
			}
			cmdHistPos = -1;
			showMsgLn(cmd, CMD_CL);
			cmdProc.procCmd(cmd);
		} else if (e.getSource() instanceof JButton) {
			cmdProc.procCmd(e.getActionCommand());
		}
	}
	
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			e.consume();
			if (cmdHistPos < cmdHist.size()-1) {
				cmdHistPos++;
				cmdTF.setText((String) cmdHist.get(cmdHistPos));
			} else {
				cmdHistPos = cmdHist.size()-1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			e.consume();
			if (cmdHistPos > 0) {
				cmdHistPos--;
				if (cmdHistPos < cmdHist.size()) {cmdTF.setText((String) cmdHist.get(cmdHistPos));}
			} 
		} else {
			String key = KeyEvent.getKeyText(e.getKeyCode());
			for (int i = 0; i < hotkeys.length; i++) {
				if (key.equals(hotkeys[i][0])) {
					cmdProc.procCmd(hotkeys[i][1]);
					break;
				}
			}
		}
	}
	
	public void keyReleased(KeyEvent e) { }
	
	public void keyTyped(KeyEvent e) {
		if (e.getSource() != cmdTF) {
			char ch = e.getKeyChar();
			if (Character.isDigit(ch) || Character.isLetter(ch)) {
				cmdTF.setText(cmdTF.getText() + ch);
				cmdTF.requestFocusInWindow();
				e.consume();
			}
		}		
	}

	public void dispData(int addr, byte[] data, boolean wordMode) {
		if ((data == null) || (data.length == 0)) {
//			System.err.println("No data received");
			return;
		}
		if (wordMode && ((data.length%2 != 0) || (addr%2 != 0))) {
			System.err.println("Data length or beginning address is not even");
			return;
		}
		String s = "     ";
		if (wordMode) {for (int j = 0; j < 8; j++) {s += "   x" + Integer.toHexString(2*j).toUpperCase();}}
		else {for (int j = 0; j < 16; j++) {s += " x" + Integer.toHexString(j).toUpperCase();}}
		showMsgLn(s, DATA_HDR_CL, DATA_FONT);
		int off = 0;
		int begb, begl;
		int lng = data.length;
		if (wordMode) {
			lng /= 2;
			begb = (addr/2) % 8;
			begl = (8 - begb) % 8;
		} else {
			begb = addr%16;
			begl = (16 - begb)%16;
		}
		int mid = 0, end = 0;
		if (lng > begl) {
			if (wordMode) {end = (addr/2 + lng) % 8;}
			else {end = (addr+lng)%16;}
			mid = lng - begl - end;
		} else {
			begl = lng;
		}
		if (begl > 0) {
			showMsg(Utils.wordToHex(addr-begb*(wordMode?2:1)) + ":", DATA_HDR_CL, DATA_FONT);
			s = "";
			for (int j = 0; j < (wordMode?8:16); j++) {
				if (j < begb+begl) {
					if (j < begb) {
						s += wordMode?"     ":"   ";
					} else {
						if (wordMode) {
							s += " " + Utils.byteToHex(Utils.ub(data[off+1])) + Utils.byteToHex(Utils.ub(data[off]));
							off += 2;
						} else {
							s += " " + Utils.byteToHex(data[off]);
							off++;
						}
					}					
				}
			}
			showMsgLn(s, DATA_CL, DATA_FONT);			
		}
		if (mid > 0) {
			for (int i = 0; i < (mid/(wordMode?8:16)); i++) {
				showMsg(Utils.wordToHex(off+addr) + ":", DATA_HDR_CL, DATA_FONT);
				s = "";
				for (int j = 0; j < (wordMode?8:16); j++) {
					if (wordMode) {
						s += " " + Utils.byteToHex(Utils.ub(data[off+1])) + Utils.byteToHex(Utils.ub(data[off]));
						off += 2;
					} else {
						s += " " + Utils.byteToHex(Utils.ub(data[off]));
						off++;
					}
				}
				showMsgLn(s, DATA_CL, DATA_FONT);
			}
		}
		if (end > 0) {
			showMsg(Utils.wordToHex(off+addr) + ":", DATA_HDR_CL, DATA_FONT);
			s = "";
			for (int j = 0; j < (wordMode?8:16); j++) {
				if (j < end) {
					if (wordMode) {
						s += " " + Utils.byteToHex(Utils.ub(data[off+1])) + Utils.byteToHex(Utils.ub(data[off]));
						off += 2;
					} else {						
						s += " " + Utils.byteToHex(data[off]);
						off++;
					}
				}
			}
			showMsgLn(s, DATA_CL, DATA_FONT);			
		}
	}
		
	
	
	public void dispValue(int addr, int data) {
		if (data < 0) {System.err.println("Reading " + addr + " failed");}
		else {showMsgLn(Utils.wordToHex(addr) + ": " + Utils.byteToHex(data) + " (dec " + data + ")", DATA_CL);}		
	}

	/*
	public void showFile(String fileName) {
		try {
			File f = new File(appPath);
			InputStreamReader isr;			
			if (f.isFile()) {
				JarFile jf = new JarFile(f);
				isr = new InputStreamReader(jf.getInputStream(jf.getEntry(fileName)));
			} else {
				f = new File(f, fileName);
				isr = new InputStreamReader(new FileInputStream(f));
			}
			
			int cnt;
			char[] buf;
			while (isr.ready()) {
				buf = new char[FILE_BUF_SIZE];
				cnt = isr.read(buf);
				if (cnt > 0) {showMsg(new String(buf, 0, cnt), DEFAULT_CL, DEFAULT_FONT);}
			}
		} catch (IOException ex) {
			System.err.println("Error loading file: " + ex);
		}
	}*/

	public void showMsgLn(String txt) {
		showMsg(txt+"\n", DEFAULT_CL, DEFAULT_FONT);
	}

	public void showMsgLn(String txt, Color col) {
		showMsg(txt+"\n", col, DEFAULT_FONT);
	}

	public void showMsgLn(String txt, Color col, Font font) {
		showMsg(txt+"\n", col, font);
	}
	
	public void showMsgsLn(String[] txts, Color col, Font font) {
		for (int i = 0; i < txts.length; i++) {
			showMsgLn(txts[i], col, font);
		}		
	}

	public void showMsg(String txt, Color col, Font font) {
		StyleConstants.setFontFamily(paneAttrs, font.getFamily());
        StyleConstants.setFontSize(paneAttrs, font.getSize());
        StyleConstants.setItalic(paneAttrs, (font.getStyle() & Font.ITALIC) != 0);
        StyleConstants.setBold(paneAttrs, (font.getStyle() & Font.BOLD) != 0);
        StyleConstants.setForeground(paneAttrs, col);

        try {
//			int i = paneDoc.getLength();
//			if (i > MAX_PANE_LENGTH) {paneDoc.remove(0, i-MAX_PANE_LENGTH);}
			paneDoc.insertString(paneDoc.getLength(), txt, paneAttrs);
//			pane.scrollRectToVisible(new Rectangle(0, 20000, 100, 20100));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void showHelp() {
		showMsgLn("Available commands:", HELP_CL, DEFAULT_FONT);
		showMsgsLn(Texts.CMD_HELP_TEXT, HELP_CL, CMD_LIST_FONT);
	}
	
	public void showAbout() {
		showMsgsLn(Texts.ABOUT_TEXT, ABOUT_CL, DEFAULT_FONT);
		showMsgLn(VERSION_TEXT, ABOUT_CL, DEFAULT_FONT);		
	}
	
	public void loadIncludeFile() {
		MapFileReader.loadIncludeMap(globalMap, cfg.includeFile, " \t=", ".");
	}

	public void loadSourceMap() {
		if (cfg.sourceIsC) {
			MapFileReader.loadCMap(sourceMap, cfg.sourceFile + MAP_EXT);
		} else {
			MapFileReader.loadIncludeMap(sourceMap, cfg.sourceFile + MAP_EXT, " \t", "");
		}
	}

	
	public void setSourceFiles(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot < 0) {
			System.err.println("Invalid file name: " + fileName);
			return;
		}
		cfg.sourceFile = fileName.substring(0, dot);
		File f, fC;
		f  = new File(cfg.sourceFile + LST_EXT);
		fC = new File(cfg.sourceFile + LST_C_EXT);
		if (f.exists()) {cfg.sourceIsC = false;}
		else if (fC.exists()) {cfg.sourceIsC = true;}
		else {cfg.sourceIsC = false;}
		
		loadSourceMap();
	}
	
	public int translateName(String name) {
		name = name.trim();
		StringTokenizer st = new StringTokenizer(name, "()+-*", true);
		return translateName(st, true);
	}

	public int translateName(StringTokenizer st, boolean root) {
		String s;
		ArrayList elements = new ArrayList();
		while (st.hasMoreTokens()) {
			s = st.nextToken().trim();
			if (s.equals("(")) {
				elements.add(new Integer(translateName(st, false)));
			} else if (s.equals(")")) {
				if (root) {
					System.err.println("Unbalanced parentheses");
					return -1;
				} else {
					return evaluate(elements);
				}				
			} else if ("+-*".indexOf(s) >= 0) {
				elements.add(s);
			} else {
				elements.add(new Integer(translateVal(s)));
			}
		}
		if (root) {
			return evaluate(elements);
		} else {
			System.err.println("Unbalanced parentheses");
			return -1;			
		}
	}
	
	private int evaluate(ArrayList elements) {
		int times;
		int i1, i2;
		while ((times = elements.indexOf("*")) >= 0) {
			if ((times < 1) || (times > elements.size()-2)) {
				System.err.println("Expression error in multiplication");
				return -1;
			}
			if (!((elements.get(times-1) instanceof Integer) && (elements.get(times+1) instanceof Integer))) {
				System.err.println("Expression type error in multiplication");
				return -1;				
			}
			i1 = ((Integer) elements.get(times-1)).intValue(); 
			i2 = ((Integer) elements.get(times+1)).intValue();
			if ((i1 < 0) || (i2 < 0)) {
				System.err.println("Expression value error in multiplication");
				return -1;
			}
			elements.set(times, new Integer(i1*i2));
			elements.remove(times+1);
			elements.remove(times-1);
		}
		int val = 0;
		boolean plus = true;
		String s;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof String) {
				s = (String) elements.get(i);
				if (s.equals("+")) {plus = true;}
				else if (s.equals("-")) {plus = false;}
				else {
					System.err.println("Invalid sign");
					return -1;									
				}				
			} else if (elements.get(i) instanceof Integer) {
				i1 = ((Integer) elements.get(i)).intValue();
				if (i1 < 0) {
					System.err.println("Expression value error in addition/subtraction");
					return -1;					
				}
				if (plus) {val += i1;}
				else      {val -= i1;}
			} else {
				System.err.println("Expression data type error");
				return -1;				
			}
		}
		return val;
	}
	
	public int translateVal(String name) {
		if ((name == null) || (name.equals(""))) {
			System.err.println("Empty value string");
			return -1;
		}
		String n = name.toUpperCase();		
		boolean defHex = cfg.defaultHex;
		int i;
		MapItem it;
		while ((i = Utils.strToInt(n, defHex)) < 0) {
			it = MapItem.findItemInSet(sourceMap, n);
			if (it != null) {
				n = it.getValue().toUpperCase();
				defHex = true;
				continue;
			}
			it = MapItem.findItemInSet(globalMap, n);
			if (it != null) {
				n = it.getValue().toUpperCase();
				defHex = false;
				continue;
			}
			if (n.startsWith("R")) {
				i = Utils.strToInt(n.substring(1), false);
				if ((i >= 0) && (i < 32)) {return i;}
				else {
					System.err.println("Invalid register");
					return -1;
				}
			}
			System.err.println("Symbol not found");
			return -1;
		}
		return i;
	}

	public int translateNameToSize(String name) {
		if ((name == null) || (name.equals(""))) {return -1;}
		String n = name.toUpperCase();
		
		MapItem it = MapItem.findItemInSet(sourceMap, n);
		if (it == null) {
			return MapItem.DEFAULT_SIZE;
		} else {
			return it.getSize();
		}
	}
	

	public void windowOpened(WindowEvent ev) {
		cmdHist = new ArrayList();
		cmdHistPos = -1;
		globalMap = new HashSet();
		sourceMap = new HashSet();
		//sourceVarSizeMap = new HashMap();
		cfg = new Config(appPath, cmdHist);
		cfg.load();		
		comm = new Comm(this);
		comm.open(cfg.portName);
		loadIncludeFile();
		loadSourceMap();
		flbc = new FuseLockBitsCodec(this, comm);
		cmdProc = new CmdProcessor(this, comm, cfg, flbc);
	}

	public void windowClosing(WindowEvent ev) { }

	public void windowClosed(WindowEvent ev) {
		comm.close();
		cfg.save();
		System.exit(0);
	}

	public void windowIconified(WindowEvent ev) { }

	public void windowDeiconified(WindowEvent ev) { }

	public void windowActivated(WindowEvent ev) { }

	public void windowDeactivated(WindowEvent ev) { }

	public void showMap() {
		Iterator iter;
		MapItem it;
		showMsgLn("GLOBAL:\n", HEAD_CL, MAP_FONT);
		iter = globalMap.iterator();
		while (iter.hasNext()) {
			it = (MapItem) iter.next();
			showMsgLn(it.getName() + ": " + it.getValue(), DATA_CL, MAP_FONT);
		}
		showMsgLn("\nSOURCE:\n", HEAD_CL, MAP_FONT);
		iter = sourceMap.iterator();
		while (iter.hasNext()) {
			it = (MapItem) iter.next();
			showMsgLn(it.getName() + ": " + it.getValue() + " [" + it.getSize() + "] (" + it.getTypeStr() + ")" , DATA_CL, MAP_FONT);
		}
		/*if (cfg.sourceIsC) {
			showMsgLn("\nSOURCE VAR SIZES:\n", HEAD_CL, MAP_FONT);
			iter = sourceVarSizeMap.keySet().iterator();
			while (iter.hasNext()) {
				s = (String) iter.next();
				showMsgLn(s + ": " + sourceVarSizeMap.get(s), DATA_CL, MAP_FONT);
			}		
		}*/
	}

	public void showListing() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(cfg.sourceFile + (cfg.sourceIsC ? LST_C_EXT : LST_EXT)));
			String ln = br.readLine();
			while (ln != null) {
				this.showMsgLn(ln, LISTING_CL, LISTING_FONT);
				ln = br.readLine();
			}
		} catch (FileNotFoundException fnfex) {
			System.err.println("Listing file not found");
		} catch (IOException ioex) {
			System.err.println("Listing file reading error: " + ioex);
		}
	}
	
	public MapItem findNearestFlashSymbol(int addr) {
		Iterator iter = sourceMap.iterator();
		MapItem it, nearest = null;
		int val, nearestVal = -1;
		while (iter.hasNext()) {
			it = (MapItem) iter.next();
			if (it.getType() != MapItem.TYPE_FLASH) {continue;}
			val = translateName(it.getValue());
			if (val < 0) {continue;}
			if (val > addr) {continue;}
			if (nearest == null) {
				nearest = it;
				nearestVal = val;
			} else {
				if (val > nearestVal) {
					nearest = it;
					nearestVal = val;
				}
			}
		}
		return nearest;
	}

	public String convFlashAddrToString(int addr) {
		MapItem item = findNearestFlashSymbol(addr);
		String s = "";
		if (item != null) { 
			int itemAddr = translateName(item.getValue());
			s += item.getName();
			if (addr-itemAddr > 0) {s += "+" + (addr-itemAddr);}
			s += " ";
		}
		s += "[" + Utils.wordToHex(addr) + "]";
		return s;
	}


}
