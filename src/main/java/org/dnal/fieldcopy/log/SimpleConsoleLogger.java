package org.dnal.fieldcopy.log;

public class SimpleConsoleLogger implements SimpleLogger {
	private boolean enabled = false;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void enableLogging(boolean b) {
		enabled = b;
	}
	@Override
	public void log(String fmt, Object... args) {
		if (enabled) {
			doLog(fmt, args);
		}
	}

	protected void doLog(String fmt, Object... args) {
		String prefix = ""; 
		if (args.length == 0) {
			System.out.println(prefix + fmt);
		} else {
			String s = String.format(fmt, args);
			System.out.println(prefix + s);
		}
	}
}