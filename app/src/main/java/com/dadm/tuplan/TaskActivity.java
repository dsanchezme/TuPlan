package com.dadm.tuplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {


    private ArrayList<Plan> planes = new ArrayList<>();
    private ArrayList<Plan> planesCompletados = new ArrayList<>();
    private int planesCompletadosPrioridadBaja = 0;
    private int planesCompletadosPrioridadMedia = 0;
    private int planesCompletadosPrioridadAlta = 0;
    private ArrayList<Plan> planesPendientes = new ArrayList<>();
    private int planesPendientesPrioridadBaja = 0;
    private int planesPendientesPrioridadMedia = 0;
    private int planesPendientesPrioridadAlta = 0;
    private PlanDAO planDAO = new PlanDAO();
    User currentUser;
    DatabaseReference planReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
    }
}