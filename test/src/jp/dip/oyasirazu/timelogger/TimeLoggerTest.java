package jp.dip.oyasirazu.timelogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger;
import jp.dip.oyasirazu.timelogger.util.WorkLogDatabase;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

public class TimeLoggerTest extends
        ActivityInstrumentationTestCase2<OASIZ_TimeLogger> {
    
    private static final String PACKAGE_NAME = "jp.dip.oyasirazu.timelogger";
    private static final String TEST_WORK_NAME1 = "TestWorkName1";
    private static final String TEST_WORK_NAME2 = "TestWorkName2";
    private static final String TEST_WORK_NAME3 = "TestWorkName3";
    private static final long START_TIME = 10000;
    private static final long END_TIME = 12000;
    private static final double DELTA = 1000.0;
    
    public TimeLoggerTest() {
        super(PACKAGE_NAME, OASIZ_TimeLogger.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        if (getName().equals("testFirstDisplayWork")) {
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
     * Record 画面が表示されることを確認します。
     */
    public void testShowActivity() {
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // View の取得
        final ListView listView = (ListView) activity.findViewById(R.id.log_view);
        Adapter adapter = listView.getAdapter();
        
        // サイズは 0 のはず
        assertEquals(0, adapter.getCount());
    }
    
    /**
     * 以下の手順で、記録開始・終了ボタンが正しく動作するかを確認します。
     * 1. 作業名を入力<br />
     * 2. 記録開始ボタンを押す<br />
     * 3. 記録終了ボタンを押す<br />
     * 4. リストビューに正しく追加されているかを確認<br />
     */
    public void testAddWork() {
        final Instrumentation instrumentation = getInstrumentation();
        
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // EditText の取得
        final EditText editText = (EditText) activity.findViewById(R.id.work_name);
        
        activity.runOnUiThread(new Runnable() {
            public void run() {
                editText.requestFocus();
            }
        });
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // 文字入力
        final String workName = TEST_WORK_NAME1;
        instrumentation.sendStringSync(workName);
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // 作業記録ボタンの取得
        final ToggleButton record = (ToggleButton) activity.findViewById(R.id.start_stop_button);
        
        // 開始時刻を取得
        final Date startDate = new Date();
        
        // ボタンをクリック
        activity.runOnUiThread(new Runnable() {
            public void run() {
                record.performClick();
                
            }
        });
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // 終了時刻取得
        Date endDate = new Date();
        
        // ボタンをクリック
        activity.runOnUiThread(new Runnable() {
            public void run() {
                record.performClick();
            }
        });
        
        // UI スレッドと同期
        instrumentation.waitForIdleSync();
        
        // ListView の取得と内容確認
        final ListView listView = (ListView) activity.findViewById(R.id.log_view);
        final Adapter adapter = listView.getAdapter();
        
        // サイズは 1 のはず
        assertEquals(1, adapter.getCount());
        
        // adapter に格納されている Work 内容が正しいかをチェック
        final Work work = (Work)adapter.getItem(0);
        
        final long startTime = startDate.getTime();
        final long endTime = endDate.getTime();
        
        // 作業名のチェック
        assertEquals(workName, work.getName());
        
        // 開始・終了時刻のチェック(1 秒以内の誤差は許す)
        assertEquals((double)startTime, (double)work.getStartDate().getTime(), DELTA);
        assertEquals((double)endTime, (double)work.getEndDate().getTime(), DELTA);
    }
    
    /**
     * アクティビティ起動時の作業ログ表示を確認します。<br />
     * アクティビティ起動時に、データベースにデータが存在する場合は、 ListView にログを表示する。<br />
     * 正しく表示されるかを確認する。<br />
     * setUp メソッドで、予めデータベースにデータを追加してある。<br />
     */
    public void testFirstDisplayWork() {
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // ListView の取得と内容確認
        final ListView listView = (ListView) activity.findViewById(R.id.log_view);
        final Adapter adapter = listView.getAdapter();
        
        // サイズは 3 のはず
        assertEquals(3, adapter.getCount());
        
        // adapter に格納されている Work 内容が正しいかをチェック(startTime 降順に格納されているはず)
        final Work work1 = (Work)adapter.getItem(0);
        final Work work2 = (Work)adapter.getItem(1);
        final Work work3 = (Work)adapter.getItem(2);
        
        // 作業名のチェック
        assertEquals(TEST_WORK_NAME3, work3.getName());
        assertEquals(TEST_WORK_NAME2, work2.getName());
        assertEquals(TEST_WORK_NAME1, work1.getName());
        
        // 開始・終了時刻のチェック(1 秒以内の誤差は許す)
        assertEquals((double)START_TIME + 2, (double)work1.getStartDate().getTime(), DELTA);
        assertEquals((double)END_TIME + 2, (double)work1.getEndDate().getTime(), DELTA);
        assertEquals((double)START_TIME + 1, (double)work2.getStartDate().getTime(), DELTA);
        assertEquals((double)END_TIME + 1, (double)work2.getEndDate().getTime(), DELTA);
        assertEquals((double)START_TIME, (double)work3.getStartDate().getTime(), DELTA);
        assertEquals((double)END_TIME, (double)work3.getEndDate().getTime(), DELTA);
    }
    
    /**
     * Detail ボタンを押したときに、 DetailViewr が呼び出されることを確認します。
     */
    public void testShowDetailView() {
        final Instrumentation instrumentation = getInstrumentation();
        
        // 詳細ビューが呼び出されたかを確認するために、 ActivityMonitor を作成
        final String detailView = "jp.dip.oyasirazu.timelogger.DetailViewer";
        final ActivityMonitor monitor = new ActivityMonitor(detailView, null, false);
        instrumentation.addMonitor(monitor);
        
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // 詳細画面移動ボタンの取得
        final Button button = (Button) activity.findViewById(R.id.button_detail_view);
        
        // 詳細画面移動ボタンをクリック
        activity.runOnUiThread(new Runnable() {
            public void run() {
                button.performClick();
            }
        });
        
        // クリックイベントが ActivityMonitor に反映されるのを待つ(?)
        final Activity callee = instrumentation.waitForMonitor(monitor);
        
        // 期待した通り呼び出されているかをチェック
        assertEquals(1, monitor.getHits());
        
        // ActivityMonitor の後始末
        instrumentation.removeMonitor(monitor);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 呼び出した Activity を終了
        if (callee != null) {
            callee.finish();
        }
    }
}
