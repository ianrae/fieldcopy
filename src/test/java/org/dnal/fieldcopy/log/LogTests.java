package org.dnal.fieldcopy.log;

import org.junit.jupiter.api.Test;

public class LogTests {

    @Test
    public void testSimple() {
        SimpleLog log = new SimpleLog();
        log.setLevel(LogLevel.DEBUG);
        log.log("log level INFO");
        log.logError("log level INFO");
        log.logDebug("log level INFO");
    }

    @Test
    public void testLogback() {
        //the 'test' scope in pom.xml includes logback so we can log here using that
        StandardLog log = new StandardLog(LogTests.class);
//        log.setLevel(LogLevel.DEBUG);
        log.log("log level INFO");
        log.logError("log level ERROR");
        log.logDebug("log level DEBUG");
    }
}
