package jp.dip.oyasirazu.timelogger;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.Context;

public class TestConst {
    public static final String DB_NAME = "log.db";
    
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static final SimpleDateFormat ONLY_YMD_FORMAT = new SimpleDateFormat("yyyy/MM/dd"); 
    
    /**
     * 指定されたコンテクストのデータベースファイルを削除します。<br />
     * OASIZ_TimeLogger 専用の作りです。(DB_NAME固定)
     * @param context データベースファイルを削除するコンテクスト
     */
    public static void deleteDatabase(Context context) {
        File databaseFile = context.getDatabasePath(DB_NAME);
        databaseFile.delete();
    }
}
