package atmon;

public class ATmon {
	
	public static void main(String[] args) {
		String path = System.getProperty("java.class.path", ".");
		new MainFrame(path);
	}

}
