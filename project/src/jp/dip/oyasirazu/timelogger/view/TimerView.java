/**
 * TimerVIew.java
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

import java.util.Timer;
import java.util.TimerTask;

import jp.dip.oyasirazu.timelogger.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimerView extends TextView {
    
    private String mTimePattern;
    private long mCurrentTime;
    private long mStartTime;
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
        mTimePattern = context.getResources().getString(R.string.time_pattern);
        
        mHandler = new Handler();
        mTimer = new Timer();
    }
    
    /**
     * 時間の記録を開始します。
     */
    public void start() {
        mStartTime = System.currentTimeMillis();
        mTimeLogTask = new TimerTask() {
            @Override
            public void run() {
                mCurrentTime = System.currentTimeMillis();
                long spentTimeMsec = mCurrentTime - mStartTime;
                long spentTimeHours = spentTimeMsec / 360000; // 時間
                long spentTimeMinutes = (spentTimeMsec % 360000) / 60000; // 分
                long spentTimeSeconds = ((spentTimeMsec % 360000) % 60000) / 1000; // 秒
                final String formattedTime = String.format(
                        mTimePattern,
                        spentTimeHours, // 時間
                        spentTimeMinutes, // 分
                        spentTimeSeconds); // 秒
                mHandler.post(new Runnable() {
                    public void run() {
                setText(formattedTime);
                    }
                });
            }
        };
        mTimer.schedule(mTimeLogTask, 0, 1000); // 1 秒毎に更新
    }
    
    /**
     * 時間の記録を終了します。
     * @return start してからの時間(ms)
     */
    public long stop() {
        mTimeLogTask.cancel();
        return System.currentTimeMillis() - mStartTime;
    }
}