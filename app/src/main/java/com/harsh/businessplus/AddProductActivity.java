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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.fragments.ProductsFragment;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    TextView tvAddProductTitle;
    ImageButton ibProductBack;
    EditText etProductName, etProductHsnSac, etProductPrice;
    Spinner spProductGSTPercentage;
    String pName="", pHsnSac="", pGSTPercentage="", pPrice="";

    HashMap<String, String> hmProduct = new HashMap<>();
    Button btnAddProduct, btnAddProductCancel;
    FirebaseFirestore fbStore;

    public static Boolean editProductDetails = false;
    public static String productLookUpName = "";
    public static String productLookUpID = "";

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));

        ibProductBack = findViewById(R.id.ib_product_back);
        tvAddProductTitle = findViewById(R.id.tv_add_product_title);
        etProductName = findViewById(R.id.et_product_name);
        etProductHsnSac = findViewById(R.id.et_product_hsn_sac);
        etProductPrice = findViewById(R.id.et_product_price);
        spProductGSTPercentage = findViewById(R.id.sp_product_gst_percentage);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnAddProductCancel = findViewById(R.id.btn_add_product_cancel);
        fbStore = FirebaseFirestore.getInstance();

        if(editProductDetails){
            Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").whereEqualTo("name", productLookUpName).get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot docs = task.getResult();
                    for(DocumentSnapshot doc : docs){
                        productLookUpID = doc.getId();
                        etProductName.setText(doc.getString("name"));
                        etProductPrice.setText(doc.getString("price"));
                        etProductHsnSac.setText(doc.getString("hsnsac"));
                        String[] gstPers = getResources().getStringArray(R.array.gst_percentages);
                        for(int i=0;i<gstPers.length;i++){
                            if(gstPers[i].equals(doc.getString("gstpercentage"))){
                                spProductGSTPercentage.setSelection(i);
                            }
                        }
                    }
                }
            });
            tvAddProductTitle.setText("Edit Product Details:");
            btnAddProduct.setText("SAVE CHANGES");
            btnAddProductCancel.setText("DELETE THIS PRODUCT");
        }

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean productAlreadyExists = false;
                if(validDetails()){
                    for(String name : ProductsFragment.addedProductNames){
                        if(pName.equals(name)){
                            productAlreadyExists = true;
                        }
                    }
                    if(editProductDetails){
                        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").whereEqualTo("name", pName).get();
                        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot docs = task.getResult();
                                if(docs.size()==1){
                                    for(DocumentSnapshot doc : docs){
                                        if(doc.getId().equals(productLookUpID)){
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("name", pName);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("gstpercentage", pGSTPercentage);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("hsnsac", pHsnSac);
                                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("price", pPrice);
                                            Toast.makeText(AddProductActivity.this, "Product details updated successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(AddProductActivity.this, "Product with this name already exists!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else{
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("name", pName);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("gstpercentage", pGSTPercentage);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("hsnsac", pHsnSac);
                                    fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).update("price", pPrice);
                                    Toast.makeText(AddProductActivity.this, "Product details updated successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }else{
                        if(!productAlreadyExists){
                            Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").add(hmProduct);
                            finish();
                        }else{
                            Toast.makeText(AddProductActivity.this, "Product with this name already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddProductActivity.this, "Enter all required details correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddProductCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editProductDetails){
                    AlertDialog.Builder adb = new AlertDialog.Builder(AddProductActivity.this);
                    AlertDialog ad = adb.create();
                    adb.setTitle("Delete this product?");
                    adb.setMessage("Are you sure you want to delete this product?");
                    adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            productLookUpName="";
                            editProductDetails = false;
                            fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").document(productLookUpID).delete();
                            Toast.makeText(AddProductActivity.this, "Product deleted Successfully!", Toast.LENGTH_SHORT).show();
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
                    productLookUpName="";
                    finish();
                }
            }
        });

        ibProductBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public Boolean validDetails(){
        pName = etProductName.getText().toString().trim();
        pHsnSac = etProductHsnSac.getText().toString().trim();
        pGSTPercentage = spProductGSTPercentage.getSelectedItem().toString();
        pPrice = etProductPrice.getText().toString().trim();
        if(!pName.isEmpty() && !pHsnSac.isEmpty() && !pGSTPercentage.equals("Select GST%")){
            hmProduct.put("name", pName);
            hmProduct.put("hsnsac", pHsnSac);
            hmProduct.put("price", pPrice);
            hmProduct.put("gstpercentage", pGSTPercentage);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        productLookUpName="";
        productLookUpID="";
    }
}