package atmon;

import java.util.*;

public class BreakpointManager {

	private static final int BRKPT_OFF_VALUE = 0xFFFF;
	
	private MainFrame mf;
	private Comm comm;
	private ArrayList breakPts;
	
	public BreakpointManager(MainFrame mf, Comm comm) {
		this.mf = mf;
		this.comm = comm;		
		breakPts = new ArrayList();
	}
	
	public void addPt(int pt) {
		if (breakPts.size() >= Comm.BREAK_PT_CNT) {
			System.err.println("Maximum number of breakpoints (" + Comm.BREAK_PT_CNT + ") reached");
			return;
		}
		breakPts.add(new Integer(pt));
		updatePts();
	}
	
	public void removePt(int pt) {
		Integer i = new Integer(pt);
		if (!breakPts.remove(i)) {
			System.out.println("Such breakpoint does not exist");
		} else {			
			updatePts();
		}
	}

	public void quit() {
		breakPts.clear();
		comm.stepQuit();
	}
	
	public void listPts() {
		if (breakPts.size() == 0) {
			System.out.println("No breakpoints");
			return;
		}
		System.out.println("Breakpoints:");
		Integer integ;
		for (int i = 0; i < breakPts.size(); i++) {
			integ = (Integer) breakPts.get(i);
			System.out.println(">" + mf.convFlashAddrToString(integ.intValue()));
		}
	}
	
	private void updatePts() {
		if (breakPts.size() > Comm.BREAK_PT_CNT) {
			System.err.println("Internal error in breakpoint system");
			return;
		}
		int[] pts = new int[Comm.BREAK_PT_CNT];
		for (int i = 0; i < Comm.BREAK_PT_CNT; i++) {
			if (i < breakPts.size()) {pts[i] = ((Integer) breakPts.get(i)).intValue();}
			else {pts[i] = BRKPT_OFF_VALUE;}
		}		
		comm.setBreakpoints(pts);		
		comm.stepEnter(true);
	}
	
	
}
