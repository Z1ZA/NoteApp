package com.example.noteapplications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailTxt, passwordTxt, confirmPassTxt;
    Button createAcc;
    ProgressBar progressBar;
    TextView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        confirmPassTxt = findViewById(R.id.confirm_password);
        createAcc = findViewById(R.id.create_button);
        progressBar = findViewById(R.id.progress_bar);
        loginBtn = findViewById(R.id.login_text);

        createAcc.setOnClickListener(v -> createAccount());
        loginBtn.setOnClickListener(v -> finish());
    }
    void createAccount(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String confirm = confirmPassTxt.getText().toString();
        boolean isValidated = validateData(email, password, confirm);
        if (!isValidated) {
            return;
        }
        CreateAccountInFirebase(email,password);
    }
    void CreateAccountInFirebase(String email, String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
        new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                //if success
                if(task.isSuccessful()){
                    utility.showToast(CreateAccountActivity.this, "Succesfully create account. Check your email to verify! ");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else{
                    //if not
                    utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                }
            }
        }
        );
    }
    void changeInProgress(boolean inPrgoress){
        if(inPrgoress){
            progressBar.setVisibility(View.VISIBLE);
            createAcc.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAcc.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirm){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTxt.setError("Email is invalid!");
            return false;
        }
        if(password.length()<6){
            passwordTxt.setError("6 characters minimum!");
            return false;
        }
        if(!password.equals(confirm)){
            confirmPassTxt.setError("Your password doesn't match!");
            return false;
        }
        return true;
    }
}