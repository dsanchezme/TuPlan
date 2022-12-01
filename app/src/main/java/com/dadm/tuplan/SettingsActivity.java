package com.dadm.tuplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.dadm.tuplan.dao.GroupDAO;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.dao.UserDAO;
import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity  {
    private Button buttonCerrarSesion;
    private TextView userName;
    private TextView userEmail;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.get("user");
        initNavBar();
        initViews();
    }
    private void initNavBar() {
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.Settings);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.MyTasks:
                        Intent toMyTasks = new Intent(SettingsActivity.this, MyTasksActivity.class);
                        toMyTasks.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toMyTasks);
                        return true;
                    case R.id.Home:
                        Intent toHome = new Intent(SettingsActivity.this, HomeTasksActivity.class);
                        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toHome);
                        return true;
                    case R.id.CreateTask:
                        Intent toSettings = new Intent(SettingsActivity.this, CreateTaskActivity.class);
                        toSettings.putExtra("user", currentUser);
                        toSettings.putExtra("update", false);
                        startActivity(toSettings);
                        return true;
                    case R.id.Settings:
                        return true;
                }
                return false;
            }
        });
    }
    private void initViews(){
        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userName.setText(currentUser.getName());
        userEmail   .setText(currentUser.getEmail());
        buttonCerrarSesion = (Button) findViewById(R.id.buttonCerrarSesion);
        buttonCerrarSesion.setOnClickListener(view -> {
            handleCerrarSesion();
        });
    }

    private void handleCerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        Intent toMain = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(toMain);
    }
}

