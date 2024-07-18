package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.CreateBillActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BillNameFragment extends Fragment {

    EditText etBillName;
    Button btnBillDate;
    String billDateToShow="", billDateForDB="", billName;
    FirebaseFirestore fbStore;


    public BillNameFragment() {
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
        View fragBillName =  inflater.inflate(R.layout.fragment_bill_name, container, false);

        try{
            etBillName = fragBillName.findViewById(R.id.et_bill_name);
            btnBillDate = fragBillName.findViewById(R.id.btn_bill_date);
            fbStore = FirebaseFirestore.getInstance();
        } catch (Exception e) {}

        etBillName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etBillName.getText().toString().isEmpty()){
                    CreateBillActivity.validBillName=false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                for(String bill : GlobalStaticData.alAddedBills){
                    if(etBillName.getText().toString().equals(bill)){
                        CreateBillActivity.validBillName = false;
                    }
                }
            }
        });

        btnBillDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String[] today = sdf.format(Calendar.getInstance().getTime()).split("-");
                DatePickerDialog dp = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = (i1+1);
                        billDateForDB = i+"-"+i1+"-"+i2;
                        String[] d = billDateForDB.split("-");
                        if(Integer.parseInt(d[0])<=9){
                            d[0] = "0"+d[0];
                        }
                        if(Integer.parseInt(d[1])<=9){
                            d[1] = "0"+d[1];
                        }
                        if(Integer.parseInt(d[2])<=9){
                            d[2] = "0"+d[2];
                        }
                        billDateForDB = d[0]+"-"+d[1]+"-"+d[2];
                        billDateToShow = d[2]+"-"+d[1]+"-"+d[0];
                        btnBillDate.setText(billDateToShow);
                        billName = etBillName.getText().toString();
                        if(!btnBillDate.getText().toString().endsWith("E")){
                            CreateBillActivity.validBillDate = true;
                            CreateBillActivity.billDate = billDateForDB;
                            if(!billName.contains(billDateToShow)){
                                if(billName.isEmpty()){
                                    etBillName.setText(billDateToShow);
                                }else{
                                    etBillName.setText(billName+"_"+billDateToShow);
                                }
                            }
                            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").whereEqualTo("name", billName).get();
                            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.getResult().size()>0){
                                        CreateBillActivity.validBillName = false;
                                    }else{
                                        CreateBillActivity.validBillName = true;
                                        CreateBillActivity.billName = etBillName.getText().toString();
                                    }
                                }
                            });
                        }
                    }
                }, Integer.parseInt(today[0]), Integer.parseInt((today[1]))-1, Integer.parseInt(today[2]));
                dp.show();
            }
        });

        return fragBillName;
    }
}