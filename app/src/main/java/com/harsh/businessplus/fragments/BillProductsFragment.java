package com.harsh.businessplus.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.CreateBillActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.adapters.BillProductAdapter;
import com.harsh.businessplus.models.BillProductModel;
import com.harsh.businessplus.models.ProductsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillProductsFragment extends Fragment {

    AutoCompleteTextView actvProductName;
    EditText etPrice, etHsnSac, etQuantity;
    Spinner spGstPercentage;
    Button btnAddProduct;
    FirebaseFirestore fbStore;
    ArrayList<String> alProducts = new ArrayList<>();
    String productName="";
    ArrayList<ProductsModel> alProductDetails = new ArrayList<>();
    ArrayList<BillProductModel> alProductAdded = new ArrayList<>();
    BillProductAdapter adapterProductsAdded;
    HashMap<String, String> hmProduct = new HashMap<>();
    String pName, pHsnSac, pGSTPercentage, pPrice, pQuantity;
    RecyclerView rvProductsAdded;
    static RelativeLayout rlProductsHeader;
    public static Boolean hasItems = false;

    public BillProductsFragment() {
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
        View fragBillProducts = inflater.inflate(R.layout.fragment_bill_products, container, false);

        try{
            fbStore = FirebaseFirestore.getInstance();
            actvProductName = fragBillProducts.findViewById(R.id.actv_bill_product_name);
            etPrice = fragBillProducts.findViewById(R.id.et_bill_product_price);
            etHsnSac = fragBillProducts.findViewById(R.id.et_bill_product_hsn_sac);
            etQuantity = fragBillProducts.findViewById(R.id.et_bill_product_quantity);
            btnAddProduct = fragBillProducts.findViewById(R.id.btn_add_bill_product);
            spGstPercentage = fragBillProducts.findViewById(R.id.sp_bill_product_gst_percentage);
            rlProductsHeader = fragBillProducts.findViewById(R.id.rl_bill_product_table_header);
            rvProductsAdded = fragBillProducts.findViewById(R.id.rv_bill_products);

            rvProductsAdded.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapterProductsAdded = new BillProductAdapter(requireContext(), alProductAdded);
            rvProductsAdded.setAdapter(adapterProductsAdded);
            BillProductAdapter.hideDeleteIB = false;

            actvProductName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actvProductName.showDropDown();
                }
            });
            btnAddProduct.setVisibility(View.GONE);

            actvProductName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
                    productName = actvProductName.getText().toString();
                    if(productName.equals("")) {
                        btnAddProduct.setVisibility(View.GONE);
                    }else {
                        btnAddProduct.setVisibility(View.VISIBLE);
                    }
                    Boolean found=false;
                    for(int i=0;i<alProducts.size();i++){
                        if(alProducts.get(i).equals(productName)){
                            found = true;
                            etPrice.setText(alProductDetails.get(i).getPrice().toString());
                            etHsnSac.setText(alProductDetails.get(i).getHsnSac().toString());
                            String[] pers = getResources().getStringArray(R.array.gst_percentages);
                            for(int j=0;j<pers.length;j++){
                                if(pers[j].equals(alProductDetails.get(i).getGstPercentage())){
                                    spGstPercentage.setSelection(j);
                                }
                            }
                            etPrice.setEnabled(false);
                            etHsnSac.setEnabled(false);
                            spGstPercentage.setEnabled(false);
                            btnAddProduct.setText("ADD");
                            btnAddProduct.setVisibility(View.VISIBLE);
                        }
                    }
                    if(!found){
                        etPrice.setEnabled(true);
                        etHsnSac.setEnabled(true);
                        spGstPercentage.setEnabled(true);
                        etPrice.setText("");
                        etHsnSac.setText("");
                        spGstPercentage.setSelection(0);
                        btnAddProduct.setText("ADD AS NEW PRODUCT");
                    }
                }
            });

            btnAddProduct.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    if(btnAddProduct.getText().toString().equals("ADD")){
                        if(validDetails() && !pQuantity.isEmpty()){
                            alProductAdded.add(new BillProductModel(pName,Double.parseDouble(pQuantity), pGSTPercentage, Double.parseDouble(pPrice)));
                            rlProductsHeader.setVisibility(View.VISIBLE);
                            adapterProductsAdded.notifyDataSetChanged();
                            actvProductName.setText("");
                            etQuantity.setText("");
                            InputMethodManager imm = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(fragBillProducts.getWindowToken(), 0);
                        }else{
                            if(pQuantity.isEmpty()){
                                Toast.makeText(requireContext(), "Enter product's quantity!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        if(validDetailsForAddingNewProduct()){
                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").add(hmProduct);
                            refreshACTV();
                        }else{
                            Toast.makeText(requireContext(), "Enter all product details!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } catch (Exception e) {

        }
        return fragBillProducts;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            refreshACTV();
        }
    }

    private void refreshACTV() {
        try{
            alProducts.clear();
            alProductDetails.clear();
            actvProductName.setText("");
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        alProducts.add(doc.getString("name"));
                        alProductDetails.add(new ProductsModel(doc.getString("name"), doc.getString("hsnsac"), doc.getString("gstpercentage"), Double.parseDouble(doc.getString("price"))));
                    }
                    actvProductName.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, alProducts));
                    actvProductName.showDropDown();
                }
            });
        } catch (Exception e) {
        }
    }

    public Boolean validDetails(){
        pName = actvProductName.getText().toString().trim();
        pHsnSac = etHsnSac.getText().toString().trim();
        pGSTPercentage = spGstPercentage.getSelectedItem().toString();
        pPrice = etPrice.getText().toString().trim();
        pQuantity = etQuantity.getText().toString().trim();
        if(!pName.isEmpty() && !pHsnSac.isEmpty() && !pQuantity.isEmpty() && !pGSTPercentage.equals("Select GST%")){
            hasItems = true;
            return true;
        }
        return false;
    }
    public Boolean validDetailsForAddingNewProduct(){
        pName = actvProductName.getText().toString().trim();
        pHsnSac = etHsnSac.getText().toString().trim();
        pGSTPercentage = spGstPercentage.getSelectedItem().toString();
        pPrice = etPrice.getText().toString().trim();
        if(!pName.isEmpty() && !pHsnSac.isEmpty() && !pGSTPercentage.equals("Select GST%")){
            hmProduct.put("name", pName);
            hmProduct.put("hsnsac", pHsnSac);
            hmProduct.put("price", pPrice);
            hmProduct.put("gstpercentage", pGSTPercentage);
            return true;
        }
        return false;
    }
    public static void hideRlTableHeader(){
        rlProductsHeader.setVisibility(View.GONE);
    }
}