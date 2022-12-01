package com.dadm.tuplan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.dao.GroupDAO;
import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameInput;
    private EditText groupMembersInput;
    private Button buttonCreateGroup;
    private Snackbar mySnackbar;
    private boolean isUpdating;

    private GroupDAO groupDAO;
    private User currentUser;

    private String groupID;
    private String groupName;
    private String groupMembers;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        groupDAO = new GroupDAO();
        Bundle extras = getIntent().getExtras();

        isUpdating = (boolean) extras.get("update");

        if (isUpdating){
            groupID = (String) extras.get("groupID");
            groupName = (String) extras.get("groupName");
            groupMembers = (String) extras.get("groupMembers");

            mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();

            if (firebaseUser.getDisplayName().isEmpty()){
                currentUser = new User("", firebaseUser.getEmail());
            }else{
                currentUser = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail());
            }

        }else{
            currentUser = (User) extras.get("user");
        }

        initViews();
    }

    private void initViews(){
        groupNameInput = findViewById(R.id.group_name);
        groupMembersInput = findViewById(R.id.group_members);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        buttonCreateGroup.setOnClickListener(view -> {
            handleClickCreation();
        });

        if (isUpdating){
            groupNameInput.setText(groupName);
            groupMembersInput.setText(groupMembers);
            buttonCreateGroup.setText("ACTUALIZAR GRUPO");
        }else{
            buttonCreateGroup.setText("CREAR GRUPO");
        }


    }
    private void handleClickCreation(){

        if (groupNameInput.getText().toString().isEmpty() || groupMembersInput.getText().toString().isEmpty())
            return;

        List<String> members = new ArrayList<>(Arrays.asList(groupMembersInput.getText().toString().split(" ")));

        if (!isUpdating) {
            members.add(currentUser.getEmail());
        }

        Group newGroup = new Group(groupNameInput.getText().toString(), members);

        if (isUpdating){
            groupDAO.addGroup(groupID, newGroup);
            mySnackbar = Snackbar.make(findViewById(R.id.LayoutCreateGroup), "Grupo actualizado exitosamente", Snackbar.LENGTH_SHORT);
        } else {
            long id = groupDAO.getCountChildren();
            groupDAO.addGroup(String.valueOf(id + 1), newGroup);
            mySnackbar = Snackbar.make(findViewById(R.id.LayoutCreateGroup), "Grupo creado exitosamente", Snackbar.LENGTH_SHORT);
        }
        mySnackbar.show();

        Intent toHome = new Intent(CreateGroupActivity.this, HomeTasksActivity.class);
        if (isUpdating){
            toHome.putExtra("user",currentUser);
        }else{
            toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
        }

        startActivity(toHome);
    }

}
