package com.uas.kimpetrol.Transaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uas.kimpetrol.Adapter.OrderDetailAdapter;
import com.uas.kimpetrol.Component.ErrorDialog;
import com.uas.kimpetrol.Component.LoadingDialog;
import com.uas.kimpetrol.Component.SuccessDialog;
import com.uas.kimpetrol.Helper.API;
import com.uas.kimpetrol.Helper.Modul;
import com.uas.kimpetrol.Model.OrderDetailModel;
import com.uas.kimpetrol.R;
import com.uas.kimpetrol.Response.OrderDetailResponse;
import com.uas.kimpetrol.databinding.ActivityDetailOrderBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrder extends AppCompatActivity {
    ActivityDetailOrderBinding bind;
    OrderDetailAdapter adapter;
    List<OrderDetailModel> data = new ArrayList<>();
    String snapToken;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        snapToken = getIntent().getStringExtra("snap_token");

        adapter = new OrderDetailAdapter(this, data);

        bind.item.setLayoutManager(new LinearLayoutManager(this));
        bind.item.setAdapter(adapter);

        fetchOrderDetails(snapToken);
        load(snapToken);
    }

    public void load(String snap_token){
        bind.txtNominalTransaksi.setText(String.valueOf("Rp. "+Modul.numberFormat(getIntent().getStringExtra("total_harga"))));
        bind.txtTanggalTransaksi.setText(String.valueOf(getIntent().getStringExtra("tanggal_transaksi")));

        status = getIntent().getStringExtra("status_order");
        bind.txtStatus.setText(status);

        if (status.equalsIgnoreCase("success")) {
            bind.txtStatus.setBackgroundResource(R.drawable.background_status_teal);
            bind.btnBayar.setVisibility(View.GONE);
        } else {
            bind.txtStatus.setBackgroundResource(R.drawable.backgound_status_navy);
            bind.btnBayar.setVisibility(View.VISIBLE);
        }

        bind.btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(snap_token);
                fetchOrderDetails(snap_token);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOrderDetails(snapToken);

    }

    private void fetchOrderDetails(String snapToken) {
        LoadingDialog.load(DetailOrder.this);
        Call<OrderDetailResponse> call = API.getRetrofit(DetailOrder.this).getOrderDetails(snapToken);
        call.enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                LoadingDialog.close();
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetailResponse orderDetailResponse = response.body();
                    data.clear();
                    data.addAll(orderDetailResponse.getOrders());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DetailOrder.this, "Response not successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                LoadingDialog.close();
                Toast.makeText(DetailOrder.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateStatus(String snap_token){
        new AlertDialog.Builder(DetailOrder.this)
                .setTitle("Konfirmasi")
                .setMessage("Update status pembayaran?")
                .setPositiveButton("Iya", (dialog, which) -> {
                    LoadingDialog.load(DetailOrder.this);
                    Call<Void> call = API.getRetrofit(DetailOrder.this).updateStatus(snap_token);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            LoadingDialog.close();
                            if (response.isSuccessful()){
                                status = "success";bind.txtStatus.setText("Success");
                                bind.txtStatus.setBackgroundResource(R.drawable.background_status_teal);
                                bind.btnBayar.setVisibility(View.GONE);

                                SuccessDialog.message(DetailOrder.this, getString(R.string.saved), bind.getRoot());

                            } else {
                                ErrorDialog.message(DetailOrder.this, getString(R.string.unsaved), bind.getRoot());
                            }
                            fetchOrderDetails(snap_token);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            LoadingDialog.close();
                            Toast.makeText(DetailOrder.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
