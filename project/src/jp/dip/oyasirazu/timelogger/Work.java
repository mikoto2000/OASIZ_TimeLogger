package jp.dip.oyasirazu.timelogger;

import java.util.Date;

public class Work {
    private String mName;
    private Date mStartDate;
    private long mSpentTime;
    
    public Work(String name, Date startDate, long spentTime) {
        this.mName = name;
        this.mStartDate = startDate;
        this.mSpentTime = spentTime;
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
    
    public long getSpentTime() {
        return mSpentTime;
    }
    
    public void setSpentTime(long spentTime) {
        this.mSpentTime = spentTime;
    }
}
