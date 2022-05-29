package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medapp.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class EditReminder extends AppCompatActivity {
    Button afterDinnerButton, afterBreakfastButton, afterLunchButton, beforeDinnerButton, beforeBreakfastButton, beforeLunchButton;
    Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    boolean afterDinnerSelected = false;
    boolean afterLunchSelected = false;
    boolean afterBreakfastSelected = false;
    boolean beforeDinnerSelected = false;
    boolean beforeLunchSelected = false;
    boolean beforeBreakfastSelected = false;
    boolean[] daySelection = {false, false, false, false, false, false, false};
    RadioButton genderRadioButton, lastButtonReference;
    RadioGroup radioGroup;
    private Stack<String> ll;
    private Stack<String> daysSelect;
    private boolean check = false;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Button donereminder;
    EditText mname;
    TextView t1, mo;
    DatePickerDialog.OnDateSetListener setListener1, setListener2;
    public int selectedId;
    Date date1, date2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = EditReminder.this.getResources().getDrawable(R.drawable.color);
        getWindow().setBackgroundDrawable(background);

        afterDinnerButton = findViewById(R.id.afterDinnerButton);
        afterBreakfastButton = findViewById(R.id.afterBreakfastButton);
        afterLunchButton = findViewById(R.id.afterLunchButton);
        beforeDinnerButton = findViewById(R.id.beforeDinnerButton);
        beforeBreakfastButton = findViewById(R.id.beforeBreakfastButton);
        beforeLunchButton = findViewById(R.id.beforeLunchButton);
        mondayButton = findViewById(R.id.mondayButton);
        tuesdayButton = findViewById(R.id.tuesdayButton);
        wednesdayButton = findViewById(R.id.wednesdayButton);
        thursdayButton = findViewById(R.id.thursdayButton);
        fridayButton = findViewById(R.id.fridayButton);
        saturdayButton = findViewById(R.id.saturdayButton);
        sundayButton = findViewById(R.id.sundayButton);
        radioGroup = findViewById(R.id.rg);
        donereminder = findViewById(R.id.done);
        mname = findViewById(R.id.medname);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mo = findViewById(R.id.monthedit);
        t1 = findViewById(R.id.daily);
        ll = new Stack<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        daysSelect = new Stack<>();
        Button[] timeSelectionButton = {afterBreakfastButton, afterLunchButton, afterDinnerButton, beforeBreakfastButton, beforeLunchButton, beforeDinnerButton};
        Button[] daysSelectionButton = {mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton};

        afterDinnerButton.setOnClickListener(v -> afterDinnerSelected = afterClickChange(afterDinnerButton, afterDinnerSelected, ll));
        afterBreakfastButton.setOnClickListener(v -> afterBreakfastSelected = afterClickChange(afterBreakfastButton, afterBreakfastSelected, ll));
        afterLunchButton.setOnClickListener(v -> afterLunchSelected = afterClickChange(afterLunchButton, afterLunchSelected, ll));
        beforeLunchButton.setOnClickListener(v -> beforeLunchSelected = afterClickChange(beforeLunchButton, beforeLunchSelected, ll));
        beforeBreakfastButton.setOnClickListener(v -> beforeBreakfastSelected = afterClickChange(beforeBreakfastButton, beforeBreakfastSelected, ll));
        beforeDinnerButton.setOnClickListener(v -> beforeDinnerSelected = afterClickChange(beforeDinnerButton, beforeDinnerSelected, ll));
        mondayButton.setOnClickListener(v -> daySelection[0] = afterClickChange(mondayButton, daySelection[0], daysSelect));
        tuesdayButton.setOnClickListener(v -> daySelection[1] = afterClickChange(tuesdayButton, daySelection[1], daysSelect));
        wednesdayButton.setOnClickListener(v -> daySelection[2] = afterClickChange(wednesdayButton, daySelection[2], daysSelect));
        thursdayButton.setOnClickListener(v -> daySelection[3] = afterClickChange(thursdayButton, daySelection[3], daysSelect));
        fridayButton.setOnClickListener(v -> daySelection[4] = afterClickChange(fridayButton, daySelection[4], daysSelect));
        saturdayButton.setOnClickListener(v -> daySelection[5] = afterClickChange(saturdayButton, daySelection[5], daysSelect));
        sundayButton.setOnClickListener(v -> daySelection[6] = afterClickChange(sundayButton, daySelection[6], daysSelect));

        Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);

        Bundle extras = getIntent().getExtras();
        if (extras.getString("page").equals("Medicine")) {
            hashMap = (HashMap<String, Object>) extras.getSerializable("hashMap");
            mname.setText(hashMap.get("Name").toString());
            Date endDate = ((Timestamp) hashMap.get("EndDate")).toDate();
            Date startDate = ((Timestamp) hashMap.get("StartDate")).toDate();
            startDate = new Date(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate());
            endDate = new Date(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate());
            List<String> timeDays = new ArrayList<>();
            List<String> timimgSchedule = new ArrayList<>();
            timeDays = (List<String>) hashMap.get("Time");
            timimgSchedule = (List<String>) hashMap.get("TimingSchedule");
            Integer icon = Integer.valueOf(hashMap.get("Icon").toString());
            switch (icon) {
                case R.drawable.pill1:
                    Toast.makeText(this, "pill1 selected", Toast.LENGTH_SHORT).show();
                    genderRadioButton = findViewById(R.id.pill1);
                    genderRadioButton.setChecked(true);
                    onClickMethod(genderRadioButton);
                    break;
                case R.drawable.pill2:
                    Toast.makeText(this, "pill2 selected", Toast.LENGTH_SHORT).show();
                    genderRadioButton = findViewById(R.id.pill2);
                    genderRadioButton.setChecked(true);
                    onClickMethod(genderRadioButton);
                    break;
                case R.drawable.pill3:
                    genderRadioButton = findViewById(R.id.pill3);
                    genderRadioButton.setChecked(true);
                    onClickMethod(genderRadioButton);
                    break;
                case R.drawable.pill4:
                    Toast.makeText(this, "pill4 selected", Toast.LENGTH_SHORT).show();
                    genderRadioButton = findViewById(R.id.pill4);
                    genderRadioButton.setChecked(true);
                    onClickMethod(genderRadioButton);
                    break;
                case R.drawable.pill5:
                    Toast.makeText(this, "pill5 selected", Toast.LENGTH_SHORT).show();
                    genderRadioButton = (RadioButton) findViewById(R.id.pill5);
                    genderRadioButton.setChecked(true);
                    onClickMethod(genderRadioButton);
                    break;
                default:
                    break;
            }
            assert timeDays != null;
            for (String timeDay : timeDays) {
                switch (timeDay) {
                    case "Monday":
                        daySelection[0] = afterClickChange(daysSelectionButton[0], daySelection[0], daysSelect);
                        break;
                    case "Tuesday":
                        daySelection[1] = afterClickChange(daysSelectionButton[1], daySelection[1], daysSelect);
                        break;
                    case "Wednesday":
                        daySelection[2] = afterClickChange(daysSelectionButton[2], daySelection[2], daysSelect);
                        break;
                    case "Thursday":
                        daySelection[3] = afterClickChange(daysSelectionButton[3], daySelection[3], daysSelect);
                        break;
                    case "Friday":
                        daySelection[4] = afterClickChange(daysSelectionButton[4], daySelection[4], daysSelect);
                        break;
                    case "Saturday":
                        daySelection[5] = afterClickChange(daysSelectionButton[5], daySelection[5], daysSelect);
                        break;
                    case "Sunday":
                        daySelection[6] = afterClickChange(daysSelectionButton[6], daySelection[6], daysSelect);
                        break;
                    default:
                        break;
                }
            }
            assert timimgSchedule != null;
            for (String timeDay : timimgSchedule) {
                switch (timeDay) {
                    case "After Breakfast":
                        afterBreakfastSelected = afterClickChange(timeSelectionButton[0], afterBreakfastSelected, ll);
                        break;
                    case "After Lunch":
                        afterLunchSelected = afterClickChange(timeSelectionButton[1], afterLunchSelected, ll);
                        break;
                    case "After Dinner":
                        afterDinnerSelected = afterClickChange(timeSelectionButton[2], afterDinnerSelected, ll);
                        break;
                    case "Before Breakfast":
                        beforeBreakfastSelected = afterClickChange(timeSelectionButton[3], beforeBreakfastSelected, ll);
                        break;
                    case "Before Lunch":
                        beforeLunchSelected = afterClickChange(timeSelectionButton[4], beforeLunchSelected, ll);
                        break;
                    case "Before Dinner":
                        beforeDinnerSelected = afterClickChange(timeSelectionButton[5], beforeDinnerSelected, ll);
                        break;
                    default:
                        break;
                }
            }
            endYear = endDate.getYear();
            endMonth = endDate.getMonth();
            endDay = endDate.getDate();
            startYear = startDate.getYear();
            startMonth = startDate.getMonth();
            startDay = startDate.getDate();
            t1.setText(endDate.getDate() + "/" + (endDate.getMonth() + 1) + "/" + endDate.getYear());
            mo.setText(startDate.getDate() + "/" + (startDate.getMonth() + 1) + "/" + startDate.getYear());
        }

        int finalStartYear = startYear;
        int finalStartMonth = startMonth;
        int finalStartDay = startDay;
        mo.setOnClickListener(v -> {
            check = true;
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditReminder.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener1, finalStartYear, finalStartMonth, finalStartDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        setListener1 = (view, year12, month12, dayOfMonth) -> {
            date1 = new Date(year12 - 1900, month12, dayOfMonth);
            String date = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
            mo.setText(date);
        };
        int finalEndYear = endYear;
        int finalEndMonth = endMonth;
        int finalEndDay = endDay;
        t1.setOnClickListener(v -> {
            check = false;
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditReminder.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener2, finalEndYear, finalEndMonth, finalEndDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        setListener2 = (view, year1, month1, dayOfMonth) -> {
            date2 = new Date(year1 - 1900, month1, dayOfMonth);
            String date1 = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            t1.setText(date1);
        };

        HashMap<String, Object> finalHashMap = hashMap;
        donereminder.setOnClickListener(v -> {
            String email = firebaseAuth.getCurrentUser().getEmail();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(email);
            if (extras.getString("page").equals("Medicine")) {
                if (date1 == null || date2 == null || date2.getTime() - date1.getTime() < 0) {
                    date1 = new Date(finalStartYear - 1900, finalStartMonth, finalStartDay);
                    date2 = new Date(finalEndYear - 1900, finalEndMonth, finalEndDay);
                }
                docRef.update("MedicineNames." + finalHashMap.get("Name").toString(), FieldValue.delete()).addOnCompleteListener(task -> {
                    String value = finalHashMap.get("Name").toString();
                    docRef.update("MedicineNames.Medicines", FieldValue.arrayRemove(value)).addOnCompleteListener(task1 -> {
                        String s = mname.getText().toString();
                        Integer s1 = 0;
                        switch (genderRadioButton.getId()) {
                            case R.id.pill1:
                                s1 = R.drawable.pill1;
                                break;
                            case R.id.pill2:
                                s1 = R.drawable.pill2;
                                break;
                            case R.id.pill3:
                                s1 = R.drawable.pill3;
                                break;
                            case R.id.pill4:
                                s1 = R.drawable.pill4;
                                break;
                            case R.id.pill5:
                                s1 = R.drawable.pill5;
                                break;
                        }
                        if (s.isEmpty() || s1 == 0 || ll.isEmpty()) {
                            Toast.makeText(EditReminder.this, s + s1 + ll, Toast.LENGTH_SHORT).show();
                            Toast.makeText(EditReminder.this, "Plz Fill all fields ", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> mp = new HashMap<>();
                            Map<String, Object> mp1 = new HashMap<>();
                            Map<String, Object> map = new HashMap<>();
                            map.put("Icon", s1);
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
                            calendar1.set(Calendar.HOUR_OF_DAY, 0);
                            calendar1.set(Calendar.MINUTE, 0);
                            calendar1.set(Calendar.SECOND, 0);
                            calendar1.set(Calendar.MILLISECOND, 0);
                            map.put("Completed", calendar1.getTime());
                            map.put("Time", daysSelect);
                            map.put("Name", s);
                            map.put("TimingSchedule", ll);
                            map.put("StartDate", date1);
                            map.put("EndDate", date2);
                            mp1.put(s, map);
                            mp.put("MedicineNames", mp1);
                            docRef.update("MedicineNames.Medicines", FieldValue.arrayUnion(s)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    docRef.set(mp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            Toast.makeText(EditReminder.this, "Updated", Toast.LENGTH_SHORT).show();
                                            genderRadioButton.setChecked(false);
                                            onClickMethod(genderRadioButton);
                                            Intent intent = new Intent(EditReminder.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        }
                    });
                });
            } else {
                String s = mname.getText().toString();
                Integer s1 = 0;
                switch (genderRadioButton.getId()) {
                    case R.id.pill1:
                        s1 = R.drawable.pill1;
                        break;
                    case R.id.pill2:
                        s1 = R.drawable.pill2;
                        break;
                    case R.id.pill3:
                        s1 = R.drawable.pill3;
                        break;
                    case R.id.pill4:
                        s1 = R.drawable.pill4;
                        break;
                    case R.id.pill5:
                        s1 = R.drawable.pill5;
                        break;
                }
                if (s.isEmpty() || s1 == 0 || ll.isEmpty() || date1 == null || date2 == null || date2.getTime() - date1.getTime() < 0) {
                    Toast.makeText(EditReminder.this, s + s1 + ll + date1 + date2, Toast.LENGTH_SHORT).show();
                    Toast.makeText(EditReminder.this, "Plz Fill all fields ", Toast.LENGTH_SHORT).show();
                } else {
                    List<String> medicines = new ArrayList<>();
                    Map<String, Object> mp = new HashMap<>();
                    Map<String, Object> mp1 = new HashMap<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Icon", s1);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
                    calendar1.set(Calendar.HOUR_OF_DAY, 0);
                    calendar1.set(Calendar.MINUTE, 0);
                    calendar1.set(Calendar.SECOND, 0);
                    calendar1.set(Calendar.MILLISECOND, 0);
                    map.put("Completed", calendar1.getTime());
                    map.put("Time", daysSelect);
                    map.put("Name", s);
                    map.put("TimingSchedule", ll);
                    map.put("StartDate", date1);
                    map.put("EndDate", date2);
                    mp1.put(s, map);
                    mp.put("MedicineNames", mp1);
                    docRef.update("MedicineNames.Medicines", FieldValue.arrayUnion(s)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            docRef.set(mp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Toast.makeText(EditReminder.this, "Go To Settings Page to set Alarm", Toast.LENGTH_SHORT).show();
                                    genderRadioButton.setChecked(false);
                                    onClickMethod(genderRadioButton);
                                    Intent intent = new Intent(EditReminder.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }

            }});
    }

    @SuppressLint("ResourceAsColor")
    public boolean afterClickChange(Button button, boolean afterClick, Stack<String> list) {
        afterClick = !afterClick;
        if (afterClick == true) {
            button.getBackground().setAlpha(100);
            list.add(button.getText().toString());
        } else {
            button.getBackground().setAlpha(255);
            list.pop();
        }
        return afterClick;
    }

    public void onClickMethod(View v) {
        selectedId = radioGroup.getCheckedRadioButtonId();
        genderRadioButton = findViewById(selectedId);
        if (lastButtonReference != null) {
            lastButtonReference.getBackground().setAlpha(255);
        }
        genderRadioButton.getBackground().setAlpha(150);
        lastButtonReference = genderRadioButton;
    }

//    public void numberOFWeeks(){
//        Calendar a = new GregorianCalendar(2002,1,22);
//        Calendar b = new GregorianCalendar(2002,1,28);
//        System.out.println(a.get(Calendar.WEEK_OF_YEAR));
//        System.out.println(b.get(Calendar.WEEK_OF_YEAR));
//        int weeks = b.get(Calendar.WEEK_OF_YEAR)-a.get(Calendar.WEEK_OF_YEAR);
//        System.out.println(weeks);
//    }
}