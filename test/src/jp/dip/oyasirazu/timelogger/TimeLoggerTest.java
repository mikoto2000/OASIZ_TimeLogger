package jp.dip.oyasirazu.timelogger;

import jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

public class TimeLoggerTest extends
        ActivityInstrumentationTestCase2<OASIZ_TimeLogger> {
    
    private static String PACKAGE_NAME = "jp.dip.oyasirazu.timelogger";
    private static String DETAIL_VIEW = "jp.dip.oyasirazu.timelogger.DetailView";

    public TimeLoggerTest() {
        super(PACKAGE_NAME, OASIZ_TimeLogger.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testShowDetailView() {
        Instrumentation instrumentation = getInstrumentation();
        
        // 詳細ビューが呼び出されたかを確認するために、 ActivityMonitor を作成
        ActivityMonitor monitor = new ActivityMonitor(DETAIL_VIEW, null, false);
        instrumentation.addMonitor(monitor);
        
        // アクティビティと、詳細画面移動ボタンの取得
        final OASIZ_TimeLogger activity = getActivity();
        final Button button = (Button) activity.findViewById(R.id.button_detail_view);
        
        // 詳細画面移動ボタンをクリック
        activity.runOnUiThread(new Runnable() {
            public void run() {
                button.performClick();
            }
        });
        
        // クリックイベントが monitor に反映されるのを待つ(?)
        instrumentation.waitForMonitor(monitor);
        
        // 期待した通り呼び出されているかをチェック
        assertEquals(1, monitor.getHits());
    }
}
