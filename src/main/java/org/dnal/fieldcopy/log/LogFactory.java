package org.dnal.fieldcopy.log;

import java.util.List;

/**
 * Factory to create log instances
 * @author Ian Rae
 *
 */
public interface LogFactory {
	FieldCopyLog create(String name);
	FieldCopyLog create(Class<?> clazz);
	void setDefaultLogLevel(LogLevel level);
	LogLevel getDefaultLogLevel();
	void setLogLevelMap(List<String> levelMapList);
}
