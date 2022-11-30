package com.dadm.tuplan.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dadm.tuplan.R;
import com.dadm.tuplan.models.Plan;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedTasksListAdapter extends BaseAdapter {

    private int layout;
    private Context context;
    private List<String> groups;
    private Map<String, List<Plan>> tasks;

    public SharedTasksListAdapter(Activity context, int layout, List<String> groups, Map<String, List<Plan>> tasks){
        this.context = context;
        this.layout = layout;
        this.groups = groups;
        this.tasks = tasks;
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
        System.out.println(selectedGroup);
        TextView groupName = v.findViewById(R.id.group);
        TableLayout tasksTable = v.findViewById(R.id.tableLayout);
        System.out.println(selectedTasks);
        groupName.setText(selectedGroup);

        for (Plan plan : selectedTasks) {
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.weight = 1;

            TableRow row = new TableRow(context.getApplicationContext());
            TextView column1 = new TextView(context.getApplicationContext());
            column1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            column1.setBackground(getResources().getDrawable(R.drawable.border));
            column1.setLayoutParams(params);
            column1.setText(plan.getTitle());

            TextView column2 = new TextView(context.getApplicationContext());
            column2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            column2.setBackground(getResources().getDrawable(R.drawable.border));
            column2.setLayoutParams(params);
            column2.setText(plan.getOwner().getName());

            TextView column3 = new TextView(context.getApplicationContext());
            column3.setText(plan.getStartDate());
            column3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            column3.setBackground(getResources().getDrawable(R.drawable.border));
            column3.setLayoutParams(params);
            column3.setText(plan.getStartDate());

            TextView column4 = new TextView(context.getApplicationContext());
            column4.setText(plan.getStatus());
            column4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            column4.setBackground(getResources().getDrawable(R.drawable.border));
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

            tasksTable.addView(row);
        }

        return v;
    }
}
