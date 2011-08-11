/**
 * WorkListAdapter.java
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

import java.util.List;

import jp.dip.oyasirazu.timelogger.util.TimeUtility;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WorkListAdapter extends ArrayAdapter<Work> {
    private String mLogFormatString;

    public WorkListAdapter(Context context, int textViewResourceId, String logFormatString) {
        super(context, textViewResourceId);
        init(logFormatString);
    }
    
    public WorkListAdapter(Context context, int textViewResourceId, List<Work> list, String logFormatString) {
        super(context, textViewResourceId, list);
        init(logFormatString);
    }
    
    private void init(String logFormatString) {
        mLogFormatString = logFormatString;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  
        TextView view = (TextView)super.getView(position, convertView, parent);
        
        Work work = getItem(position);
        
        view.setText(
                String.format(
                        mLogFormatString,
                        work.getName(),
                        TimeUtility.formatSpentTime(work.getSpentTime())
          ));
        
        return view;
    }
}
