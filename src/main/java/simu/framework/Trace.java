package simu.framework;

public class Trace {
    /**
     * Enum Level defines the different levels of log messages.
     * - INFO: General informational messages.
     * - WAR: Warning messages indicating potential issues.
     * - ERR: Error messages indicating critical issues.
     */
	public enum Level { INFO, WAR, ERR }

    // Static field to store the current trace level.
    private static Level traceLevel;

    // Set the current trace level
    public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}

    // Outputs a message to the console if its level is equal to or higher than the current trace level.
    public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
}