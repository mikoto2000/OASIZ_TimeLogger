/**
 * LogDumper.java
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

package jp.dip.oyasirazu.timelogger.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.text.format.DateFormat;

public class LogDumper {
    private static final String DEFAULT_LOG_FILE_NAME_PATTERN = "yyyyMMdd.log";
    
    private File mOutputDir;
    private String mLogFileNamePattern = DEFAULT_LOG_FILE_NAME_PATTERN;
    
    /**
     * コンストラクタ
     * @param logFilesBasePath
     */
    public LogDumper(String logFilesBasePath) {
        this(new File(logFilesBasePath));
    }
    
    /**
     * コンストラクタ
     * @param logFilesBaseDir
     */
    public LogDumper(File logFilesBaseDir) {
        if (logFilesBaseDir.exists()
                && !logFilesBaseDir.isDirectory()) {
            throw new IllegalArgumentException("logFilesBaseDir is not directory.");
        }
        
        if (!logFilesBaseDir.exists()) {
            logFilesBaseDir.mkdirs();
        }
        
        
        mOutputDir = logFilesBaseDir;
    }
    
    /**
     * ログをファイルに書き出します。
     * @param date ログ出力時刻
     * @param logMessage ログメッセージ
     * @throws IOException ファイルが無い、権限が無い等で投げられる？
     */
    public void dump(Date date, String logMessage) throws IOException {
        if (date == null) {
            throw new IllegalArgumentException("'date == null' not permit.");
        }
        
        if (logMessage == null) {
            throw new IllegalArgumentException("'logMessage == null' not permit.");
        }
        String outputFileName = DateFormat.format(mLogFileNamePattern, date).toString();
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(
                        mOutputDir.getAbsolutePath() + File.separator + outputFileName, true)));
        
        bw.write(logMessage);
        bw.flush();
        bw.close();
    }
}
