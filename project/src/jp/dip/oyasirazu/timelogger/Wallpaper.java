/**
 * Wallpaper.java
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

package jp.dip.oyasirazu.timelogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class Wallpaper {
    
    private static final String WALLPAPER_DIR = "wallpaper"; 
    private static final String WALLPAPER_NAME = "wallpaper.img";
    private static final int WALLPAPER_QUALITY = 100;
    
    private File mWallpaperDir;
    private File mWallpaperFile;
    private View mRootLayout;
    
    public Wallpaper(View rootLayout, File baseDir) {
        mRootLayout = rootLayout;
        mWallpaperDir = new File(baseDir + File.separator + WALLPAPER_DIR);
        mWallpaperFile = new File(
                baseDir +
                File.separator +
                WALLPAPER_DIR +
                File.separator +
                WALLPAPER_NAME);
        
        createWallpaperDir();
        
        if (mWallpaperFile.exists()) {
            BitmapDrawable drawable = new BitmapDrawable(mWallpaperFile.getAbsolutePath());
            mRootLayout.setBackgroundDrawable(drawable);
        }
    }
    
    private void createWallpaperDir() {
        if (!mWallpaperDir.exists()) {
            mWallpaperDir.mkdirs();
        }
    }
    
    public void clearWallpaper() {
        mWallpaperFile.delete();
        mRootLayout.setBackgroundDrawable(null);
    }
    
    public static void chooseWallpaper(Activity activity, int requestCode) {
        // ギャラリーから画像を選択し、バックグラウンドに設定する。
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }
    
    public void setWallpaper(Bitmap bitmap) throws IOException {
        // 受け取った画像をアプリケーションディレクトリ内に記録する
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(mWallpaperFile));
        bitmap.compress(CompressFormat.PNG, WALLPAPER_QUALITY, bos);
        bos.close();
        
        BitmapDrawable drawable = new BitmapDrawable(mWallpaperFile.getAbsolutePath());
        mRootLayout.setBackgroundDrawable(drawable);
    }
    
    public void openWallpaperDialog(final Activity activity, final int requestCode) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                activity);
        
        dialogBuilder.setTitle(R.string.wallpaper);
        
        // 表示項目とリスナの設定
        final String[] dialogItems = activity.getResources()
                .getStringArray(R.array.dialog_wallpaper);
        
        dialogBuilder.setItems(dialogItems,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                        case 0:
                            // バックグラウンドイメージを削除する
                            clearWallpaper();
                            break;
                        case 1:
                            // ギャラリーから画像を選択し、バックグラウンドに設定する。
                            Wallpaper.chooseWallpaper(activity,
                                    requestCode);
                            break;
                        }
                    }
                });
        
        // ダイアログを表示
        dialogBuilder.create().show();
    }
}
