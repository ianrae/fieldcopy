package org.dnal.fieldcopy.log;

/**
 * FieldCopy log.  FielCopy comes with two loggers:
 *  -SimpleLog that uses System.out.
 *  -StandardLog that uses sl4j. This will use whatever sl4j library your overall uses, such as logback or log4j.
 *
 * Note. FieldCopy includes logback but only for 'test' scope.
 *
 * @author Ian Rae
 *
 */
public interface FieldCopyLog {
	void setLevel(LogLevel level);
	LogLevel getLevel();
	void log(String fmt, Object... args);
	void logDebug(String fmt, Object... args);
	void logError(String fmt, Object... args);
	void logException(LogLevel level, String message, Throwable ex);
	boolean isLevelEnabled(LogLevel level); //true if that level or more
}