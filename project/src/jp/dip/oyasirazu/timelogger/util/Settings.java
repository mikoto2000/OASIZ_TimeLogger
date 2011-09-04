package jp.dip.oyasirazu.timelogger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.dip.oyasirazu.timelogger.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;

public class Settings {
    
    private static final String LICENSES_FILE_NAME = "LICENSES";
    
    private static final String PREF_NAME = "pref";
    private static final String TEXT_COLOR = "TextColor";
    
    private Settings() {};
    
    public static void saveTextColor(Context context, int textColor) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt(TEXT_COLOR, textColor);
        e.commit();
    }
    
    public static int getTextColor(Context context, int defaultTextColor) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(TEXT_COLOR, defaultTextColor);
    }

    public static void showLicenses(Context context) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                context);
        
        dialogBuilder.setTitle(R.string.menu_info);
        
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(LICENSES_FILE_NAME);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            StringBuffer sb = new StringBuffer();
            
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                
                line = br.readLine();
            }
            
            dialogBuilder.setMessage(sb);
            dialogBuilder.create();
            dialogBuilder.show();
        } catch (IOException e) {
            // 読み込みエラーの場合は何もせずに終了
            e.printStackTrace();
        }
    }
}
