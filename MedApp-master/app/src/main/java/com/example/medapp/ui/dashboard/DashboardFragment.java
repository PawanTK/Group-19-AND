package com.example.medapp.ui.dashboard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.medapp.EditReminder;
import com.example.medapp.MedicinePage;
import com.example.medapp.R;
import com.example.medapp.ui.home.MyListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    ListView listView;
    Map<String, Object> dataMap;
    List<String> mainTitleList;
    List<String> button1TitleList;
    List<String> button2TitleList;
    List<Integer> imgIdList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        dataMap = new HashMap<>();
        mainTitleList = new ArrayList<>();
        button1TitleList = new ArrayList<>();
        button2TitleList = new ArrayList<>();
        imgIdList = new ArrayList<>();
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditReminder.class);
            intent.putExtra("page", "Dashboard");
            startActivity(intent);
        });

        listView = root.findViewById(R.id.medicineList);
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

                        Map<String, HashMap<String, Object>> finalMedicineStore = medicineStore;
                        MyListAdapter adapter = new MyListAdapter(getActivity(), group, finalMedicineStore, mainTitleList.toArray(new String[0]), button1TitleList.toArray(new String[0]), button2TitleList.toArray(new String[0]), imgIdList.toArray(new Integer[0]));
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            HashMap<String, Object> hashmap = finalMedicineStore.get(group.get((int) id));
                            Intent intent = new Intent(getContext(), MedicinePage.class);
                            Bundle extras = new Bundle();
                            extras.putSerializable("hashMap", hashmap);
                            extras.putString("page", "Dashboard");
                            intent.putExtras(extras);
                            startActivity(intent);
                        });
                    }
                }
            }
        });
        return root;
    }
}