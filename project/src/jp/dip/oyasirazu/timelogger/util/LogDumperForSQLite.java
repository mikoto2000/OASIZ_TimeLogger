/**
 * LogDumper.java
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDumperForSQLite {
    
    private SQLiteDatabase mDatabase;
    private SimpleDateFormat mDateFormat;
    
    /**
     * コンストラクタ
     * @param logFilesBasePath
     */
    public LogDumperForSQLite(Context context, String dbFilePath, SimpleDateFormat dateFormat) {
        mDatabase = new LogDatabaseOpenHelper(context, dbFilePath).getWritableDatabase();
        mDateFormat = dateFormat;
    }
    
    /**
     * ログをデータベースに追加します。
     * @param workName
     * @param startDate
     * @param endDate
     * @throws IOException
     */
    public void dump(String workName, Date startDate, Date endDate) throws IOException {
        if (workName == null) {
            throw new NullPointerException("'workName == null' not permit.");
        }
        
        if (startDate == null) {
            throw new NullPointerException("'startDate == null' not permit.");
        }
        
        if (endDate == null) {
            throw new NullPointerException("'endDate == null' not permit.");
        }
        
        mDatabase.execSQL("insert into log_tbl values('" +
                workName + "', '" +
                mDateFormat.format(startDate) + "', '" +
                mDateFormat.format(endDate) + "')");
        
    }
    
    private class LogDatabaseOpenHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;

      public LogDatabaseOpenHelper(Context context,String dbName){
          super(context, dbName, null, VERSION);
      }

      public void onCreate(SQLiteDatabase db){
      db.execSQL("create table log_tbl(work_name text, star_date text, end_date text);");
      }

      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
              // not implement
      }
      }
}
