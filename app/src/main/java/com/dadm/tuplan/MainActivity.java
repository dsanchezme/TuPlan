package com.dadm.tuplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dadm.tuplan.dao.UserDAO;
import com.dadm.tuplan.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView userNameTextView;
    private String userName;
    private Button logoutButton;

    FirebaseUser currentUser;
    DatabaseReference userReference;

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userName);

        mAuth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        System.out.println("###################");
        System.out.println("###################");
        System.out.println("ID: "+currentUser.getUid());
        System.out.println("Phone number: "+currentUser.getPhoneNumber());
        System.out.println("Email: "+currentUser.getEmail());
        System.out.println("Name: "+currentUser.getDisplayName());
        System.out.println("Photo URI: "+currentUser.getPhotoUrl());
        System.out.println("###################");
        System.out.println("###################");

        userReference = userDAO.getUsersReference().child(currentUser.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent toHome = new Intent(MainActivity.this, MyTasksActivity.class);
                User user;
                try {
                    System.out.println(dataSnapshot.getValue());
                    user = new User((Map<String, Object>) dataSnapshot.getValue()) ;
                    userName = user != null ? user.getName() : currentUser.getDisplayName();

                }catch (Exception e){
                    userName = currentUser.getDisplayName();
                    user = new User(userName, currentUser.getEmail());
                }
                toHome.putExtra("user", user);
                userNameTextView.setText("¡Hola " + userName + "!");
                startActivity(toHome);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}