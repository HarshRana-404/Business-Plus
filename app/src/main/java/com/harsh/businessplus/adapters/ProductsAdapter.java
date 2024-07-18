package com.harsh.businessplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.businessplus.AddProductActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.models.ClientsModel;
import com.harsh.businessplus.models.ProductsModel;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    Context context;
    ArrayList<ProductsModel> alProduct = new ArrayList<>();

    public ProductsAdapter(Context context, ArrayList<ProductsModel> alProduct){
        this.context = context;
        this.alProduct = alProduct;
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_ui, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductViewHolder holder, int position) {
        try{
            holder.tvProductName.setText(alProduct.get(position).getName());
            holder.tvProductGSTPercentage.setText(alProduct.get(position).getGstPercentage());
            holder.tvProductHsnSac.setText(alProduct.get(position).getHsnSac());
            holder.getTvProductPrice.setText(String.valueOf(alProduct.get(position).getPrice()));
            holder.cvProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddProductActivity.editProductDetails = true;
                    AddProductActivity.productLookUpName = alProduct.get(position).getName();
                    context.startActivity(new Intent(context, AddProductActivity.class));
                }
            });
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return alProduct.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductGSTPercentage, tvProductHsnSac, getTvProductPrice;
        CardView cvProduct;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cvProduct = itemView.findViewById(R.id.cv_product);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductGSTPercentage = itemView.findViewById(R.id.product_gst_percentage);
            tvProductHsnSac = itemView.findViewById(R.id.product_hsn_sac);
            getTvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
