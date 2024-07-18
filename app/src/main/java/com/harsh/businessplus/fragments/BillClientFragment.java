package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.CreateBillActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BillClientFragment extends Fragment {

    AutoCompleteTextView actvClientName;
    FirebaseFirestore fbStore;
    ArrayList<String> alClients = new ArrayList<>();
    ProgressBar pbClients;

    public BillClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragBillClient = inflater.inflate(R.layout.fragment_bill_client, container, false);

        try{
            actvClientName = fragBillClient.findViewById(R.id.actv_bill_client_name);
            pbClients = fragBillClient.findViewById(R.id.pb_bill_clients);
            pbClients.setVisibility(View.VISIBLE);
            fbStore = FirebaseFirestore.getInstance();
            actvClientName.setEnabled(false);

            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try{
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        for(DocumentSnapshot doc : docs){
                            alClients.add(doc.getString("name"));
                        }
                        Collections.sort(alClients);
                        actvClientName.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, alClients));
                        actvClientName.showDropDown();
                        actvClientName.setEnabled(true);
                        pbClients.setVisibility(View.GONE);
                    } catch (Exception e) {}
                }
            });

        } catch (Exception e) {}

        actvClientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CreateBillActivity.validClient = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                for(String client : alClients){
                    if(actvClientName.getText().toString().equals(client)){
                        CreateBillActivity.validClient = true;
                        CreateBillActivity.billClient = actvClientName.getText().toString();
                    }
                }
            }
        });
        actvClientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvClientName.showDropDown();
            }
        });
        return fragBillClient;
    }
}