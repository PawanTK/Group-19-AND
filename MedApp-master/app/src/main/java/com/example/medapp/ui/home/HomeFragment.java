
package com.example.medapp.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.medapp.LoginActivity;
import com.example.medapp.MainActivity;
import com.example.medapp.MedicinePage;
import com.example.medapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    ListView listView;
    DatePickerDialog picker;
    int day, month, year, dayWeek;
    Calendar cldr;
    Button todayButton, monthButton, weekButton;
    ImageButton logOut;
    String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    Map<String, Object> dataMap;
    List<String> mainTitleList;
    List<String> button1TitleList;
    List<String> button2TitleList;
    List<Integer> imgIdList;

    private HomeViewModel homeViewModel;

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
//        Auth.GoogleSignInApi.signOut()
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).getSupportActionBar().hide();
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        TextView calendarDateDisplay = root.findViewById(R.id.calendarDateDisplay);
        TextView dayDisplay = root.findViewById(R.id.dayDisplay);
        logOut = root.findViewById(R.id.logout);
        todayButton = root.findViewById(R.id.today);
        weekButton = root.findViewById(R.id.week);
        monthButton = root.findViewById(R.id.month);
        listView = root.findViewById(R.id.listview);
        dataMap = new HashMap<>();
        mainTitleList = new ArrayList<>();
        button1TitleList = new ArrayList<>();
        button2TitleList = new ArrayList<>();
        imgIdList = new ArrayList<>();

        calendarDisplay(days, calendarDateDisplay, dayDisplay);

        logOut.setOnClickListener(v -> {
            signOut();
        });
        showLists(listView);
        return root;
    }

    private void listDisplay(List<String> mainTitleList, List<String> button1TitleList, List<String> button2TitleList, List<Integer> imgIdList, ListView listView, Map<String, HashMap<String, Object>> medicineStore, List<String> group1) {
        MyListAdapter adapter = new MyListAdapter(getActivity(), group1, medicineStore, mainTitleList.toArray(new String[0]), button1TitleList.toArray(new String[0]), button2TitleList.toArray(new String[0]), imgIdList.toArray(new Integer[0]));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, Object> hashmap = medicineStore.get(group1.get((int) id));
            Intent intent = new Intent(getContext(), MedicinePage.class);
            Bundle extras = new Bundle();
            extras.putSerializable("hashMap", hashmap);
            extras.putString("page", "HomeFragment");
            intent.putExtras(extras);
            startActivity(intent);
        });
    }

    private void lineDisplay(Button todayButton, Button weekButton, Button monthButton) {
        todayButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.button_onoff_indicator_on);
        weekButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        monthButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void calendarDisplay(String[] days, TextView calendarDateDisplay, TextView dayDisplay) {
        cldr = Calendar.getInstance();
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);
        dayWeek = cldr.get(Calendar.DAY_OF_WEEK);
        calendarDateDisplay.setText(day + "/" + (month + 1) + "/" + year);
        dayDisplay.setText(days[dayWeek]);

        calendarDateDisplay.setOnClickListener(v -> {
            // date picker dialog
            picker = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendarDateDisplay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        cldr.set(year, monthOfYear, dayOfMonth);
                        day = cldr.get(Calendar.DAY_OF_MONTH);
                        month = cldr.get(Calendar.MONTH);
                        this.year = cldr.get(Calendar.YEAR);
                        dayWeek = cldr.get(Calendar.DAY_OF_WEEK);
                        dayDisplay.setText(days[dayWeek]);
                        showLists(listView);
                    }, year, month, day);
            picker.show();
        });
    }

    public void showLists(ListView listView) {
        mainTitleList.clear();
        button1TitleList.clear();
        button2TitleList.clear();
        imgIdList.clear();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snap = task.getResult();
                    assert snap != null;
                    if (snap.exists()) {
                        dataMap = snap.getData();
                        assert dataMap != null;
                        String name = dataMap.get("Name").toString();
                        Map<String, HashMap<String, Object>> medicineStore = new HashMap<>();
                        medicineStore = (Map<String, HashMap<String, Object>>) dataMap.get("MedicineNames");
                        List<String> group = (List<String>) medicineStore.get("Medicines");
                        assert group != null;
                        for (String MedicineName : group) {
                            Map<String, Object> medicineName;
                            medicineName = medicineStore.get(MedicineName);
                            assert medicineName != null;
                            String name1 = medicineName.get("Name").toString();
                            List<String> daysOfMedicine = (List<String>) medicineName.get("Time");
                            Date endDate = ((Timestamp) medicineName.get("EndDate")).toDate();
                            Date startDate = ((Timestamp) medicineName.get("StartDate")).toDate();
                            startDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate());
                            endDate = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate());
                            cldr.set(Calendar.HOUR_OF_DAY, 0);
                            cldr.set(Calendar.MINUTE, 0);
                            cldr.set(Calendar.SECOND, 0);
                            cldr.set(Calendar.MILLISECOND, 0);
                            if (getDateBetweenDates(startDate, endDate, cldr.getTime())) {
                                for (String str : daysOfMedicine) {
                                    if (str.equals(days[cldr.get(Calendar.DAY_OF_WEEK)])) {
                                        String iconString = medicineName.get("Icon").toString();
                                        Integer icon = Integer.valueOf(iconString);
                                        mainTitleList.add(name1);
                                        List<String> timeStamp;
                                        timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                        assert timeStamp != null;
                                        if(timeStamp.size() == 1){
                                            button1TitleList.add(timeStamp.get(0));
                                            button2TitleList.add("");
                                        } else{
                                            button1TitleList.add(timeStamp.get(0));
                                            button2TitleList.add(timeStamp.get(1));
                                        }
                                        imgIdList.add(icon);
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "No Medicine Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        List<String> group1 = new ArrayList<>();
                        lineDisplay(todayButton, weekButton, monthButton);
                        todayButton.setOnClickListener(v -> {
                            lineDisplay(todayButton, weekButton, monthButton);
                            showLists(listView);
                        });
                        Map<String, HashMap<String, Object>> finalMedicineStore = medicineStore;
                        weekButton.setOnClickListener(v -> {
                            List<String> groups = new ArrayList<>();
                            lineDisplay(weekButton, todayButton, monthButton);
                            mainTitleList.clear();
                            button1TitleList.clear();
                            button2TitleList.clear();
                            imgIdList.clear();
                            Date newDate = cldr.getTime();
                            newDate.setDate(newDate.getDate() + (7 - cldr.get(Calendar.DAY_OF_WEEK)));
                            List<Date> dates = new ArrayList<>();
                            while (dates.size() != 7) {
                                dates.add(newDate);
                                newDate = new Date(newDate.getYear(), newDate.getMonth(), newDate.getDate() - 1);
                            }
                            newDate.setDate(newDate.getDate() + 7);
                            for (String MedicineName : group) {
                                Map<String, Object> medicineName;
                                medicineName = finalMedicineStore.get(MedicineName);
                                assert medicineName != null;
                                String name1 = medicineName.get("Name").toString();
                                List<String> daysOfMedicine = (List<String>) medicineName.get("Time");
                                Date endDate = ((Timestamp) medicineName.get("EndDate")).toDate();
                                Date startDate = ((Timestamp) medicineName.get("StartDate")).toDate();
                                startDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate());
                                endDate = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate());
                                if (getDateBetweenDates(startDate, endDate, newDate)) {
                                    assert daysOfMedicine != null;
                                    loop:
                                    for (String str : daysOfMedicine) {
                                        for (Date date : dates) {
                                            if (str.equals(days[date.getDay() + 1])) {
                                                String iconString = medicineName.get("Icon").toString();
                                                Integer icon = Integer.valueOf(iconString);
                                                mainTitleList.add(name1);
                                                List<String> timeStamp;
                                                timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                                assert timeStamp != null;
                                                if(timeStamp.size() == 1){
                                                    button1TitleList.add(timeStamp.get(0));
                                                    button2TitleList.add("");
                                                } else{
                                                    button1TitleList.add(timeStamp.get(0));
                                                    button2TitleList.add(timeStamp.get(1));
                                                }
                                                imgIdList.add(icon);
                                                break loop;
                                            }
                                        }
                                    }
                                } else {
                                    assert daysOfMedicine != null;
                                    loop:
                                    for (String str : daysOfMedicine) {
                                        for (Date date : dates) {
                                            if (endDate.equals(date)) {
                                                break loop;
                                            }
                                            if (getDateBetweenDates(startDate, endDate, date)) {
                                                if (str.equals(days[date.getDay() + 1])) {
                                                    String iconString = medicineName.get("Icon").toString();
                                                    Integer icon = Integer.valueOf(iconString);
                                                    mainTitleList.add(name1);
                                                    List<String> timeStamp;
                                                    timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                                    assert timeStamp != null;
                                                    if(timeStamp.size() == 1){
                                                        button1TitleList.add(timeStamp.get(0));
                                                        button2TitleList.add("");
                                                    } else{
                                                        button1TitleList.add(timeStamp.get(0));
                                                        button2TitleList.add(timeStamp.get(1));
                                                    }
                                                    imgIdList.add(icon);
                                                    break loop;
                                                }
                                            } else {
                                                break loop;
                                            }
                                        }
                                    }
                                    if (mainTitleList.isEmpty()) {
                                        Toast.makeText(getContext(), "No medicine Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            groups.addAll(mainTitleList);
                            listDisplay(mainTitleList, button1TitleList, button2TitleList, imgIdList, listView, finalMedicineStore, groups);
                        });
                        monthButton.setOnClickListener(v -> {
                            List<String> groups = new ArrayList<>();
                            lineDisplay(monthButton, weekButton, todayButton);
                            mainTitleList.clear();
                            button1TitleList.clear();
                            button2TitleList.clear();
                            imgIdList.clear();
                            int lastDate = cldr.getActualMaximum(Calendar.DATE);
                            Date newDate = cldr.getTime();
                            newDate.setDate(1);
                            List<Date> dates = new ArrayList<>();
                            while (dates.size() != lastDate) {
                                dates.add(newDate);
                                newDate = new Date(newDate.getYear(), newDate.getMonth(), newDate.getDate() + 1);
                            }
                            newDate.setDate(newDate.getDate() - 1);
                            assert group != null;
                            for (String MedicineName : group) {
                                Map<String, Object> medicineName;
                                medicineName = finalMedicineStore.get(MedicineName);
                                assert medicineName != null;
                                String name1 = medicineName.get("Name").toString();
                                List<String> daysOfMedicine = (List<String>) medicineName.get("Time");
                                Date endDate = ((Timestamp) medicineName.get("EndDate")).toDate();
                                Date startDate = ((Timestamp) medicineName.get("StartDate")).toDate();
                                startDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate());
                                endDate = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate());
                                if (getDateBetweenDates(startDate, endDate, newDate)) {
                                    assert daysOfMedicine != null;
                                    loop:
                                    for (String str : daysOfMedicine) {
                                        for (Date date : dates) {
                                            if (str.equals(days[date.getDay() + 1])) {
                                                String iconString = medicineName.get("Icon").toString();
                                                Integer icon = Integer.valueOf(iconString);
                                                mainTitleList.add(name1);
                                                List<String> timeStamp;
                                                timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                                assert timeStamp != null;
                                                if(timeStamp.size() == 1){
                                                    button1TitleList.add(timeStamp.get(0));
                                                    button2TitleList.add("");
                                                } else{
                                                    button1TitleList.add(timeStamp.get(0));
                                                    button2TitleList.add(timeStamp.get(1));
                                                }
                                                imgIdList.add(icon);
                                                break loop;
                                            }
                                        }
                                    }
                                } else {
                                    assert daysOfMedicine != null;
                                    loop:
                                    for (String str : daysOfMedicine) {
                                        for (Date date : dates) {
                                            if (endDate.equals(date)) {
                                                break loop;
                                            }
                                            if (getDateBetweenDates(startDate, endDate, date)) {
                                                if (str.equals(days[date.getDay() + 1])) {
                                                    String iconString = medicineName.get("Icon").toString();
                                                    Integer icon = Integer.valueOf(iconString);
                                                    mainTitleList.add(name1);
                                                    List<String> timeStamp;
                                                    timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                                    assert timeStamp != null;
                                                    if(timeStamp.size() == 1){
                                                        button1TitleList.add(timeStamp.get(0));
                                                        button2TitleList.add("");
                                                    } else{
                                                        button1TitleList.add(timeStamp.get(0));
                                                        button2TitleList.add(timeStamp.get(1));
                                                    }
                                                    imgIdList.add(icon);
                                                    break loop;
                                                }
                                            } else {
                                                break loop;
                                            }
                                        }
                                    }
                                    if (mainTitleList.isEmpty()) {
                                        Toast.makeText(getContext(), "No medicine Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            groups.addAll(mainTitleList);
                            listDisplay(mainTitleList, button1TitleList, button2TitleList, imgIdList, listView, finalMedicineStore, groups);
                        });
                        group1.addAll(mainTitleList);
                        listDisplay(mainTitleList, button1TitleList, button2TitleList, imgIdList, listView, medicineStore, group1);
                    }
                }
            }
        });
    }

    public boolean getDateBetweenDates(Date startdate, Date enddate, Date givenDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        for (Date date : dates) {
            if (date.equals(givenDate)) {
                return true;
            }
        }
        return false;
    }
}