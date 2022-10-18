package com.dadm.tuplan;

import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.*;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class HomeActivity extends AppCompatActivity
{

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

    private XYPlot plot;

    BarFormatter completadasFormatter;
    private XYSeries completadas = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Completadas",0,0,0);

    BarFormatter pendientesFormatter;
    private XYSeries pendientes  = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pendientes",0,0,0);

    private PieChart pie;

    SegmentFormatter sf1;
    private Segment s1;
    SegmentFormatter sf2;
    private Segment s2;
    private int numeroTareasCompletadas = 0;
    private int numeroTareasPendientes = 0;

    private TableLayout table;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        completadasFormatter = new BarFormatter(Color.parseColor("#17008b"), Color.BLACK);
        pendientesFormatter = new BarFormatter(Color.parseColor("#e47b30"), Color.BLACK);
        initPieChart();
        initBarChart();
        initTable();
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
                        Intent toCreateTask = new Intent(HomeActivity.this, CreateTaskActivity.class);
                        toCreateTask.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toCreateTask);
                        return true;
                    case R.id.Home:
                        return true;
                    /*case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;*/
                }
                return false;
            }
        });
    }
    private void initData(Bundle extras) {
        currentUser = (User) extras.get("user");
        planReference = planDAO.getPlansReference();
        planReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot   dataSnapshot) {
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
    }
    private void initTable() {
        table = (TableLayout) findViewById(R.id.tableLayout);
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
    private void initPieChart() {
        pie = (PieChart) findViewById(R.id.torta);

        final float padding = PixelUtils.dpToPix(30);
        pie.getPie().setPadding(padding, padding, padding, padding);

        s1 = new Segment("Completadas", numeroTareasCompletadas);
        s2 = new Segment("Pendientes", numeroTareasPendientes);

        sf1 = new SegmentFormatter(this, R.xml.pie_segment_formatter1);
        sf1.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);

        sf2 = new SegmentFormatter(this, R.xml.pie_segment_formatter2);
        sf2.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);

        pie.addSegment(s1, sf1);
        pie.addSegment(s2, sf2);

        pie.getBackgroundPaint().setColor(Color.TRANSPARENT);
        pie.getTitle().getLabelPaint().setColor(Color.BLACK);
    }
    private void updateCharts(){

        updateTable();
        updatePie();
        updateBars();
    }
    private void updateTable() {
        table.removeAllViews();
        TableRow.LayoutParams paramsHeader = new TableRow.LayoutParams();
        paramsHeader.weight = 1;

        TableRow rowHeader = new TableRow(this);
        TextView header1 = new TextView(this);
        header1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header1.setTypeface(header1.getTypeface(),Typeface.BOLD);
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

        for (Plan plan:
                planesPendientes) {
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
            table.addView(row);
        }

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
    private void updatePie() {
        pie.removeSegment(s1);
        pie.removeSegment(s2);
        numeroTareasCompletadas = planesCompletados.size();
        numeroTareasPendientes = planesPendientes.size();
        s1 = new Segment("Completadas", numeroTareasCompletadas);
        s2 = new Segment("Pendientes", numeroTareasPendientes);
        pie.addSegment(s1,sf1);
        pie.addSegment(s2,sf2);
        pie.redraw();
    }


}