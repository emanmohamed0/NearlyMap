package com.example.emyeraky.nearlymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText Email, Password;
    AppCompatButton btn;
    TextView v;
    static String temail, tpass;
    public static String emailuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Email = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);
        btn = (AppCompatButton) findViewById(R.id.btn_login);
        v = (TextView) findViewById(R.id.link_signup);
        btn.setOnClickListener(this);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.link_signup:
                Intent i = new Intent(Login.this, Signin.class);
                startActivity(i);
                break;
            default:
        }
    }
    private void login() {
        temail = Email.getText().toString();
        tpass = Password.getText().toString();
        final FirebaseAuth myAuth = FirebaseAuth.getInstance();
        myAuth.signInWithEmailAndPassword(temail, tpass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                    editor.putString("user_id", myAuth.getCurrentUser().getUid());
                    editor.commit();
                    Toast.makeText(Login.this, R.string.SuccefulLogIn, Toast.LENGTH_SHORT).show();
                    Intent ii = new Intent(Login.this, MapsActivity.class);
                    ii.putExtra("name", Email.getText().toString());
                    startActivity(ii);
                    finish();
                } else {
                    Toast.makeText(Login.this,R.string.unSuccefulLogin, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


