package org.dnal.fieldcopy.log;

import java.util.List;

public class SimpleLogFactory implements LogFactory {
	private LogLevel defaultLevel = LogLevel.INFO;
//	private LogLevelMapBuilder levelMapBuilder = new LogLevelMapBuilder();

	@Override
	public FieldCopyLog create(String name) {
		FieldCopyLog log = new SimpleLog();
		log.setLevel(calcLevel(name));
		return log;
	}

	@Override
	public FieldCopyLog create(Class<?> clazz) {
		FieldCopyLog log = new SimpleLog();
		log.setLevel(calcLevel(clazz.getName()));
		return log;
	}

	@Override
	public void setDefaultLogLevel(LogLevel level) {
		this.defaultLevel = level;
	}
	@Override
	public LogLevel getDefaultLogLevel() {
		return defaultLevel;
	}

	@Override
	public void setLogLevelMap(List<String> levelMapList) {
//		levelMapBuilder.buildMap(levelMapList);
	}
	
	protected LogLevel calcLevel(String name) {
//		return levelMapBuilder.calcLevel(name, defaultLevel);
		return LogLevel.DEBUG;
	}


}
