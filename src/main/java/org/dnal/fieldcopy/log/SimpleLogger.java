package org.dnal.fieldcopy.log;

public interface SimpleLogger {
	boolean isEnabled();
	void enableLogging(boolean b);
	void log(String fmt, Object... args);
}