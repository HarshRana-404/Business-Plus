package com.harsh.businessplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.R;
import com.harsh.businessplus.fragments.ClientsFragment;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class AddClientActivity extends AppCompatActivity {
    EditText etClientBusinessName, etClientPhone, etClientAddress, etClientGST;
    TextView tvAddClienttitle;
    ImageButton ibClientBack;
    Spinner spClientState;
    FirebaseFirestore fbStore;
    HashMap<String, String> hmClientData = new HashMap<>();
    String cBusinessName, cPhone, cAddress, cState, cGST;
    Button btnAddClient, btnAddClientCancel;
    public static Boolean editClientDetails = false;
    public static String lookUpClientGST = "";
    public static String lookUpClientDocumentID = "";

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));

        ibClientBack = findViewById(R.id.ib_client_back);
        etClientBusinessName = findViewById(R.id.et_client_business_name);
        etClientPhone = findViewById(R.id.et_client_business_phone);
        etClientAddress = findViewById(R.id.et_client_business_address);
        etClientGST = findViewById(R.id.et_client_business_gst);
        tvAddClienttitle = findViewById(R.id.tv_add_client_title);
        spClientState = findViewById(R.id.sp_client_business_state);
        btnAddClient = findViewById(R.id.btn_add_client);
        btnAddClientCancel = findViewById(R.id.btn_add_client_cancel);

        fbStore = FirebaseFirestore.getInstance();

        if(editClientDetails){
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").whereEqualTo("gst", lookUpClientGST).get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot docs = task.getResult();
                    for(DocumentSnapshot doc : docs){
                        lookUpClientDocumentID = doc.getId();
                        etClientBusinessName.setText(doc.getString("name"));
                        etClientPhone.setText(doc.getString("phone"));
                        etClientAddress.setText(doc.getString("address"));
                        etClientGST.setText(doc.getString("gst"));
                        String states[] = getResources().getStringArray(R.array.states);
                        for(int i=0;i<states.length;i++){
                            if(doc.getString("state").equals(states[i])){
                                spClientState.setSelection(i);
                            }
                        }
                    }
                }
            });
            tvAddClienttitle.setText("Edit Client Details:");
            btnAddClient.setText("SAVE CHANGES");
            btnAddClientCancel.setText("DELETE CLIENT");
        }

        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validDetails()){
                    Boolean GSTAlreadyExists=false;
                    for(String gst : ClientsFragment.addedClientGST){
                        if(gst.equals(cGST)){
                            GSTAlreadyExists = true;
                        }
                    }
                    if(editClientDetails){

                        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").whereEqualTo("gst", cGST).get();
                        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot docs = task.getResult();
                                if(docs.size()==1){
                                    for(DocumentSnapshot doc : docs){
                                        if(doc.getId().equals(lookUpClientDocumentID)){
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("name", cBusinessName);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("phone", cPhone);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("address", cAddress);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("gst", cGST);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("state", cState);
                                            Toast.makeText(AddClientActivity.this, "Client details saved!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(AddClientActivity.this, "Client with this GSTIN already added!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else{
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("name", cBusinessName);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("phone", cPhone);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("address", cAddress);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("gst", cGST);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).update("state", cState);
                                    Toast.makeText(AddClientActivity.this, "Client details saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }else{
                        if(!GSTAlreadyExists){
                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").add(hmClientData);
                            Toast.makeText(AddClientActivity.this, "Client added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(AddClientActivity.this, "Client with this GSTIN already added!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddClientActivity.this, "Please enter all required details!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddClientCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editClientDetails){
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddClientActivity.this);
                    AlertDialog ad = adb.create();
                    adb.setTitle("Delete this client?");
                    adb.setMessage("Are you sure you want to delete this client?");
                    adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editClientDetails=false;
                            lookUpClientGST="";
                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").document(lookUpClientDocumentID).delete();
                            Toast.makeText(AddClientActivity.this, "Client deleted Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad.dismiss();
                        }
                    });
                    adb.show();
                }else{
                    editClientDetails=false;
                    lookUpClientGST="";
                    finish();
                }
            }
        });

        ibClientBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public Boolean validDetails(){
        try{
            cBusinessName = etClientBusinessName.getText().toString().trim();
            cPhone = etClientPhone.getText().toString().trim();
            cAddress = etClientAddress.getText().toString().trim();
            cState = spClientState.getSelectedItem().toString();
            cGST = etClientGST.getText().toString().trim();
            if(!cBusinessName.isEmpty() && !cPhone.isEmpty() && !cAddress.isEmpty() && !cState.equals("Select State") && !cGST.isEmpty()){
                hmClientData.put("name", cBusinessName);
                hmClientData.put("phone", cPhone);
                hmClientData.put("address", cAddress);
                hmClientData.put("state", cState);
                hmClientData.put("gst", cGST);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editClientDetails=false;
        lookUpClientGST="";
    }
}