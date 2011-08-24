/**
 * Work.java
 * 
 * The MIT License
 * 
 * Copyright (c) 2011 mikoto2000<mikoto2000@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jp.dip.oyasirazu.timelogger;

import java.util.Date;

/**
 * 1 件の作業記録を表すクラスです。
 * @author mikoto
 */
public class Work {
    /**
     * 番号未指定の代表として INVALID_NO を定義
     */
    public static int INVALID_NO = Integer.MIN_VALUE;
    
    /**
     * 作業番号。番号未指定の場合は負数。
     */
    private int mWorkNo;
    private String mName;
    private Date mStartDate;
    private Date mEndDate;
    
    /**
     * 作業番号未指定で作業記録を作成します。
     * @param name 作業名
     * @param startDate 作業開始時刻
     * @param endDate 作業終了時刻
     * @see INVALID_NO
     */
    public Work(String name, Date startDate, Date endDate) {
        this(INVALID_NO, name, startDate, endDate);
    }
    
    /**
     * 作業番号を指定して、作業記録を作成します。
     * @param workNo 作業番号
     * @param name 作業名
     * @param startDate 作業開始時刻
     * @param endDate 作業終了時刻
     */
    public Work(int workNo, String name, Date startDate, Date endDate) {
        if (name == null) {
            throw new NullPointerException("Not allow null in 'name'.");
        }
        
        if (startDate == null) {
            throw new NullPointerException("Not allow null in 'startDate'.");
        }
        
        if (endDate == null) {
            throw new NullPointerException("Not allow null in 'endDate'.");
        }
        
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("Not allow endDate before than startDate.");
        }
        
        if (endDate.equals(startDate)) {
            throw new IllegalArgumentException("Not allow endDate equals startDate.");
        }
        
        this.mWorkNo = workNo;
        this.mName = name;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }
    
    public int getWorkNo() {
        return mWorkNo;
    }
    
    public void setWorkNo(int no) {
        this.mWorkNo = no;
    }
    
    public String getName() {
        return mName;
    }
    
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Not allow null in 'name'.");
        }
        this.mName = name;
    }
    
    public Date getStartDate() {
        return mStartDate;
    }
    
    public void setStartDate(Date startDate) {
        if (startDate == null) {
            throw new NullPointerException("Not allow null in 'startDate'.");
        }
        
        if (mEndDate.before(startDate)) {
            throw new IllegalArgumentException("Not allow endDate before than startDate.");
        }
        
        if (mEndDate.equals(startDate)) {
            throw new IllegalArgumentException("Not allow endDate equals startDate.");
        }
        this.mStartDate = startDate;
    }
    
    public Date getEndDate() {
        return mEndDate;
    }
    
    public void setEndDate(Date endDate) {
        if (endDate == null) {
            throw new NullPointerException("Not allow null in 'endDate'.");
        }
        
        if (endDate.before(mStartDate)) {
            throw new IllegalArgumentException("Not allow endDate before than startDate.");
        }
        
        if (endDate.equals(mStartDate)) {
            throw new IllegalArgumentException("Not allow endDate equals startDate.");
        }
        
        this.mEndDate = endDate;
    }
    
    public void setDates(Date startDate, Date endDate) {
        if (startDate == null) {
            throw new NullPointerException("Not allow null in 'startDate'.");
        }
        
        if (endDate == null) {
            throw new NullPointerException("Not allow null in 'endDate'.");
        }
        
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("Not allow endDate before than startDate.");
        }
        
        if (endDate.equals(startDate)) {
            throw new IllegalArgumentException("Not allow endDate equals startDate.");
        }
        
        mStartDate = startDate;
        mEndDate = endDate;
    }
    
    public long getSpentTime() {
        // getter/setter を使う限り、これを呼び出す時点では、 2 つの Date に矛盾が発生しない。
        
        return this.mEndDate.getTime() - this.mStartDate.getTime();
    }
}
