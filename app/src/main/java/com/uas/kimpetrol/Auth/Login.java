package com.uas.kimpetrol.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.uas.kimpetrol.Component.ErrorDialog;
import com.uas.kimpetrol.Component.LoadingDialog;
import com.uas.kimpetrol.Component.SuccessDialog;
import com.uas.kimpetrol.Helper.API;
import com.uas.kimpetrol.Helper.SPHelper;
import com.uas.kimpetrol.MainActivity;
import com.uas.kimpetrol.Model.LoginModel;
import com.uas.kimpetrol.Response.LoginResponse;
import com.uas.kimpetrol.databinding.ActivityLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    ActivityLoginBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }

    public void login(View view) {
        if (!validateInputs()) {
            LoginModel loginModel = new LoginModel(bind.username.getText().toString(), bind.password.getText().toString());
            processLogin(loginModel);
        }
    }

    private void processLogin(LoginModel loginModel) {
        SPHelper sp = new SPHelper(Login.this);
        LoadingDialog.load(Login.this);
        Call<LoginResponse> loginResponseCall = API.getRetrofit(Login.this).login(loginModel);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoadingDialog.close();
                if (response.isSuccessful() && response.body() != null) {
                    // Save token and user info
                    sp.setToken(response.body().getToken());
                    sp.setEmail(response.body().getData().getEmail());
                    sp.setUsername(response.body().getData().getName());
                    sp.setIdPengguna(response.body().getData().getId());
                    SuccessDialog.message(Login.this, "Login berhasil", bind.getRoot());

                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    ErrorDialog.message(Login.this, "Akun tidak ditemukan, periksa kembali password anda", bind.getRoot());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoadingDialog.close();
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(Login.this, Register.class));
        finish();
    }

    private boolean validateInputs() {
        EditText username = bind.username;
        EditText password = bind.password;
        boolean hasError = false;

        if (username.getText().toString().isEmpty()) {
            username.setError("Harap diisi");
            username.requestFocus();
            hasError = true;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError("Harap diisi");
            password.requestFocus();
            hasError = true;
        }

        return hasError;
    }
}
