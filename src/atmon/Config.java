package atmon;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.*;

public class Config {
	
	private static final String CONFIG_FILE  = "ATmonCfg.xml";
	
	private static final String ROOT_ELEMENT = "ATmon";
	private static final String PORT_ELEMENT = "port";
	private static final String INCLFILE_ELEMENT = "includefile";
	private static final String SRCFILE_ELEMENT = "sourcefile";
	private static final String SRCISC_ELEMENT = "sourceisc";
	private static final String HIST_ELEMENT = "history";
	private static final String HIST_ITEM_ELEMENT = "hi";
	private static final String EDREFR_ELEMENT = "edrefresh";
	private static final String DEFHEX_ELEMENT = "defhex";
	
	String portName, includeFile, sourceFile;
	int edRefresh;
	boolean defaultHex;
	boolean sourceIsC;
	
	private String fileName;
	private ArrayList cmdHist;
	
	public Config(String appPath, ArrayList cmdHist) {
		this.cmdHist = cmdHist;
		File f = new File(appPath);
		if (f.isFile()) {
			fileName = (new File(f.getParentFile(), CONFIG_FILE)).getAbsolutePath();
		} else {
			fileName = (new File(f, CONFIG_FILE)).getAbsolutePath();
		}
		
		portName = "COM1";		
		includeFile = "D:/atmel/AVRTools/AvrAssembler2/Appnotes/m32def.inc";
		sourceFile = "";
		sourceIsC = false;
		edRefresh = 500;
		defaultHex = true;
	}
		
	public void save() {
        try {
            OutputStream os = new FileOutputStream(fileName);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document d = builder.newDocument();
            Element root = d.createElement(ROOT_ELEMENT);
            d.appendChild(root);

            Element n, e;
            
            n = d.createElement(PORT_ELEMENT);            
            n.setTextContent(portName);
            root.appendChild(n);

            n = d.createElement(INCLFILE_ELEMENT);            
            n.setTextContent(includeFile);
            root.appendChild(n);

            n = d.createElement(SRCFILE_ELEMENT);            
            n.setTextContent(sourceFile);
            root.appendChild(n);

            n = d.createElement(SRCISC_ELEMENT);            
            n.setTextContent(""+sourceIsC);
            root.appendChild(n);

            n = d.createElement(EDREFR_ELEMENT);
            n.setTextContent(""+edRefresh);
            root.appendChild(n);

            n = d.createElement(DEFHEX_ELEMENT);
            n.setTextContent(""+defaultHex);
            root.appendChild(n);

            n = d.createElement(HIST_ELEMENT);
            root.appendChild(n);
            for (int i = 0; i < cmdHist.size(); i++) {
                e = d.createElement(HIST_ITEM_ELEMENT);
                e.setTextContent((String) cmdHist.get(i));
                n.appendChild(e);
            }

            /*
            Element rules = d.createElement("rules");
            root.appendChild(rules);
            for (int i = 0; i < fz.rules.size(); i++) {
                r = (Rule) fz.rules.get(i);
                n = d.createElement("rule");
                for (int j = 0; j < r.getInputCount(); j++) {
                    mf = r.getInput(j);
                    e = d.createElement("in");
                    n.appendChild(e);
                    e.setAttribute("var", mf.getVar().getTitle());
                    e.setAttribute("name", mf.getTitle());
                }
                for (int j = 0; j < r.getOutputCount(); j++) {
                    mf = r.getOutput(j);
                    //n.setAttribute("out"+j, mf.getTitle());
                    e = d.createElement("out");
                    n.appendChild(e);
                    e.setAttribute("var", mf.getVar().getTitle());
                    e.setAttribute("name", mf.getTitle());
                }
                rules.appendChild(n);
            }
            */

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer trans = factory.newTransformer();
            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(os);
            trans.transform(source, result);

            os.flush();
            os.close();

        } catch (Exception ex) {
            System.err.println("Saving config failed: " + ex);
        }
	}


	public void load() {
        try {
            InputStream is = new FileInputStream(fileName);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document d = builder.parse(is);
            is.close();

            NodeList nl;
            Node n;
            
            nl = d.getElementsByTagName(ROOT_ELEMENT);
            if (nl.getLength() < 1) {
            	System.err.println("Incorrect config file");
            	return;
            }
            
            nl = d.getElementsByTagName(PORT_ELEMENT);
            if (nl.getLength() > 0) {
            	portName = nl.item(0).getTextContent();
            }

            nl = d.getElementsByTagName(INCLFILE_ELEMENT);
            if (nl.getLength() > 0) {
            	includeFile = nl.item(0).getTextContent();
            }

            nl = d.getElementsByTagName(SRCFILE_ELEMENT);
            if (nl.getLength() > 0) {
            	sourceFile = nl.item(0).getTextContent();
            }

            nl = d.getElementsByTagName(SRCISC_ELEMENT);
            if (nl.getLength() > 0) {
            	try {sourceIsC = Boolean.parseBoolean(nl.item(0).getTextContent());}
            	catch (Exception ex) {System.err.println("Wrong sourceIsC configuration: " + nl.item(0).getTextContent());}            	
            }

            nl = d.getElementsByTagName(EDREFR_ELEMENT);
            if (nl.getLength() > 0) {
            	try {edRefresh = Integer.parseInt(nl.item(0).getTextContent());}
            	catch (Exception ex) {System.err.println("Wrong ed-refresh configuration: " + nl.item(0).getTextContent());}
            }

            nl = d.getElementsByTagName(DEFHEX_ELEMENT);
            if (nl.getLength() > 0) {
            	try {defaultHex = Boolean.parseBoolean(nl.item(0).getTextContent());}
            	catch (Exception ex) {System.err.println("Wrong defaultHex configuration: " + nl.item(0).getTextContent());}
            }

            nl = d.getElementsByTagName(HIST_ELEMENT);
            if (nl.getLength() > 0) {
            	nl = nl.item(0).getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                	n = nl.item(i);
                	if (n.getNodeName().equals(HIST_ITEM_ELEMENT)) {
                		cmdHist.add(n.getTextContent());
                	}
                }	
            }
            
            /*
            NamedNodeMap na;
            Rule r;
            MF mf;
            Var v;

            NodeList rules = rulesL.item(0).getChildNodes();
            NodeList rvars;
            fz.rules.clear();
            for (int i = 0; i < rules.getLength(); i++) {
                r = new Rule();
                n = rules.item(i);
                rvars = n.getChildNodes();
                for (int j = 0; j < rvars.getLength(); j++) {
                    nn = rvars.item(j);
                    na = nn.getAttributes();
                    mf = fz.findMFByName(na.getNamedItem("var").getNodeValue(), na.getNamedItem("name").getNodeValue());

                    if (nn.getNodeName() == "in") {
                        r.addInput(mf);
                    } else if (nn.getNodeName() == "out") {
                        r.addOutput(mf);
                    } else {
                        System.out.println("XML uknown rule item !");
                    }
                }
                fz.rules.add(r);
            }
            */
        } catch (FileNotFoundException fnfex) {
            System.err.println("Config file does not exist");
		} catch (Exception ex) {
            System.err.println("Loading config failed: " + ex);
        }
	}

	
}
