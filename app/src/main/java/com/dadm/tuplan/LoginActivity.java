package com.dadm.tuplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.utils.AccountUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView goToRegister;

    private ProgressDialog progressDialog;

    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private ImageView googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goToRegister = findViewById(R.id.noAccountQuestion);
        goToRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        progressDialog = new ProgressDialog(this);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            handleLogin();
        });

        googleButton = findViewById(R.id.googleButton);
        googleButton.setOnClickListener(view -> {
            System.out.println("SignIn with Google");
            startActivity(new Intent(LoginActivity.this, GoogleSignInActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        goToHomePage(currentUser);

    }

    protected void goToHomePage(FirebaseUser currentUser){
        if (currentUser != null){
            System.out.println("###################");
            System.out.println("Going to home page");
            System.out.println("###################");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private void handleLogin(){
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!AccountUtil.isValidEmail(email)){
            inputEmail.setError(getString(R.string.emailError));
            return;
        }
        if (password.trim().length() < 6){
            inputPassword.setError(getString(R.string.passwordError));
            return;
        }

        progressDialog.setMessage("Validando usuario...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }else{
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

}