package com.example.jhu.seminar.Account;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jhu.seminar.R;

import java.util.Calendar;

public class InputData extends AppCompatActivity {

    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;

    private String email;
    private String gender;
    private String age;
    private TextView heightTV;
    private TextView heightOP;
    private SeekBar seekBar;
    private Button submit;

    private boolean heick = false;
    private int step = 1;
    private int max = 200;
    private int min = 100;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        Bundle info = this.getIntent().getExtras();
        email = info.getString("email");
        gender = info.getString("gender");
        age = info.getString("age");

        heightTV = (TextView)findViewById(R.id.heightTV);
        heightOP = (TextView) findViewById(R.id.heightOP);
        seekBar = (SeekBar) findViewById(R.id.heightET);
        seekBar.setMax((max-min)/step);
        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                heick = true;
                int v = min + (progress * step);
                heightOP.setText(Integer.toString(v));
            }
        });

        db = openOrCreateDatabase(db_name,  Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name + " (" +
                "email VARCHAR(64) NOT NULL, " +
                "gender VARCHAR(1) NOT NULL, " +
                "age decimal(3,0) NOT NULL, " +
                "height float NOT NULL, " +
                "bindcheck tinyint(1) NOT NULL, " +
                "regdate date NOT NULL, " +
                "left_MAC VARCHAR(20), " +
                "right_MAC VARCHAR(20), " +
                "PRIMARY KEY (email))";
        db.execSQL(createTable);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String h = heightOP.getText().toString();
                if(heick == false) {
                    heightTV.setTextColor(Color.RED);
                    heightTV.setError("");
                }
                else {
                    heightTV.setTextColor(Color.GRAY);
                    heightTV.setError(null);
                    //new InsetProfile().execute(email, gender, age, h);
                    addData(email, gender, age, h);

                    Intent intent = new Intent();
                    intent.setClass(InputData.this, BindAccount.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void addData(String email, String gender, String age, String height) {
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd", mCal.getTime());

        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("gender", gender);
        cv.put("age", Integer.valueOf(age));
        cv.put("height", height);
        cv.put("bindcheck", false);
        cv.put("regdate", s.toString());

        db.insert(tb_name, null, cv);
    }

    /*private class InsetProfile extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(InputData.this);
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
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/inputdata.inc.php");
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
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("gender", params[1])
                        .appendQueryParameter("age", params[2])
                        .appendQueryParameter("height", params[3]);
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
                if (response_code == HttpURLConnection.HTTP_OK)
                {
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
                Toast.makeText(InputData.this, "新增註冊資料有誤！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(InputData.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
