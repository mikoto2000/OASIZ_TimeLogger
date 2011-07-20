/**
 * OASIZ_TimeLogger.java
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

import java.io.IOException;
import java.util.Date;

import jp.dip.oyasirazu.timelogger.util.LogDumper;
import jp.dip.oyasirazu.timelogger.util.TimeUtility;
import jp.dip.oyasirazu.timelogger.view.TimerView;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

/**
 * 時間を記録するアクティビティ
 * @author mikoto
 */
public class OASIZ_TimeLogger extends Activity {
    
    private Resources mResources;
    private TimerView mTimerView;
    private EditText mWorkName;
    private ListView mLogView;
    private ArrayAdapter<String> mLogAdapter;
    
    private LogDumper mLogger;
    private long mStartTime;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_record);
        
        mLogger = new LogDumper(getFilesDir());
        
        mResources = getResources();
        
        mTimerView = (TimerView)findViewById(R.id.timer_view);
        
        mWorkName = (EditText)findViewById(R.id.work_name);
        
        mLogView = (ListView)findViewById(R.id.log_view);
        mLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mLogView.setAdapter(mLogAdapter);
    }
    
    /**
     * 作業時間の記録開始・終了を行います。
     * @param view 記録開始・終了トグルボタン
     * @throws IOException 
     */
    public void onStartStop(View view) throws IOException {
        ToggleButton button = (ToggleButton)view;
        
        if(button.isChecked()) {
            mStartTime = mTimerView.start();
        } else {
            long spentTime = mTimerView.stop();
            
            // リストビューに書き出し
            String logFormatString = mResources.getString(R.string.log_string);
            mLogAdapter.add(
                    String.format(
                            logFormatString,
                            mWorkName.getText().toString(),
                            spentTime
              ));
            
            // ログファイルに書き出し
            Date startDate = new Date(mStartTime);
            String dumpMessage =
                    String.format(
                            "\"%s\", \"%s\", \"%s\"",
                            mWorkName.getText().toString(),
                            DateFormat.format("yyyy/MM/dd", startDate),
                            TimeUtility.formatSpentTime(spentTime)
              );
            
            mLogger.dump(startDate, dumpMessage);
        }
    }
}