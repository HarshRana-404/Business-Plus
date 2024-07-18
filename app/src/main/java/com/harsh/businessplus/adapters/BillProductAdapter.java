package com.harsh.businessplus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.businessplus.R;
import com.harsh.businessplus.fragments.BillProductsFragment;
import com.harsh.businessplus.models.BillProductModel;

import java.util.ArrayList;

public class BillProductAdapter extends RecyclerView.Adapter<BillProductAdapter.ProductViewHolder> {

    Context context;
    public static ArrayList<BillProductModel> alProduct = new ArrayList<>();
    public static Boolean hideDeleteIB = false;

    public BillProductAdapter(Context context, ArrayList<BillProductModel> alProduct){
        this.context = context;
        BillProductAdapter.alProduct = alProduct;
    }

    @NonNull
    @Override
    public BillProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_product_ui, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull BillProductAdapter.ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            holder.tvProductName.setText(alProduct.get(position).getName());
            holder.tvProductQuantity.setText(alProduct.get(position).getQuantity().toString());
            holder.tvProductGstPercentage.setText(alProduct.get(position).getGstPercentage());
            holder.tvProductPrice.setText(alProduct.get(position).getPrice().toString());
            holder.tvProductTotal.setText(alProduct.get(position).getTotal().toString());

            if(hideDeleteIB){
                holder.ibProductDelete.setVisibility(View.GONE);
            }else{
                holder.ibProductDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alProduct.remove(position);
                        notifyDataSetChanged();
                        if(alProduct.isEmpty()){
                            BillProductsFragment.hideRlTableHeader();
                            BillProductsFragment.hasItems = false;
                        }
                    }
                });
            }

        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return alProduct.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductQuantity, tvProductGstPercentage,  tvProductPrice, tvProductTotal;
        ImageButton ibProductDelete;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_bill_product_name);
            tvProductQuantity = itemView.findViewById(R.id.tv_bill_product_quantity);
            tvProductGstPercentage = itemView.findViewById(R.id.tv_bill_product_gst_per);
            tvProductPrice = itemView.findViewById(R.id.tv_bill_product_price);
            tvProductTotal = itemView.findViewById(R.id.tv_bill_product_total);
            ibProductDelete = itemView.findViewById(R.id.ib_bill_product_delete);
        }
    }
}
