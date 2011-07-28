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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import static jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger.LOG_DIR;
import static jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger.REQUEST_CODE_GET_CONTENT;;

public class DetailViewer extends ListActivity {

    private Wallpaper mWallpaper;
    
    private File mLogBaseDir;
    private File[] mLogFiles;
    
    private ArrayAdapter<String> mLogAdapter;
    
    private int mCurrentLogFileIndex;
    private FileSorter mLogSorter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_list);
        
        mLogSorter = new FileSorter();
        mLogBaseDir = new File(getFilesDir() + File.separator + LOG_DIR);
        
        // ログファイルの一覧を取得
        mLogFiles = mLogBaseDir.listFiles();
        Arrays.sort(mLogFiles, mLogSorter);
        
        // 最新のログを表示する指定
        mCurrentLogFileIndex = mLogFiles.length - 1;
        
        // リストの設定
        if (mLogFiles.length > 0) {
            try {
                mLogAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        createLogList(mCurrentLogFileIndex));
            } catch (IOException e) {
                e.printStackTrace();
                String errorMessage = getResources().getString(R.string.log_input_error);
                mLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
                mLogAdapter.add(errorMessage);
            }
        }
        
        setListAdapter(mLogAdapter);
        
        // リストの要素の背景色を透明にする
        getListView().setCacheColorHint(Color.argb(0, 0, 0, 0));
        
        // 壁紙の設定
        View rootView = (View)findViewById(R.id.root);
        mWallpaper = new Wallpaper(rootView, getFilesDir());
    }
    
    /**
     * 指定されたインデックスに格納されたログデータが格納されたリストを作成する。
     * @param logFileIndex リストに表示させたいログのインデックス番号
     * @return ログの 1 行が 1 要素となっているリスト
     * @throws IOException ログファイルの読み込みに失敗した場合
     * @throws IllegalStateException 不正なログファイルのインデックス番号が指定された場合
     */
    private List<String> createLogList(int logFileIndex) throws IOException, IllegalStateException {
        List<String> logList = new ArrayList<String>();
        
        // 不正なログファイルのインデックス番号が指定された場合
        if (logFileIndex >= mLogFiles.length) {
            throw new IllegalStateException("log file not found.");
        }
        
        File logFile = mLogFiles[logFileIndex];
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
        
        String str = br.readLine();
        while (str != null) {
            logList.add(str);
            str = br.readLine();
        }
        br.close();
        
        // 新しいものが上にくるようにリバース
        Collections.reverse(logList);
        return logList;
    }

    private class FileSorter implements Comparator<File> {
        public int compare(File file1, File file2) {
            String fileName1 = file1.getName();
            String fileName2 = file2.getName();
            return fileName1.compareTo(fileName2);
        }
    }
    
    ////////////
    // ボタン設定
    
    public void onNext(View view) throws IllegalStateException, IOException {
        if (mCurrentLogFileIndex < mLogFiles.length - 1) {
            mCurrentLogFileIndex++;
            updateList();
        }
    }
    
    public void onPrev(View view) throws IllegalStateException, IOException {
        if (mCurrentLogFileIndex > 0) {
            mCurrentLogFileIndex--;
            updateList();
        }
    }
    
    private void updateList() throws IllegalStateException, IOException {
            mLogAdapter.clear();
            List<String> logList = createLogList(mCurrentLogFileIndex);
            for(String log : logList) {
                mLogAdapter.add(log);
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
        if (mLogFiles.length != 0) {
            // 表示中のファイルを取得
            File currentFile = mLogFiles[mCurrentLogFileIndex];
            
            // ファイルの内容を取得
            StringBuffer sb = new StringBuffer();
            int itemNum = mLogAdapter.getCount();
            for (int i = 0; i < itemNum; i++) {
                sb.append(mLogAdapter.getItem(i));
                sb.append("\n");
            }
            
            intent = new Intent(Intent.ACTION_SEND, null);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, currentFile.getName());
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
