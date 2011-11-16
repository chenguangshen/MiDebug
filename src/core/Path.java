package core;

public class Path {
	public static final String basePath = "/home/nesl/workspace_ee/CloudCompiler";
	public static final String pjPath = basePath + "/projects/";
	public static final String scriptPath = basePath + "/script/";
	public static final String buildPath = basePath + "/build_env/";
	
	public static final String buildPath_stm = basePath + "/stm_h103/";
	
	public static final String arm_bare_buildEnv = basePath + "/ARMTools/bin/";

	public static boolean compileFlag = false;
	public static String currentBinary = null;	
	
	public static String NXPgdb = arm_bare_buildEnv + "arm-bare_newlib_cortex_m3_nommu-eabi-gdb";
	public static String ARMgdb = arm_bare_buildEnv + "arm-none-eabi-gdb";
}
