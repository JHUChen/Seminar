package com.example.jhu.seminar.Account;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jhu.seminar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;

    private TextInputLayout emailLayout;
    private EditText emailET;
    private String EmailET;
    private Button send;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;

    /*public class SerialNumGenerator {
        int strLen = 6;            // default length:6
        String outStr = "";        // 產生的密碼

        public String generator(int strLen) {
            int num = 0;
            while (this.outStr.length() < strLen) {
                num = (int) (Math.random() * (90 - 50 + 1)) + 50;    //亂數取編號為 50~90 的字符	(排除 0 和 1)
                if (num > 57 && num < 65)
                    continue;            //排除非數字非字母
                else if (num == 73 || num == 76 || num == 79)
                    continue;            //排除 I、L、O
                this.outStr += (char) num;
            }
            return this.outStr.toLowerCase();
        }
    }*/

    //final SerialNumGenerator serNum = new SerialNumGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("ForgetPassword");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailET = (EditText) findViewById(R.id.emailET);
        emailLayout = (TextInputLayout) findViewById(R.id.email_layout);
        emailLayout.setErrorEnabled(true);
        emailLayout.setError("");

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

        send = (Button) this.findViewById(R.id.submit);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //serNum.outStr = "";
                //serNum.outStr = serNum.generator(serNum.strLen);

                EmailET = emailET.getText().toString();
                if(TextUtils.isEmpty(EmailET))
                    emailLayout.setError("請輸入電子信箱");
                else {
                    emailLayout.setError("");
                    select();
                    //new FPcheck().execute(EmailET);
                }
            }
        });
    }

    private void select() {
        String[] tableColumns = {"bindcheck"};
        String whereClause = "email = ?";
        String[] whereArgs = { EmailET };
        Cursor c = db.query(tb_name, tableColumns, whereClause, whereArgs, null, null, null);

        if(c.getCount() != 0) {
            c.moveToFirst();
            if(c.getInt(0) > 0) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(EmailET)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this, R.string.reset_success, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(ForgetPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
            else {
                Toast.makeText(ForgetPassword.this, "非信箱註冊會員！", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(ForgetPassword.this, "非足GO健康之會員！", Toast.LENGTH_LONG).show();
        }
    }

    /*private class FPcheck extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ForgetPassword.this);
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
                // localhost: 10.0.0.2
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/FPcheck.inc.php");

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
            if (result.equalsIgnoreCase("true"))
            {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(EmailET)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this, R.string.reset_success, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(ForgetPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
            else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(ForgetPassword.this, "非信箱註冊會員！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("notuser") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(ForgetPassword.this, "非足GO健康之會員！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(ForgetPassword.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
