package com.harsh.businessplus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harsh.businessplus.AddClientActivity;
import com.harsh.businessplus.BillPreviewActivity;
import com.harsh.businessplus.MainActivity;
import com.harsh.businessplus.R;
import com.harsh.businessplus.fragments.BillsFragment;
import com.harsh.businessplus.models.BillsModel;
import com.harsh.businessplus.staticdata.GlobalStaticData;

import java.util.ArrayList;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {

    Context context;
    ArrayList<BillsModel> alBill = new ArrayList<>();
    public static Boolean isLongPressed = false;
    ArrayList<String> alBillsDocIdToBeDeleted = new ArrayList<>();
    FirebaseFirestore fbStore;

    public BillsAdapter(Context context, ArrayList<BillsModel> alBill){
        this.context = context;
        this.alBill = alBill;
    }

    @NonNull
    @Override
    public BillsAdapter.BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_ui, parent, false);
        return new BillViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull BillsAdapter.BillViewHolder holder, int position) {
        try{
            Animation animSlideIn = new TranslateAnimation(500, 0, 0, 0);
            animSlideIn.setDuration(300);
            holder.cvBill.startAnimation(animSlideIn);
            holder.tvBillName.setText(alBill.get(position).getName());
            holder.tvClient.setText(alBill.get(position).getClient());
            holder.tvBillAmount.setText(alBill.get(position).getTotal().toString());
            String[] dateFromDB = alBill.get(position).getDate().split("-");
            String dateToShow = dateFromDB[2]+"-"+dateFromDB[1]+"-"+dateFromDB[0];
            holder.tvBillDate.setText(dateToShow);
            if(!alBill.get(position).getSelection()){
                holder.cvBill.setBackgroundColor(context.getResources().getColor(R.color.main_bg_off));
                holder.ivBill.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bill_focused));
            }else{
                holder.cvBill.setBackgroundColor(context.getResources().getColor(R.color.cv_selection_color));
                holder.ivBill.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_checked));
            }
            BillsFragment.tvDeleteSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    AlertDialog ad = adb.create();
                    adb.setTitle("Delete these bills?");
                    adb.setMessage("Are you sure you want to delete selected bill(s)?");
                    adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(String dlDocID : alBillsDocIdToBeDeleted){
                                fbStore.collection("business").document(GlobalStaticData.getUID()).collection("bills").document(dlDocID).delete();
                            }
                            Toast.makeText(context, "Bills deleted Successfully!", Toast.LENGTH_SHORT).show();
                            BillsFragment.rlBillsActionBar.setVisibility(View.GONE);
                            isLongPressed = false;
                            alBillsDocIdToBeDeleted.clear();
                            context.startActivity(new Intent(context, MainActivity.class));
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
            });
            holder.cvBill.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    try{
                        if(isLongPressed){
                            if(!alBill.get(position).getSelection()){
                                alBillsDocIdToBeDeleted.add(alBill.get(position).getDocId());
                                alBill.get(position).setSelection(true);
                                BillsFragment.tvSelectionCount.setText(alBillsDocIdToBeDeleted.size()+" Selected");
                                holder.cvBill.setBackgroundColor(context.getResources().getColor(R.color.cv_selection_color));
                                holder.ivBill.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_checked));
                            }else{
                                alBillsDocIdToBeDeleted.remove(alBill.get(position).getDocId());
                                alBill.get(position).setSelection(false);
                                BillsFragment.tvSelectionCount.setText(alBillsDocIdToBeDeleted.size()+" Selected");
                                holder.cvBill.setBackgroundColor(context.getResources().getColor(R.color.main_bg_off));
                                holder.ivBill.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bill_focused));
                            }
                            if(alBillsDocIdToBeDeleted.isEmpty()){
                                BillsFragment.rlBillsActionBar.setVisibility(View.GONE);
                                isLongPressed = false;
                            }
                        }else{
                            BillPreviewActivity.billDocID = alBill.get(position).getDocId();
                            context.startActivity(new Intent(context, BillPreviewActivity.class));
                        }
                    } catch (Exception e) {

                    }
                }
            });
            holder.cvBill.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public boolean onLongClick(View v) {
                    try{
                        BillsFragment.rlBillsActionBar.setVisibility(View.VISIBLE);
                        isLongPressed = true;
                        alBillsDocIdToBeDeleted.add(alBill.get(position).getDocId());
                        BillsFragment.tvSelectionCount.setText(alBillsDocIdToBeDeleted.size()+" Selected");
                        alBill.get(position).setSelection(true);
                        holder.cvBill.setBackgroundColor(context.getResources().getColor(R.color.cv_selection_color));
                        holder.ivBill.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_checked));
                    } catch (Exception e) {
                    }
                    return true;
                }
            });
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return alBill.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillName, tvBillAmount, tvClient, tvBillDate;
        CardView cvBill;
        ImageView ivBill;
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            cvBill = itemView.findViewById(R.id.cv_bill);
            tvBillName = itemView.findViewById(R.id.tv_bill_name);
            tvBillAmount = itemView.findViewById(R.id.tv_bill_amount);
            tvClient = itemView.findViewById(R.id.tv_bill_client_name);
            tvBillDate = itemView.findViewById(R.id.tv_bill_date);
            ivBill = itemView.findViewById(R.id.iv_bill);
            fbStore = FirebaseFirestore.getInstance();
        }
    }
}
