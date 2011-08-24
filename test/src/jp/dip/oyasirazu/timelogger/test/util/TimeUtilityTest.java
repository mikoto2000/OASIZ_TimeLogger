package jp.dip.oyasirazu.timelogger.test.util;

import jp.dip.oyasirazu.timelogger.util.TimeUtility;
import junit.framework.TestCase;

public class TimeUtilityTest extends TestCase {
    public void testFormatSpentTime() {
        // 正常系
        assertTrue("00:00:00".equals(TimeUtility.formatSpentTime(0)));
        
        assertTrue("00:00:00".equals(TimeUtility.formatSpentTime(999)));
        
        assertTrue("00:00:01".equals(TimeUtility.formatSpentTime(1000)));
        
        assertTrue("00:00:59".equals(TimeUtility.formatSpentTime(59000)));
        
        assertTrue("00:01:00".equals(TimeUtility.formatSpentTime(60000)));
        
        assertTrue("00:59:00".equals(TimeUtility.formatSpentTime(60000 * 59)));
        
        assertTrue("01:00:00".equals(TimeUtility.formatSpentTime(60000 * 60)));
        
        // 異常系
        try {
            TimeUtility.formatSpentTime(-1);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        try {
            TimeUtility.formatSpentTime(Long.MIN_VALUE);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
    }
}
