package org.netbeans.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestFailure;
import junit.framework.TestResult;

/** Provides an example of randomized test.
 */
public class TimeOutHasToPrintLogTest extends NbTestCase {
    private Logger LOG = Logger.getLogger("my.log.for.test");
    
    public TimeOutHasToPrintLogTest(String testName) {
        super(testName);
    }
    
    protected Level logLevel() {
        if (getName().equals("printAhojAndTimeOut")) {
            return null;
        } else {
            return Level.FINE;
        }
    }
    
    protected int timeOut() {
        if (getName().equals("printAhojAndTimeOut")) {
            return 700;
        } else {
            return 0;
        }
    }
    
    public void printAhojAndTimeOut() throws Exception {
        LOG.fine("Ahoj");
        Thread.sleep(10000);
    }

    public void testThatTheTimeOutStillPrintsTheWarning() throws Exception {
        TimeOutHasToPrintLogTest t = new TimeOutHasToPrintLogTest("printAhojAndTimeOut");
        
        CharSequence seq = Log.enable(LOG.getName(), Level.FINE);
        
        TestResult res = t.run();
        
        assertEquals("One test has been run", 1, res.runCount());
        
        String s = seq.toString();
        
        if (s.indexOf("Ahoj") == -1) {
            fail("Ahoj has to be logged:\n" + s);
        }
        
        assertEquals("No error", 0, res.errorCount());
        assertEquals("One failure", 1, res.failureCount());
        
        TestFailure f = (TestFailure)res.failures().nextElement();
        s = f.exceptionMessage();
        if (s.indexOf("Ahoj") == -1) {
            fail("Ahoj has to be part of the message:\n" + s);
        }
    }
}
