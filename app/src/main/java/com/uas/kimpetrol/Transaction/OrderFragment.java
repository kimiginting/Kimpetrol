package com.uas.kimpetrol.Transaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uas.kimpetrol.Adapter.OrderAdapter;
import com.uas.kimpetrol.Component.ErrorDialog;
import com.uas.kimpetrol.Component.LoadingDialog;
import com.uas.kimpetrol.Component.SuccessDialog;
import com.uas.kimpetrol.Helper.API;
import com.uas.kimpetrol.Helper.SPHelper;
import com.uas.kimpetrol.Auth.Login;
import com.uas.kimpetrol.Model.OrderModel;
import com.uas.kimpetrol.R;
import com.uas.kimpetrol.databinding.FragmentOrderBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {

    FragmentOrderBinding bind;
    private SPHelper sp;
    List<OrderModel> data = new ArrayList<>();
    OrderAdapter orderAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentOrderBinding.inflate(inflater, container, false);
        sp = new SPHelper(getContext());
        bind.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        bind.item.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(this, data);
        bind.item.setAdapter(orderAdapter);

        fetchData();

        return bind.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onPause() {
        super.onPause();
        fetchData();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchData();
    }

    public void fetchData() {
        if (getContext() == null) {
            return; // Exit if the context is not available
        }

        LoadingDialog.load(getContext());
        Call<List<OrderModel>> karyaGetRespCall = API.getRetrofit(getContext()).getDataOrder(sp.getIdPengguna());
        karyaGetRespCall.enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                LoadingDialog.close();
                if (response.isSuccessful()) {
                    if (response.body() == null || response.body().isEmpty()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        data.clear();
                        data.addAll(response.body());
                        orderAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (getContext() != null) {
                        ErrorDialog.message(getContext(), getString(R.string.cant_access), bind.getRoot());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                LoadingDialog.close();
                if (getContext() != null) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logout() {
        if (getContext() == null) {
            return;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Item")
                .setMessage("Ingin keluar?")
                .setPositiveButton("Iya", (dialog, which) -> {
                    sp.clearData();
                    startActivity(new Intent(getContext(), Login.class));
                    getActivity().finish();
                })
                .setNegativeButton("Tidak", (dialog, which) -> {

                })
                .show();
    }

    public void updateStatus(String snap_token) {
        if (getContext() == null) {
            return; // Exit if the context is not available
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi")
                .setMessage("Update status pembayaran?")
                .setPositiveButton("Iya", (dialog, which) -> {
                    LoadingDialog.load(getContext());
                    Call<Void> call = API.getRetrofit(getContext()).updateStatus(snap_token);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            LoadingDialog.close();
                            if (response.isSuccessful()) {
                                if (getContext() != null) {
                                    SuccessDialog.message(getContext(), getString(R.string.saved), bind.getRoot());
                                }
                                fetchData();
                            } else {
                                if (getContext() != null) {
                                    ErrorDialog.message(getContext(), getString(R.string.unsaved), bind.getRoot());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            LoadingDialog.close();
                            if (getContext() != null) {
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Tidak", (dialog, which) -> {

                })
                .show();
    }

}
