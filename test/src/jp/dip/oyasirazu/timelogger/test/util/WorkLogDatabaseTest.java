package jp.dip.oyasirazu.timelogger.test.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import jp.dip.oyasirazu.timelogger.Work;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;

/**
 * WorkLogDatabase のテスト
 * 
 * 所々、日付の変わり目でテストした場合通らない場所があるが、無視する。
 * 日付の変わり目まで 7 分以上ない場合はおそらくテストが通らない。
 * 
 * @author mikoto
 *
 */
public class WorkLogDatabaseTest extends AndroidTestCase {
    private static final String DB_NAME = "log.db";
    
    private SimpleDateFormat mDefaultFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
     * 追加テスト
     */
    public void testAdd() {
        Context context = getContext();
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, mDefaultFormat);
        
        // リストは空
        assertTrue(workLogDatabase.getWorkList().size() == 0);
        
        // 作業追加
        // 作業開始時間を 1 分ずつ増やして登録していく
        long currentTime = new Date().getTime();
        Date date1 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date2 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date3 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date4 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date5 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date6 = new Date(currentTime);
        
        Work work1 = new Work(20, "test1", date1, date2);
        Work work2 = new Work("test2", date3, date4);
        Work work3 = new Work(10, "test3", date5, date6);
        
        // 順番を入れ替えて追加
        workLogDatabase.add(work1);
        workLogDatabase.add(work3);
        workLogDatabase.add(work2);
        
        // 開始日の降順で帰ってくるはず
        List<Work> works = workLogDatabase.getWorkList();
        assertTrue(compareWorks(works.get(0), work3, true));
        assertTrue(compareWorks(works.get(1), work2, true));
        assertTrue(compareWorks(works.get(2), work1, true));
        
        // add では workNo が無視され、 workNo は追加した順にふられる。
        assertTrue(works.get(0).getWorkNo() == 2);
        assertTrue(works.get(1).getWorkNo() == 3);
        assertTrue(works.get(2).getWorkNo() == 1);
    }
    
    /**
     * データがひとつもない場合の挙動テスト
     */
    public void testNoData() {
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        
        // リストは空
        assertTrue(workLogDatabase.getWorkList().size() == 0);
        
        // データがない場合は今日が currentDate となる
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
    
    private boolean compareWorks(Work work1, Work work2, boolean withoutNo) {
        if (!work1.getName().equals(work2.getName())) {
            return false;
        }
        
        String startDate1 = mDefaultFormat.format(work1.getStartDate());
        String startDate2 = mDefaultFormat.format(work2.getStartDate());
        
        if (!startDate1.equals(startDate2)) {
            return false;
        }
        
        String endDate1 = mDefaultFormat.format(work1.getEndDate());
        String endDate2 = mDefaultFormat.format(work2.getEndDate());
        
        if (!endDate1.equals(endDate2)) {
            return false;
        }
        
       if (!withoutNo && work1.getWorkNo() != work2.getWorkNo()) {
            return false;
        }
       
        return true;
    }

}
