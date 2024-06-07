package com.uas.kimpetrol.Helper;

import com.uas.kimpetrol.Model.LoginModel;
import com.uas.kimpetrol.Model.OrderModel;
import com.uas.kimpetrol.Model.PaymentModel;
import com.uas.kimpetrol.Model.ProductModel;
import com.uas.kimpetrol.Model.RegisterModel;
import com.uas.kimpetrol.Response.LoginResponse;
import com.uas.kimpetrol.Response.OrderDetailResponse;
import com.uas.kimpetrol.Response.RegisterResponse;
import com.uas.kimpetrol.Response.PaymentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Service {
    //AUTH
    @POST("custom-login_api")
    Call<LoginResponse> login(@Body LoginModel loginModel);
    @POST("custom-registration_api")
    Call<RegisterResponse> register(@Body RegisterModel registerModel);

    //Product
    @GET("barangs_api")//get
    Call<List<ProductModel>> getDataProduct();
    @GET("barangs_api/{id}")
    Call<List<ProductModel>> getDataProduct(@Path("id") int id);

    //Order
    @GET("orders_api/user/{id}") // Index order
    Call<List<OrderModel>> getDataOrder(@Path("id") int id);

    @GET("orders/detail/{snap_token}") // Order detail
    Call<OrderDetailResponse> getOrderDetails(@Path("snap_token") String snapToken);

    @POST("orders/update_status/{snap_token}") // Update status
    Call<Void> updateStatus(@Path("snap_token") String snap_token);

    @POST("orders") // Create order
    Call<PaymentResponse> createPayment(@Body PaymentModel paymentModel);
}
