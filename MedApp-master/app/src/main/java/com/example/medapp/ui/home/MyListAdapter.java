package com.example.medapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medapp.EditReminder;
import com.example.medapp.MainActivity;
import com.example.medapp.R;
import com.example.medapp.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] mainTitle;
    private final String[] button1Title;
    private final String[] button2Title;
    private final Integer[] imgId;
    private final List<String> group;
    private final Map<String, HashMap<String, Object>> hashMap;


    public MyListAdapter(Activity context, List<String> group, Map<String, HashMap<String, Object>> finalMedicineStore, String[] mainTitle, String[] button1Title, String[] button2Title, Integer[] imgId) {
        super(context, R.layout.my_layout, mainTitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.mainTitle = mainTitle;
        this.button1Title = button1Title;
        this.button2Title = button2Title;
        this.imgId = imgId;
        this.group = group;
        this.hashMap = finalMedicineStore;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_layout, null, true);

        TextView titleText = rowView.findViewById(R.id.title);
        ImageView imageView = rowView.findViewById(R.id.iconImage);
        TextView button1Text = rowView.findViewById(R.id.textview1Display);
        TextView button2Text = rowView.findViewById(R.id.textview2Display);
        ImageView imageView1 = rowView.findViewById(R.id.imageView10);

        titleText.setText(mainTitle[position]);
        imageView.setImageResource(imgId[position]);
        button1Text.setText(button1Title[position]);
        if(button2Title[position].equals("")){
            button2Text.setVisibility(View.INVISIBLE);
        } else{
            button2Text.setVisibility(View.VISIBLE);
            button2Text.setText(button2Title[position]);
        }

        imageView1.setOnClickListener(v -> {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(email);
            HashMap<String, Object> map = hashMap.get(group.get(position));
            docRef.update("MedicineNames." + map.get("Name").toString(), FieldValue.delete()).addOnCompleteListener(task -> {
                String value = map.get("Name").toString();
                docRef.update("MedicineNames.Medicines", FieldValue.arrayRemove(value)).addOnCompleteListener(task1 -> {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    getContext().startActivity(new Intent(getContext(), MainActivity.class));
                });
            });
        });

        button1Text.setOnClickListener(v -> Toast.makeText(getContext(), button1Text.getText(), Toast.LENGTH_SHORT).show());

        button2Text.setOnClickListener(v -> Toast.makeText(getContext(), button2Text.getText(), Toast.LENGTH_SHORT).show());

        return rowView;
    }

    ;
}
