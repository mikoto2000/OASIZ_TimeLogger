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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    static final String WALLPAPER_DIR = "wallpaper";
    static final String WALLPAPER_NAME = "wallpaper.img";
    static final int WALLPAPER_QUALITY = 100;
    
    private ViewGroup mRootLayout;
    
    private Resources mResources;
    private TimerView mTimerView;
    private EditText mWorkName;
    private ListView mLogView;
    private ArrayAdapter<String> mLogAdapter;
    
    private LogDumper mLogger;
    private long mStartTime;
    private SimpleDateFormat mDateFormat;
    private String mLogFormatPattern;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_record);
        
        mRootLayout = (ViewGroup)findViewById(R.id.root);
        
        mResources = getResources();
        mLogFormatPattern = mResources.getString(R.string.log_dump_format);
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        mLogger = new LogDumper(getFilesDir() + File.separator + LOG_DIR);
        
        createWallpaperDir();
        
        mTimerView = (TimerView)findViewById(R.id.timer_view);
        
        mWorkName = (EditText)findViewById(R.id.work_name);
        
        mLogView = (ListView)findViewById(R.id.log_view);
        mLogAdapter = new ArrayAdapter<String>(this, R.layout.list_column);
        mLogView.setAdapter(mLogAdapter);
        mLogView.setDividerHeight(0);
        
        setWallpaper();
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
    

    
    //////////////
    // メニュー設定
    private static final int REQUEST_CODE_GET_CONTENT = 1;
    private static final int REQUEST_CODE_CROP = 2;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.work_record_menu, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_wallpaper:
                // ギャラリーから画像を選択し、バックグラウンドに設定する。
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
                break;
            default:
                throw new IllegalArgumentException("unknown menu id.");
        }
        return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_CONTENT) {
            // 画像の Uri を受け取る
            Uri uri = data.getData();
            
            // クリッピングするためのインテントに Uri を渡してクリップしてもらう
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setData(uri);
            intent.putExtra("outputX", mRootLayout.getWidth());
            intent.putExtra("outputY", mRootLayout.getHeight());
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_CROP);
        }else if (requestCode == REQUEST_CODE_CROP) {
            // クリップ済みの画像(Bitmap)を受け取る
            Bitmap bitmap = data.getExtras().getParcelable("data");
            try{
                // 受け取った画像をアプリケーションディレクトリ内に記録する
                File wallpaper = getWallpaperFile();
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(wallpaper));
                bitmap.compress(CompressFormat.PNG, WALLPAPER_QUALITY, bos);
                bos.close();
                setWallpaper();
            }catch(Exception e){
                Toast.makeText(this, R.string.image_io_error, Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
     }
    
    private void createWallpaperDir() {
        File wallpaperDir = new File(getFilesDir() + File.separator + WALLPAPER_DIR);
        if (!wallpaperDir.exists()) {
            wallpaperDir.mkdirs();
        }
    }
    
    private void setWallpaper() {
        File wallpaper = getWallpaperFile();
        BitmapDrawable drawable = new BitmapDrawable(wallpaper.getAbsolutePath());
        mRootLayout.setBackgroundDrawable(drawable);
    }
    
    private File getWallpaperFile() {
        return new File(
                getFilesDir().getAbsolutePath() +
                File.separator +
                WALLPAPER_DIR +
                File.separator +
                WALLPAPER_NAME);
    }
}