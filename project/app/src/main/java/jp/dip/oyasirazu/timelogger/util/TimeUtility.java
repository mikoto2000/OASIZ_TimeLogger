/**
 * TimeUtility.java
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

package jp.dip.oyasirazu.timelogger.util;

/**
 * 時間・時刻操作に便利な機能を提供します。
 * @author mikoto
 */
public class TimeUtility {
    
    private static final String DEFAULT_TIME_PATTERN = "%1$02d:%2$02d:%3$02d";
    
    private static String sTimePattern = DEFAULT_TIME_PATTERN;
    
    private TimeUtility() {}
    
    /**
     * 作業時間(msec)をフォーマット文字列(hh:mm:ss)に変換します。
     * @param spentTimeMsec 作業時間(msec)
     * @return フォーマット文字列(hh:mm:ss)
     */
    public static String formatSpentTime(long spentTimeMsec) {
        if (spentTimeMsec < 0) {
            throw new IllegalArgumentException("Not allow negative numbers in 'spentTimeMsec'.");
        }
        
        long tmp = (spentTimeMsec / 1000); // 秒
        long spentTimeSeconds = tmp % 60; // 秒
        long spentTimeMinutes = (tmp / 60) % 60; // 分
        long spentTimeHours = (tmp / 60 / 60); // 時間
        final String formattedTime = String.format(
                sTimePattern,
                spentTimeHours, // 時間
                spentTimeMinutes, // 分
                spentTimeSeconds); // 秒
        return formattedTime;
    }
}
