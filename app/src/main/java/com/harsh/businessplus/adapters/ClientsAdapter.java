package com.harsh.businessplus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.businessplus.AddClientActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.models.ClientsModel;

import java.util.ArrayList;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientViewHolder> {

    Context context;
    ArrayList<ClientsModel> alClient = new ArrayList<>();

    public ClientsAdapter(Context context, ArrayList<ClientsModel> alClient){
        this.context = context;
        this.alClient = alClient;
    }

    @NonNull
    @Override
    public ClientsAdapter.ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.client_ui, parent, false);
        return new ClientViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ClientsAdapter.ClientViewHolder holder, int position) {
        try{
            holder.tvClientName.setText(alClient.get(position).getName());
            holder.tvClientBusiness.setText(String.valueOf("Monthly business: "+alClient.get(position).getMonthlyBusiness()));
            holder.tvClientGST.setText("GST: "+alClient.get(position).getGst());
            holder.rlClientRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddClientActivity.editClientDetails = true;
                    AddClientActivity.lookUpClientGST = alClient.get(position).getGst();
                    context.startActivity(new Intent(context, AddClientActivity.class));
                }
            });
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return alClient.size();
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvClientBusiness, tvClientGST;
        RelativeLayout rlClientRoot;
        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvClientBusiness = itemView.findViewById(R.id.tv_monthly_business_amount);
            tvClientGST = itemView.findViewById(R.id.tv_client_gst);
            rlClientRoot = itemView.findViewById(R.id.rl_client_root);
        }
    }
}
