package com.example.medapp.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.medapp.LoginActivity;
import com.example.medapp.MainActivity;
import com.example.medapp.MedicinePage;
import com.example.medapp.R;
import com.example.medapp.ui.home.MyListAdapter;
import com.example.medapp.ui.notifications.NotificationsViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class SettingsFragment extends Fragment {

    Map<String, Object> dataMap;
    EditText userName, emailChange;
    Button changeButton;

    String[] weekDays = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch alarmState = root.findViewById(R.id.alarmState);
        userName = root.findViewById(R.id.userName);
        emailChange = root.findViewById(R.id.emailChange);
        changeButton = root.findViewById(R.id.changeButton);
        dataMap = new HashMap<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(email);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (currentUser != null) {
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot snap = task.getResult();
                    String name = (String) snap.getData().get("Name");
                    userName.setText(name);
                    Map<String, HashMap<String, Object>> medicineStore;
                    medicineStore = (Map<String, HashMap<String, Object>>) snap.getData().get("MedicineNames");
                    List<String> group = (List<String>) medicineStore.get("Medicines");
                    assert group != null;
                    for (String MedicineName : group) {
                        Map<String, Object> medicineName;
                        medicineName = medicineStore.get(MedicineName);
                        assert medicineName != null;
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);
                        today.set(Calendar.MILLISECOND, 0);
                        Date completedDate = ((Timestamp) medicineName.get("Completed")).toDate();
                        if(completedDate.compareTo(today.getTime()) == 0){
                            alarmState.setChecked(true);
                        } else{
                            alarmState.setChecked(false);
                        }
                    }
                }
            });
            emailChange.setText(currentUser.getEmail());
        }
        if (account != null) {
            userName.setText(account.getDisplayName());
            emailChange.setText(account.getEmail());
            emailChange.setEnabled(false);
            userName.setEnabled(false);
        }

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snap = task.getResult();
                        assert snap != null;
                        String name = (String) snap.getData().get("Name");
                        String email = (String) snap.getData().get("Email");
                        if (emailChange.getText().toString().equals(email)) {
                            if (userName.getText().toString().equals(name)) {
                                Toast.makeText(getContext(), "No Data Change", Toast.LENGTH_SHORT).show();
                            } else {
                                docRef.update("Name", userName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName.getText().toString())
                                                .build();
                                        assert currentUser != null;
                                        currentUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Name Changed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        } else {
                            if (userName.getText().toString().equals(name)) {
                                currentUser.updateEmail("user@example.com")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    docRef.update("Email", emailChange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                            FirebaseFirestore.getInstance().collection("Users").document(emailChange.getText().toString()).set(snap.getData());
                                                            Toast.makeText(getContext(), "Email Updated Successfully", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                                }
                                            }
                                        });
                            } else {
                                docRef.update("Name", userName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName.getText().toString())
                                                .build();
                                        assert currentUser != null;
                                        currentUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            currentUser.updateEmail(emailChange.getText().toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                docRef.update("Email", emailChange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                        FirebaseFirestore.getInstance().collection("Users").document(emailChange.getText().toString()).set(snap.getData());
                                                                                        Toast.makeText(getContext(), "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                        FirebaseAuth.getInstance().signOut();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        alarmState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmState.isChecked()) {
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
                                    Map<String, HashMap<String, Object>> medicineStore;
                                    medicineStore = (Map<String, HashMap<String, Object>>) dataMap.get("MedicineNames");
                                    List<String> group = (List<String>) medicineStore.get("Medicines");
                                    assert group != null;
                                    for (String MedicineName : group) {
                                        Map<String, Object> medicineName;
                                        medicineName = medicineStore.get(MedicineName);
                                        assert medicineName != null;
                                        List<String> days = (List<String>) medicineName.get("Time");
                                        Date completedDate = ((Timestamp) medicineName.get("Completed")).toDate();
                                        Date endDate = ((Timestamp) medicineName.get("EndDate")).toDate();
                                        Date startDate = ((Timestamp) medicineName.get("StartDate")).toDate();
                                        List<String> timeStamp;
                                        timeStamp = (List<String>) medicineName.get("TimingSchedule");
                                        Calendar today = Calendar.getInstance();
                                        today.set(Calendar.HOUR_OF_DAY, 0);
                                        today.set(Calendar.MINUTE, 0);
                                        today.set(Calendar.SECOND, 0);
                                        today.set(Calendar.MILLISECOND, 0);
                                        assert timeStamp != null;
                                        if (getDateBetweenDates(startDate, endDate, today.getTime())) {
                                            assert days != null;
                                            for (String day : days) {
                                                if (weekDays[today.get(Calendar.DAY_OF_WEEK)].equals(day)) {
                                                    assert completedDate != null;
                                                    if(completedDate.compareTo(today.getTime()) == 0){
                                                        Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
                                                        alarmState.setChecked(true);
                                                    } else {
                                                        today = Calendar.getInstance();
                                                        for (String s : timeStamp) {
                                                            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                                                            intent.putExtra(AlarmClock.EXTRA_HOUR, today.get(Calendar.HOUR));
                                                            intent.putExtra(AlarmClock.EXTRA_MINUTES, today.get((Calendar.MINUTE)));
                                                            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Please Take " + s + " medicine " + MedicineName);
                                                            startActivity(intent);
                                                        }
                                                        today.set(Calendar.HOUR_OF_DAY, 0);
                                                        today.set(Calendar.MINUTE, 0);
                                                        today.set(Calendar.SECOND, 0);
                                                        today.set(Calendar.MILLISECOND, 0);
                                                        completedDate = today.getTime();
                                                        docRef.update("MedicineNames." + MedicineName + ".Completed", completedDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        return root;
    }

    public boolean getDateBetweenDates(Date startDate, Date endDate, Date givenDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
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
