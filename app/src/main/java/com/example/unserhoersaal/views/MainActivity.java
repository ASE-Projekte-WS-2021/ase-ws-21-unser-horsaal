package com.example.unserhoersaal.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.unserhoersaal.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
    }
}