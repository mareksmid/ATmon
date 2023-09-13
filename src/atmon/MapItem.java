package atmon;

import java.util.*;

public class MapItem {
	
	public static final String[] TYPE_CHARS = {"G", "F", "D", "E", "R"};
	
	public static final int TYPE_GENERAL  = 0;
	public static final int TYPE_FLASH    = 1;
	public static final int TYPE_RAM      = 2;
	public static final int TYPE_EEPROM   = 3;
	public static final int TYPE_REGISTER = 4;
	
	public static final int DEFAULT_SIZE = 1;
	
	String name, value;
	int size, type;

	public MapItem(String name, String value, int type) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.size = DEFAULT_SIZE;
	}
	
	public String getName() {return name;}
	public String getValue() {return value;}
	public String toString() {return name + ": " + value;}
	public int getSize() {return size;}
	public int getType() {return type;}
	public String getTypeStr() {return TYPE_CHARS[type];}

	public void setSize(int size) {this.size = size;} 

	public static MapItem findItemInSet(Set set, String n) {
		Iterator iter = set.iterator();
		MapItem it;
		n = n.toUpperCase();
		while (iter.hasNext()) {
			it = (MapItem) iter.next();
			if (it.getName().toUpperCase().equals(n)) {return it;}
		}
		return null;
	}
	
}
