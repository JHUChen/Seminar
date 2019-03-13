package com.example.jhu.seminar.Account;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhu.seminar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class Registration extends AppCompatActivity {

    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;
    private FirebaseAuth mAuth;
    private TextInputLayout accountLayout;
    private TextInputLayout passwordLayout;
    private SeekBar seekBar;
    private Button submit;
    private LinearLayout lack;

    private TextView GenderTV;
    private TextView AgeTV;
    private TextView heightTV;
    private EditText AccountET;
    private EditText PwdET;
    private RadioGroup rg;
    private String ageS = "";
    private TextView heightOP;

    private Spinner spinner;
    final String[] age = {"", "0~9", "10~19", "20~29", "30~39", "40~49", "50~59", "60~69", "70~79", "80~89", "90~99", "100up"};

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
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lack = (LinearLayout)findViewById(R.id.lack);
        final TextView tvl_0 = new TextView(Registration.this);
        final TextView tvl_1 = new TextView(Registration.this);
        final TextView tvl_2 = new TextView(Registration.this);
        final TextView tvl_3 = new TextView(Registration.this);
        final TextView tvl_4 = new TextView(Registration.this);
        final TextView tvl_5 = new TextView(Registration.this);
        lack.addView(tvl_0);
        lack.addView(tvl_1);
        lack.addView(tvl_2);
        lack.addView(tvl_3);
        lack.addView(tvl_4);
        lack.addView(tvl_5);

        accountLayout = (TextInputLayout) findViewById(R.id.name_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        passwordLayout.setErrorEnabled(true);
        accountLayout.setErrorEnabled(true);
        passwordLayout.setError("");
        accountLayout.setError("");

        spinner = (Spinner) findViewById(R.id.age);
        ArrayAdapter<String> ageList = new ArrayAdapter<>(Registration.this, android.R.layout.simple_spinner_dropdown_item, age);
        spinner.setAdapter(ageList);
        spinner.setOnItemSelectedListener(spnOnItemSelected);

        seekBar = (SeekBar) findViewById(R.id.heightET);
        heightOP = (TextView) findViewById(R.id.heightOP);
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

        mAuth = FirebaseAuth.getInstance();
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                GenderTV = (TextView)findViewById(R.id.GenderTV);
                AgeTV = (TextView)findViewById(R.id.AgeTV);
                heightTV = (TextView)findViewById(R.id.heightTV);
                AccountET = (EditText) findViewById(R.id.AccountET);
                PwdET = (EditText) findViewById(R.id.PwdET);
                rg = (RadioGroup)findViewById(R.id.radiogroup1);

                tvl_0.setText("");
                tvl_1.setText("");
                tvl_2.setText("");
                tvl_3.setText("");
                tvl_4.setText("");
                tvl_5.setText("");

                final String Email = AccountET.getText().toString();
                final String Password = PwdET.getText().toString();
                String G = "";
                final String Height = heightOP.getText().toString();

                switch(rg.getCheckedRadioButtonId()) {
                    case R.id.radiobutton1:
                        G = "B";
                        break;
                    case R.id.radiobutton2:
                        G = "G";
                        break;
                }
                final String Gender = G;
                final String Age = ageS;

                if(AccountET.getText().length()== 0 || PwdET.getText().length()== 0 || G == "" || ageS == "0" || heick == false)
                    tvl_0.setText("尚未輸入：");
                else if(!(AccountET.getText().length()== 0 || PwdET.getText().length()== 0 || G == "" || ageS == "0" || heick == false))
                    tvl_0.setText("");

                if(TextUtils.isEmpty(Email)) {
                    accountLayout.setError(getString(R.string.plz_input_accout));
                    tvl_1.setText("帳號 ");
                    if(TextUtils.isEmpty(Password)) {
                        passwordLayout.setError(getString(R.string.plz_input_pw));
                        tvl_2.setText("密碼 ");
                    }
                    else {
                        passwordLayout.setError("");
                        tvl_2.setText("");
                    }
                    if(G == "") {
                        GenderTV.setTextColor(Color.RED);
                        GenderTV.setError("");
                        tvl_3.setText("性別 ");
                    }
                    else {
                        GenderTV.setTextColor(Color.GRAY);
                        GenderTV.setError(null);
                        tvl_3.setText("");
                    }
                    if(ageS == "0") {
                        AgeTV.setTextColor(Color.RED);
                        AgeTV.setError("");
                        tvl_4.setText("年齡 ");
                    }
                    else {
                        AgeTV.setTextColor(Color.GRAY);
                        AgeTV.setError(null);
                        tvl_4.setText("");
                    }
                    if(heick == false) {
                        heightTV.setTextColor(Color.RED);
                        heightTV.setError("");
                        tvl_5.setText("身高 ");
                    }
                    else {
                        heightTV.setTextColor(Color.GRAY);
                        heightTV.setError(null);
                        tvl_5.setText("");
                    }
                    return;
                }
                else {
                    accountLayout.setError("");
                    tvl_1.setText("");
                }
                if(TextUtils.isEmpty(Password)) {
                    passwordLayout.setError(getString(R.string.plz_input_pw));
                    tvl_2.setText("密碼 ");
                    if(TextUtils.isEmpty(Email)) {
                        accountLayout.setError(getString(R.string.plz_input_accout));
                        tvl_1.setText("帳號 ");
                    }
                    else {
                        accountLayout.setError("");
                        tvl_1.setText("");
                    }
                    if(G == "") {
                        GenderTV.setTextColor(Color.RED);
                        GenderTV.setError("");
                        tvl_3.setText("性別 ");
                    }
                    else {
                        GenderTV.setTextColor(Color.GRAY);
                        GenderTV.setError(null);
                        tvl_3.setText("");
                    }
                    if(ageS == "0") {
                        AgeTV.setTextColor(Color.RED);
                        AgeTV.setError("");
                        tvl_4.setText("年齡 ");
                    }
                    else {
                        AgeTV.setTextColor(Color.GRAY);
                        AgeTV.setError(null);
                        tvl_4.setText("");
                    }
                    if(heick == false) {
                        heightTV.setTextColor(Color.RED);
                        heightTV.setError("");
                        tvl_5.setText("身高 ");
                    }
                    else {
                        heightTV.setTextColor(Color.GRAY);
                        heightTV.setError(null);
                        tvl_5.setText("");
                    }
                    return;
                }
                else {
                    passwordLayout.setError("");
                    tvl_2.setText("");
                }
                if(G == "") {
                    GenderTV.setTextColor(Color.RED);
                    GenderTV.setError("");
                    tvl_3.setText("性別 ");
                    if(TextUtils.isEmpty(Email)) {
                        accountLayout.setError(getString(R.string.plz_input_accout));
                        tvl_1.setText("帳號 ");
                    }
                    else {
                        accountLayout.setError("");
                        tvl_1.setText("");
                    }
                    if(TextUtils.isEmpty(Password)) {
                        passwordLayout.setError(getString(R.string.plz_input_pw));
                        tvl_2.setText("密碼 ");
                    }
                    else {
                        passwordLayout.setError("");
                        tvl_2.setText("");
                    }
                    if(heick == false) {
                        heightTV.setTextColor(Color.RED);
                        heightTV.setError("");
                        tvl_5.setText("身高 ");
                    }
                    else {
                        heightTV.setTextColor(Color.GRAY);
                        heightTV.setError(null);
                        tvl_5.setText("");
                    }
                    return;
                }
                else {
                    GenderTV.setTextColor(Color.GRAY);
                    GenderTV.setError(null);
                    tvl_3.setText("");
                }
                if(ageS == "0") {
                    AgeTV.setTextColor(Color.RED);
                    AgeTV.setError("");
                    tvl_4.setText("年齡 ");
                    if(TextUtils.isEmpty(Email)) {
                        accountLayout.setError(getString(R.string.plz_input_accout));
                        tvl_1.setText("帳號 ");
                    }
                    else {
                        accountLayout.setError("");
                        tvl_1.setText("");
                    }
                    if(TextUtils.isEmpty(Password)) {
                        passwordLayout.setError(getString(R.string.plz_input_pw));
                        tvl_2.setText("密碼 ");
                    }
                    else {
                        passwordLayout.setError("");
                        tvl_2.setText("");
                    }
                    if(heick == false) {
                        heightTV.setTextColor(Color.RED);
                        heightTV.setError("");
                        tvl_5.setText("身高 ");
                    }
                    else {
                        heightTV.setTextColor(Color.GRAY);
                        heightTV.setError(null);
                        tvl_5.setText("");
                    }
                    return;
                }
                else {
                    AgeTV.setTextColor(Color.GRAY);
                    AgeTV.setError(null);
                    tvl_4.setText("");
                }
                if(heick == false) {
                    heightTV.setTextColor(Color.RED);
                    heightTV.setError("");
                    tvl_5.setText("身高 ");
                    if(TextUtils.isEmpty(Email)) {
                        accountLayout.setError(getString(R.string.plz_input_accout));
                        tvl_1.setText("帳號 ");
                    }
                    else {
                        accountLayout.setError("");
                        tvl_1.setText("");
                    }
                    if(TextUtils.isEmpty(Password)) {
                        passwordLayout.setError(getString(R.string.plz_input_pw));
                        tvl_2.setText("密碼 ");
                    }
                    else {
                        passwordLayout.setError("");
                        tvl_2.setText("");
                    }
                    if(G == "") {
                        GenderTV.setTextColor(Color.RED);
                        GenderTV.setError("");
                        tvl_3.setText("性別 ");
                    }
                    else {
                        GenderTV.setTextColor(Color.GRAY);
                        GenderTV.setError(null);
                        tvl_3.setText("");
                    }
                    if(ageS == "0") {
                        AgeTV.setTextColor(Color.RED);
                        AgeTV.setError("");
                        tvl_4.setText("年齡 ");
                    }
                    else {
                        AgeTV.setTextColor(Color.GRAY);
                        AgeTV.setError(null);
                        tvl_4.setText("");
                    }
                    return;
                }
                else {
                    heightTV.setTextColor(Color.GRAY);
                    heightTV.setError(null);
                    tvl_5.setText("");
                }

                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                            //new AsyncLogin().execute(Email, Gender, Age, Height);
                            addData(Email, Gender, Age, Height);

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {}
                                }
                            });
                            FirebaseAuth.getInstance().signOut();

                            Intent intent = new Intent();
                            intent.setClass(Registration.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
        public void onNothingSelected(AdapterView<?> arg0) {}
    };

    private void addData(String email, String gender, String age, String height) {
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd", mCal.getTime());

        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("gender", gender);
        cv.put("age", Integer.valueOf(age));
        cv.put("height", height);
        cv.put("bindcheck", true);
        cv.put("regdate", s.toString());

        db.insert(tb_name, null, cv);
    }

    /*private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Registration.this);
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
                url = new URL("http://10.0.2.2/insert.inc.php");
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
                Toast.makeText(Registration.this, "新增註冊資料有誤！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(Registration.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}