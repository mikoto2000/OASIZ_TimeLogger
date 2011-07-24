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
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DetailViewer extends ListActivity {

    File mLogBaseDir;
    File[] mLogFiles;
    
    ListView mListView;
    ArrayAdapter<String> mLogAdapter;
    
    int mCurrentLogFileIndex;
    FileSorter mLogSorter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_list);
        
        mLogSorter = new FileSorter();
        mLogBaseDir = getFilesDir();
        
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
    
    public void onNext(View view) {
        
    }
    
    public void onPrev(View view) {
        
    }
}