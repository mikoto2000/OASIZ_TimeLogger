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

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.DigitalClock;

public class TimerView extends DigitalClock {
    
    private long mStartTime;
    
    /**
     * コンストラクタ
     * @param context
     */
    public TimerView(Context context) {
        super(context);
    }
    
    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * 時間の記録を開始します。
     */
    public void start() {
        mStartTime = System.currentTimeMillis();
    }
    
    /**
     * 時間の記録を終了します。
     * @return start してからの時間(ms)
     */
    public long stop() {
        return System.currentTimeMillis() - mStartTime;
    }
}
