package com.example.jhu.seminar.WMSA;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.ArrayList;

/**
 * Created by JHU PC on 2017/8/2.
 */

public class WMSAHour extends Fragment {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private Button speed;
    private Button distance;
    private Button frequency;
    private LineChart lineChart;
    private LinearLayout btn;
    private ViewGroup btn_layout;
    private Boolean isRemove = false;

    private ArrayList<String> num = new ArrayList<String>();
    private ArrayList<String> prgmNameList_0 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_1 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_2 = new ArrayList<String>();
    private ArrayList<String> prgmNameList_3 = new ArrayList<String>();
    private ArrayList<String> labels;
    private ArrayList<Entry> entries;
    private String[] tmp;
    private ListView lv;
    private int len;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wmsa_hour, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!getUserVisibleHint())
            reload();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String start = "2017-08-04";
        String end = "2017-08-10";
        new AsyncLogin().execute(start, end);

        btn = (LinearLayout)getActivity().findViewById(R.id.btn);
        btn_layout = (ViewGroup) btn.getParent();
        speed = (Button)getActivity().findViewById(R.id.speed);
        distance = (Button)getActivity().findViewById(R.id.distance);
        frequency = (Button)getActivity().findViewById(R.id.frequency);
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

    private void reload(){
        if(isRemove)
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
            isRemove = false;
        }
    }

    public void setSpeeded()
    {
        if(btn_layout != null)
            btn_layout.removeView(btn);
        isRemove = true;

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+5)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+2]);
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
        isRemove = true;

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+5)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+3]);
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
        isRemove = true;

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (230 * scale + 0.5f);
        btn_layout.addView(lineChart, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

        labels = new ArrayList<String>();
        entries = new ArrayList<>();
        int i,j=0;
        for(i=0;i<len;i=i+5)
        {
            num.add(Integer.toString(j+1));
            labels.add(Integer.toString(j+1));
            Float f = Float.parseFloat(tmp[i+4]);
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
                url = new URL("http://intern.here-apps.com/JMLab/SemFinal/selhour.php");
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
                for (i=0;i<len;i=i+5)
                {
                    num.add(Integer.toString(j+1));
                    prgmNameList_0.add(tmp[i] + "~" + tmp[i+1]);
                    prgmNameList_1.add(tmp[i+2]);
                    prgmNameList_2.add(tmp[i+3]);
                    prgmNameList_3.add(tmp[i+4]);
                    j++;
                }

                lv = (ListView)getActivity().findViewById(R.id.List_3);
                lv.setAdapter(new WMSAAdapter((WalkModeStatsAnal)getActivity(),num,prgmNameList_0,prgmNameList_1,prgmNameList_2,prgmNameList_3));
            }
        }
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