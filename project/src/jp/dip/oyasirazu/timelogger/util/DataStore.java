/**
 * DataStore.java
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

import java.util.List;

import jp.dip.oyasirazu.timelogger.Work;

/**
 * 作業記録を記録・更新するためのインタフェースを提供します。
 * @author mikoto
 */
public interface DataStore {
    
    /**
     * 設定された表示日の作業記録のリストを取得します。
     * @return 表示日の作業記録のリスト
     */
    public List<Work> getWorkList();
    
    /**
     * 設定された表示日の名前(yyyy/MM/dd の形式になった日付文字列)を取得します。
     * @return 設定された表示日の名前(yyyy/MM/dd の形式)
     */
    public String getCurrentDateName();
    
    /**
     * 次の日付の作業情報が存在する場合に true を返します。
     * @return true:次の日付の作業情報が存在する false:次の日付の作業情報が存在しない
     */
    public boolean hasNext();
    
    /**
     * 前の日付の作業情報が存在する場合に true を返します。
     * @return true:前の日付の作業情報が存在する false:前の日付の作業情報が存在しない
     */
    public boolean hasPrev();
    
    /**
     * 表示日を、一日後の日付に変更します。<br />
     * 表示日よりも後の作業記録が存在しない場合は何もしません。
     */
     public void next();
    
     /**
      * 表示日を、一日前の日付に変更します。<br />
      * 表示日よりも前の作業記録が存在しない場合は何もしません。
      */
    public void prev();
    
    /**
     * 新しい作業記録を追加します。
     * @param work 追加する作業記録
     */
    public void add(Work work);
    
    /**
     * データストアに記録されている作業記録情報を更新します。<br />
     * データストアに登録されていない作業記録番号が指定された場合は何もしません。
     * @param beforWorkNo 更新前の作業記録の作業記録番号
     * @param afterWork 更新後の作業記録
     */
    public void update(int beforWorkNo, Work afterWork);

    public void close();
}
