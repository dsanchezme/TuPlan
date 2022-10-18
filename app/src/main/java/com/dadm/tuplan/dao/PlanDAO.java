package com.dadm.tuplan.dao;

import androidx.annotation.NonNull;

import com.dadm.tuplan.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dadm.tuplan.models.Plan;
import com.google.firebase.database.ValueEventListener;

public class PlanDAO {
    private FirebaseDatabase mDatabase;
    private DatabaseReference plansReference;
    private long lastID;

    public PlanDAO(){
        mDatabase = FirebaseDatabase.getInstance("https://tuplan-app-default-rtdb.firebaseio.com/");
        plansReference = mDatabase.getReference("plans");
        lastID = getCountChildren();
    }

    public Task<Void> addPlan(String id, Plan plan){
        return plansReference.child(id).setValue(plan);
    }

    public DatabaseReference getPlansReference() {
        return plansReference;
    }
    public long getCountChildren(){
        plansReference.addValueEventListener(new ValueEventListener() {
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
