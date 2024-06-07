package com.uas.kimpetrol.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.uas.kimpetrol.databinding.ActivityFailedBinding;

public class Failed extends AppCompatActivity {
    ActivityFailedBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityFailedBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Animation fadeOut=new AlphaAnimation(1,0);
        bind.icon.setAnimation(fadeOut);
    }
}