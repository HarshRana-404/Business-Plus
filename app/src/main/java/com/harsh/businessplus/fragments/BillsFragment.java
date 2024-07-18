package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.BillPreviewActivity;
import com.harsh.businessplus.CreateBillActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.adapters.BillsAdapter;
import com.harsh.businessplus.models.BillProductModel;
import com.harsh.businessplus.models.BillsModel;
import com.harsh.businessplus.models.ProductsModel;
import com.harsh.businessplus.models.TotalsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BillsFragment extends Fragment {

    ImageButton ibAddNewBill;
    ProgressBar pbBills;
    FirebaseFirestore fbStore;
    RecyclerView rvBills;
    ArrayList<BillsModel> alBills = new ArrayList<>();
    ArrayList<BillsModel> alSearchResultBills = new ArrayList<>();
    BillsAdapter billsAdapter;
    Double gst0 = 0., gst10 = 0., gst12 = 0., gst18 = 0., gst28 = 0.;
    Double tax = 0., price = 0., total = 0., gstPer = 0., grandTotal = 0., temp = 0.;
    ArrayList<ProductsModel> alProductAllDetails = new ArrayList<>();
    public static RelativeLayout rlBillsActionBar;
    public static TextView tvSelectionCount, tvDeleteSelection;
    EditText etBillsSearch;

    public BillsFragment() {
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
        View fragBills = inflater.inflate(R.layout.fragment_bills, container, false);
        try {
            etBillsSearch = fragBills.findViewById(R.id.et_bill_search);
            pbBills = fragBills.findViewById(R.id.pb_bills);
            ibAddNewBill = fragBills.findViewById(R.id.ib_add_bill);
            rvBills = fragBills.findViewById(R.id.rv_bills);
            rlBillsActionBar = fragBills.findViewById(R.id.rl_bills_action_bar);
            tvSelectionCount = fragBills.findViewById(R.id.tv_bills_action_bar_selection_count);
            tvDeleteSelection = fragBills.findViewById(R.id.tv_bills_action_bar_delete);
            billsAdapter = new BillsAdapter(requireContext(), alBills);
            rvBills.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvBills.setAdapter(billsAdapter);
            fbStore = FirebaseFirestore.getInstance();
            loadProductDetails();

            rvBills.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ibAddNewBill.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }else{
                        ibAddNewBill.setVisibility(View.GONE);
                    }
                }
            });

            ibAddNewBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(requireContext(), CreateBillActivity.class));
                }
            });
        } catch (Exception e) {

        }

        etBillsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                String search=etBillsSearch.getText().toString();
                if(!search.isEmpty()){
                    try{
                        alSearchResultBills.clear();
                        for(BillsModel bill : alBills){
                            if(bill.getName().toLowerCase().contains(search.toLowerCase())){
                                alSearchResultBills.add(bill);
                            }
                        }
                        billsAdapter = new BillsAdapter(requireContext(), alSearchResultBills);
                        rvBills.setAdapter(billsAdapter);
                        billsAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                }else{
                    billsAdapter = new BillsAdapter(requireContext(), alBills);
                    rvBills.setAdapter(billsAdapter);
                    billsAdapter.notifyDataSetChanged();
                }
            }
        });
        return fragBills;
    }

    public void refreshBills() {
        try {
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(doc.getId()).collection("products").get();
                        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                try{
                                    List<DocumentSnapshot> pDocs = task.getResult().getDocuments();
                                    for (DocumentSnapshot pDoc : pDocs) {
                                        temp = getProductTotalWithoutTaxAmount(pDoc.getString("name"), Double.parseDouble(pDoc.getString("quantity")));
                                        grandTotal += temp;
                                    }
                                    grandTotal += (gst0 + gst10 + gst12 + gst18 + gst28);
                                    alBills.add(new BillsModel(doc.getString("name"), grandTotal, doc.getString("client"), doc.getString("date"), doc.getId()));
                                    grandTotal=0.;
                                    gst0=0.;
                                    gst10=0.;
                                    gst12=0.;
                                    gst18=0.;
                                    gst28=0.;
                                    billsAdapter.notifyDataSetChanged();
                                    alBills.sort(Comparator.comparing(BillsModel::getDate).reversed());
                                    pbBills.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        alBills.clear();
        refreshBills();
    }

    public Double getProductTotalWithoutTaxAmount(String product, Double quantity) {
        try {
            for (ProductsModel p : alProductAllDetails) {
                if (p.getName().equals(product)) {
                    gstPer = Double.parseDouble(p.getGstPercentage().replaceAll("%", ""));
                    price = p.getPrice();
                    total = price * quantity;
                    tax = (total * gstPer) / 100;
                    if (gstPer == 0.) {
                        gst0 += tax;
                    } else if (gstPer == 10.0) {
                        gst10 += tax;
                    } else if (gstPer == 12.0) {
                        gst12 += tax;
                    } else if (gstPer == 18.0) {
                        gst18 += tax;
                    } else if (gstPer == 28.0) {
                        gst28 += tax;
                    }
                }
            }
            return total;
        } catch (Exception e) {
            return 0.;
        }
    }

    public void loadProductDetails() {
        try {
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        alProductAllDetails.add(new ProductsModel(doc.getString("name"), doc.getString("hsnsac"), doc.getString("gstpercentage"), Double.parseDouble(doc.getString("price"))));
                    }
                }
            });
        } catch (Exception e) {

        }
    }
}