package com.example.unser_hoersaal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// source: https://firebase.google.com/docs/auth/android

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, registration;
    private EditText email, password;
    private String emailString, passwordString;
    private ProgressDialog progressDialog;
    private FirebaseAuth authentificator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        progressDialog = new ProgressDialog(this);
        authentificator = FirebaseAuth.getInstance();
        isUserLogged();

        email = (EditText) findViewById(R.id.loginMail);
        password = (EditText) findViewById(R.id.loginPassword);

        login = (Button) findViewById(R.id.login);
        registration = (Button) findViewById(R.id.registration);

        setOnClick();
    }

    private void setOnClick(){
        login.setOnClickListener(this);
        registration.setOnClickListener(this);
    }


    //if user is already logged he should start at the menu screen

    private void isUserLogged(){
        if(authentificator.getCurrentUser() != null){

            // here next step

        }
    }

    //login or create an account

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                login();
                break;

            case R.id.registration:
                Intent i = new Intent(MainActivity.this, RegistrationScreenActivity.class);
                startActivity(i);
                break;
        }
    }

    private void login(){
        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();

        //Note if an input field is empty

        if(TextUtils.isEmpty(emailString)){
            Toast.makeText(this, "Bitte geben Sie Ihre Email ein", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordString)){
            Toast.makeText(this, "Bitte geben Sie Ihr Passwort ein", Toast.LENGTH_SHORT).show();
            return;
        }

        singIn();
    }

    //login process

    private void singIn(){
        progressDialog.setMessage("einloggen..");
        progressDialog.show();

        authentificator.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            finish();
                          //  next step
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Einloggen fehlgeschlagen", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}