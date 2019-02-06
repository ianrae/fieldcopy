package org.dnal.fieldcopy.log;

public interface SimpleLogger {
	void enableLogging(boolean b);
	void log(String fmt, Object... args);
}