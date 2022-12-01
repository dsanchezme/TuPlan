package com.dadm.tuplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.dao.UserDAO;
import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView inputTarea;
    private Spinner inputPriorityFilter;
    private Spinner inputStatusFilter;
    private TextView inputFecha;

    private String[] arrayStatus = {"Pendiente", "Completada"};
    private String[] arrayPriority = {"Baja","Media","Alta"};
    private ArrayAdapter<String> aaStatus;
    private ArrayAdapter<String> aaPriority;
    private String prioritySelected;
    private String statusSelected;
    private TableLayout table;
    private int planesCompletadosPrioridadBaja = 0;
    private int planesCompletadosPrioridadMedia = 0;
    private int planesCompletadosPrioridadAlta = 0;
    private int planesPendientesPrioridadBaja = 0;
    private int planesPendientesPrioridadMedia = 0;
    private int planesPendientesPrioridadAlta = 0;

    private ArrayList<Plan> planes = new ArrayList<>();
    private ArrayList<Plan> planesCompletados = new ArrayList<>();
    private ArrayList<Plan> planesPendientes = new ArrayList<>();
    private PlanDAO planDAO = new PlanDAO();
    private UserDAO userDAO = new UserDAO();

    User currentUser;
    FirebaseUser firebaseUser;
    DatabaseReference userReference;
    DatabaseReference planReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(TaskActivity.this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_task);
        planDAO = new PlanDAO();
        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.get("user");
        initNavBar();
        initTable();
        initViews();
    }
    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initData(extras);
        }
        initNavBar();
    }

    private void initData(Bundle extras) {
        currentUser = (User) extras.get("user");

        userReference = userDAO.getUsersReference().child(firebaseUser.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                try {
                    user = new User((Map<String, Object>) dataSnapshot.getValue()) ;
                    currentUser.setName(user != null ? user.getName() : firebaseUser.getDisplayName());

                }catch (Exception e){
                    currentUser.setName(firebaseUser.getDisplayName());
                }
                System.out.println(">>> Changing name to "+ currentUser.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        planReference = planDAO.getPlansReference();
        planReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                planesCompletados.clear();
                planesPendientes.clear();
                planesCompletadosPrioridadBaja = 0;
                planesCompletadosPrioridadMedia = 0;
                planesCompletadosPrioridadAlta = 0;
                planesPendientesPrioridadAlta = 0;
                planesPendientesPrioridadMedia = 0;
                planesPendientesPrioridadBaja = 0;
                System.out.println("-----------------");
                System.out.println(currentUser.getEmail());
                System.out.println("-----------------");

                for (DataSnapshot planesDataSnapshot: dataSnapshot.getChildren()) {
                    if (planesDataSnapshot.getValue() == null)
                        continue;
                    Plan plan = new Plan((Map<String, Object>) planesDataSnapshot.getValue());
                    if (!plan.getOwner().getEmail().equals(currentUser.getEmail()))
                        continue;
                    System.out.println("#############");
                    System.out.println(plan);
                    System.out.println("#############");
                    if (plan.getStatus().equals("Completada")){
                        planesCompletados.add(plan);
                        if (plan.getPriority().equals("Baja")){
                            planesCompletadosPrioridadBaja += 1;
                        }else if(plan.getPriority().equals("Media")){
                            planesCompletadosPrioridadMedia += 1;
                        }else{
                            planesCompletadosPrioridadAlta += 1;
                        }
                    }else if(plan.getStatus().equals("Pendiente")){
                        planesPendientes.add(plan);
                        if (plan.getPriority().equals("Baja")){
                            planesPendientesPrioridadBaja += 1;
                        }else if (plan.getPriority().equals("Media")){
                            planesPendientesPrioridadMedia += 1;
                        }else{
                            planesPendientesPrioridadAlta += 1;
                        }
                    }

                }
                updateCharts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void updateCharts(){
        updateTable();
    }

    private void updateTable() {
        table.removeAllViews();
        TableRow.LayoutParams paramsHeader = new TableRow.LayoutParams();
        paramsHeader.weight = 1;

        TableRow rowHeader = new TableRow(this);
        TextView header1 = new TextView(this);
        header1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header1.setTypeface(header1.getTypeface(), Typeface.BOLD);
        header1.setBackground(getResources().getDrawable(R.drawable.border));
        header1.setLayoutParams(paramsHeader);
        header1.setText("Tarea");
        TextView header2 = new TextView(this);
        header2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header2.setTypeface(header2.getTypeface(),Typeface.BOLD);
        header2.setBackground(getResources().getDrawable(R.drawable.border));
        header2.setLayoutParams(paramsHeader);
        header2.setText("Responsable");
        TextView header3 = new TextView(this);
        header3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header3.setTypeface(header3.getTypeface(),Typeface.BOLD);
        header3.setBackground(getResources().getDrawable(R.drawable.border));
        header3.setLayoutParams(paramsHeader);
        header3.setText("Fecha Inicio");
        TextView header4 = new TextView(this);
        header4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header4.setTypeface(header4.getTypeface(),Typeface.BOLD);
        header4.setBackground(getResources().getDrawable(R.drawable.border));
        header4.setLayoutParams(paramsHeader);
        header4.setText("Prioridad");

        rowHeader.addView(header1);
        rowHeader.addView(header2);
        rowHeader.addView(header3);
        rowHeader.addView(header4);

        table.addView(rowHeader);

        for (Plan plan : planesPendientes) {
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.weight = 1;

            TableRow row = new TableRow(this);
            TextView column1 = new TextView(this);
            column1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column1.setBackground(getResources().getDrawable(R.drawable.border));
            column1.setLayoutParams(params);
            column1.setText(plan.getTitle());

            TextView column2 = new TextView(this);
            column2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column2.setBackground(getResources().getDrawable(R.drawable.border));
            column2.setLayoutParams(params);
            column2.setText(plan.getOwner().getName());

            TextView column3 = new TextView(this);
            column3.setText(plan.getStartDate());
            column3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column3.setBackground(getResources().getDrawable(R.drawable.border));
            column3.setLayoutParams(params);
            column3.setText(plan.getStartDate());

            TextView column4 = new TextView(this);
            column4.setText(plan.getStatus());
            column4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column4.setBackground(getResources().getDrawable(R.drawable.border));
            column4.setLayoutParams(params);
            column4.setText(plan.getPriority());

            row.addView(column1);
            row.addView(column2);
            row.addView(column3);
            row.addView(column4);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked: "+ column1.getText());
                }
            });

            table.addView(row);
        }

    }

    private void initTable() {
        table = (TableLayout) findViewById(R.id.tasksTable);
    }

    private void initNavBar() {
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.CreateTask);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.MyTasks:
                        Intent toMyTasks = new Intent(TaskActivity.this, MyTasksActivity.class);
                        toMyTasks.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toMyTasks);
                        return true;
                    case R.id.Home:
                        Intent toHome = new Intent(TaskActivity.this, HomeTasksActivity.class);
                        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toHome);
                        return true;
                    case R.id.CreateTask:
                        return true;
                    case R.id.Settings:
                        Intent toSettings = new Intent(TaskActivity.this, SettingsActivity.class);
                        toSettings.putExtra("user", currentUser);
                        startActivity(toSettings);
                        return true;
                }
                return false;
            }
        });
    }
    private void initViews(){
        aaStatus = new ArrayAdapter<>(this,R.layout.layout_list_item,arrayStatus);
        aaPriority = new ArrayAdapter<>(this,R.layout.layout_list_item,arrayPriority);

        inputStatusFilter =(Spinner) findViewById(R.id.statusTareasFilter);
        inputPriorityFilter =(Spinner) findViewById(R.id.prioridadTareaFilter);

        inputStatusFilter.setAdapter(aaStatus);
        inputPriorityFilter.setAdapter(aaPriority);


        inputStatusFilter.setOnItemSelectedListener(this);
        inputPriorityFilter.setOnItemSelectedListener(this);

        inputTarea =(TextView) findViewById(R.id.nombreTarea);
        inputFecha =(TextView) findViewById(R.id.fechaTarea);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}