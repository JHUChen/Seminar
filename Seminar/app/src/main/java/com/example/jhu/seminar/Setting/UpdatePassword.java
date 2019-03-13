package com.example.jhu.seminar.Setting;

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

import com.example.jhu.seminar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    private EditText oldPW;
    private EditText newPW;
    private EditText newPWCheck;
    private TextInputLayout oldLayout;
    private TextInputLayout newLayout;
    private TextInputLayout newCheckLayout;
    private Button submit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("UpdatePassword");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        oldPW = (EditText) UpdatePassword.this.findViewById(R.id.OldPasswordET);
        newPW = (EditText) UpdatePassword.this.findViewById(R.id.NewPasswordET);
        newPWCheck = (EditText) UpdatePassword.this.findViewById(R.id.NewPasswordCheckET);
        oldLayout = (TextInputLayout) findViewById(R.id.OldPassword_layout);
        newLayout = (TextInputLayout) findViewById(R.id.NewPassword_layout);
        newCheckLayout = (TextInputLayout) findViewById(R.id.NewPasswordCheck_layout);
        submit = (Button) UpdatePassword.this.findViewById(R.id.submit);
        oldLayout.setErrorEnabled(true);
        newLayout.setErrorEnabled(true);
        newCheckLayout.setErrorEnabled(true);
        oldLayout.setError("");
        newLayout.setError("");
        newCheckLayout.setError("");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldS = oldPW.getText().toString();
                final String newS = newPW.getText().toString();
                final String newCS = newPWCheck.getText().toString();

                if (TextUtils.isEmpty(oldS)) {
                    oldLayout.setError("請輸入舊密碼");
                    if(TextUtils.isEmpty(newS))
                    {
                        newLayout.setError("請輸入新密碼");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                    }
                    else
                    {
                        newLayout.setError("");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                    }
                    return;
                }
                else
                {
                    oldLayout.setError("");
                    if(TextUtils.isEmpty(newS))
                    {
                        newLayout.setError("請輸入新密碼");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                        return;
                    }
                    else
                    {
                        newLayout.setError("");
                        if(TextUtils.isEmpty(newCS))
                        {
                            newCheckLayout.setError("請再次輸入新密碼");
                            return;
                        }
                        else
                            newCheckLayout.setError("");
                    }
                }
                if (TextUtils.isEmpty(newS)) {
                    newLayout.setError("請輸入新密碼");
                    if(TextUtils.isEmpty(oldS))
                    {
                        oldLayout.setError("請輸入舊密碼");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                    }
                    else
                    {
                        oldLayout.setError("");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                    }
                    return;
                }
                else
                {
                    newLayout.setError("");
                    if(TextUtils.isEmpty(oldS))
                    {
                        oldLayout.setError("請輸入舊密碼");
                        if(TextUtils.isEmpty(newCS))
                            newCheckLayout.setError("請再次輸入新密碼");
                        else
                            newCheckLayout.setError("");
                        return;
                    }
                    else
                    {
                        oldLayout.setError("");
                        if(TextUtils.isEmpty(newCS))
                        {
                            newCheckLayout.setError("請再次輸入新密碼");
                            return;
                        }
                        else
                            newCheckLayout.setError("");
                    }
                }
                if (TextUtils.isEmpty(newCS)) {
                    newCheckLayout.setError("請再次輸入新密碼");
                    if(TextUtils.isEmpty(newS))
                    {
                        newLayout.setError("請輸入新密碼");
                        if(TextUtils.isEmpty(oldS))
                            oldLayout.setError("請輸入舊密碼");
                        else
                            oldLayout.setError("");
                    }
                    else
                    {
                        newLayout.setError("");
                        if(TextUtils.isEmpty(oldS))
                            oldLayout.setError("請輸入舊密碼");
                        else
                            oldLayout.setError("");
                    }
                    return;
                }
                else
                {
                    newCheckLayout.setError("");
                    if(TextUtils.isEmpty(newS))
                    {
                        newLayout.setError("請輸入新密碼");
                        if(TextUtils.isEmpty(oldS))
                            oldLayout.setError("請輸入舊密碼");
                        else
                            oldLayout.setError("");
                        return;
                    }
                    else
                    {
                        newLayout.setError("");
                        if(TextUtils.isEmpty(oldS))
                        {
                            oldLayout.setError("請輸入舊密碼");
                            return;
                        }
                        else
                            oldLayout.setError("");
                    }
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldS);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    if(oldS.equals(newS))
                                    {
                                        Toast.makeText(UpdatePassword.this, "舊密碼與新密碼一致！", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        if(newS.equals(newCS))
                                        {
                                            user.updatePassword(newS)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(UpdatePassword.this, "密碼已修改完成！", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent();
                                                                intent.setClass(UpdatePassword.this, SettingMain.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else{
                                                                Toast.makeText(UpdatePassword.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                            Toast.makeText(UpdatePassword.this, "新密碼輸入不一致！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                    Toast.makeText(UpdatePassword.this, "使用者身分驗證失敗！", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
