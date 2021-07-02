package com.scandecode_example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class TestActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView barcode_scanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        //zxing
        barcode_scanner = (ZXingScannerView)findViewById(R.id.barcode_scanner);
        barcode_scanner.setResultHandler(TestActivity.this);
        barcode_scanner.startCamera();
        barcode_scanner.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleResult(Result result) {

    }
}
