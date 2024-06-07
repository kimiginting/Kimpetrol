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
import com.uas.kimpetrol.Model.RegisterModel;
import com.uas.kimpetrol.R;
import com.uas.kimpetrol.Response.RegisterResponse;
import com.uas.kimpetrol.databinding.ActivityRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    ActivityRegisterBinding bind;
    EditText etEmail, etPassword, etConfirm, etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.login.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });
    }

    public void buatAkun(View view) {
        if (!validateInputs()) {
            RegisterModel rg = new RegisterModel();
            rg.setEmail(bind.email.getText().toString());
            rg.setPassword(bind.password.getText().toString());
            rg.setName(bind.username.getText().toString());
            processRegister(rg);
        }
    }

    private void processRegister(RegisterModel registerModel) {
        LoadingDialog.load(Register.this);
        SPHelper sp = new SPHelper(Register.this);
        Call<RegisterResponse> registerResponseCall = API.getRetrofit().register(registerModel);
        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                LoadingDialog.close();
                if (response.isSuccessful() && response.body() != null) {
                    // Save token and user info
                    sp.setToken(response.body().getToken());
                    sp.setEmail(response.body().getData().getEmail());
                    sp.setUsername(response.body().getData().getName());
                    sp.setIdPengguna(response.body().getData().getId());

                    SuccessDialog.message(Register.this, "Akun berhasil dibuat", bind.getRoot());

                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                } else {
                    ErrorDialog.message(Register.this, "Tidak dapat membuat akun", bind.getRoot());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                LoadingDialog.close();
                Toast.makeText(Register.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        etEmail = bind.email;
        etPassword = bind.password;
        etConfirm = bind.confirmpassword;
        etUsername = bind.username;

        boolean hasError = false;

        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Harap diisi");
            etEmail.requestFocus();
            hasError = true;
        } else if (!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            ErrorDialog.message(Register.this, getString(R.string.is_email), bind.getRoot());
            hasError = true;
        }

        if (etUsername.getText().toString().isEmpty()) {
            etUsername.setError("Harap diisi");
            etUsername.requestFocus();
            hasError = true;
        }

        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Harap diisi");
            etPassword.requestFocus();
            hasError = true;
        } else if (!etPassword.getText().toString().matches(etConfirm.getText().toString())) {
            ErrorDialog.message(Register.this, getString(R.string.unmatch), bind.getRoot());
            hasError = true;
        } else if (etPassword.getText().toString().length() < 8) {
            ErrorDialog.message(Register.this, "Password harus terdisi dari minimal 8 digit, angka, dan karakter", bind.getRoot());
            hasError = true;
        }

        return hasError;
    }
}
