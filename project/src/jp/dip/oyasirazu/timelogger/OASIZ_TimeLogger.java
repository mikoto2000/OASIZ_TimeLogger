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
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.oyasirazu.timelogger.util.DataStore;
import jp.dip.oyasirazu.timelogger.util.Settings;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;
import jp.dip.oyasirazu.timelogger.view.TimerView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 時間を記録するアクティビティ
 * @author mikoto
 */
public class OASIZ_TimeLogger extends Activity {
    
    private static final String IS_RECORDING = "IS_RECORDING"; 
    private static final String WORK_NAME = "WORK_NAME"; 
    private static final String START_TIME = "START_TIME"; 
    
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE; 
    private static final String SELECTED_COLOR = "SelectedColor"; 
    
    private Wallpaper mWallpaper;
    
    private Resources mResources;
    private TimerView mTimerView;
    private DigitalClock mDigitalClock;
    private EditText mWorkName;
    private ListView mLogView;
    private WorkListAdapter mLogAdapter;
    
    private DataStore mDataStore;
    private Date mStartDate;
    private SimpleDateFormat mDateFormat;
    
    private ToggleButton mStartStopButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_record);
        
        mResources = getResources();
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        mDataStore = new WorkLogDatabase(this, mDateFormat);
        
        mTimerView = (TimerView)findViewById(R.id.timer_view);
        
        mDigitalClock = (DigitalClock)findViewById(R.id.time_view);
        
        mWorkName = (EditText)findViewById(R.id.work_name);
        
        mStartStopButton = (ToggleButton)findViewById(R.id.start_stop_button);
        
        mLogView = (ListView)findViewById(R.id.log_view);
        String logFormatString = mResources.getString(R.string.log_list_string);
        mLogAdapter = new WorkListAdapter(this, R.layout.list_column, mDataStore.getWorkList(), logFormatString);
        
        mLogView.setAdapter(mLogAdapter);
        mLogView.setDividerHeight(0);
        mLogView.setOnItemClickListener(new OnWorkListItemClickListener());
        
        // 壁紙の設定
        WindowManager windowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowmanager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        
        View rootView = (View)findViewById(R.id.root);
        mWallpaper = new Wallpaper(rootView, getFilesDir(), width, height);
        
        int textColor = Settings.getTextColor(this, DEFAULT_TEXT_COLOR);
        setTextColor(textColor);
    }
    
    @Override
    protected void onDestroy() {
        mDataStore.close();
        
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        mLogAdapter.clear();
        
        for (Work work : mDataStore.getWorkList()) {
            mLogAdapter.add(work);
        }
        
        int textColor = Settings.getTextColor(this, DEFAULT_TEXT_COLOR);
        setTextColor(textColor);
    };
    
    /**
     * 作業時間の記録開始・終了を行います。
     * @param view 記録開始・終了トグルボタン
     * @throws IOException 
     */
    public void onStartStop(View view) throws IOException {
        if(mStartStopButton.isChecked()) {
            mStartDate = mTimerView.start();
        } else {
            Date endDate = mTimerView.stop();
            
            Work work = new Work(
                    mWorkName.getText().toString(),
                    mStartDate,
                    endDate);
            
            // リストビューに書き出し
            mLogAdapter.insert(work, 0);
            
            // ログ
            mDataStore.add(work);
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
            
            outState.putString(WORK_NAME, workName);
            outState.putSerializable(START_TIME, mStartDate);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {  
        super.onRestoreInstanceState(savedInstanceState);  
        
        boolean isRecording = savedInstanceState.getBoolean(IS_RECORDING);
        
        if (isRecording) {
            // 引き継いだ startTime はそのままに、タイマーだけスタートする。
            mWorkName.setText(savedInstanceState.getString(WORK_NAME));
            mStartDate = (Date)savedInstanceState.getSerializable(START_TIME);
            
            mTimerView.start();
            mTimerView.overrideStartDate(mStartDate);
            mStartStopButton.setChecked(true);
        } else {
            mStartStopButton.setChecked(false);
        }
    }
    
    //////////////
    // メニュー設定
    static final int REQUEST_CODE_GET_CONTENT = 1;
    static final int REQUEST_CODE_GET_COLOR = 2;
    
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
                // 壁紙設定ダイアログを開く
                mWallpaper.openWallpaperDialog(this, REQUEST_CODE_GET_CONTENT);
            break;
            case R.id.menu_text_color:
                Intent intent = new Intent(this, org.superdry.util.colorpicker.lib.SuperdryColorPicker.class);
                int currentTextColor = Settings.getTextColor(this, DEFAULT_TEXT_COLOR);
                intent.putExtra(SELECTED_COLOR, currentTextColor);
                startActivityForResult(intent, REQUEST_CODE_GET_COLOR);
                break;
            case R.id.menu_info:
                Settings.showLicenses(this);
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
                    mWallpaper.setWallpaper(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.image_io_error, Toast.LENGTH_LONG);
                }
            }
        } else if (requestCode == REQUEST_CODE_GET_COLOR) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra(SELECTED_COLOR)) {
                    int textColor = data.getIntExtra(SELECTED_COLOR, DEFAULT_TEXT_COLOR);
                    
                    Settings.saveTextColor(this, textColor);
                    
                    setTextColor(textColor);
                }
            }
        }
    }
    
    /**
     * このアクティビティの文字色を設定します。
     * @param textColor 文字色
     */
    private void setTextColor(int textColor) {
        mTimerView.setTextColor(textColor);
        mDigitalClock.setTextColor(textColor);
        mLogAdapter.setTextColor(textColor);
    }
    
    /**
     * 作業履歴リストをタップされた時に呼び出されるリスナ、<br />
     * タップされたアイテムの作業名を EditText に設定する。
     * @author mikoto
     *
     */
    private class OnWorkListItemClickListener implements OnItemClickListener {
        /**
         * タップされたアイテムの作業名を EditText に設定する。
         */
        public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
            Work work = mLogAdapter.getItem(position);
            mWorkName.setText(work.getName());
        }
    }
}