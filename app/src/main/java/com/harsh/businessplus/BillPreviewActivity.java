package com.harsh.businessplus;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.businessplus.adapters.BillProductAdapter;
import com.harsh.businessplus.models.BillProductModel;
import com.harsh.businessplus.models.ProductsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;
import java.util.List;

public class BillPreviewActivity extends AppCompatActivity {

    RecyclerView rvProductsAdded;
    ArrayList<BillProductModel> alProductAdded = new ArrayList<>();
    BillProductAdapter adapterProductsAdded;
    public static String billDocID="";
    FirebaseFirestore fbStore;
    TextView tvBillName, tvClientName, tvClientGST, tvClientAddress, tvClientState, tvBillDate, tvSubTotal, tvGstTotal, tvGrandTotal, tvTotalInWords;
    ImageButton ibBack;
    Button btnDeleteBill;
    RelativeLayout rlTotals;
    ProgressBar pbBillPreview;
    public static Double subTotal=0., gstTotal=0., grandTotal=0.;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_preview);

        try{
            rvProductsAdded = findViewById(R.id.rv_bill_preview_products);
            ibBack = findViewById(R.id.ib_bill_preview_back);
            btnDeleteBill = findViewById(R.id.btn_bill_preview_delete_bill);
            rlTotals = findViewById(R.id.rl_bill_preview_totals);
            tvBillName = findViewById(R.id.tv_bill_preview_name);
            tvClientName = findViewById(R.id.tv_bill_preview_client);
            tvClientGST = findViewById(R.id.tv_bill_preview_gst);
            tvClientAddress = findViewById(R.id.tv_bill_preview_address);
            tvClientState = findViewById(R.id.tv_bill_preview_state);
            tvBillDate = findViewById(R.id.tv_bill_preview_date);
            tvSubTotal = findViewById(R.id.tv_bill_sub_total_value);
            tvGstTotal = findViewById(R.id.tv_bill_gst_total_value);
            tvGrandTotal = findViewById(R.id.tv_bill_grand_total_value);
            tvTotalInWords = findViewById(R.id.tv_bill_total_in_words_value);
            pbBillPreview = findViewById(R.id.pb_bill_preview);
            fbStore = FirebaseFirestore.getInstance();
            pbBillPreview.setVisibility(View.VISIBLE);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));
            rlTotals.setVisibility(View.GONE);
            btnDeleteBill.setEnabled(false);

            ibBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            btnDeleteBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!billDocID.isEmpty()){
                        AlertDialog.Builder adb = new AlertDialog.Builder(BillPreviewActivity.this);
                        AlertDialog ad = adb.create();
                        adb.setTitle("Delete this bill?");
                        adb.setMessage("Are you sure you want to delete this bill?");
                        adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(billDocID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(BillPreviewActivity.this, "Bill deleted Successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                        adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ad.dismiss();
                            }
                        });
                        adb.show();
                    }
                }
            });

            if(!billDocID.isEmpty()){
                rvProductsAdded.setLayoutManager(new LinearLayoutManager(BillPreviewActivity.this));
                BillProductAdapter.hideDeleteIB = true;
                adapterProductsAdded = new BillProductAdapter(BillPreviewActivity.this, alProductAdded);
                rvProductsAdded.setAdapter(adapterProductsAdded);

                Task<DocumentSnapshot> ds = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(billDocID).get();
                ds.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String client = task.getResult().getString("client");
                        String billName = task.getResult().getString("name");
                        tvBillName.setText(billName);
                        tvClientName.setText(client);
                        loadDataForClient(client);
                        String[] dateFromDB = task.getResult().getString("date").split("-");
                        String dateToShow = dateFromDB[2]+"-"+dateFromDB[1]+"-"+dateFromDB[0];
                        tvBillDate.setText("Date: "+dateToShow);
                        Task<QuerySnapshot> qs = fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(billDocID).collection("products").get();
                        qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                for(DocumentSnapshot doc : docs){
                                    loadDataForProduct(doc.getString("name"), Double.parseDouble(doc.getString("quantity")));
                                }
                                rlTotals.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }

        } catch (Exception e) {}

    }

    @SuppressLint("SetTextI18n")
    public void refreshTotals() {
        for(BillProductModel product : alProductAdded){
            subTotal+=(product.getQuantity()*product.getPrice());
            Double gstPer = Double.parseDouble(product.getGstPercentage().replaceAll("%",""));
            gstTotal+=(((product.getQuantity()*product.getPrice())*gstPer)/100);
        }
        grandTotal = subTotal+gstTotal;

        tvSubTotal.setText("₹"+subTotal.toString());
        tvGstTotal.setText("₹"+gstTotal.toString());
        tvGrandTotal.setText("₹"+grandTotal.toString());

        tvTotalInWords.setText(GlobalStaticData.convertToIndianCurrency(grandTotal.toString()));

        pbBillPreview.setVisibility(View.GONE);
        btnDeleteBill.setEnabled(true);
        subTotal=0.;
        gstTotal=0.;
        grandTotal=0.;
    }

    public void loadDataForClient(String client) {
        try{
            Task<QuerySnapshot> qs =  fbStore.collection("business").document(GlobalStaticData.getUID()).collection("clients").whereEqualTo("name", client).get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    if(docs.size()==1){
                        tvClientGST.setText(docs.get(0).getString("gst"));
                        tvClientAddress.setText(docs.get(0).getString("address"));
                        tvClientState.setText(docs.get(0).getString("state"));
                    }
                }
            });
        } catch (Exception e) {

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void loadDataForProduct(String product, Double quantity) {
        try{
            Task<QuerySnapshot> qs =  fbStore.collection("business").document(GlobalStaticData.getUID()).collection("products").whereEqualTo("name", product).get();
            qs.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    if(docs.size()==1){
                        alProductAdded.add(new BillProductModel(product,quantity,docs.get(0).getString("gstpercentage"),Double.parseDouble(docs.get(0).getString("price"))));
                        refreshTotals();
                        adapterProductsAdded.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {

        }
    }
}