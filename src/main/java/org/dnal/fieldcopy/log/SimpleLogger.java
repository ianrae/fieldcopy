package org.dnal.fieldcopy.log;

/**
 * A simple logger that is used by FieldCopy.  
 * FieldCopy defaults to a SimpleConsoleLogger but you can create a custom SimpleLogger class
 * to integrate with log4j, slf4j, etc.
 * 
 * @author Ian Rae
 *
 */
public interface SimpleLogger {
	boolean isEnabled();
	void enableLogging(boolean b);
	void log(String fmt, Object... args);
}