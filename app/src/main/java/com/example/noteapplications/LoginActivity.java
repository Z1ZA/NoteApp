package com.example.noteapplications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailTxt, passwordTxt;
    Button loginAcc;
    ProgressBar progressBar;
    TextView registBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        loginAcc = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        registBtn = findViewById(R.id.register_text);

        loginAcc.setOnClickListener(v -> logAcc());
        registBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));

    }
    void logAcc(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }
        loginAccountInFirebase(email, password);
    }
    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                //if success
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else {
                        utility.showToast(LoginActivity.this, "Email is not verify, please verify your emaiil!");
                    }
                }else {
                    //if unsuccess
                    utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }
    void changeInProgress(boolean inPrgoress){
        if(inPrgoress){
            progressBar.setVisibility(View.VISIBLE);
            loginAcc.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            loginAcc.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTxt.setError("Email is invalid!");
            return false;
        }
        if(password.length()<6){
            passwordTxt.setError("6 characters minimum!");
            return false;
        }
        return true;
}}