package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.AddClientActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.adapters.ClientsAdapter;
import com.harsh.businessplus.models.ClientsModel;
import com.harsh.businessplus.models.ProductsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClientsFragment extends Fragment {

    ArrayList<ClientsModel> alClients = new ArrayList<>();
    ArrayList<ClientsModel> alSearchResultClients = new ArrayList<>();
    RecyclerView rvClients;
    ClientsAdapter clientsAdapter;
    ImageButton ibAddClient;
    TextView tvClientsTitle;
    ProgressBar pbClients;
    FirebaseFirestore fbStore;
    public static ArrayList<String> addedClientGST = new ArrayList<>();
    EditText etClientSearch;
    public static ArrayList<String> alBillDocIdsForMonthlyBusiness = new ArrayList<>();
    ArrayList<ProductsModel> alProductAllDetails = new ArrayList<>();
    Double monthlyBusiness=0.;
    Double gstPer=0., gst0=0., gst10=0., gst12=0., gst18=0., gst28=0., total=0., tax=0., price=0.;

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragClients = inflater.inflate(R.layout.fragment_clients, container, false);
        etClientSearch = fragClients.findViewById(R.id.et_client_search);
        pbClients = fragClients.findViewById(R.id.pb_clients);
        rvClients = fragClients.findViewById(R.id.rv_clients);
        ibAddClient = fragClients.findViewById(R.id.ib_add_client);
        tvClientsTitle = fragClients.findViewById(R.id.tv_clients_title);
        rvClients.setLayoutManager(new LinearLayoutManager(fragClients.getContext()));
        clientsAdapter = new ClientsAdapter(fragClients.getContext(), alClients);
        fbStore = FirebaseFirestore.getInstance();
        rvClients.setAdapter(clientsAdapter);
        loadProductDetails();

        rvClients.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ibAddClient.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }else{
                    ibAddClient.setVisibility(View.GONE);
                }
            }
        });

        ibAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(fragClients.getContext(), AddClientActivity.class));
            }
        });

        etClientSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                alSearchResultClients.clear();
                String search = etClientSearch.getText().toString();
                if(!search.isEmpty()){
                    for(ClientsModel client : alClients){
                        if(client.getName().toLowerCase().contains(search.toLowerCase())){
                            alSearchResultClients.add(client);
                        }
                    }
                    clientsAdapter = new ClientsAdapter(requireContext(), alSearchResultClients);
                    clientsAdapter.notifyDataSetChanged();
                    rvClients.setAdapter(clientsAdapter);
                }else{
                    clientsAdapter = new ClientsAdapter(requireContext(), alClients);
                    clientsAdapter.notifyDataSetChanged();
                    rvClients.setAdapter(clientsAdapter);
                }
            }
        });

        return fragClients;
    }

    @Override
    public void onResume() {
        super.onResume();
        alClients.clear();
        addedClientGST.clear();
        GlobalStaticData.alAddedClients.clear();
        refreshClients();
    }

    private void refreshClients() {
        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").get();
        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for(DocumentSnapshot doc : docs){
                    try{
                        alClients.add(new ClientsModel(doc.getString("name"), doc.getString("phone"), doc.getString("address"), doc.getString("state"), doc.getString("gst"), loadMonthlyBusiness(doc.getString("name"))));
                        monthlyBusiness=0.;
                        addedClientGST.add(doc.getString("gst"));
                        GlobalStaticData.alAddedClients.add(doc.getString("name"));
                        pbClients.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }
                }
                clientsAdapter.notifyDataSetChanged();
            }
        });
    }

    public Double loadMonthlyBusiness(String client){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String[] today = sdf.format(Calendar.getInstance().getTime()).split("-");
            String currentMonth = today[0]+"-"+today[1];
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").whereEqualTo("client",client).get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> bills = task.getResult().getDocuments();
                    for(DocumentSnapshot bill : bills){
                        if(bill.getString("date").contains(currentMonth)){
                            alBillDocIdsForMonthlyBusiness.add(bill.getId());
                        }
                    }
                    for(String docId : alBillDocIdsForMonthlyBusiness){
                        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(docId).collection("products").get();
                        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> products = task.getResult().getDocuments();
                                for(DocumentSnapshot product : products){
                                    monthlyBusiness+=getProductTotalWithTaxAmount(product.getString("name"), Double.parseDouble(product.getString("quantity")));
                                }
                            }
                        });
                    }
                    alBillDocIdsForMonthlyBusiness.clear();
                }
            });
            return monthlyBusiness;
        } catch (Exception e) {
            return 0.;
        }
    }
    public Double getProductTotalWithTaxAmount(String product, Double quantity) {
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
            return total+tax;
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