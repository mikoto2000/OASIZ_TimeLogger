package jp.dip.oyasirazu.timelogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;

public class DetailViewerTest extends 
        ActivityInstrumentationTestCase2<DetailViewer> {

    private static final String PACKAGE_NAME = "jp.dip.oyasirazu.timelogger";
    private static final String TEST_WORK_NAME1 = "TestWorkName1";
    private static final String TEST_WORK_NAME2 = "TestWorkName2";
    private static final String TEST_WORK_NAME3 = "TestWorkName3";
    private static final long START_TIME = 10000;
    private static final long END_TIME = 12000;
    private static final double DELTA = 1000.0;
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;
    
    public DetailViewerTest() {
        super(PACKAGE_NAME, DetailViewer.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        if (getName().equals("testFirstDisplayWork")) {
            WorkLogDatabase wld = new WorkLogDatabase(getInstrumentation().getTargetContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
            wld.add(new Work(TEST_WORK_NAME1, new Date(START_TIME), new Date(END_TIME)));
            wld.add(new Work(TEST_WORK_NAME2, new Date(START_TIME + 1), new Date(END_TIME + 1)));
            wld.add(new Work(TEST_WORK_NAME3, new Date(START_TIME + 2), new Date(END_TIME + 2)));
            wld.close();
        } else if (getName().equals("testThreeDay")) {
            WorkLogDatabase wld = new WorkLogDatabase(getInstrumentation().getTargetContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
            wld.add(new Work(TEST_WORK_NAME1, new Date(START_TIME), new Date(END_TIME)));
            wld.add(new Work(TEST_WORK_NAME2, new Date(START_TIME + ONE_DAY), new Date(END_TIME + ONE_DAY)));
            wld.add(new Work(TEST_WORK_NAME3, new Date(START_TIME + ONE_DAY * 2), new Date(END_TIME + ONE_DAY * 2)));
            wld.close();
        } else if (getName().equals("testLogEdit")) {
            WorkLogDatabase wld = new WorkLogDatabase(getInstrumentation().getTargetContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
            wld.add(new Work(TEST_WORK_NAME1, new Date(START_TIME), new Date(END_TIME)));
            wld.add(new Work(TEST_WORK_NAME2, new Date(START_TIME + 1), new Date(END_TIME + 1)));
            wld.add(new Work(TEST_WORK_NAME3, new Date(START_TIME + 2), new Date(END_TIME + 2)));
            wld.close();
        }
    }
    
    protected void tearDown() throws Exception {
        TestConst.deleteDatabase(getActivity());
        super.tearDown();
    }
    
    /**
     * 詳細画面が表示されることを確認します。
     */
    public void testFirstDisplayWork() {
        // アクティビティの取得
        final DetailViewer activity = getActivity();
        
        // View の取得
        final ListView listView = activity.getListView();
        Adapter adapter = listView.getAdapter();
        
        // サイズは 3 のはず
        assertEquals(3, adapter.getCount());
        
        // 一日分しかデータがないので無効なはず
        Button prev = (Button) activity.findViewById(R.id.prev);
        assertFalse(prev.isEnabled());
        
        // 2 日分しかデータがないので無効なはず
        Button next = (Button) activity.findViewById(R.id.next);
        assertFalse(next.isEnabled());
    }
    
    /**
     * 前へ・次へボタンが正しく動作することを確認します。
     */
    public void testThreeDay() {
        final Instrumentation instrumentation = getInstrumentation();
        
        // アクティビティの取得
        final DetailViewer activity = getActivity();
        
        // View の取得
        final ListView listView = activity.getListView();
        Adapter adapter = listView.getAdapter();
        
        // サイズは 1 のはず
        assertEquals(1, adapter.getCount());
        
        // 最新の日付のアイテムが格納されているはず。
        Work work = (Work)adapter.getItem(0);
        assertEquals(work.getName(), TEST_WORK_NAME3);
        assertEquals(work.getStartDate().getTime(), START_TIME + ONE_DAY * 2, DELTA);
        assertEquals(work.getEndDate().getTime(), END_TIME + ONE_DAY * 2, DELTA);
        
        // 3 日分しかデータがない、かつ最初は最も新しいデータが表示されるので無効なはず
        final Button next = (Button) activity.findViewById(R.id.next);
        assertFalse(next.isEnabled());
        
        // 有効なはず
        final Button prev = (Button) activity.findViewById(R.id.prev);
        assertTrue(prev.isEnabled());
        
        // 前へボタンを押す
        activity.runOnUiThread(new Runnable() {
            public void run() {
                prev.performClick();
            }
        });
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // 2 番目のデータが入っているはず
        work = (Work)adapter.getItem(0);
        assertEquals(work.getName(), TEST_WORK_NAME2);
        assertEquals(work.getStartDate().getTime(), START_TIME + ONE_DAY, DELTA);
        assertEquals(work.getEndDate().getTime(), END_TIME + ONE_DAY, DELTA);

        // 両方有効なはず
        assertTrue(next.isEnabled());
        assertTrue(prev.isEnabled());
        
        // 前へボタンを押す
        activity.runOnUiThread(new Runnable() {
            public void run() {
                prev.performClick();
            }
        });
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // 3 番目のデータが入っているはず
        work = (Work)adapter.getItem(0);
        assertEquals(work.getName(), TEST_WORK_NAME1);
        assertEquals(work.getStartDate().getTime(), START_TIME, DELTA);
        assertEquals(work.getEndDate().getTime(), END_TIME, DELTA);
        
        // 次へだけ有効なはず
        assertTrue(next.isEnabled());
        assertFalse(prev.isEnabled());
    }
}
