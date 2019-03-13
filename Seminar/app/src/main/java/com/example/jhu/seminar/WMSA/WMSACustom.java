package com.example.jhu.seminar.WMSA;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhu.seminar.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by JHU PC on 2017/8/2.
 */

public class WMSACustom extends Fragment {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private Button speed;
    private Button distance;
    private Button frequency;
    private LineChart lineChart;
    private LinearLayout btn;
    private ViewGroup btn_layout;

    private ArrayList<String> num = new ArrayList<String>();
    private ArrayList<String> prgmNameList_0 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_1 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_2 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_3 = new ArrayList<String>();
    //private ArrayList<String> prgmNameList_4 = new ArrayList<String>();
    private ArrayList<String> labels;
    private ArrayList<Entry> entries;
    private int Year_1, Month_1, Day_1;
    private int Year_2, Month_2, Day_2;
    private String[] tmp;
    private ListView lv;
    private int len;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wmsa_custom, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint())
            showDatePickerDialog();
        else
        {
            if(!num.isEmpty() || !prgmNameList_0.isEmpty() || !prgmNameList_1.isEmpty() || !prgmNameList_2.isEmpty() || !prgmNameList_3.isEmpty())
            {
                num.clear();
                prgmNameList_0.clear();
                prgmNameList_1.clear();
                prgmNameList_2.clear();
                prgmNameList_3.clear();
                //prgmNameList_4.clear();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    public void lazyLoadData() {
        btn = (LinearLayout)getActivity().findViewById(R.id.btnbtnbtn);
        btn_layout = (ViewGroup) btn.getParent();
        btn.setVisibility(View.VISIBLE);
        speed = (Button)getActivity().findViewById(R.id.speed_3);
        distance = (Button)getActivity().findViewById(R.id.distance_3);
        frequency = (Button)getActivity().findViewById(R.id.frequency_3);
        lineChart = new LineChart(getActivity());
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeeded();
            }
        });
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistance();
            }
        });
        frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrequency();
            }
        });
    }

    public void setSpeeded()
    {
        if(btn_layout != null)
            btn_layout.removeView(btn);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+4)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+1]);
            entries.add(new Entry(f,j));
            j++;
        }

        LineDataSet dataset = new LineDataSet(entries, "步速");
        dataset.setDrawFilled(true);

        LimitLine yLimitLine = new LimitLine(1.2f,"平均步速");
        yLimitLine.setLineColor(Color.RED);
        yLimitLine.setTextColor(Color.RED);
        yLimitLine.setTextSize(8f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(yLimitLine);

        LineData data = new LineData(labels, dataset);
        data.setValueTextSize(6f);
        lineChart.setData(data);
        lineChart.setDescription("按返回鍵回到選單");

        dataset.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void setDistance()
    {
        if(btn_layout != null)
            btn_layout.removeView(btn);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+4)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+2]);
            entries.add(new Entry(f,j));
            j++;
        }

        LineDataSet dataset = new LineDataSet(entries, "步距");
        dataset.setDrawFilled(true);

        LimitLine yLimitLine = new LimitLine(0.65f,"平均步距");
        yLimitLine.setLineColor(Color.RED);
        yLimitLine.setTextColor(Color.RED);
        yLimitLine.setTextSize(8f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(yLimitLine);

        LineData data = new LineData(labels, dataset);
        data.setValueTextSize(6f);
        lineChart.setData(data);
        lineChart.setDescription("按返回鍵回到選單");

        dataset.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void setFrequency()
    {
        if(btn_layout != null) //for safety only  as you are doing onClick
            btn_layout.removeView(btn);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+4)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+3]);
            entries.add(new Entry(f,j));
            j++;
        }

        LineDataSet dataset = new LineDataSet(entries, "步頻");
        dataset.setDrawFilled(true);

        LimitLine yLimitLine = new LimitLine(1.5f,"平均步頻");
        yLimitLine.setLineColor(Color.RED);
        yLimitLine.setTextColor(Color.RED);
        yLimitLine.setTextSize(1.5f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(yLimitLine);

        LineData data = new LineData(labels, dataset);
        data.setValueTextSize(6f);
        lineChart.setData(data);
        lineChart.setDescription("按返回鍵回到選單");

        dataset.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // localhost: 10.0.2.2
                url = new URL("http://intern.here-apps.com/JMLab/SemFinal/custom.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("start", params[0])
                        .appendQueryParameter("end", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return(result.toString());
                }
                else {
                    return("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(getActivity(), "新增註冊資料有誤！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(getActivity(), "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
            else
            {
                tmp = result.toString().split("/");
                len = tmp.length;

                int i,j=0;
                for (i=0;i<len;i=i+4)
                {
                    num.add(Integer.toString(j+1));
                    prgmNameList_0.add(tmp[i]);
                    prgmNameList_1.add(tmp[i+1]);
                    prgmNameList_2.add(tmp[i+2]);
                    prgmNameList_3.add(tmp[i+3]);
                    //prgmNameList_4.add(tmp[i+3]);
                    j++;
                }

                lv = (ListView)getActivity().findViewById(R.id.List_6);
                lv.setAdapter(new WMSAAdapter((WalkModeStatsAnal)getActivity(),num,prgmNameList_0,prgmNameList_1,prgmNameList_2,prgmNameList_3));
            }
        }
    }

    public void showDatePickerDialog() {
        final Calendar c_2 = Calendar.getInstance();
        Year_2 = c_2.get(Calendar.YEAR);
        Month_2 = c_2.get(Calendar.MONTH);
        Day_2 = c_2.get(Calendar.DAY_OF_MONTH);

        TextView tv_2 = new TextView(getActivity());
        tv_2.setGravity(Gravity.CENTER);
        tv_2.setPadding(50, 50, 50, 50);
        tv_2.setTextColor(Color.parseColor("#ffffff"));
        tv_2.setText("請選擇統計結束日期");

        DatePickerDialog dpd_2 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
                Year_2 = year;
                Month_2 = monthOfYear + 1;
                Day_2 = dayOfMonth;

                String m_1, m_2, d_1, d_2;
                m_1 = Month_1 > 9 ? Integer.toString(Month_1) : "0" + Integer.toString(Month_1);
                m_2 = Month_2 > 9 ? Integer.toString(Month_2) : "0" + Integer.toString(Month_2);
                d_1 = Day_1 > 9 ? Integer.toString(Day_1) : "0" + Integer.toString(Day_1);
                d_2 = Day_2 > 9 ? Integer.toString(Day_2) : "0" + Integer.toString(Day_2);
                String Fdate = Year_1 + "-" + m_1 + "-" + d_1 + " ~ " + Year_2 + "-" + m_2 + "-" + d_2;
                //Toast.makeText(getActivity(), Fdate, Toast.LENGTH_SHORT).show();

                String start = Year_1 + "-" + m_1 + "-" + d_1;
                String end = Year_2 + "-" + m_2 + "-" + d_2;
                Calendar mCal = Calendar.getInstance();
                String dateformat = "yyyy-MM-dd";
                SimpleDateFormat df = new SimpleDateFormat(dateformat);
                String today = df.format(mCal.getTime());
                if (java.sql.Date.valueOf(start).after(java.sql.Date.valueOf(end)))
                {
                    Toast.makeText(getActivity(), "起始日期必須小於結束日期", Toast.LENGTH_SHORT).show();
                    showDatePickerDialog();
                }
                else if (java.sql.Date.valueOf(start).after(java.sql.Date.valueOf(today)))
                {
                    Toast.makeText(getActivity(), "起始日期輸入錯誤", Toast.LENGTH_SHORT).show();
                    showDatePickerDialog();
                }
                else if (java.sql.Date.valueOf(end).after(java.sql.Date.valueOf(today)))
                {
                    Toast.makeText(getActivity(), "結束日期輸入錯誤", Toast.LENGTH_SHORT).show();
                    showDatePickerDialog();
                }
                else
                {
                    new AsyncLogin().execute(start, end);
                    lazyLoadData();
                }
            }

        }, Year_2, Month_2, Day_2);

        final Calendar c_1 = Calendar.getInstance();
        Year_1 = c_1.get(Calendar.YEAR);
        Month_1 = c_1.get(Calendar.MONTH);
        Day_1 = c_1.get(Calendar.DAY_OF_MONTH);

        TextView tv_1 = new TextView(getActivity());
        tv_1.setGravity(Gravity.CENTER);
        tv_1.setPadding(50, 50, 50, 50);
        tv_1.setTextColor(Color.parseColor("#ffffff"));
        tv_1.setText("請選擇統計開始日期");

        DatePickerDialog dpd_1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Year_1 = year;
                Month_1 = monthOfYear + 1;
                Day_1 = dayOfMonth;
            }
        }, Year_1, Month_1, Day_1);

        dpd_2.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });

        dpd_1.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });

        dpd_2.setCanceledOnTouchOutside(false); // disable click home button and other area
        dpd_2.setCancelable(false);  // disable click back button
        dpd_2.setCustomTitle(tv_2);
        dpd_1.setCanceledOnTouchOutside(false); // disable click home button and other area
        dpd_1.setCancelable(false);  // disable click back button
        dpd_1.setCustomTitle(tv_1);

        dpd_2.show();
        dpd_1.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getFocus();
    }

    private void getFocus() {
        getView().setFocusable(true);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    if(lineChart.isShown())
                    {
                        btn_layout.removeView(lineChart);
                        btn_layout.addView(btn, 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }
}