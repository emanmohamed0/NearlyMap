package com.example.emyeraky.nearlymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signin extends AppCompatActivity implements View.OnClickListener {
    private Button reg;
    private TextView tvLogin;
    private EditText etEmail, etPass, etName;
    FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        myAuth = FirebaseAuth.getInstance();
        reg = (Button) findViewById(R.id.btn_signup);
        tvLogin = (TextView) findViewById(R.id.link_login);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPass = (EditText) findViewById(R.id.input_password);
        etName = (EditText) findViewById(R.id.input_name);

        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                register();
                break;
            case R.id.link_login:
                startActivity(new Intent(Signin.this, Login.class));
                finish();
                break;
            default:

        }
    }

    private void register() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String name = etName.getText().toString();
        if (name.isEmpty() && email.isEmpty() && pass.isEmpty()) {
            displayToast(R.string.Message_when_EmptyUserName_And_Password+"");
        } else {
            registerFirebase(name, email, pass);
        }
    }

    private void registerFirebase(final String name, final String email, final String pass) {
        myAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Signin.this,R.string.Message_success_Login, Toast.LENGTH_SHORT).show();
                    addDataToFirebase(name, email);
                } else {
                    if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        Toast.makeText(Signin.this,R.string.Message_If_Signin_before, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(Signin.this, R.string.Message_If_failLoad_from_FireBase, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addDataToFirebase(String name, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("users");

        User user = new User(name, email);

        posts.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    displayToast(R.string.CorrectSignin+"");
                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                    editor.putString("user_id", myAuth.getCurrentUser().getUid()).apply();
                    editor.commit();
                    Intent i = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(Signin.this,R.string.Message_Fail_ToAdd_ToFireBase, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}