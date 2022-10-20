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
import com.androidplot.pie.PieChart;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView inputTitle;
    private TextView inputDescription;
    private DatePicker inputStartDate;
    private Spinner inputPriority;
    private Spinner inputStatus;
    private Button buttonCreatePlan;
    private Snackbar mySnackbar;
    private PlanDAO planDAO;
    private User currentUser;

    private String[] arrayStatus = {"Completada","Pendiente"};
    private String[] arrayPriority = {"Baja","Media","Alta"};

    private ArrayAdapter<String> aaStatus;
    private ArrayAdapter<String> aaPriority;
    private String prioritySelected;
    private String statusSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        planDAO = new PlanDAO();
        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.get("user");
        initNavBar();
        initViews();


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
                    case R.id.Home:
                        Intent toHome = new Intent(CreateTaskActivity.this, HomeActivity.class);
                        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
                        startActivity(toHome);
                        return true;
                    case R.id.CreateTask:
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
    private void initViews(){
        aaStatus = new ArrayAdapter<>(this,R.layout.layout_list_item,arrayStatus);
        aaPriority = new ArrayAdapter<>(this,R.layout.layout_list_item,arrayPriority);
        inputStatus =(Spinner) findViewById(R.id.status_input);
        inputPriority =(Spinner) findViewById(R.id.priority_input);

        inputStatus.setAdapter(aaStatus);
        inputPriority.setAdapter(aaPriority);

        inputStatus.setOnItemSelectedListener(this);
        inputPriority.setOnItemSelectedListener(this);


        inputTitle =(TextView) findViewById(R.id.title_input);
        inputDescription =(TextView) findViewById(R.id.description_input);
        inputStartDate =(DatePicker) findViewById(R.id.start_date_input);
        buttonCreatePlan = (Button) findViewById(R.id.buttonCreatePlan);
        buttonCreatePlan.setOnClickListener(view -> {
            handleClickCreation();
        });
    }
    private void handleClickCreation(){
        int year = inputStartDate.getYear();
        int month = inputStartDate.getMonth();
        int day = inputStartDate.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String startDate = format.format(calendar.getTime());


        String title =  inputTitle.getText().toString();
        String desc = inputDescription.getText().toString();
        String status = statusSelected;
        String priority = prioritySelected;
        Plan newPlan = new Plan(title,desc,status,startDate,currentUser,priority);
        long id = planDAO.getCountChildren();
        planDAO.addPlan(String.valueOf(id+1),newPlan);
        mySnackbar = Snackbar.make(findViewById(R.id.LayoutCreatePlan), "Creación plan exitosa", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
        Intent toHome = new Intent(CreateTaskActivity.this, HomeActivity.class);
        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
        startActivity(toHome);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.status_input)
        {
           statusSelected = spinner.getSelectedItem().toString();
        }
        else if(spinner.getId() == R.id.priority_input)
        {
            prioritySelected = spinner.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
