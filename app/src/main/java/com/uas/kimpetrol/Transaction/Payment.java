package com.uas.kimpetrol.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.uas.kimpetrol.Component.LoadingDialog;
import com.uas.kimpetrol.MainActivity;
import com.uas.kimpetrol.databinding.ActivityPaymentBinding;

public class Payment extends AppCompatActivity {
    ActivityPaymentBinding bind;
    String snap_token, snap_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        load();
    }

    public void load() {
        LoadingDialog.load(Payment.this);

        WebSettings webSettings = bind.webviewPayment.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        bind.webviewPayment.setWebChromeClient(new WebChromeClient());
        WebView.setWebContentsDebuggingEnabled(true);

        snap_token = getIntent().getStringExtra("snap_token");
        snap_url = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + snap_token;

        bind.webviewPayment.loadUrl(snap_url);
        bind.webviewPayment.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LoadingDialog.load(Payment.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LoadingDialog.close();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LoadingDialog.close();
                Toast.makeText(Payment.this, "Failed to load URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToNextFragment() {
        startActivity(new Intent(Payment.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (bind.webviewPayment.canGoBack()) {
            bind.webviewPayment.goBack();
        } else {
            super.onBackPressed();
            navigateToNextFragment();
        }
    }

}
