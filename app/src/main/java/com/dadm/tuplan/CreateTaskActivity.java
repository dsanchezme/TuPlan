package com.dadm.tuplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.androidplot.pie.PieChart;
import com.dadm.tuplan.dao.PlanDAO;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class CreateTaskActivity extends AppCompatActivity {
    private TextView inputTitle;
    private TextView inputDescription;
    private TextView inputStartDate;
    private TextView inputPriority;
    private TextView inputStatus;
    private Button buttonCreatePlan;
    private Snackbar mySnackbar;
    private PlanDAO planDAO;
    private User currentUser;

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
        inputTitle =(TextView) findViewById(R.id.title_input);
        inputDescription =(TextView) findViewById(R.id.description_input);
        inputStatus =(TextView) findViewById(R.id.status_input);
        inputStartDate =(TextView) findViewById(R.id.start_date_input);
        inputPriority =(TextView) findViewById(R.id.priority_input);
        buttonCreatePlan = (Button) findViewById(R.id.buttonCreatePlan);
        buttonCreatePlan.setOnClickListener(view -> {
            handleClickCreation();
        });
    }
    private void handleClickCreation(){

        String title =  inputTitle.getText().toString();
        String desc = inputDescription.getText().toString();
        String status = inputStatus.getText().toString();
        String startDate =  inputStartDate.getText().toString();
        String priority = inputPriority.getText().toString();
        Plan newPlan = new Plan(title,desc,status,startDate,currentUser,priority);
        long id = planDAO.getCountChildren();
        planDAO.addPlan(String.valueOf(id+1),newPlan);
        mySnackbar = Snackbar.make(findViewById(R.id.LayoutCreatePlan), "Creación plan exitosa", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
        Intent toHome = new Intent(CreateTaskActivity.this, HomeActivity.class);
        toHome.putExtra("user",(User) getIntent().getExtras().get("user"));
        startActivity(toHome);
    }
}
