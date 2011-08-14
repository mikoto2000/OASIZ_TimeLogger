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
    public static int INVALID_NO = -1;
    
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
    public Work(int workNo, String name, Date startDate, Date spentTime) {
        this.mWorkNo = workNo;
        this.mName = name;
        this.mStartDate = startDate;
        this.mEndDate = spentTime;
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
        this.mName = name;
    }
    
    public Date getStartDate() {
        return mStartDate;
    }
    
    public void setStartDate(Date startTime) {
        this.mStartDate = startTime;
    }
    
    public Date getEndDate() {
        return mEndDate;
    }
    
    public void setEndDate(Date endDate) {
        this.mEndDate = endDate;
    }
    
    public long getSpentTime() {
        return this.mEndDate.getTime() - this.mStartDate.getTime();
    }
}
