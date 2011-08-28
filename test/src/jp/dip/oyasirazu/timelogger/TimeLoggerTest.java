package jp.dip.oyasirazu.timelogger;

import jp.dip.oyasirazu.timelogger.OASIZ_TimeLogger;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;

public class TimeLoggerTest extends
        ActivityInstrumentationTestCase2<OASIZ_TimeLogger> {
    
    private static final String PACKAGE_NAME = "jp.dip.oyasirazu.timelogger";
    
    public TimeLoggerTest() {
        super(PACKAGE_NAME, OASIZ_TimeLogger.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // データベースファイルを削除
        TestConst.deleteDatabase(getActivity());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testPreConditions() {
        // setUp で データベースファイルを削除しても、そのテスト中は反映されない？
        // (deleteDatabaseは実行されるのに、 adapter にはアイテムが入ったままになる、削除タイミングの問題か？
        //  setUp の時点ではもう Activity もその中のオブジェクトも全部作成・設定済みということか？)
        // なので、データベースファイルの削除をさせるためだけにこのメソッドを定義。
        // 要原因究明
    }
    
    public void testShowActivityLand() {
        testShowActivity(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    public void testShowActivityPort() {
        testShowActivity(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /**
     * Record 画面が表示されることを確認します。
     * @param screenOrientation 画面の向き
     */
    private void testShowActivity(int screenOrientation) {
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // 画面向きの指定
        activity.setRequestedOrientation(screenOrientation);
        
        // View の取得
        final ListView listView = (ListView) activity.findViewById(R.id.log_view);
        Adapter adapter = listView.getAdapter();
        
        // サイズは 0 のはず
        assertEquals(0, adapter.getCount());
    }
    
    public void testShowDetailViewLand() {
        testShowDetailView(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    public void testShowDetailViewPort() {
        testShowDetailView(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /**
     * Detail ボタンを押したときに、 DetailViewr が呼び出されることを確認します。
     * @param screenOrientation 画面の向き
     */
    private void testShowDetailView(int screenOrientation) {
        final Instrumentation instrumentation = getInstrumentation();
        
        // 詳細ビューが呼び出されたかを確認するために、 ActivityMonitor を作成
        final String detailView = "jp.dip.oyasirazu.timelogger.DetailViewer";
        final ActivityMonitor monitor = new ActivityMonitor(detailView, null, false);
        instrumentation.addMonitor(monitor);
        
        // アクティビティの取得
        final OASIZ_TimeLogger activity = getActivity();
        
        // 画面向きの指定
        activity.setRequestedOrientation(screenOrientation);
        
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
        
        // 呼び出した Activity を終了
        if (callee != null) {
            callee.finish();
        }
    }
}
