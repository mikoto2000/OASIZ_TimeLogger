package jp.dip.oyasirazu.timelogger;

import jp.dip.oyasirazu.timelogger.util.TimeUtility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WorkListAdapter extends ArrayAdapter<Work> {
    private String mLogFormatString;

    public WorkListAdapter(Context context, int textViewResourceId, String logFormatString) {
        super(context, textViewResourceId);
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
