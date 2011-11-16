package core;

public class GDBProxyWrapper {
	public static GDBServerProxy proxy = null;
	public static String currentInst = "";
	public static AsynGDBDebug agdb = null;
	
	public static boolean sychronizedf = false;
	public static boolean sychronizedp = false;
}
