package com.uas.kimpetrol.Response;

import com.uas.kimpetrol.Model.LoginModel;

public class LoginResponse {
    private String pesan, token;
    private LoginModel data;

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginModel getData() {
        return data;
    }

    public void setData(LoginModel data) {
        this.data = data;
    }
}
