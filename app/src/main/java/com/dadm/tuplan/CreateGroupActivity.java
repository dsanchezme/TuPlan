package com.dadm.tuplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.dao.GroupDAO;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupName;
    private EditText groupMembers;
    private Button buttonCreateGroup;
    private Snackbar mySnackbar;

    private GroupDAO groupDAO;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        groupDAO = new GroupDAO();
        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.get("user");
        initViews();
    }

    private void initViews(){
        groupName = findViewById(R.id.group_name);
        groupMembers = findViewById(R.id.group_members);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        buttonCreateGroup.setOnClickListener(view -> {
            handleClickCreation();
        });

    }
    private void handleClickCreation(){

        if (groupName.getText().toString().isEmpty() || groupMembers.getText().toString().isEmpty())
            return;

        List<String> members = new ArrayList<>(Arrays.asList(groupMembers.getText().toString().split(" ")));
        System.out.println(">>>>>> " + currentUser.getEmail());
        System.out.println(">>>>>> " + members);
        members.add(currentUser.getEmail());
        System.out.println(">>>>>> " + members);

        Group newGroup = new Group(groupName.getText().toString(), members);
        long id = groupDAO.getCountChildren();
        groupDAO.addGroup(String.valueOf(id+1),newGroup);
        mySnackbar = Snackbar.make(findViewById(R.id.LayoutCreateGroup), "Grupo creado exitosamente", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
        Intent toHome = new Intent(CreateGroupActivity.this, HomeTasksActivity.class);
        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
        startActivity(toHome);
    }

}
