package com.uas.kimpetrol.Cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uas.kimpetrol.Adapter.CartAdapter;
import com.uas.kimpetrol.Component.LoadingDialog;
import com.uas.kimpetrol.Helper.API;
import com.uas.kimpetrol.Helper.Modul;
import com.uas.kimpetrol.Helper.SPHelper;
import com.uas.kimpetrol.Auth.Login;
import com.uas.kimpetrol.Model.CartItem;
import com.uas.kimpetrol.Model.PaymentModel;
import com.uas.kimpetrol.Response.PaymentResponse;
import com.uas.kimpetrol.Transaction.Payment;
import com.uas.kimpetrol.databinding.FragmentCartBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {

    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private SPHelper spHelper;
    private int totalPrice;

    private static final String TAG = "CartFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        initCart();
        setupCheckoutButton();
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return binding.getRoot();
    }

    private void setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Checkout dan lakukan pembayaran?")
                    .setPositiveButton("Iya", (dialog, which) -> {
                        List<CartItem> selectedItems = new ArrayList<>();
                        StringBuilder selectedItemsInfo = new StringBuilder();

                        // Kumpulkan item yang dipilih
                        for (CartItem item : cartItems) {
                            if (item.isChecked()) {
                                selectedItems.add(item);
                                selectedItemsInfo.append("Item ID: ").append(item.getId())
                                        .append(", Quantity: ").append(item.getQuantity())
                                        .append("\n");
                            }
                        }

                        if (!selectedItems.isEmpty()) {
//                            Toast.makeText(getContext(), selectedItemsInfo.toString(), Toast.LENGTH_LONG).show();

                            createPaymentRequest(selectedItems);
                        } else {
                            Toast.makeText(getContext(), "Tidak ada item yang dipilih", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        });
    }

    private void initCart() {
        spHelper = new SPHelper(getActivity());
        cartItems = getCartItems(spHelper);

        if (cartItems == null || cartItems.isEmpty()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Tak ada item untuk ditampilkan", Toast.LENGTH_SHORT).show();
            }
        }

        cartAdapter = new CartAdapter(cartItems, getActivity(), this);
        binding.item.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.item.setAdapter(cartAdapter);

        updateTotalPriceAndCheckoutButton();
    }


    private List<CartItem> getCartItems(SPHelper spHelper) {
        List<String> cartItemsJson = spHelper.getCartItems();
        Gson gson = new Gson();
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(cartItemsJson.toString(), type);
    }

    private void updateTotalPriceAndCheckoutButton() {
        totalPrice = 0;
        boolean hasCheckedItems = false;

        for (CartItem item : cartItems) {
            if (item.isChecked()) {
                totalPrice += Integer.parseInt(item.getPrice()) * item.getQuantity();
                hasCheckedItems = true;
            }
        }

        binding.txtTotalPrice.setText("Total: Rp. " + Modul.numberFormat(String.valueOf(totalPrice)));

        if (hasCheckedItems) {
            binding.btnCheckout.setEnabled(true);
            binding.btnCheckout.setBackgroundColor(Color.parseColor("#800000")); // Maroon color
        } else {
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setBackgroundColor(Color.parseColor("#DADADA")); // Gray color
        }
    }

    @Override
    public void onCartUpdated() {
        updateTotalPriceAndCheckoutButton();
    }

    private void createPaymentRequest(List<CartItem> selectedItems) {
        LoadingDialog.load(getContext());

        List<PaymentModel.Item> items = new ArrayList<>();
        for (CartItem item : selectedItems) {
            items.add(new PaymentModel.Item(String.valueOf(item.getId()), String.valueOf(item.getQuantity())));
        }

        PaymentModel paymentModel = new PaymentModel(spHelper.getIdPengguna(), items);

        // Debugging step to check JSON conversion
        Gson gson = new Gson();
        String jsonString = gson.toJson(paymentModel);
        Log.d(TAG, "PaymentModel JSON: " + jsonString);

        Call<PaymentResponse> call = API.getRetrofit(getContext()).createPayment(paymentModel);
        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                LoadingDialog.close();
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    if (paymentResponse.isSuccess()) {
                        String snapToken = paymentResponse.getSnap_token();

                        Intent intent = new Intent(getContext(), Payment.class);
                        intent.putExtra("snap_token", snapToken);
                        intent.putExtra("total_price", totalPrice);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "Payment failed: " + paymentResponse.getMessage());
                        Toast.makeText(getContext(), paymentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                    Toast.makeText(getContext(), response+"", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                LoadingDialog.close();
                Log.e(TAG, "Error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void logout(){
        spHelper = new SPHelper(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Item")
                .setMessage("Ingin keluar?")
                .setPositiveButton("Iya", (dialog, which) -> {
                    spHelper.clearData();

                    startActivity(new Intent(getContext(), Login.class));
                    getActivity().finish();
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
