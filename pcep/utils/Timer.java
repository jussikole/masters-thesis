package pcep.utils;

public class Timer {
	private static long startTime;
	
	public static void start() {
		startTime = System.currentTimeMillis();
	}
	
	public static long end() {
		return System.currentTimeMillis() - startTime;
	}
}
