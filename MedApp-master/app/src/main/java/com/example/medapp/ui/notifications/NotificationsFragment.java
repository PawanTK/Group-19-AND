package com.example.medapp.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.medapp.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    String url;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Button nedmeds = root.findViewById(R.id.netmeds);
        Button mg = root.findViewById(R.id.mg);
        Button mchemist = root.findViewById(R.id.mchemist);

        Button gogomeds = root.findViewById(R.id.gogomeds);

        nedmeds.setOnClickListener(this::onClickIntent);
        mg.setOnClickListener(this::onClickIntent);
        mchemist.setOnClickListener(this::onClickIntent);

        gogomeds.setOnClickListener(this::onClickIntent);

        return root;
    }

    public void onClickIntent(View v){
        switch(v.getId()){
            case R.id.netmeds:
                url = "https://www.netmeds.com/";
                break;
            case R.id.mg:
                url = "https://www.1mg.com/";
                break;
            case R.id.pharmeasy:
                url = "https://www.pharmeasy.com/";
                break;
            case R.id.mchemist:
                url = "https://www.mchemist.com/";
                break;
            case R.id.medibuddy:
                url = "https://www.medibuddy.com/";
                break;
            case R.id.gogomeds:
                url = "https://www.gogomeds.com/";
                break;

        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }
}