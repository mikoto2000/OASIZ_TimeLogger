package jp.dip.oyasirazu.timelogger.test;

import java.util.Date;

import jp.dip.oyasirazu.timelogger.Work;
import junit.framework.TestCase;

public class WorkTest extends TestCase {
    
    public void testConstructor() {
        String str = "TEST1";
        Date earlyDate = new Date(111, 1, 1);
        Date lateDate = new Date(111, 1, 2);
        long msecOfDay = 24 * 60 * 60 * 1000;
        
        // 正常系(String, Date, Date)
        Work work = new Work(str, earlyDate, lateDate);
        
        assertTrue(work.getWorkNo() == Work.INVALID_NO);
        assertTrue(work.getName() == str);
        assertTrue(work.getStartDate() == earlyDate);
        assertTrue(work.getEndDate() == lateDate);
        assertTrue(work.getSpentTime() == msecOfDay);
        
        // 異常系(String, Date, Date)
        try {
            work = new Work(null, earlyDate, lateDate);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(str, null, lateDate);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(str, lateDate, null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(str, lateDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        try {
            work = new Work(str, earlyDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        // 正常系(int, String, Date, Date)
        work = new Work(1, str, earlyDate, lateDate);
        
        assertTrue(work.getWorkNo() == 1);
        assertTrue(work.getName() == str);
        assertTrue(work.getStartDate() == earlyDate);
        assertTrue(work.getEndDate() == lateDate);
        assertTrue(work.getSpentTime() == msecOfDay);
        
        work = new Work(Integer.MAX_VALUE, str, earlyDate, lateDate);
        
        assertTrue(work.getWorkNo() == Integer.MAX_VALUE);
        assertTrue(work.getName() == str);
        assertTrue(work.getStartDate() == earlyDate);
        assertTrue(work.getEndDate() == lateDate);
        assertTrue(work.getSpentTime() == msecOfDay);
        
        work = new Work(-1, str, earlyDate, lateDate);
        
        assertTrue(work.getWorkNo() == -1);
        assertTrue(work.getName() == str);
        assertTrue(work.getStartDate() == earlyDate);
        assertTrue(work.getEndDate() == lateDate);
        assertTrue(work.getSpentTime() == msecOfDay);
        
        work = new Work(Integer.MIN_VALUE, str, earlyDate, lateDate);
        
        assertTrue(work.getWorkNo() == Integer.MIN_VALUE);
        assertTrue(work.getName() == str);
        assertTrue(work.getStartDate() == earlyDate);
        assertTrue(work.getEndDate() == lateDate);
        assertTrue(work.getSpentTime() == msecOfDay);
        
        
        // 異常系(int, String, Date, Date)
        try {
            work = new Work(1, null, earlyDate, lateDate);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(1, str, null, lateDate);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(1, str, lateDate, null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work = new Work(1, str, lateDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        try {
            work = new Work(1, str, earlyDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
    }
    
    public void testSet() {
        String str = "TEST1";
        Date earlyDate = new Date(111, 1, 1);
        Date lateDate = new Date(111, 1, 2);
        
        Work work = new Work(1, str, earlyDate, lateDate);
        
        
        
        // workNo
        // 正常系
        work.setWorkNo(Integer.MAX_VALUE);
        assertTrue(work.getWorkNo() == Integer.MAX_VALUE);
        
        work.setWorkNo(1);
        assertTrue(work.getWorkNo() == 1);
        
        work.setWorkNo(0);
        assertTrue(work.getWorkNo() == 0);
        
        work.setWorkNo(-1);
        assertTrue(work.getWorkNo() == -1);
        
        work.setWorkNo(Integer.MIN_VALUE);
        assertTrue(work.getWorkNo() == Integer.MIN_VALUE);
        
        
        
        // name
        work = new Work(1, str, earlyDate, lateDate);
        // 正常系
        String str2 = "TEST2";
        work.setName(str2);
        assertTrue(work.getName() == str2);
        
        // 異常系
        try {
            work.setName(null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        Date earlyDate2 = new Date(110, 1, 1);
        Date lateDate2 = new Date(112, 1, 1);
        
        
        
        // startDate
        work = new Work(1, str, earlyDate, lateDate);
        
        // 正常系
        work = new Work(1, str, earlyDate, lateDate);
        work.setStartDate(earlyDate2);
        assertTrue(work.getStartDate() == earlyDate2);
        
        // 異常系
        // null
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setStartDate(null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        // endDate.after(startDate)
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setStartDate(lateDate2);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        // endDate.equals(startDate)
        work = new Work(1, str, earlyDate, lateDate);
        try {
            work.setStartDate(lateDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        
        
        // endDate
        
        // null
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setEndDate(null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        // endDate.after(startDate)
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setEndDate(earlyDate2);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        // endDate.equals(startDate)
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setEndDate(earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        
        
        // setDates
        
        // 正常系
        work = new Work(1, str, earlyDate, lateDate);
        
        work.setDates(earlyDate2, lateDate2);
        assertTrue(work.getStartDate() == earlyDate2);
        assertTrue(work.getEndDate() == lateDate2);
        
        // 異常系
        work = new Work(1, str, earlyDate, lateDate);
        
        try {
            work.setDates(null, lateDate);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            work.setDates(earlyDate, null);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        // endDate.befor(startDate)
        try {
            work.setDates(lateDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
        
        // endDate.equals(startDate)
        try {
            work.setDates(earlyDate, earlyDate);
            assertTrue(false);
        } catch (IllegalArgumentException e) {}
    }
    
    public void testSpentTime() {
        String str = "TEST1";
        Date earlyDate = new Date(111, 1, 1);
        Date lateDate = new Date(111, 1, 2);
        
        // 正常系
        Work work = new Work(1, str, earlyDate, lateDate);
        assertTrue(work.getSpentTime() == 1 * 24 * 60 * 60 * 1000);
        
        Date earlyDate2 = new Date(111, 1, 0);
        Date lateDate2 = new Date(111, 1, 10);
        
        work = new Work(1, str, earlyDate2, lateDate2);
        assertTrue(work.getSpentTime() == 10 * 24 * 60 * 60 * 1000);
    }
}
