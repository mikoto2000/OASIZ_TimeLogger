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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.oyasirazu.timelogger.util.LogDumper;
import jp.dip.oyasirazu.timelogger.util.TimeUtility;
import jp.dip.oyasirazu.timelogger.view.TimerView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 時間を記録するアクティビティ
 * @author mikoto
 */
public class OASIZ_TimeLogger extends Activity {
    
    static final String LOG_DIR = "logs";
    
    private static final String IS_RECORDING = "IS_RECORDING"; 
    private static final String WORK_NAME = "WORK_NAME"; 
    private static final String START_TIME = "START_TIME"; 
    
    private Wallpaper mWallpaper;
    
    private Resources mResources;
    private TimerView mTimerView;
    private EditText mWorkName;
    private ListView mLogView;
    private ArrayAdapter<String> mLogAdapter;
    
    private LogDumper mLogger;
    private long mStartTime;
    private SimpleDateFormat mDateFormat;
    private String mLogFormatPattern;
    
    private ToggleButton mStartStopButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_record);
        
        mResources = getResources();
        mLogFormatPattern = mResources.getString(R.string.log_dump_format);
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        mLogger = new LogDumper(getFilesDir() + File.separator + LOG_DIR);
        
        mTimerView = (TimerView)findViewById(R.id.timer_view);
        
        mWorkName = (EditText)findViewById(R.id.work_name);
        
        mStartStopButton = (ToggleButton)findViewById(R.id.start_stop_button);
        
        mLogView = (ListView)findViewById(R.id.log_view);
        mLogAdapter = new ArrayAdapter<String>(this, R.layout.list_column);
        mLogView.setAdapter(mLogAdapter);
        mLogView.setDividerHeight(0);
        
        // 壁紙の設定
        View rootView = (View)findViewById(R.id.root);
        mWallpaper = new Wallpaper(rootView, getFilesDir());
    }
    
    /**
     * 作業時間の記録開始・終了を行います。
     * @param view 記録開始・終了トグルボタン
     * @throws IOException 
     */
    public void onStartStop(View view) throws IOException {
        if(mStartStopButton.isChecked()) {
            mStartTime = mTimerView.start();
        } else {
            long spentTime = mTimerView.stop();
            
            // リストビューに書き出し
            String logFormatString = mResources.getString(R.string.log_list_string);
            mLogAdapter.insert(
                    String.format(
                            logFormatString,
                            mWorkName.getText().toString(),
                            spentTime
              ), 0);
            
            // ログファイルに書き出し
            Date startDate = new Date(mStartTime);
            String dumpMessage =
                    String.format(
                            mLogFormatPattern,
                            mWorkName.getText().toString(),
                            mDateFormat.format(startDate),
                            TimeUtility.formatSpentTime(spentTime)
              );
            
            mLogger.dump(startDate, dumpMessage);
        }
    }
    
    /**
     * 詳細リスト表示アクティビティに切り替えます。
     * @param view
     */
    public void onDetail(View view) {
        Intent intent = new Intent(this, DetailViewer.class);
        startActivity(intent);
    }
    
    ///////////////////////////////////////////////
    // 回転したときやバックグラウンドに行ったときでも、
    // 時間計測を続けられるように
    // Bundle に作業名と開始時間を記録しておく。
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);
        boolean isRecording = mStartStopButton.isChecked();
        outState.putBoolean(IS_RECORDING, isRecording);
        
        if (isRecording) {
            String workName = mWorkName.getText().toString();
            long startTime = mStartTime;
            
            outState.putString(WORK_NAME, workName);
            outState.putLong(START_TIME, startTime);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {  
        super.onRestoreInstanceState(savedInstanceState);  
        
        boolean isRecording = savedInstanceState.getBoolean(IS_RECORDING);
        
        if (isRecording) {
            // 引き継いだ startTime はそのままに、タイマーだけスタートする。
            mWorkName.setText(savedInstanceState.getString(WORK_NAME));
            mStartTime = savedInstanceState.getLong(START_TIME);
            
            mTimerView.start();
            mTimerView.overrideStartTime(mStartTime);
            mStartStopButton.setChecked(true);
        } else {
            mStartStopButton.setChecked(false);
        }
    }
    
    //////////////
    // メニュー設定
    static final int REQUEST_CODE_GET_CONTENT = 1;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.work_record_menu, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wallpaper:
                // ギャラリーから画像を選択し、バックグラウンドに設定する。
                Wallpaper.chooseWallpaper(this, REQUEST_CODE_GET_CONTENT);
                break;
            default:
                throw new IllegalArgumentException("unknown menu id.");
        }
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_CONTENT) {
            if (resultCode == RESULT_OK) {
                try {
                    // 受け取った Bitmap を壁紙に設定する
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    mWallpaper.setWallpaper(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.image_io_error, Toast.LENGTH_LONG);
                }
            }
        }
    }
}