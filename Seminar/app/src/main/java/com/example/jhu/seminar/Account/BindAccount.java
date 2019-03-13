package com.example.jhu.seminar.Account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jhu.seminar.MainActivity;
import com.example.jhu.seminar.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class BindAccount extends AppCompatActivity {

    //public static final int CONNECTION_TIMEOUT=10000;
    //public static final int READ_TIMEOUT=15000;

    static Activity activity;
    private Button emailLogin;
    private LoginButton loginButton;
    private boolean is_exit = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    CallbackManager callbackManager;

    private String email;
    private String gender;
    private String birthday;
    private String Age;
    private String FBEmail;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        setTitle("Login");
        activity = this;

        emailLogin = (Button) findViewById(R.id.Email);
        loginButton = (LoginButton) findViewById(R.id.Facebook);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile", "user_birthday");

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

        emailLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BindAccount.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                                email = object.getString("email");
                                String g = response.getJSONObject().getString("gender");
                                birthday = object.getString("birthday");
                                String[] strs= birthday.split("/");

                                switch (g){
                                    case "female":
                                        gender = "G";
                                        break;
                                    case "male":
                                        gender = "B";
                                        break;
                                }

                                int age = (2017 - Integer.parseInt(strs[2])) / 10;
                                switch (age){
                                    case 0:
                                        Age = "0";
                                        break;
                                    case 1:
                                        Age = "1";
                                        break;
                                    case 2:
                                        Age = "2";
                                        break;
                                    case 3:
                                        Age = "3";
                                        break;
                                    case 4:
                                        Age = "4";
                                        break;
                                    case 5:
                                        Age = "5";
                                        break;
                                    case 6:
                                        Age = "6";
                                        break;
                                    case 7:
                                        Age = "7";
                                        break;
                                    case 8:
                                        Age = "8";
                                        break;
                                    case 9:
                                        Age = "9";
                                        break;
                                    default:
                                        Age = "10";
                                        break;
                                }
                        } catch (JSONException e) {
                                    e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("FB","CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("FB",exception.toString());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    FBEmail = user.getEmail();
                    select();
                    //new BindCheck().execute(FBEmail,gender,Age);
                }
            }
        };
    }

    private void handleFacebookAccessToken(AccessToken accessToken)
    {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "" + task.getException(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private String user = "elaine850524@yahoo.com.tw";

    private void del() {
        String whereClause = "email = ?";
        String[] whereArgs = { user };
        db.delete(tb_name,whereClause,whereArgs);
    }

    private void select() {
        String[] tableColumns = {"email", "gender", "age", "height", "bindcheck"};
        String whereClause = "email = ?";
        String[] whereArgs = { FBEmail };
        Cursor c = db.query(tb_name, tableColumns, whereClause, whereArgs, null, null, null);

        if(c.getCount() != 0) {
            Intent intent = new Intent();
            intent.setClass(BindAccount.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent();
            intent.setClass(BindAccount.this, InputData.class);
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("gender", gender);
            bundle.putString("age", Age);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    /*private class BindCheck extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(BindAccount.this);
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
                url = new URL("http://intern.here-apps.com/JMLab/Seminar/bindcheck.inc.php");
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
                        .appendQueryParameter("age", params[2]);
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
            if (result.equalsIgnoreCase("first"))
            {
                Intent intent = new Intent();
                intent.setClass(BindAccount.this, InputData.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("gender", gender);
                bundle.putString("age", Age);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
            else if (result.equalsIgnoreCase("already"))
            {
                Intent intent = new Intent();
                intent.setClass(BindAccount.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if (result.equalsIgnoreCase("exception"))
            {
                Toast.makeText(BindAccount.this, "糟糕， 連接出了些問題！", Toast.LENGTH_LONG).show();
            }
        }
    }*/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean returnValue;
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0 && is_exit == false)
        {
            Toast.makeText(getBaseContext(), "再按一次返回鍵離開", Toast.LENGTH_SHORT).show();
            is_exit = true;
            new Thread(new Runnable() {
                public void run()
                {
                    try {
                        Thread.sleep(2000);
                        is_exit = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            returnValue = true;
        }
        else {
            returnValue = super.onKeyDown(keyCode, event);
        }

        return returnValue;
    }
}
