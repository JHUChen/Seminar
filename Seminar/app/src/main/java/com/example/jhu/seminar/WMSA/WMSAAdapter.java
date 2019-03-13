package com.example.jhu.seminar.WMSA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jhu.seminar.R;

import java.util.ArrayList;

import static com.example.jhu.seminar.R.id.num;

/**
 * Created by JHU PC on 2017/8/4.
 */

public class WMSAAdapter extends BaseAdapter {
    ArrayList<String> number;
    ArrayList<String> result_0;
    ArrayList<String> result_1;
    ArrayList<String> result_2;
    ArrayList<String> result_3;
    //ArrayList<String> result_4;
    Context context;

    private static LayoutInflater inflater = null;
    public WMSAAdapter(WalkModeStatsAnal mainActivity, ArrayList<String> num, ArrayList<String> prgmNameList_0, ArrayList<String> prgmNameList_1, ArrayList<String> prgmNameList_2, ArrayList<String> prgmNameList_3)
    {
        number = num;
        result_0 = prgmNameList_0;
        result_1 = prgmNameList_1;
        result_2 = prgmNameList_2;
        result_3 = prgmNameList_3;
        //result_4 = prgmNameList_4;
        context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() { return result_1.size(); }

    @Override
    public Object getItem(int position) { return position; }

    @Override
    public long getItemId(int position) { return position; }

    public class Holder
    {
        TextView no;
        TextView tv_0;
        TextView tv_1;
        TextView tv_2;
        TextView tv_3;
        TextView tv_4;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Holder holder = new Holder();
        View rowView;
        String t_1 = "平均步頻";
        String t_2 = "平均步距";
        String t_3 = "平均步速";
        //String t_4 = "步距:";
        rowView = inflater.inflate(R.layout.custom_walk_list_item, null);

        TextView tv = (TextView) rowView.findViewById(R.id.TV_1);
        TextView tvtv = (TextView) rowView.findViewById(R.id.TV_3);
        TextView tvtvtv = (TextView) rowView.findViewById(R.id.TV_5);
        //TextView tvtvtvtv = (TextView) rowView.findViewById(R.id.TV_7);
        tv.setText(t_1);
        tvtv.setText(t_2);
        tvtvtv.setText(t_3);
        //tvtvtvtv.setText(t_4);
        holder.no = (TextView) rowView.findViewById(num);
        holder.tv_0 = (TextView) rowView.findViewById(R.id.TV_0);
        holder.tv_1 = (TextView) rowView.findViewById(R.id.TV_2);
        holder.tv_2 = (TextView) rowView.findViewById(R.id.TV_4);
        holder.tv_3 = (TextView) rowView.findViewById(R.id.TV_6);
        //holder.tv_4 = (TextView) rowView.findViewById(R.id.TV_8);

        holder.no.setText(number.get(position));
        holder.tv_0.setText(result_0.get(position));
        holder.tv_1.setText(result_3.get(position) + " step/s");
        holder.tv_2.setText(result_2.get(position) + " m/step");
        holder.tv_3.setText(result_1.get(position) + " m/s");
        //holder.tv_4.setText(result_4.get(position));
        return rowView;
    }
}
