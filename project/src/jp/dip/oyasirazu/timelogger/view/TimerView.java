/**
 * TimerView.java
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

package jp.dip.oyasirazu.timelogger.view;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jp.dip.oyasirazu.timelogger.util.TimeUtility;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 作業時間を表示する View です。
 * @author mikoto
 */
public class TimerView extends TextView {
    
    private long mCurrentTime;
    private Date mStartDate;
    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTimeLogTask;
    
    /**
     * コンストラクタ
     * @param context
     */
    public TimerView(Context context) {
        super(context);
        init(context);
    }
    
    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    private void init(Context context) {
        mHandler = new Handler();
        mTimer = new Timer();
    }
    
    /**
     * 時間の記録を開始します。
     */
    public Date start() {
        mStartDate = new Date();
        mTimeLogTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        TimerView.this.displayTime();
                    }
                });
            }
        };
        mTimer.schedule(mTimeLogTask, 0, 1000); // 1 秒毎に更新
        return mStartDate;
    }
    
    private void displayTime() {
        mCurrentTime = System.currentTimeMillis();
        long spentTimeMsec = mCurrentTime - mStartDate.getTime();
        final String formattedTime = TimeUtility.formatSpentTime(spentTimeMsec);
        setText(formattedTime);
    }

    /**
     * 時間の記録を終了します。
     * @return 終了時刻
     */
    public Date stop() {
        mTimeLogTask.cancel();
        return new Date();
    }
    
    /**
     * 開始時間を上書きします。<br />
     * start を読んでから実行してください。
     * @param startDate 上書きする開始時刻
     */
    public void overrideStartDate(Date startDate) {
        mStartDate = startDate;
        displayTime();
    }
}