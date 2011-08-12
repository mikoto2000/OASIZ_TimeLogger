/**
 * DetailViewer.java
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
import java.util.List;

import jp.dip.oyasirazu.timelogger.util.DataStore;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import static jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger.REQUEST_CODE_GET_CONTENT;;

public class DetailViewer extends ListActivity {
    
    private Wallpaper mWallpaper;
    
    private DataStore mDataStore;
    private SimpleDateFormat mDateFormat;
    
    private ArrayAdapter<Work> mLogAdapter;
    
    private Button mNextButton;
    private Button mPrevButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_list);
        
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        mDataStore = new WorkLogDatabase(this, mDateFormat);
        
        mNextButton = (Button) findViewById(R.id.next);
        mPrevButton = (Button) findViewById(R.id.prev);
        
        updateButtonsStatus();
        
        // リストの設定
        String logFormatString = getResources().getString(R.string.log_list_string);
        mLogAdapter = new WorkListAdapter(
                this,
                android.R.layout.simple_list_item_1,
                logFormatString
                );
        
        setListAdapter(mLogAdapter);
        
        updateList();
        
        // リストの要素の背景色を透明にする
        getListView().setCacheColorHint(Color.argb(0, 0, 0, 0));
        
        // 壁紙の設定
        WindowManager windowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowmanager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        
        View rootView = (View)findViewById(R.id.root);
        mWallpaper = new Wallpaper(rootView, getFilesDir(), width, height);
    }
    
    ////////////
    // ボタン設定
    
    public void onNext(View view) throws IllegalStateException {
        mDataStore.next();
        updateList();
        updateButtonsStatus();
    }
    
    public void onPrev(View view) throws IllegalStateException {
        mDataStore.prev();
        updateList();
        updateButtonsStatus();
    }
    
    private void updateList() throws IllegalStateException {
            mLogAdapter.clear();
            List<Work> logList = mDataStore.getWorkList();
            for(Work log : logList) {
                mLogAdapter.add(log);
            }
        setTitle(mDataStore.getCurrentDateName());
    }
    
    private void updateButtonsStatus() {
        if (mDataStore.hasNext()) {
            mNextButton.setEnabled(true);
        } else {
            mNextButton.setEnabled(false);
        }
        
        if (mDataStore.hasPrev()) {
            mPrevButton.setEnabled(true);
        } else {
            mPrevButton.setEnabled(false);
        }
    }
    
    //////////////
    // メニュー設定
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.detail_viewre_menu, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_view_send:
                sendLog();
                break;
            case R.id.menu_wallpaper:
                // ギャラリーから画像を選択し、バックグラウンドに設定する。
                mWallpaper.openWallpaperDialog(this, REQUEST_CODE_GET_CONTENT);
                break;
            default:
                throw new IllegalArgumentException("unknown menu id.");
        }
        return true;
    }
    
    private void sendLog() {
        Intent intent;
        if (mLogAdapter.getCount() != 0) {
            // ファイルの内容を取得
            StringBuffer sb = new StringBuffer();
            int itemNum = mLogAdapter.getCount();
            for (int i = 0; i < itemNum; i++) {
                sb.append(mLogAdapter.getItem(i));
                sb.append("\n");
            }
            
            intent = new Intent(Intent.ACTION_SEND, null);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, mDataStore.getCurrentDateName());
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(intent, getResources()
                    .getString(R.string.choose_send_activity)));
        } else {
            // ログファイルが無かった場合
            Toast.makeText(this,
                    getResources().getString(R.string.no_log_data),
                    Toast.LENGTH_LONG).show();
        }
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
        }
    }
}
