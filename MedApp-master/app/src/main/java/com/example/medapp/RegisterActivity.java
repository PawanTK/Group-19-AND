package com.example.medapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, password, confirm;
    boolean isNameValid, isEmailValid, isPasswordValid, ispasswordConfirm;
    TextInputLayout nameError, emailError, passError, confirmError;
    FirebaseAuth mAuth;
    Button register_btn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = RegisterActivity.this.getResources().getDrawable(R.drawable.color);
        getWindow().setBackgroundDrawable(background);

        register_btn = findViewById(R.id.register_btn);
        name = findViewById(R.id.name_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirm = findViewById(R.id.confirm_edit_text);
        nameError = findViewById(R.id.name_text_layout);
        emailError = findViewById(R.id.email_text_layout);
        passError = findViewById(R.id.user_text_layout);
        confirmError = findViewById(R.id.password_register_text_layout);
        mAuth = FirebaseAuth.getInstance();
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValidation();
            }
        });

    }

    public void setValidation() {
        // Check for a valid name.
        if (name.getText().toString().isEmpty()) {
            nameError.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (confirm.getText().toString().isEmpty()) {
            confirmError.setError(getResources().getString(R.string.password_error));
            ispasswordConfirm = false;
        } else if (confirm.getText().length() < 6) {
            confirmError.setError(getResources().getString(R.string.error_invalid_password));
            ispasswordConfirm = false;
        } else if (confirm.getText().toString().equals(password.getText().toString())) {
            ispasswordConfirm = true;
            passError.setErrorEnabled(false);
        }

        if (isNameValid && isEmailValid && isPasswordValid && ispasswordConfirm) {
            UserDetails details = new UserDetails(name.getText().toString(), email.getText().toString(), password.getText().toString());
            mAuth.createUserWithEmailAndPassword(details.getEmail(), details.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in details's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(details.getEmail());
                                Map<String, Object> map = new HashMap<>();
                                map.put("Name", details.getName());
                                map.put("Email", details.getEmail());
                                Map<String, Object> medicinesName = new HashMap<String, Object>();
                                medicinesName.put("Medicines", new ArrayList<String>());
                                map.put("MedicineNames", medicinesName);
                                docRef.set(map).addOnSuccessListener(unused -> {
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
