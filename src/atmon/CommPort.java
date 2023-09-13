package atmon;

import gnu.io.*;
import java.io.*;
import java.util.*;

public class CommPort {
	
	private static final int BAUD_RATE = 19200; //57600;
	
	private static final int REC_TIMEOUT = 200;
	
    private RXTXPort port = null;
    private InputStream is = null;
    private OutputStream os = null;

    public void open(String comm) {
        close();
        try {
            port = new RXTXPort(comm);

            port.setFlowControlMode(RXTXPort.FLOWCONTROL_NONE);
            port.setSerialPortParams(BAUD_RATE, RXTXPort.DATABITS_8, RXTXPort.STOPBITS_1, RXTXPort.PARITY_NONE);
            
            port.enableReceiveTimeout(REC_TIMEOUT);
//            port.enableReceiveThreshold(800);
            
            is = port.getInputStream();
            os = port.getOutputStream();

            System.out.println("Port " + comm + " opened");
        } catch (Exception ex) {
            System.err.println("Opening port " + comm + " failed: " + ex);
        }
	}
	
	public void close() {
        try {
            if (is != null) {
				is.close();
                is = null;
            }
            if (os != null) {
                os.close();
                os = null;
            }
            if (port != null) {
                //port.clearCommInput();
                port.close();
                port = null;
            }
        } catch (Exception ex) {
            System.err.println("Closing port failed !");
        }
	}
	
	public String[] getCommPorts() {
	    CommPortIdentifier cpi;
	    Enumeration en = CommPortIdentifier.getPortIdentifiers();
	    ArrayList list = new ArrayList(); 

	    while (en.hasMoreElements()) {
	        cpi = (CommPortIdentifier) en.nextElement();
	        if (cpi.getPortType() != CommPortIdentifier.PORT_SERIAL) {continue;}
	        list.add(cpi.getName());
	    }
	    return (String[]) list.toArray(new String[0]);
	}


	public void sendBuf(byte[] buf, int off, int len) {
        try {os.write(buf, off, len);}
        catch (Exception ex) {
            System.err.println("Cannot send data: " + ex);
        }
	}

	public void sendBuf(byte[] buf) {
        try {os.write(buf);}
        catch (Exception ex) {
            System.err.println("Cannot send data: " + ex);
        }
	}
	
	public void send(byte b) {
		try {os.write(b);}
		catch (Exception ex) {
            System.err.println("Cannot send data: " + ex);
		}
	}
	
	public int read() {
		try {return is.read();}
		catch (Exception ex) {return -1;}
	}	
		
	/*
	public boolean read(byte[] buf, int off, int len) {
		try {
			return (is.read(buf, off, len) == len);
		} catch (IOException ex) {
			return false;			
		}
	}*/
	
	public boolean read(byte[] buf, int off, int len) {
		try {
			int v;
			for (int i = off; i < off+len; i++) {
				v = is.read();
				if (v < 0) {
					return false;					
				} else {
					buf[i] = (byte) v;
				}
			}			
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public int getAvailable() {
		try {return is.available();}
		catch (Exception ex) {return 0;}
	}
		
}
