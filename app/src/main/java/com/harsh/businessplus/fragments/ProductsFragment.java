package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.AddProductActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.adapters.ProductsAdapter;
import com.harsh.businessplus.models.ProductsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    RecyclerView rvProducts;
    ProductsAdapter productsAdapter;
    ArrayList<ProductsModel> alProduct = new ArrayList<>();
    ArrayList<ProductsModel> alSearchProductResult = new ArrayList<>();
    FirebaseFirestore fbStore;
    TextView tvProductsTitle;
    ImageButton ibAddProduct;
    ProgressBar pbProducts;
    public static ArrayList<String> addedProductNames =  new ArrayList<>();
    EditText etSearchClient;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragProducts = inflater.inflate(R.layout.fragment_products, container, false);
        try{
            etSearchClient = fragProducts.findViewById(R.id.et_product_search);
            pbProducts = fragProducts.findViewById(R.id.pb_products);
            rvProducts = fragProducts.findViewById(R.id.rv_products);
            tvProductsTitle = fragProducts.findViewById(R.id.tv_products_title);
            ibAddProduct = fragProducts.findViewById(R.id.ib_add_product);
            fbStore = FirebaseFirestore.getInstance();
            rvProducts.setLayoutManager(new GridLayoutManager(fragProducts.getContext(), 2));
            productsAdapter = new ProductsAdapter(fragProducts.getContext(), alProduct);
            rvProducts.setAdapter(productsAdapter);

            rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ibAddProduct.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }else{
                        ibAddProduct.setVisibility(View.GONE);
                    }
                }
            });

            ibAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(fragProducts.getContext(), AddProductActivity.class));
                }
            });

            etSearchClient.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    alSearchProductResult.clear();
                    String search = etSearchClient.getText().toString();
                    if(!search.isEmpty()){
                        for(ProductsModel product : alProduct){
                            if(product.getName().toLowerCase().contains(search.toLowerCase())){
                                alSearchProductResult.add(product);
                            }
                        }
                        productsAdapter = new ProductsAdapter(requireContext(), alSearchProductResult);
                        productsAdapter.notifyDataSetChanged();
                        rvProducts.setAdapter(productsAdapter);
                    }else{
                        productsAdapter = new ProductsAdapter(requireContext(), alProduct);
                        productsAdapter.notifyDataSetChanged();
                        rvProducts.setAdapter(productsAdapter);
                    }
                }
            });

        } catch (Exception e) {
        }
        return fragProducts;
    }

    @Override
    public void onResume() {
        super.onResume();
        alProduct.clear();
        addedProductNames.clear();
        GlobalStaticData.alAddedProducts.clear();
        refreshProducts();
    }

    public void refreshProducts(){
        Task<QuerySnapshot> qs= fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").get();
        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for(DocumentSnapshot doc : docs){
                    alProduct.add(new ProductsModel(doc.getString("name"),doc.getString("hsnsac"),doc.getString("gstpercentage"), Double.parseDouble(doc.getString("price"))));
                    addedProductNames.add(doc.getString("name"));
                    GlobalStaticData.alAddedProducts.add(doc.getString("name"));
                    pbProducts.setVisibility(View.GONE);
                }
                if(alProduct.size()==0){
                    tvProductsTitle.setText("No Products added yet!");
                    tvProductsTitle.setVisibility(View.VISIBLE);
                }
                productsAdapter.notifyDataSetChanged();
            }
        });
    }
}