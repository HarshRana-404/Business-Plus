package com.harsh.businessplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.adapters.BillProductAdapter;
import com.harsh.businessplus.adapters.CreateBillViewPagerAdapter;
import com.harsh.businessplus.fragments.BillClientFragment;
import com.harsh.businessplus.fragments.BillNameFragment;
import com.harsh.businessplus.fragments.BillProductsFragment;
import com.harsh.businessplus.models.BillProductModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateBillActivity extends AppCompatActivity {

    ViewPager vp;
    CreateBillViewPagerAdapter createBillAdapter;
    FloatingActionButton fabVPNext, fabVPPrev;
    Button btnCreateBill;
    public static Boolean validClient = false;
    public static Boolean validBillName = true;
    public static Boolean validBillDate = false;
    public static ArrayList<BillProductModel> alProductAddedForBill = new ArrayList<>();
    public static String billName = "", billDate = "", billClient="";
    HashMap<String, String> hmBillProducts = new HashMap<>();
    FirebaseFirestore fbStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);

        vp = findViewById(R.id.vp);
        fabVPNext = findViewById(R.id.btn_vp_next);
        fabVPPrev = findViewById(R.id.btn_vp_prev);
        btnCreateBill = findViewById(R.id.btn_create_bill);
        createBillAdapter = new CreateBillViewPagerAdapter(getSupportFragmentManager());
        createBillAdapter.add(new BillClientFragment());
        createBillAdapter.add(new BillNameFragment());
        createBillAdapter.add(new BillProductsFragment());
        fbStore = FirebaseFirestore.getInstance();
        vp.setAdapter(createBillAdapter);

        vp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        fabVPNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseViewPagerIndex();
            }
        });
        fabVPPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseViewPagerIndex();
            }
        });
        btnCreateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BillProductsFragment.hasItems){
                    alProductAddedForBill = BillProductAdapter.alProduct;
                    if(!billName.isEmpty() && !billClient.isEmpty()){
                        hmBillProducts.put("name", billName);
                        hmBillProducts.put("client", billClient);
                        hmBillProducts.put("date", billDate);
                        fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document().set(hmBillProducts).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").whereEqualTo("name", billName).get();
                                qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                        if(docs.size()==1){
                                            String docID = docs.get(0).getId();
                                            BillPreviewActivity.billDocID = docID;
                                            startActivity(new Intent(CreateBillActivity.this, BillPreviewActivity.class));
                                            for(BillProductModel product : alProductAddedForBill){
                                                HashMap<String, String> hmProduct = new HashMap<>();
                                                hmProduct.put("name", product.getName());
                                                hmProduct.put("quantity", product.getQuantity().toString());
                                                fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(docID).collection("products").document().set(hmProduct);
                                            }
                                            Toast.makeText(CreateBillActivity.this, "Bill creation successful!", Toast.LENGTH_SHORT).show();
                                            BillProductsFragment.hasItems=false;
                                        }
                                    }
                                });
                            }
                        });
                    }
                    finish();
                }else{
                    Toast.makeText(CreateBillActivity.this, "Add Products to create a bill!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));

    }

    public void increaseViewPagerIndex(){
        if(vp.getCurrentItem()==0){
            if(validClient){
                vp.setCurrentItem(vp.getCurrentItem()+1);
                updateNextPrevButtons();
            }else{
                Toast.makeText(this, "Enter valid Client name!", Toast.LENGTH_SHORT).show();
            }
        }else if(vp.getCurrentItem()==1){
            if(validBillDate){
                if(validBillName){
                    vp.setCurrentItem(vp.getCurrentItem()+1);
                    updateNextPrevButtons();
                }else{
                    Toast.makeText(this, "Bill with given name already exists!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Select a Bill date!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void decreaseViewPagerIndex(){
        vp.setCurrentItem(vp.getCurrentItem()-1);
        updateNextPrevButtons();
    }
    public void updateNextPrevButtons(){
        if(vp.getCurrentItem()==0){
            fabVPPrev.setEnabled(false);
            fabVPPrev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_bg_opposite)));
            btnCreateBill.setVisibility(View.GONE);
            fabVPNext.setVisibility(View.VISIBLE);
        }else if(vp.getCurrentItem()==2){
            fabVPNext.setEnabled(false);
            fabVPNext.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_bg_opposite)));
            fabVPNext.setVisibility(View.GONE);
            btnCreateBill.setVisibility(View.VISIBLE);
        }else{
            fabVPNext.setEnabled(true);
            fabVPPrev.setEnabled(true);
            fabVPPrev.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
            fabVPNext.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
            btnCreateBill.setVisibility(View.GONE);
            fabVPNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        validBillName = true;
        validClient = false;
        validBillDate = false;
        vp.setCurrentItem(0);
        updateNextPrevButtons();
    }
    @Override
    protected void onResume() {
        super.onResume();
        vp.setCurrentItem(0);
        updateNextPrevButtons();
    }
}