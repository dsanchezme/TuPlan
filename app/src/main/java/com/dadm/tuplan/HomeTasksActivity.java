package com.dadm.tuplan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.dadm.tuplan.dao.GroupDAO;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.helpers.SharedTasksListAdapter;
import com.dadm.tuplan.models.Group;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeTasksActivity extends AppCompatActivity
{

    private Map<String, List<Plan>> tasksByGroup = new HashMap<>();
    private List<Plan> planesCompletados = new ArrayList<>();
    private List<String> myGroups = new ArrayList<>();
    private int planesCompletadosPrioridadBaja = 0;
    private int planesCompletadosPrioridadMedia = 0;
    private int planesCompletadosPrioridadAlta = 0;
    private List<Plan> planesPendientes = new ArrayList<>();
    private int planesPendientesPrioridadBaja = 0;
    private int planesPendientesPrioridadMedia = 0;
    private int planesPendientesPrioridadAlta = 0;
    private PlanDAO planDAO = new PlanDAO();
    private GroupDAO groupDAO = new GroupDAO();
    User currentUser;
    DatabaseReference planReference;
    DatabaseReference groupReference;
    private ImageView newGroupButton;
    private ListView tasksView;


    private XYPlot plot;

    BarFormatter completadasFormatter;
    private XYSeries completadas = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Completadas",0,0,0);

    BarFormatter pendientesFormatter;
    private XYSeries pendientes  = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pendientes",0,0,0);

    private int numeroTareasCompletadas = 0;
    private int numeroTareasPendientes = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_tasks_activity);
        completadasFormatter = new BarFormatter(Color.parseColor("#17008b"), Color.BLACK);
        pendientesFormatter = new BarFormatter(Color.parseColor("#e47b30"), Color.BLACK);
        initBarChart();
        initNavBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            initData(extras);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initData(extras);
            updateCharts();
        }

    }

    private void initNavBar() {
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.Home);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.CreateTask:
                        Intent toCreateTask = new Intent(HomeTasksActivity.this, CreateTaskActivity.class);
                        toCreateTask.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toCreateTask);
                        return true;
                    case R.id.MyTasks:
                        Intent toMyTasks = new Intent(HomeTasksActivity.this, MyTasksActivity.class);
                        toMyTasks.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toMyTasks);
                        return true;
                    case R.id.Home:
                        return true;

                    case R.id.Settings:
                    Intent toSettings = new Intent(HomeTasksActivity.this, SettingsActivity.class);
                    toSettings.putExtra("user", currentUser);
                    startActivity(toSettings);
                    return true;
                }
                return false;
            }
        });
    }
    private void initData(Bundle extras) {
        currentUser = (User) extras.get("user");

        tasksView = findViewById(R.id.groups_list);

        newGroupButton = findViewById(R.id.addGroup);
        newGroupButton.setOnClickListener(view -> {
            Intent toCreateGroup = new Intent(HomeTasksActivity.this, CreateGroupActivity.class);
            toCreateGroup.putExtra("user",(User) getIntent().getExtras().get("user"));
            startActivity(toCreateGroup);
        });

        groupReference = groupDAO.getGroupsReference();
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myGroups.clear();
                for (DataSnapshot groupsDataSnapshot: snapshot.getChildren()) {
                    if (groupsDataSnapshot.getValue() == null)
                        continue;
                    Group group = new Group((Map<String, Object>) groupsDataSnapshot.getValue());
                    if (group.getMembers().contains(currentUser.getEmail())){
                        myGroups.add(group.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                for (DataSnapshot planesDataSnapshot: dataSnapshot.getChildren()) {
                    if (planesDataSnapshot.getValue() != null){
                        Plan plan = new Plan((Map<String, Object>) planesDataSnapshot.getValue());
                        System.out.println("MY GROUPS: "+ myGroups);
                        if (!myGroups.contains(plan.getSharedWith()))
                            continue;
                        System.out.println("MY Plan: "+ plan);
                        if (!tasksByGroup.keySet().contains(plan.getSharedWith())){
                            tasksByGroup.put(plan.getSharedWith(), new ArrayList<>(Arrays.asList(plan)));
                        }else{
                            System.out.println(tasksByGroup.get(plan.getSharedWith()));
                            List<Plan> auxGroupPlans = new ArrayList<>(Arrays.asList(plan));


                            if (!listContainsPlan(tasksByGroup.get(plan.getSharedWith()),plan)) {
                                auxGroupPlans.addAll(tasksByGroup.get(plan.getSharedWith()));
                                tasksByGroup.put(plan.getSharedWith(), auxGroupPlans);
                            }
                        }


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
                }
                updateCharts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        SharedTasksListAdapter tasksAdapter = new SharedTasksListAdapter(this,R.layout.layout_group_tasks,myGroups, tasksByGroup);
        tasksView.setAdapter(tasksAdapter);
    }

    private void initBarChart() {
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.barras);

        //This gets rid of the gray grid
        plot.getGraph().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getLegend().setVisible(true);
        plot.getLegend().getTextPaint().setColor(Color.BLACK);
        plot.getLegend().getTextPaint().setTextSize(10.0F);
        plot.getTitle().getLabelPaint().setColor(Color.BLACK);
        plot.getDomainTitle().setVisible(true);
        plot.getDomainTitle().getLabelPaint().setColor(Color.BLACK);

        // draw wins bars with a green fill:
        plot.addSeries(completadasFormatter,completadas);

        // draw losses bars with a red fill:
        plot.addSeries(pendientesFormatter,pendientes );

    }

    private void updateCharts(){

        updateBars();
    }
       private void updateBars() {
        /*
        *System.out.println("planesCompletadosPrioridadBaja" + String.valueOf(planesCompletadosPrioridadBaja));
        System.out.println("planesCompletadosPrioridadMedia" + String.valueOf(planesCompletadosPrioridadMedia));
        System.out.println("planesCompletadosPrioridadAlta" + String.valueOf(planesCompletadosPrioridadAlta));

        System.out.println("planesPendientesPrioridadBaja" + String.valueOf(planesPendientesPrioridadBaja));
        System.out.println("planesPendientesPrioridadMedia" + String.valueOf(planesPendientesPrioridadMedia));
        System.out.println("planesPendientesPrioridadAlta" + String.valueOf(planesPendientesPrioridadAlta));
        * */
        plot.removeSeries(completadas);
        plot.removeSeries(pendientes);

        BarRenderer renderer = plot.getRenderer(BarRenderer.class);
        renderer.setBarOrientation((BarRenderer.BarOrientation.STACKED));
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(25));
        completadas = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Completadas",planesCompletadosPrioridadBaja,planesCompletadosPrioridadMedia,planesCompletadosPrioridadAlta);
        pendientes =  new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pendientes",planesPendientesPrioridadBaja,planesPendientesPrioridadMedia,planesPendientesPrioridadAlta);
        plot.addSeries(completadas,completadasFormatter);
        plot.addSeries(pendientes,pendientesFormatter);
        plot.redraw();
    }

    private boolean listContainsPlan(List<Plan> list, Plan plan){
        for (Plan item : list){
            if (item.getTitle().equals(plan.getTitle())){
                return true;
            }
        }
        return false;
    }

}