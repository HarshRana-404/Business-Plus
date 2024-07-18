package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.R;
import com.harsh.businessplus.staticdata.GlobalStaticData;

public class ProfileFragment extends Fragment {
    
    EditText etBusinessName, etBusinessPhone, etBusinessAddress, etBusinessGST;
    Button btnSaveChanges;
    Spinner spBusinessState;
    String bName, bPhone, bAddress, bState, bGST;
    FirebaseFirestore fbStore;
    ScrollView svProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragProfile = inflater.inflate(R.layout.fragment_profile, container, false);
        try{
            svProfile = fragProfile.findViewById(R.id.sv_profile);
            etBusinessName = fragProfile.findViewById(R.id.et_profile_business_name);
            etBusinessPhone = fragProfile.findViewById(R.id.et_profile_business_phone);
            etBusinessAddress = fragProfile.findViewById(R.id.et_profile_business_address);
            etBusinessGST = fragProfile.findViewById(R.id.et_profile_business_gst);
            spBusinessState = fragProfile.findViewById(R.id.sp_profile_business_state);
            btnSaveChanges = fragProfile.findViewById(R.id.btn_profile_business_save_changes);
            fbStore = FirebaseFirestore.getInstance();
        } catch (Exception e) {}

        populateData();

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validDetails()){
                    fbStore.collection("business").document(GlobalStaticData.getUID()).update("name", bName);
                    fbStore.collection("business").document(GlobalStaticData.getUID()).update("phone", bPhone);
                    fbStore.collection("business").document(GlobalStaticData.getUID()).update("address", bAddress);
                    fbStore.collection("business").document(GlobalStaticData.getUID()).update("state", bState);
                    fbStore.collection("business").document(GlobalStaticData.getUID()).update("gst", bGST);
                    populateData();
                    Toast.makeText(requireContext(), "All changes saved!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "Provide all details to save changes!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        return fragProfile;
    }
    public Boolean validDetails(){
        bName = etBusinessName.getText().toString().trim();
        bPhone = etBusinessPhone.getText().toString().trim();
        bAddress = etBusinessAddress.getText().toString().trim();
        bGST = etBusinessGST.getText().toString().trim();
        bState = spBusinessState.getSelectedItem().toString().trim();
        if(!bName.isEmpty() && !bPhone.isEmpty() && !bAddress.isEmpty() && !bGST.isEmpty() && !bState.equals("Select State")){
            return true;
        }
        return false;
    }
    public void populateData(){
        try{
            Task<DocumentSnapshot> docs = fbStore.collection("business").document(GlobalStaticData.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    try{
                        DocumentSnapshot doc = task.getResult();
                        etBusinessName.setText(doc.getString("name"));
                        etBusinessPhone.setText(doc.getString("phone"));
                        etBusinessAddress.setText(doc.getString("address"));
                        etBusinessGST.setText(doc.getString("gst"));
                        String[] states = getResources().getStringArray(R.array.states);
                        for(int i=0;i<states.length;i++){
                            if(states[i].equals(doc.getString("state"))){
                                spBusinessState.setSelection(i);
                            }
                        }
                        svProfile.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }
                }
            });

        } catch (Exception e) {
        }
    }
}