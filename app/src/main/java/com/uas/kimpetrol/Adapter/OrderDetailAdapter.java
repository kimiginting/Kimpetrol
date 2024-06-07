package com.uas.kimpetrol.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uas.kimpetrol.Helper.Modul;
import com.uas.kimpetrol.Model.OrderDetailModel;
import com.uas.kimpetrol.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>{
    private Context context;
    private List<OrderDetailModel> data;

    public OrderDetailAdapter(Context context, List<OrderDetailModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        OrderDetailModel orderDetailModel = data.get(position);
        holder.harga.setText("Rp. " + Modul.numberFormat(String.valueOf(orderDetailModel.getHarga())));
        holder.nama.setText(Modul.upperCaseFirst(orderDetailModel.getNama()));
        holder.jumlah.setText(String.valueOf(orderDetailModel.getJumlah()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga, jumlah;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.txtProduk);
            harga = itemView.findViewById(R.id.txtHarga);
            jumlah = itemView.findViewById(R.id.txtJumlah);
        }
    }
}
