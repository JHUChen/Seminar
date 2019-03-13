package com.example.jhu.seminar.Setting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jhu.seminar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingMain extends AppCompatActivity {
    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;

    private ListView listView;
    private String[] list = {"修改密碼(僅限信箱註冊會員使用)","修改個人資料","鞋墊綁定設定"};
    private ArrayAdapter<String> listAdapter;
    private FirebaseAuth mAuth;
    private String useremail;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        useremail = user.getEmail().toString();
        listView = (ListView)findViewById(R.id.list_view);

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

        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                switch(position){
                    case 0:
                        //new AsyncLogin().execute(user.getEmail().toString());
                        select();
                        break;
                    case 1:
                        i.setClass(SettingMain.this, UpdateUserProfile.class);
                        startActivity(i);
                        break;
                    case 2:
                        i.setClass(SettingMain.this, InsoleBind.class);
                        startActivity(i);
                        break;
                }
            }
        });
    }

    private void select() {
        String[] tableColumns = {"bindcheck"};
        String whereClause = "email = ?";
        String[] whereArgs = { useremail };
        Cursor c = db.query(tb_name, tableColumns, whereClause, whereArgs, null, null, null);

        c.moveToFirst();
        if(c.getInt(0) > 0) {
            Intent i = new Intent();
            i.setClass(SettingMain.this, UpdatePassword.class);
            startActivity(i);
        }
        else {
            Toast.makeText(SettingMain.this, "非信箱註冊會員！", Toast.LENGTH_LONG).show();
        }
    }

    /*private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SettingMain.this);
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
                // localhost: 10.0.2.2
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/UDPWcheck.inc.php");

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
                Intent i = new Intent();
                i.setClass(SettingMain.this, UpdatePassword.class);
                startActivity(i);
            }
            else if (result.equalsIgnoreCase("false") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(SettingMain.this, "非信箱註冊會員！", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(SettingMain.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
