package com.dadm.tuplan.dao;

import androidx.annotation.NonNull;

import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.User;
import com.dadm.tuplan.utils.AccountUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupDAO {

    private FirebaseDatabase mDatabase;
    private DatabaseReference groupsReference;
    private long lastID;

    public GroupDAO(){
        mDatabase = FirebaseDatabase.getInstance("https://tuplan-app-default-rtdb.firebaseio.com/");
        groupsReference = mDatabase.getReference("groups");
        lastID = getCountChildren();
    }

    public Task<Void> addGroup(String uid, Group group){
        return groupsReference.child(uid).setValue(group);
    }

    public DatabaseReference getGroupsReference() {
        return groupsReference;
    }
    public long getCountChildren(){
        groupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lastID = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return lastID;
    }
}
