package com.example.jhu.seminar.Setting;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhu.seminar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUserProfile extends AppCompatActivity {
    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;

    private String email;
    private String ageS = "";
    private RadioGroup rg;
    private RadioButton radio1;
    private RadioButton radio2;
    private TextView heightTV;
    private TextView heightOP;
    private SeekBar seekBar;
    private Button submit;
    private Spinner spinner;
    final String[] age = {"0~9", "10~19", "20~29", "30~39", "40~49", "50~59", "60~69", "70~79", "80~89", "90~99", "100up"};
    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_update_user_profile);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("UpdateUserProfile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rg = (RadioGroup)findViewById(R.id.radiogroup);
        radio1 = (RadioButton)findViewById(R.id.radiobutton1);
        radio2 = (RadioButton)findViewById(R.id.radiobutton2);
        heightTV = (TextView)findViewById(R.id.heightTV);
        heightOP = (TextView) findViewById(R.id.heightOP);
        seekBar = (SeekBar) findViewById(R.id.heightET);
        seekBar.setMax((max-min)/step);
        seekBar.setProgress(50);
        submit = (Button) UpdateUserProfile.this.findViewById(R.id.submit);
        spinner = (Spinner)findViewById(R.id.age);

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

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail().toString();
        ArrayAdapter<String> ageList = new ArrayAdapter<>(UpdateUserProfile.this, android.R.layout.simple_spinner_dropdown_item, age);
        spinner.setAdapter(ageList);
        //new AsyncLogin_1().execute(email);
        select();

        spinner.setOnItemSelectedListener(spnOnItemSelected);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String G = "";
                switch(rg.getCheckedRadioButtonId()){
                    case R.id.radiobutton1:
                        G = "B";
                        break;
                    case R.id.radiobutton2:
                        G = "G";
                        break;
                }
                final String Gender = G;
                final String Age = ageS;
                final String Height = heightOP.getText().toString();
                if(heick == false)
                {
                    heightTV.setTextColor(Color.RED);
                    heightTV.setError("");
                    return;
                }
                else {
                    heightTV.setTextColor(Color.GRAY);
                    heightTV.setError(null);
                }

                //new AsyncLogin_2().execute(email,Gender,Age,Height);
                updateData(email,Gender,Age,Height);
            }
        });
    }

    private AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            ageS = Integer.toString(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {

        }
    };

    private void select() {
        String[] tableColumns = {"email", "gender", "age", "height"};
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        Cursor c = db.query(tb_name, tableColumns, whereClause, whereArgs, null, null, null);

        if(c.getCount() != 0) {
            c.moveToFirst();
            if(c.getString(1).equals("B"))
                radio1.setChecked(true);
            else if(c.getString(1).equals("G"))
                radio2.setChecked(true);
            spinner.setSelection(Integer.valueOf(c.getInt(2)));
            seekBar.setProgress(c.getInt(3)-100);
        }
    }

    private void updateData(String email, String gender, String age, String height) {
        ContentValues cvv = new ContentValues();
        cvv.put("gender", gender);
        cvv.put("age", age);
        cvv.put("height", height);
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        int s = db.update(tb_name, cvv, whereClause, whereArgs);
        if(s == 1) {
            Toast.makeText(UpdateUserProfile.this, "資料已成功修改！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(UpdateUserProfile.this, SettingMain.class);
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(this, "修改資料有誤！", Toast.LENGTH_LONG).show();
    }

    /*private class AsyncLogin_1 extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UpdateUserProfile.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                // localhost: 10.0.2.2
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/getprofile.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

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
            int get = Integer.valueOf(result.toString());
            if(get / 100000 == 1)
                radio1.setChecked(true);
            else if(get / 100000 == 2)
                radio2.setChecked(true);
            get = get % 100000;
            spinner.setSelection(get / 1000);
            get = get % 1000;
            HeightET.setText(Integer.toString(get));
        }
    }

    private class AsyncLogin_2 extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UpdateUserProfile.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                // localhost: 10.0.2.2
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/UDUP.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("gender", params[1])
                        .appendQueryParameter("age", params[2])
                        .appendQueryParameter("height", params[3]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

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
            if (result.equalsIgnoreCase("1"))
            {
                Toast.makeText(UpdateUserProfile.this, "資料已成功修改！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(UpdateUserProfile.this, SettingMain.class);
                startActivity(intent);
                finish();
            }
            else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(UpdateUserProfile.this, "修改資料有誤！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(UpdateUserProfile.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}

