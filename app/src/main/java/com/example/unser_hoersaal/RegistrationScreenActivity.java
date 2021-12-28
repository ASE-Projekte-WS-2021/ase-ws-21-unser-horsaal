package com.example.unser_hoersaal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//registration
//following template was used and extended: "Simplified Coding" https://www.youtube.com/watch?v=0NFwF7L-YA8 [DATE: 15.08.2019]

public class RegistrationScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registration, back;
    private EditText email, password, passwordRepeat, usernameEditText;
    private TextView mailTitle, passwordTitle, usernameTitle;
    private String emailString, passwordString, passwordRepeatString, usernameString, userId;

    private ProgressDialog progressDialog;
    private FirebaseAuth authoficator;
    private DatabaseReference username;

    private Username userName;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        initialisation();
    }

    private void initialisation(){
        authoficator = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        email = findViewById(R.id.accountEmail);
        password = findViewById(R.id.accountPassword);
        passwordRepeat = findViewById(R.id.accountPassword2);
        usernameEditText = findViewById(R.id.accountUserName);
        mailTitle = findViewById(R.id.reg_mail_title);
        passwordTitle = findViewById(R.id.reg_password_title);
        usernameTitle = findViewById(R.id.reg_username_title);
        registration = findViewById(R.id.registrationButton);
        back = findViewById(R.id.backToLoginButton);

        setOnClick();
    }

    private void setOnClick(){
        registration.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    //start registration or get back to the login screen

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.registrationButton) {
            userRegistration();
        }
        if(view.getId() == R.id.backToLoginButton){
            finish();
        }
    }

    private void userRegistration(){
        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();
        passwordRepeatString = passwordRepeat.getText().toString().trim();
        usernameString = usernameEditText.getText().toString().trim();

        //Note if an input field is empty or the passwords do not match or is shorter than 6 chars

        if(TextUtils.isEmpty(emailString)){
            Toast.makeText(this, "Bitte geben Sie eine Email an", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordString)&& TextUtils.isEmpty(passwordRepeatString)){
            Toast.makeText(this, "Bitte geben Sie ein Passwort ein", Toast.LENGTH_SHORT).show();
            return;
        }
        if((passwordString.length() < 6) || (passwordRepeatString.length() < 6)){
            Toast.makeText(this, "Passwort muss mindestens 6 Zeichen beeinhalten", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(usernameString)){
            Toast.makeText(this, "Bitte geben Sie einen Nutzernamen ein", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!passwordRepeatString.equals(passwordString)){
            Toast.makeText(this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();
    }

    //create an user account with mail and password

    private void createAccount(){
        progressDialog.setMessage("Registrierung läuft..");
        progressDialog.show();

        authoficator.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            registration.setEnabled(false);
                            Toast.makeText(RegistrationScreenActivity.this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                            createUserName();
                        }
                        else {
                            Toast.makeText(RegistrationScreenActivity.this, "Registrierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    //create an username connected with the account

    private void createUserName(){
        username = FirebaseDatabase.getInstance().getReference("userName");
        userId = authoficator.getUid();

        userName = new Username(usernameString, userId);
        username.child(userId).setValue(userName);
    }
}
