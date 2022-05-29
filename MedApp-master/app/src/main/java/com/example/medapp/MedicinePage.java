package com.example.medapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MedicinePage extends AppCompatActivity {
    Button button;
    private final String TAG = "Firebase";
    HashMap<String, Object> dataMap;
    TextView name;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_page);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = MedicinePage.this.getResources().getDrawable(R.drawable.color);
        getWindow().setBackgroundDrawable(background);
        button = findViewById(R.id.medibuddy);
        name = findViewById(R.id.Name);
        dataMap = new HashMap<>();
        Button afterDinnerButton = findViewById(R.id.afterDinnerButton);
        Button afterBreakfastButton = findViewById(R.id.afterBreakfastButton);
        Button afterLunchButton = findViewById(R.id.afterLunchButton);
        Button beforeDinnerButton = findViewById(R.id.beforeDinnerButton);
        Button beforeBreakfastButton = findViewById(R.id.beforeBreakfastButton);
        Button beforeLunchButton = findViewById(R.id.beforeLunchButton);
        Button mondayButton = findViewById(R.id.mondayButton);
        Button tuesdayButton = findViewById(R.id.tuesdayButton);
        Button wednesdayButton = findViewById(R.id.wednesdayButton);
        Button thursdayButton = findViewById(R.id.thursdayButton);
        Button fridayButton = findViewById(R.id.fridayButton);
        Button saturdayButton = findViewById(R.id.saturdayButton);
        Button sundayButton = findViewById(R.id.sundayButton);
        TextView mo = findViewById(R.id.mo);
        TextView to = findViewById(R.id.to);
        ImageView imgv = findViewById(R.id.imageView7);
        HashMap<String, Object> hashMap = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        hashMap = (HashMap<String, Object>) extras.getSerializable("hashMap");
        name.setText(hashMap.get("Name").toString());
        Date endDate = ((Timestamp) hashMap.get("EndDate")).toDate();
        Date startDate = ((Timestamp) hashMap.get("StartDate")).toDate();
        startDate = new Date(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate());
        endDate = new Date(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate());
        List<String> timeDays = new ArrayList<>();
        List<String> timimgSchedule = new ArrayList<>();
        timeDays = (List<String>) hashMap.get("Time");
        timimgSchedule = (List<String>) hashMap.get("TimingSchedule");
        Integer icon = Integer.valueOf(hashMap.get("Icon").toString());
        imgv.setImageResource(icon);
        assert timeDays != null;
        for (String timeDay : timeDays) {
            switch (timeDay) {
                case "Monday":
                    mondayButton.setVisibility(View.VISIBLE);
                    break;
                case "Tuesday":
                    tuesdayButton.setVisibility(View.VISIBLE);
                    break;
                case "Wednesday":
                    wednesdayButton.setVisibility(View.VISIBLE);
                    break;
                case "Thursday":
                    thursdayButton.setVisibility(View.VISIBLE);
                    break;
                case "Friday":
                    fridayButton.setVisibility(View.VISIBLE);
                    break;
                case "Saturday":
                    saturdayButton.setVisibility(View.VISIBLE);
                    break;
                case "Sunday":
                    sundayButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        assert timimgSchedule != null;
        for (String timeDay : timimgSchedule) {
            switch (timeDay) {
                case "After Breakfast":
                    afterBreakfastButton.setVisibility(View.VISIBLE);
                    break;
                case "After Lunch":
                    afterLunchButton.setVisibility(View.VISIBLE);
                    break;
                case "After Dinner":
                    afterDinnerButton.setVisibility(View.VISIBLE);
                    break;
                case "Before Breakfast":
                    beforeBreakfastButton.setVisibility(View.VISIBLE);
                    break;
                case "Before Lunch":
                    beforeLunchButton.setVisibility(View.VISIBLE);
                    break;
                case "Before Dinner":
                    beforeDinnerButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        to.setText(endDate.getDate() + "/" + (endDate.getMonth() + 1) + "/" + endDate.getYear());
        mo.setText(startDate.getDate() + "/" + (startDate.getMonth() + 1) + "/" + startDate.getYear());

        HashMap<String, Object> finalHashMap = hashMap;
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditReminder.class);
            Bundle extras1 = new Bundle();
            extras1.putSerializable("hashMap", finalHashMap);
            extras1.putString("page", "Medicine");
            intent.putExtras(extras1);
            startActivity(intent);
        });
    }
}