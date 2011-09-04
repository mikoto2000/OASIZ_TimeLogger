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
import jp.dip.oyasirazu.timelogger.util.Settings;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger.REQUEST_CODE_GET_CONTENT;
import static jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger.REQUEST_CODE_GET_COLOR;

/**
 * 作業記録の詳細を見るためのアクティビティ
 * @author mikoto
 *
 */
public class DetailViewer extends ListActivity {
    
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    
    private Wallpaper mWallpaper;
    
    private DataStore mDataStore;
    private SimpleDateFormat mDateFormat;
    
    private WorkListAdapter mLogAdapter;
    private TextView mNoLogData;
    
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
        
        mNoLogData = (TextView) findViewById(android.R.id.empty);
        
        updateButtonsStatus();
        
        // リストの設定
        String logFormatString = getResources().getString(R.string.log_list_string);
        mLogAdapter = new WorkListAdapter(
                this,
                R.layout.list_column,
                logFormatString
                );
        
        int textColor = Settings.getTextColor(this, DEFAULT_TEXT_COLOR);
        setTextColor(textColor);
        
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
    // 作業名変更
    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        Work work = mLogAdapter.getItem(position);
        
        // 作業名変更ダイアログを表示
        openRenameDialog(work, position);
    };
    
    /**
     * 作業名変更ダイアログを表示
     */
    private void openRenameDialog(final Work beforWork, final int position) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                this);
        
        final EditText editText = new EditText(this);
        editText.setText(beforWork.getName());
        editText.setSingleLine(true);
        
        // 表示項目とリスナの設定
        dialogBuilder.setTitle(R.string.edit_work_name);
        dialogBuilder.setView(editText);
        
        // OK ボタンを押された場合、リストとデータストアのデータを更新する。
        dialogBuilder.setPositiveButton(R.string.edit_work_name_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int chButton) {
                        
                        // 作用名が変更された Work オブジェクトを作成
                        Work afterWork = new Work(
                                editText.getText().toString(),
                                beforWork.getStartDate(),
                                beforWork.getEndDate());
                        
                        // リストの情報を更新
                        mLogAdapter.insert(afterWork, position);
                        mLogAdapter.remove(beforWork);
                        
                        // DataStore 内の情報を更新
                        mDataStore.update(beforWork.getWorkNo(), afterWork);
                        }
                    });
        
        // キャンセルボタンを押されても何もしない
        dialogBuilder.setNegativeButton(R.string.edit_work_name_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int chButton) {}
                    });
        
        // ダイアログを表示
        dialogBuilder.create().show();
    }
    
    ////////////
    // ボタン設定
    
    /**
     * next ボタンが押された時の処理。<br />
     * 次の日付のログが存在するなら、次の日付のログを表示します。
     * @param view
     */
    public void onNext(View view) {
        mDataStore.next();
        updateList();
        updateButtonsStatus();
    }
    
    /**
     * prev ボタンが押された時の処理
     * 前の日付のログが存在するなら、次の日付のログを表示します。
     * @param view
     */
    public void onPrev(View view) {
        mDataStore.prev();
        updateList();
        updateButtonsStatus();
    }
    
    /**
     * 現在日に設定されている日付の情報を、リストアクティビティに表示します。
     */
    private void updateList() {
            mLogAdapter.clear();
            List<Work> logList = mDataStore.getWorkList();
            for(Work log : logList) {
                mLogAdapter.add(log);
            }
        setTitle(mDataStore.getCurrentDateName());
    }
    
    /**
     * 前・次の日のデータがない場合、ボタンを無効化します。
     */
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
    
    private void setTextColor(int textColor) {
        mLogAdapter.setTextColor(textColor);
        mNoLogData.setTextColor(textColor);
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
            case R.id.menu_text_color:
                Intent intent = new Intent(this, org.superdry.util.colorpicker.lib.SuperdryColorPicker.class);
                intent.putExtra("SelectedColor", DEFAULT_TEXT_COLOR);
                startActivityForResult(intent, REQUEST_CODE_GET_COLOR);
                break;
            default:
                throw new IllegalArgumentException("unknown menu id.");
        }
        return true;
    }
    
    /**
     * 現在日の情報を、 ACTION_SEND に対応しているアクティビティに渡します。
     */
    private void sendLog() {
        Intent intent;
        if (mLogAdapter.getCount() != 0) {
            // ファイルの内容を取得
            StringBuffer sb = new StringBuffer();
            int itemNum = mLogAdapter.getCount();
            for (int i = 0; i < itemNum; i++) {
                // 「"作業名", "開示時刻", "終了時刻"\n」のフォーマットで吐き出す。
                Work work = mLogAdapter.getItem(i);
                sb.append('"');
                sb.append(work.getName());
                sb.append("\", \"");
                sb.append(mDateFormat.format(work.getStartDate()));
                sb.append("\", \"");
                sb.append(mDateFormat.format(work.getEndDate()));
                sb.append("\"\n");
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
        } else if (requestCode == REQUEST_CODE_GET_COLOR) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("SelectedColor")) {
                    int textColor = data.getIntExtra("SelectedColor", DEFAULT_TEXT_COLOR);
                    Settings.saveTextColor(this, textColor);
                    setTextColor(textColor);
                }
            }
        }
    }
}
