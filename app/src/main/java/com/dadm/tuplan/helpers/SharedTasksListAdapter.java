package com.dadm.tuplan.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dadm.tuplan.CreateTaskActivity;
import com.dadm.tuplan.MyTasksActivity;
import com.dadm.tuplan.R;
import com.dadm.tuplan.models.Plan;
import com.dadm.tuplan.models.User;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedTasksListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<String> groups;
    private Map<String, List<Plan>> tasks;
    private Map<String, List<String>> tasksID;

    public SharedTasksListAdapter(Activity context, int layout, List<String> groups, Map<String, List<Plan>> tasks, Map<String, List<String>> tasksID){
        this.context = context;
        this.layout = layout;
        this.groups = groups;
        this.tasks = tasks;
        this.tasksID = tasksID;
    }

    @Override
    public int getCount() {
        return this.tasks.size();
    }
    @Override
    public Object getItem(int position) {
        return this.tasks.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v;
        if (view==null){
            LayoutInflater inflater = LayoutInflater.from(this.context);
            v = inflater.inflate(R.layout.layout_group_tasks,null);
        }else{
            v=view;
        }

        String selectedGroup = groups.get(position);
        List<Plan> selectedTasks = tasks.get(selectedGroup);
        List<String> selectedTasksID = tasksID.get(selectedGroup);

        TextView groupName = v.findViewById(R.id.group);
        TableLayout tasksTable = v.findViewById(R.id.tableLayout);

        groupName.setText(selectedGroup);
        int index = 0;
        for (Plan plan : selectedTasks) {
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.weight = 1;
            params.height = 80;

            TableRow row = new TableRow(context.getApplicationContext());
            TextView column1 = new TextView(context.getApplicationContext());
            column1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column1.setBackground(context.getResources().getDrawable(R.drawable.border));
            column1.setLayoutParams(params);
            column1.setText(plan.getTitle());

            TextView column2 = new TextView(context.getApplicationContext());
            column2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column2.setBackground(context.getResources().getDrawable(R.drawable.border));
            column2.setLayoutParams(params);
            column2.setText(plan.getOwner().getName());

            TextView column3 = new TextView(context.getApplicationContext());
            column3.setText(plan.getStartDate());
            column3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column3.setBackground(context.getResources().getDrawable(R.drawable.border));
            column3.setLayoutParams(params);
            column3.setText(plan.getStartDate());

            TextView column4 = new TextView(context.getApplicationContext());
            column4.setText(plan.getStatus());
            column4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            column4.setBackground(context.getResources().getDrawable(R.drawable.border));
            column4.setLayoutParams(params);
            column4.setText(plan.getPriority());

            row.addView(column1);
            row.addView(column2);
            row.addView(column3);
            row.addView(column4);

            int finalIndex = index;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked: "+ column1.getText());
                    Intent toUpdateTask = new Intent(context.getApplicationContext(), CreateTaskActivity.class);
                    toUpdateTask.putExtra("update", true);
                    toUpdateTask.putExtra("taskID", selectedTasksID.get(finalIndex));
                    toUpdateTask.putExtra("user", plan.getOwner());
                    toUpdateTask.putExtra("title", plan.getTitle());
                    toUpdateTask.putExtra("description", plan.getDescription());
                    toUpdateTask.putExtra("status", plan.getStatus());
                    toUpdateTask.putExtra("startDate", plan.getStartDate());
                    toUpdateTask.putExtra("priority", plan.getPriority());
                    toUpdateTask.putExtra("sharedWith", plan.getSharedWith());
                    context.startActivity(toUpdateTask);
                }
            });

            tasksTable.addView(row);
            index++;
        }

        return v;
    }
}
