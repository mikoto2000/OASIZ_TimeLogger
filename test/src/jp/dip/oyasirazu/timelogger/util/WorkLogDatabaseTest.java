package jp.dip.oyasirazu.timelogger.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import jp.dip.oyasirazu.timelogger.TestConst;
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
    protected void setUp() throws Exception {
        super.setUp();
        TestConst.deleteDatabase(getContext());
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
        
        // currentDate は現在の日付になっているはず
        assertEquals(
                workLogDatabase.getCurrentDateName(),
                mOnlyYmdFormat.format(new Date()));
        
        // 日付指定でデータベースに追加
        addOneDayWork(workLogDatabase, new Date(111, 2, 3, 4, 5, 6));
        addOneDayWork(workLogDatabase, new Date(111, 2, 5, 4, 5, 6));
        addOneDayWork(workLogDatabase, new Date(111, 2, 4, 4, 5, 6));
        
        // currentDate は最初に追加した作業の日付になっているはず
        assertEquals(
                workLogDatabase.getCurrentDateName(),
                mOnlyYmdFormat.format(new Date(111, 2, 3, 4, 5, 6)));
        
        WorkLogDatabase workLogDatabase2 = new WorkLogDatabase(context, format1);
        
        // 新しくインスタンスを作った場合は、 currentDate はデータベース内の最新の日付になっているはず
        assertEquals(
                workLogDatabase2.getCurrentDateName(),
                mOnlyYmdFormat.format(new Date(111, 2, 5, 4, 5, 6)));
        
        
        
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
        assertEquals(workLogDatabase.getWorkList().size(), 0);
        
        // 作業追加
        long currentTime = new Date(111, 2, 3, 4, 5, 6).getTime();
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
        
        // currentDate は追加した作業の日付になっているはず
        assertEquals(
                workLogDatabase.getCurrentDateName(),
                mOnlyYmdFormat.format(new Date(111, 2, 3, 4, 5, 6)));
        
        // 開始日の降順で帰ってくるはず
        List<Work> works = workLogDatabase.getWorkList();
        
        assertEquals(works.size(), 3);
        
        assertTrue(compareWorks(works.get(0), work3, true));
        assertTrue(compareWorks(works.get(1), work2, true));
        assertTrue(compareWorks(works.get(2), work1, true));
        
        // add では workNo が無視され、 workNo は追加した順にふられる。
        assertEquals(works.get(0).getWorkNo(), 2);
        assertEquals(works.get(1).getWorkNo(), 3);
        assertEquals(works.get(2).getWorkNo(), 1);
        
    }
    
    /**
     * 更新テスト
     */
    public void testUpdate() {
        Context context = getContext();
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, mDefaultFormat);
        
        // リストは空
        assertEquals(workLogDatabase.getWorkList().size(), 0);
        
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
        
        // 更新用 Work の作成
        currentTime = new Date().getTime();
        Date date7 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date8 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date9 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date10 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date11 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date12 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date13 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date14 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date15 = new Date(currentTime);
        currentTime += 1 * 60 * 1000;
        Date date16 = new Date(currentTime);
        
        Work work4 = new Work("test4", date7, date8);
        Work work5 = new Work("test5", date9, date10);
        Work work6 = new Work("test6", date11, date12);
        Work work7 = new Work("test7", date13, date14);
        Work work8 = new Work("test8", date15, date16);
        
        // 順番を入れ替えて追加
        workLogDatabase.update(1, work4);
        workLogDatabase.update(2, work5);
        workLogDatabase.update(3, work6);
        
        // 登録されていない workNo の場合は無視される
        workLogDatabase.update(4, work7);
        workLogDatabase.update(5, work8);
        
        // 開始日の降順で帰ってくるはず
        List<Work> works = workLogDatabase.getWorkList();
        
        assertEquals(works.size(), 3);
        
        assertTrue(compareWorks(works.get(0), work6, true));
        assertTrue(compareWorks(works.get(1), work5, true));
        assertTrue(compareWorks(works.get(2), work4, true));
        
        assertEquals(works.get(0).getWorkNo(), 3);
        assertEquals(works.get(1).getWorkNo(), 2);
        assertEquals(works.get(2).getWorkNo(), 1);
    }
    
    /**
     * データがひとつもない場合の挙動テスト
     */
    public void testNoData() {
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        
        // リストは空
        assertEquals(workLogDatabase.getWorkList().size(), 0);
        
        // データがない場合は今日が currentDate となる
        Date currentDate = new Date();
        String currentDateString = mOnlyYmdFormat.format(currentDate);
        String dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals( dbCurrentDateName, currentDateString);
        
        // 戻り値も何もないけれど、落ちないことだけを確認
        workLogDatabase.next();
        workLogDatabase.prev();
        
        // 次も前もないはず
        assertFalse(workLogDatabase.hasNext());
        assertFalse(workLogDatabase.hasPrev());
    }
    
    /**
     * データが1日分の場合の挙動テスト
     */
    public void testOneDayData() {
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        
        // リストは空
        assertEquals(workLogDatabase.getWorkList().size(), 0);
        
        // 作業追加
        addOneDayWork(workLogDatabase, new Date(111, 2, 3, 4, 5, 6));
        
        // 最初に追加された日付が currentDate になる
        Date currentDate = new Date(111, 2, 3, 4, 5, 6);
        String currentDateString = mOnlyYmdFormat.format(currentDate);
        String dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        
        // 戻り値も何もないけれど、落ちないことだけを確認
        workLogDatabase.next();
        workLogDatabase.prev();
        
        // 次も前もないはず
        assertFalse(workLogDatabase.hasNext());
        assertFalse(workLogDatabase.hasPrev());
    }
    
    /**
     * データが3日分の場合の挙動テスト
     */
    public void testThreeDayData() {
        Context context = getContext();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorkLogDatabase workLogDatabase = new WorkLogDatabase(context, format1);
        
        // リストは空
        assertEquals(workLogDatabase.getWorkList().size(), 0);
        
        // 作業追加
        addOneDayWork(workLogDatabase, new Date(111, 2, 3, 4, 5, 6));
        addOneDayWork(workLogDatabase, new Date(111, 2, 4, 4, 5, 6));
        addOneDayWork(workLogDatabase, new Date(111, 2, 5, 4, 5, 6));
        
        // データが初めて追加された場合は、その日付が currentDate となる
        Date currentDate = new Date(111, 2, 3, 4, 5, 6);
        String currentDateString = mOnlyYmdFormat.format(currentDate);
        String dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        
        // 次はある
        assertTrue(workLogDatabase.hasNext());
        // 前はない
        assertFalse(workLogDatabase.hasPrev());
        
        // 次の日へ
        workLogDatabase.next();
        currentDate = new Date(111, 2, 4, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        assertTrue(workLogDatabase.hasNext());
        assertTrue(workLogDatabase.hasPrev());
        
        // 次の日へ
        workLogDatabase.next();
        currentDate = new Date(111, 2, 5, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        assertFalse(workLogDatabase.hasNext());
        assertTrue(workLogDatabase.hasPrev());
        
        // 次の日がないので変わらず
        workLogDatabase.next();
        currentDate = new Date(111, 2, 5, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertFalse(workLogDatabase.hasNext());
        assertTrue(workLogDatabase.hasPrev());
        
        // 前の日へ
        workLogDatabase.prev();
        currentDate = new Date(111, 2, 4, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertTrue(workLogDatabase.hasNext());
        assertTrue(workLogDatabase.hasPrev());
        
        // 前の日へ(最初に戻る)
        workLogDatabase.prev();
        currentDate = new Date(111, 2, 3, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        assertTrue(workLogDatabase.hasNext());
        assertFalse(workLogDatabase.hasPrev());
        
        // 前の日がないので変わらず
        workLogDatabase.prev();
        currentDate = new Date(111, 2, 3, 4, 5, 6);
        currentDateString = mOnlyYmdFormat.format(currentDate);
        dbCurrentDateName = workLogDatabase.getCurrentDateName();
        assertEquals(dbCurrentDateName, currentDateString);
        assertTrue(workLogDatabase.hasNext());
        assertFalse(workLogDatabase.hasPrev());
    }
    
    private void addOneDayWork(WorkLogDatabase workLogDatabase, Date date) {
        long currentTime = date.getTime();
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
        
        Work work1 = new Work(mOnlyYmdFormat.format(date1) + "1", date1, date2);
        Work work2 = new Work(mOnlyYmdFormat.format(date1) + "2", date3, date4);
        Work work3 = new Work(mOnlyYmdFormat.format(date1) + "3", date5, date6);
        
        // 追加
        workLogDatabase.add(work1);
        workLogDatabase.add(work2);
        workLogDatabase.add(work3);
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
