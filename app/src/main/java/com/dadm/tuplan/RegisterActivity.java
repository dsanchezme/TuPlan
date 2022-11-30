package com.dadm.tuplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.dao.UserDAO;
import com.dadm.tuplan.models.User;
import com.dadm.tuplan.utils.AccountUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView goToLogin;

    private EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    private Button registerButton;

    private ProgressDialog progressDialog;

    private UserDAO userDAO = new UserDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        goToLogin = findViewById(R.id.alreadyAccountQuestion);
        goToLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        registerButton = findViewById(R.id.loginButton);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(view -> {
            handleRegistration();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity( new Intent(RegisterActivity.this, MainActivity.class));
        }
    }

    private void handleRegistration(){
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (name.isEmpty()){
            inputName.setError(getString(R.string.nameError));
            return;
        }
        if (!AccountUtil.isValidEmail(email)){
            inputEmail.setError(getString(R.string.emailError));
            return;
        }
        if (password.length() < 6){
            inputPassword.setError(getString(R.string.passwordError));
            return;
        }

        if (!password.equals(confirmPassword)){
            inputConfirmPassword.setError(getString(R.string.inputConfirmPassword));
            return;
        }

        progressDialog.setMessage("Registrando usuario...");
        progressDialog.setTitle("Register");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                User user = new User(name, email);
                userDAO.addUser(mAuth.getCurrentUser().getUid(), user);
                progressDialog.dismiss();
                startActivity( new Intent(RegisterActivity.this, LoginActivity.class));
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}