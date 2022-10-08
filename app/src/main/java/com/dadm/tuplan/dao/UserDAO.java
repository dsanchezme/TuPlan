package com.dadm.tuplan.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dadm.tuplan.models.User;
import com.dadm.tuplan.utils.AccountUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDAO {

    private FirebaseDatabase mDatabase;
    private DatabaseReference usersReference;

    public UserDAO(){
        mDatabase = FirebaseDatabase.getInstance("https://tuplan-app-default-rtdb.firebaseio.com/");
        usersReference = mDatabase.getReference("users");
    }

    public Task<Void> addUser(String uid, User user){
        return usersReference.child(uid).setValue(user);
    }

    public Task<Void> removeUser(String userEmail){
        return usersReference.child(AccountUtil.encodeUserEmail(userEmail)).removeValue();
    }

    public DatabaseReference getUsersReference() {
        return usersReference;
    }
}
