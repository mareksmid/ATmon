package atmon;

public class Texts {
	
	public static final String[] ABOUT_TEXT = {
		"ATmon - monitor for Atmel ATmega32",
	    "Created by Marek Smid (smidm@msoe.edu)",
	    "Project homepage: http://people.msoe.edu/~smidm/"
	};
	
	public static final String[] CMD_HELP_TEXT = {
		"H - show this help",
		"SRC - set source project (opens selection dialog - select either HEX or MAP file), used for writing flash and for project symbol definitions", 
		"INCLF - pop up dialog to select include file (with MCU register definitions)", 
		"T - open terminal window", 
		"G address - tell MCU to go to address", 
		"DEFDEC - treat values from command-line as decimal as default", 
		"DEFHEX - treat values from command-line as hexadecimal as default", 
		"EDREFR period - set refresh period [ms] of byte editor", 
		"RESP - check whether the bootloader is responding", 
		"TR name - translate a symbolic name to a value (using include file or project map file)", 
		"MAP - show all symbolic names", 
		"CL - close communication port",
		"OP - open communication port", 
		"PORT port - change port name to port", 
		"CFG - open configuration form", 
		"C - clear window",
		"A - show version text", 
		"X - exit this application", 
		"R - reset MCU",
		"I reg - read IO register from MCU", 
		"O reg value - write IO register to MCU",
		"D addr [len] [w] - dump SRAM memory at address addr of length len from MCU (w = display as words)", 
		"M addr value - write a value to a SRAM byte at address addr in MCU", 
		"F addr len value - fill MCU's SRAM memory from address addr of length len with a value", 
		"IE,OE reg [val] [a] - open byte editor for an IO register in MCU (and initialize it with value) (a = and set auto-refresh on)", 
		"E addr [var-len] [var-cnt] [b] [a] - open byte editor for SRAM data at addr in MCU, set variable length to var-len, and count of variables to var-cnt, b = used mapfile-defined size as count of bytes (good for buffers), a = and set auto-refresh on",
		"FD addr [len] [w] - dump Flash memory at address addr of length len from MCU (w = display as words)",
		"WR [v] - write flash from a file defined by SRC to MCU (v = and verify)",
		"RD - read whole Flash from MCU into a file",
		"VR - verify flash in MCU with a file defined by SRC",
		"WRE [v] - write EEPROM from a file defined by SRC to MCU (v = and verify)",
		"RDE - read whole EEPROM from MCU into a file",
		"ED addr [len] [w] - dump EEPROM at address addr of length len from MCU (w = display as words)",
		"VRE - verify EEPROM in MCU with a file defined by SRC",
		"S - stop execution",
		"RS - resume exection from stop",
		"LST - show listing file for current project defined by SRC",
		"HD - show contents of arbitrary HEX file",
		"BRK addr - enable breakpoint mode and add breakpoint at addr",
		"BRKR addr - remove breakpoint",
		"BRKQ - quit breakpoint mode, and remove all breakpoints",
		"BRKL - list all set breakpoints",
		"STEPE - enter single-stepping mode",
		"STEPQ - quit single-stepping mode",
		"STEP [n] - perform a single step (single-stepping mode) or continue running from a breakpoint (breakpoint mode), or perform n consecutive steps",
		"F5 - keyboard shortcut for \"step\"",
		"ER - erase whole flash memory (except bootloader section)",
		"BL - check whether flash memory is blank (all FFs) (except bootloader section)",
		"LCK, FUS - show lock bits and fuses",
		"WLCK - open a lock bits configuration window"
	};

}
