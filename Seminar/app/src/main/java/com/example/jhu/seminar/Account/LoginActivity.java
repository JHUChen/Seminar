package com.example.jhu.seminar.Account;

import android.content.Intent;
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

import com.example.jhu.seminar.MainActivity;
import com.example.jhu.seminar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout accountLayout;
    private TextInputLayout passwordLayout;
    private EditText email;
    private EditText password;
    private Button login;
    private Button fp;
    private Button reg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("EmailLogin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.AccountET);
        password = (EditText) findViewById(R.id.PwdET);
        accountLayout = (TextInputLayout) findViewById(R.id.account_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        login = (Button) findViewById(R.id.Login);
        fp = (Button) findViewById(R.id.FP);
        reg = (Button) findViewById(R.id.Reg);
        accountLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        accountLayout.setError("");
        passwordLayout.setError("");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String a = email.getText().toString();
                String p = password.getText().toString();
                if (TextUtils.isEmpty(a)) {
                    accountLayout.setError(getString(R.string.plz_input_accout));
                    if(TextUtils.isEmpty(p))
                        passwordLayout.setError(getString(R.string.plz_input_pw));
                    else
                        passwordLayout.setError("");
                    return;
                }
                else {
                    accountLayout.setError("");
                }
                if (TextUtils.isEmpty(p)) {
                    passwordLayout.setError(getString(R.string.plz_input_pw));
                    if(TextUtils.isEmpty(a))
                        accountLayout.setError(getString(R.string.plz_input_pw));
                    else
                        accountLayout.setError("");
                    return;
                }
                else
                {
                    passwordLayout.setError("");
                }

                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(a, p).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            boolean emailVerified = user.isEmailVerified();
                            if(emailVerified == true)
                            {
                                if(!BindAccount.activity.isFinishing())
                                    BindAccount.activity.finish();
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "此帳號尚未被驗證！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        fp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        reg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, Registration.class);
                startActivity(intent);
            }
        });
    }
}
