/**
 * WorkLogDatabase.java
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jp.dip.oyasirazu.timelogger.Work;

public class WorkLogDatabase implements DataStore {
    
    public static final String TABLE_NAME = "log_tbl";
    public static final String WORK_NAME = "work_name";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String[] COLUMNS = {WORK_NAME, START_DATE, END_DATE};
    
    public static final String SELECTION = START_DATE + " like ?";
    
    private LogDatabaseOpenHelper mDatabaseOpenHelper;
    private SQLiteDatabase mDatabase;

    private SimpleDateFormat mDateFormat;
    
    // 年月日までしか表示しないフォーマット
    private SimpleDateFormat mOnlyYmdFormat = new SimpleDateFormat("yyyy/MM/dd"); 
    
    private Date mCurrentDate;
    
    public WorkLogDatabase(Context context, SimpleDateFormat dateFormat) {
        mDatabaseOpenHelper = new LogDatabaseOpenHelper(context);
        mDatabase = mDatabaseOpenHelper.getWritableDatabase();
        mDateFormat = dateFormat;
        
        mCurrentDate = getLatestDate();
    }

    public List<Work> getWorkList() {
        if (mCurrentDate == null) {
            return new ArrayList<Work>();
        }
        
        // カレントの日付のレコードを取得する
        String[] selectionArgs = {getCurrentDateName() + "%"};
        
        Cursor cursor = mDatabase.query(
                TABLE_NAME,
                COLUMNS,
                SELECTION,
                selectionArgs,
                null,
                null,
                START_DATE + " desc");
        
        List<Work> workList = new ArrayList<Work>();
        if (cursor.getCount() == 0) {
            return workList;
        }
        
        cursor.moveToFirst();
        do {
            try {
                Work work = new Work(
                        cursor.getString(0),
                        mDateFormat.parse(cursor.getString(1)),
                        mDateFormat.parse(cursor.getString(2))
                        );
                workList.add(work);
            } catch (ParseException e) {
                // パースに失敗したレコードは無視する。(削除したほうがよいか？)
            }
        } while (cursor.moveToNext());
        
        return workList;
    }

    public String getCurrentDateName() {
        return mOnlyYmdFormat.format(mCurrentDate);
    }

    public void next() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(mCurrentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        mCurrentDate = calendar.getTime();
    }

    public void prev() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(mCurrentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        mCurrentDate = calendar.getTime();
    }

    public void dump(Work work) {
        ContentValues values = new ContentValues();
        values.put(WORK_NAME, work.getName());
        values.put(START_DATE, mDateFormat.format(work.getStartDate()));
        values.put(END_DATE, mDateFormat.format(work.getEndDate()));
        
        mDatabase.insert(
                TABLE_NAME,
                null,
                values);
    }
    
    private Date getLatestDate() {
        String latestDay;
        
        // 最新のレコードを取得
        Cursor cursor = mDatabase.query(
                TABLE_NAME,
                COLUMNS,
                null,
                null,
                null,
                null,
                START_DATE + " desc",
                "1");
        
        if (cursor.getCount() == 0) {
            return null;
        }
        
        // 作業開始時間を取得
        cursor.moveToFirst();
        latestDay = cursor.getString(1);
        
        try {
            return mOnlyYmdFormat.parse(latestDay);
        } catch (ParseException e) {
            return null;
        }
    }

    private class LogDatabaseOpenHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "log.db";
        private static final int VERSION = 1;

        public LogDatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + "(" +
                    WORK_NAME + " text, " +
                    START_DATE + " text, " +
                    END_DATE + " text);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // not implement
        }
    }

}