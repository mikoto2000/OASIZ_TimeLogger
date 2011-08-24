package jp.dip.oyasirazu.timelogger.test.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.test.AndroidTestCase;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;

public class WorkLogDatabaseTest extends AndroidTestCase {
    private static final String DB_NAME = "log.db";
    
    private SimpleDateFormat mOnlyYmdFormat = new SimpleDateFormat("yyyy/MM/dd"); 
    
    @Override
    protected void setUp() {
        File databaseFile = getContext().getDatabasePath(DB_NAME);
        databaseFile.delete();
    };
    
    /**
     * コンストラクタのテスト
     */
    public void testConstructor(){
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        // 正常系
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        File databaseFile = getContext().getDatabasePath(DB_NAME);
        assertTrue(databaseFile.exists());
        
        // 異常系
        try {
            workLogDatabase = new WorkLogDatabase(null, format1);
            assertTrue(false);
        } catch (NullPointerException e) {}
        
        try {
            workLogDatabase = new WorkLogDatabase(context, null);
            assertTrue(false);
        } catch (NullPointerException e) {}
    }
    
    /**
     * データがひとつもない場合の挙動テスト
     */
    public void testNoData() {
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        
        // データがない場合は今日が currentDate となる
        // 日付の変わり目でテストした場合通らない可能性があるけどそこは無視しておく。
        Date currentDate = new Date();
        String currentDateString = mOnlyYmdFormat.format(currentDate);
        String dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertTrue(dbCurrentDateName.equals(currentDateString));
        
        // 戻り値も何もないけれど、落ちないことだけを確認
        workLogDatabase.next();
        workLogDatabase.prev();
        
        // 次も前もないはず
        assertFalse(workLogDatabase.hasNext());
        assertFalse(workLogDatabase.hasPrev());
    }
}
