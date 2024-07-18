package com.harsh.businessplus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harsh.businessplus.adapters.BillsAdapter;
import com.harsh.businessplus.fragments.BillsFragment;
import com.harsh.businessplus.fragments.ClientsFragment;
import com.harsh.businessplus.fragments.ProductsFragment;
import com.harsh.businessplus.fragments.ProfileFragment;
import com.harsh.businessplus.models.ProductsModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout llBills, llClients, llProducts, llProfile;
    ImageView ivBills, ivClients, ivProducts, ivProfile;
    TextView tvBills, tvClients, tvProducts, tvProfile;
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;
    int lastNavigatedIndex = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llBills = findViewById(R.id.ll_nav_bills);
        llClients = findViewById(R.id.ll_nav_clients);
        llProducts = findViewById(R.id.ll_nav_products);
        llProfile = findViewById(R.id.ll_nav_profile);
        ivBills = findViewById(R.id.iv_nav_bills);
        ivClients = findViewById(R.id.iv_nav_clients);
        ivProducts = findViewById(R.id.iv_nav_products);
        ivProfile = findViewById(R.id.iv_nav_profile);
        tvBills = findViewById(R.id.tv_nav_bills);
        tvClients = findViewById(R.id.tv_nav_clients);
        tvProducts = findViewById(R.id.tv_nav_products);
        tvProfile = findViewById(R.id.tv_nav_profile);
        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        if(fbAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        navigateTo(0);

        llBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(0);
            }
        });
        llClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(1);
            }
        });
        llProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(2);
            }
        });
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(3);
            }
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void navigateTo(int navIndex){

        try{
            if(lastNavigatedIndex!=navIndex){
                Fragment fragToBeSelected = null;
                tvBills.setTextSize(18);
                tvClients.setTextSize(18);
                tvProducts.setTextSize(18);
                tvProfile.setTextSize(18);
                tvBills.setTextColor(getResources().getColor(R.color.main_bg_opposite));
                tvClients.setTextColor(getResources().getColor(R.color.main_bg_opposite));
                tvProducts.setTextColor(getResources().getColor(R.color.main_bg_opposite));
                tvProfile.setTextColor(getResources().getColor(R.color.main_bg_opposite));
                ivBills.setImageDrawable(getResources().getDrawable(R.drawable.ic_bill_unfocused));
                ivClients.setImageDrawable(getResources().getDrawable(R.drawable.ic_client_unfocused));
                ivProducts.setImageDrawable(getResources().getDrawable(R.drawable.ic_product_unfocused));
                ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_unfocused));
                switch (navIndex){
                    case 0:
                        tvBills.setTextSize(20);
                        tvBills.setTextColor(getResources().getColor(R.color.main_color));
                        ivBills.setImageDrawable(getResources().getDrawable(R.drawable.ic_bill_focused));
                        fragToBeSelected = new BillsFragment();
                        break;
                    case 1:
                        tvClients.setTextSize(20);
                        tvClients.setTextColor(getResources().getColor(R.color.main_color));
                        ivClients.setImageDrawable(getResources().getDrawable(R.drawable.ic_client_focused));
                        fragToBeSelected = new ClientsFragment();
                        break;
                    case 2:
                        tvProducts.setTextSize(20);
                        tvProducts.setTextColor(getResources().getColor(R.color.main_color));
                        ivProducts.setImageDrawable(getResources().getDrawable(R.drawable.ic_product_focused));
                        fragToBeSelected = new ProductsFragment();
                        break;
                    case 3:
                        tvProfile.setTextSize(20);
                        tvProfile.setTextColor(getResources().getColor(R.color.main_color));
                        ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_focused));
                        fragToBeSelected = new ProfileFragment();
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fl_fragments, fragToBeSelected);
                ft.commit();
                lastNavigatedIndex = navIndex;
            }
        } catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
        if(lastNavigatedIndex==0 && BillsAdapter.isLongPressed){
            BillsAdapter.isLongPressed=false;
            lastNavigatedIndex = -1;
            navigateTo(0);

        }else{
            super.onBackPressed();
        }
    }
}