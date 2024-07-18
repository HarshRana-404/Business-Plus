package com.harsh.businessplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etBName, etBEmail, etPassword, etBPhone, etBAddress, etBGST;
    Spinner spBState;
    Button btnRegister;
    String bName="", bEmail="", password="", bPhone="", bAddress="", bState="", bGST="", fbUID="";
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;
    HashMap<String, String> hmBusinessDetails = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));

        etBName = findViewById(R.id.et_business_name);
        etBEmail = findViewById(R.id.et_business_email);
        etPassword = findViewById(R.id.et_password);
        etBPhone = findViewById(R.id.et_business_phone);
        etBAddress = findViewById(R.id.et_business_address);
        spBState = findViewById(R.id.sp_business_state);
        etBGST = findViewById(R.id.et_business_gst);
        btnRegister = findViewById(R.id.btn_business_register);
        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        fbAuth.signOut();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validDetails()){
                    try{
                        fbAuth.createUserWithEmailAndPassword(bEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    fbUID = fbAuth.getUid();
                                    fbStore.collection("business").document(fbUID).set(hmBusinessDetails);
                                    Toast.makeText(RegisterActivity.this, "Business registered successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(RegisterActivity.this, "Something went wrong! Check every details before registration", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Provide every details for registration!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Boolean validDetails() {
        try{
            bName = etBName.getText().toString().trim();
            bEmail = etBEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            bPhone = etBPhone.getText().toString().trim();
            bAddress = etBAddress.getText().toString().trim();
            bState = spBState.getSelectedItem().toString().trim();
            bGST = etBGST.getText().toString().trim();
            if(!bName.isEmpty() && !bEmail.isEmpty() && !password.isEmpty() && !bPhone.isEmpty() && !bAddress.isEmpty() && !bState.equals("Select State") && !bGST.isEmpty()){
                hmBusinessDetails.put("name", bName);
                hmBusinessDetails.put("email", bEmail);
                hmBusinessDetails.put("phone", bPhone);
                hmBusinessDetails.put("address", bAddress);
                hmBusinessDetails.put("state", bState);
                hmBusinessDetails.put("gst", bGST);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}